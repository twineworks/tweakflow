/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Twineworks GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.twineworks.tweakflow.lang.analysis.constants;

import com.twineworks.tweakflow.lang.analysis.visitors.AExpressionDescendingVisitor;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.interpreter.ops.ConstantOp;
import com.twineworks.tweakflow.lang.interpreter.ops.ExpressionOp;
import com.twineworks.tweakflow.lang.interpreter.ops.FunctionOp;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueProvider;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class ConstantOpsFoldingVisitor extends AExpressionDescendingVisitor implements Visitor {

  private final HashSet<VarDefNode> visitedVarDefs = new HashSet<>();
  private final ConstantPool constantPool;

  public ConstantOpsFoldingVisitor() {
    this.constantPool = new ConstantPool();
  }

  public ConstantOpsFoldingVisitor(ConstantPool constantPool) {
    this.constantPool = constantPool;
  }

  @Override
  public InteractiveNode visit(InteractiveNode node) {
    node.getSections().forEach(this::visit);
    return node;
  }

  @Override
  public InteractiveSectionNode visit(InteractiveSectionNode node) {
    visit(node.getVars());
    return node;
  }

  @Override
  public ModuleNode visit(ModuleNode node) {
    if (node.hasDoc()) visit(node.getDoc());
    if (node.hasMeta()) visit(node.getMeta());
    node.getImports().forEach(this::visit);
    node.getComponents().forEach(this::visit);
    return node;
  }

  @Override
  public LibraryNode visit(LibraryNode node) {
    if (node.hasDoc()) visit(node.getDoc());
    if (node.hasMeta()) visit(node.getMeta());
    visit(node.getVars());
    return node;
  }

  @Override
  public VarDefNode visit(VarDefNode node) {
    if (visitedVarDefs.contains(node)) return node;
    visitedVarDefs.add(node);

    if (node.hasDoc()) visit(node.getDoc());
    if (node.hasMeta()) visit(node.getMeta());
    visit(node.getValueExpression());
    return node;
  }

  private Value evalConstantFunction(FunctionNode node){
    Set<ReferenceNode> refs = node.getClosedOverReferences();
    if (refs.isEmpty()){
      return Interpreter.evaluateInEmptyScope(node);
    }

    // we have some closed over references that need resolving to Values
    IdentityHashMap<ReferenceNode, ValueProvider> closures = new IdentityHashMap<>();
    for (ReferenceNode reference : node.getClosedOverReferences()) {
      foldConstantOp(reference);
      ConstantOp constantOp = (ConstantOp) reference.getOp();
      closures.put(reference, constantOp.getValue());
    }

    FunctionOp op = (FunctionOp) node.getOp();
    return op.evalWithClosures(closures);

  }

  private void foldConstantOp(ExpressionNode node){
    ExpressionOp nodeOp = node.getOp();
    node.setOp(nodeOp.refresh());

    if (nodeOp.isConstant()){
      try {
        Value c;
        if (nodeOp instanceof ConstantOp){
          c = ((ConstantOp) nodeOp).getValue();
        }
        // give functions their closed over references, deal with recursion
        else if (node instanceof FunctionNode && nodeOp instanceof FunctionOp){
          c = evalConstantFunction((FunctionNode) node);
        }
        else{
          c = Interpreter.evaluateInEmptyScope(node);
        }
        node.setOp(new ConstantOp(constantPool.get(c)));
      }
      catch(LangException ignored){
//        ignored.printDigestMessageAndStackTrace();
      }
    }
  }

  @Override
  public ReferenceNode visit(ReferenceNode node) {
    // if the node is pointing to a var that is a constant, the reference
    // itself is a constant
    super.visit(node);

    Symbol symbol = node.getReferencedSymbol();
    if (symbol != null){
      SymbolNode symbolNode = symbol.getTargetNode();
      if (symbolNode instanceof VarDefNode){
        VarDefNode targetVar = (VarDefNode) symbolNode;
        if (!visitedVarDefs.contains(targetVar)){
          visit(targetVar);
        }
        if (!targetVar.isDeclaredProvided()){
          ExpressionNode targetValue = targetVar.getValueExpression();
          if (targetValue.getOp().isConstant()){
            try {
              node.setOp(new ConstantOp(constantPool.get(Interpreter.evaluateInEmptyScope(targetValue))));
            }
            catch(LangException ignored){}
          }
        }
      }

    }
    return node;
  }

  @Override
  public ExpressionNode visit(AndNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(OrNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(IfNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseAndNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseNotNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseOrNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwisePreservingShiftRightNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseShiftLeftNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseXorNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseZeroShiftRightNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(CastNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(StringConcatNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(DivNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(CallNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(PartialApplicationNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(FunctionNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(IsNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(TypeOfNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(DefaultNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(ListNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(DictNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }

  @Override
  public ExpressionNode visit(TryCatchNode node) {
    super.visit(node);
    foldConstantOp(node);
    return node;
  }



}
