/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Twineworks GmbH
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

package com.twineworks.tweakflow.lang.analysis.references;

import com.twineworks.tweakflow.lang.analysis.visitors.AExpressionDescendingVisitor;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.meta.DocNode;
import com.twineworks.tweakflow.lang.ast.meta.MetaNode;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;

public class MetaDataAnalysisVisitor extends AExpressionDescendingVisitor implements Visitor {

  private boolean inMeta = false;
  private boolean inDoc = false;

  private void ensureNotInMeta(Node node){
    if (inMeta | inDoc){

      String nodeType;

      if (inMeta){
        nodeType = "meta";
      } else {
        nodeType = "doc";
      }

      throw new LangException(LangError.LITERAL_VALUE_REQUIRED, "computations and functions not allowed in " + nodeType, node.getSourceInfo());
    }
  }

  @Override
  public ModuleNode visit(ModuleNode node) {
    if (node.hasMeta()){
      visit(node.getMeta());
    }

    if (node.hasDoc()){
      visit(node.getDoc());
    }

    node.getComponents().forEach(this::visit);
    return node;
  }

  @Override
  public MetaNode visit(MetaNode node) {
    inMeta = true;
    visit(node.getExpression());
    inMeta = false;
    return node;
  }

  @Override
  public DocNode visit(DocNode node) {
    inDoc = true;
    visit(node.getExpression());
    inDoc = false;
    return node;
  }

  @Override
  public LibraryNode visit(LibraryNode node) {
    if (node.hasMeta()){
      visit(node.getMeta());
    }

    if (node.hasDoc()){
      visit(node.getDoc());
    }

    visit(node.getVars());
    return node;
  }

  @Override
  public VarDefNode visit(VarDefNode node) {

    if (node.hasDoc()){
      visit(node.getDoc());
    }

    if (node.hasMeta()){
      visit(node.getMeta());
    }

    visit(node.getValueExpression());
    return node;
  }

  @Override
  public ExpressionNode visit(DebugNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(CallNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(CastNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(FunctionNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(ContainerAccessNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(IfNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(IsNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(IdenticalNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(NotIdenticalNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(TypeOfNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(LetNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public BindingsNode visit(BindingsNode node) {
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(ListNode node) {
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(MatchNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(ForNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(LessThanNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(LessThanOrEqualNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(GreaterThanNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(GreaterThanOrEqualNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(AndNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(OrNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(NotNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(NegateNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(EqualNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(NotEqualNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(PlusNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(StringConcatNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(ListConcatNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(DictMergeNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(MultNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(PowNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(DivNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(IntDivNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(ModNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(MinusNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(DictNode node) {
    return super.visit(node);
  }

  @Override
  public DictEntryNode visit(DictEntryNode node) {
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(DefaultNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(ThrowNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(TryCatchNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ReferenceNode visit(ReferenceNode node) {
    ensureNotInMeta(node);
    return node;
  }

  @Override
  public VarDefs visit(VarDefs node) {
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(BitwiseNotNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(BitwiseAndNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(BitwiseOrNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(BitwiseXorNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(BitwiseShiftLeftNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(BitwisePreservingShiftRightNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

  @Override
  public ExpressionNode visit(BitwiseZeroShiftRightNode node) {
    ensureNotInMeta(node);
    return super.visit(node);
  }

}
