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

package com.twineworks.tweakflow.lang.interpreter.ops;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.ast.expressions.LetNode;
import com.twineworks.tweakflow.lang.ast.structure.BindingsNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefs;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.StackEntry;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.LocalMemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpaceType;

final public class LetOp implements ExpressionOp {

  private final LetNode node;
  private final VarDefNode[] vars;
  private final Symbol[] varSymbols;
  private final ExpressionOp[] varOps;
  private final ExpressionOp expOp;
  private final ConstShapeMap<Cell> templateShapeMap;
  private final Scope scope;
  private final ConstShapeMap.Accessor[] accessors;

  @SuppressWarnings("unchecked")
  public LetOp(LetNode node) {
    this.node = node;
    scope = node.getExpression().getScope();
    expOp = node.getExpression().getOp();

    BindingsNode bindings = node.getBindings();
    VarDefs varDefs = bindings.getVars();
    templateShapeMap = new ConstShapeMap<>(varDefs.getShapeKeys());
    vars = varDefs.getDependencyOrderedArray();

    varOps = new ExpressionOp[vars.length];
    varSymbols = new Symbol[vars.length];
    accessors = new ConstShapeMap.Accessor[vars.length];

    for (int i=0;i < vars.length; i++){
      varOps[i] = vars[i].getValueExpression().getOp();
      varSymbols[i] = vars[i].getSymbol();
      accessors[i] = ConstShapeMap.accessor(vars[i].getSymbolName());
    }

  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {
    return evalWithNewFrame(stack, context);
  }

  @SuppressWarnings("unchecked")
  private Value evalWithNewFrame(Stack stack, EvaluationContext context){

    StackEntry currentStackEntry = stack.peek();
    // create a new frame
    ConstShapeMap<Cell> bindingsCells = new ConstShapeMap<>(templateShapeMap);
    LocalMemorySpace bindingsSpace = new LocalMemorySpace(
        currentStackEntry.getSpace(),
        scope,
        MemorySpaceType.LOCAL,
        bindingsCells
    );

    Cell[] cells = new Cell[vars.length];

    for (int i = 0, varsLength = vars.length; i < varsLength; i++) {
      Cell cell = new Cell().setLeafSymbol(varSymbols[i]).setEnclosingSpace(bindingsSpace);
      cells[i] = cell;
      bindingsCells.seta(accessors[i], cell);
    }

    stack.push(new StackEntry(node, bindingsSpace, currentStackEntry.getClosures()));

    for (int i = 0, varsLength = vars.length; i < varsLength; i++) {
      Cell cell = cells[i];
      cell.setValue(varOps[i].eval(stack, context));
    }

    Value ret = expOp.eval(stack, context);
    stack.pop();

    return ret;

  }

  @Override
  public boolean isConstant() {
    return expOp.isConstant();
  }

  @Override
  public ExpressionOp specialize() {
    return new LetOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new LetOp(node);
  }


}
