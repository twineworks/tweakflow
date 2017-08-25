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

package com.twineworks.tweakflow.lang.interpreter.ops;

import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.ListNode;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.util.List;

final public class ListOp implements ExpressionOp {

  private final ListNode node;
  private final int size;
  private final ExpressionOp[] ops;
  private final Value[] values;
  private final boolean[] nonConstIndexes;
  private final boolean hasConst;
  private final int firstNonConstIndex;
  private final int lastNonConstIndex;

  public ListOp(ListNode node) {
    this.node = node;
    this.size = node.getElements().size();
    ops = new ExpressionOp[size];
    values = new Value[size];
    nonConstIndexes = new boolean[size];
    int firstDynamic = size+1;
    int lastDynamic = -1;

    List<ExpressionNode> elements = node.getElements();
    int constIndexes = 0;

    for (int i = 0; i < size; i++) {
      ExpressionNode expressionNode = elements.get(i);
      ops[i] = expressionNode.getOp();
      Value v = null;
      if (expressionNode.getOp().isConstant()){
        try {
          v = Interpreter.evaluateInEmptyScope(expressionNode);
        } catch (LangException ignored) {}
      }
      if (v != null){
        values[i] = v;
        nonConstIndexes[i] = false;
        constIndexes++;
      }
      else{
        if (i < firstDynamic) firstDynamic = i;
        if (i > lastDynamic) lastDynamic = i;
        nonConstIndexes[i] = true;
      }
    }
    firstNonConstIndex = firstDynamic;
    lastNonConstIndex = lastDynamic;
    hasConst = constIndexes > 0;
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    if (hasConst){
      for (int i = firstNonConstIndex; i <= lastNonConstIndex; i++) {
        if (nonConstIndexes[i]){
          values[i] = ops[i].eval(stack, context);
        }
      }
      Value ret = Values.make(new ListValue(values));

      // aggressively clear non-constants
      for (int i = firstNonConstIndex; i <= lastNonConstIndex; i++) {
        if (nonConstIndexes[i]){
          values[i] = null;
        }
      }

      return ret;
    }
    else{
      for (int i = 0; i < size; i++) {
        values[i] = ops[i].eval(stack, context);
      }
      Value ret = Values.make(new ListValue(values));

      // aggressively clear non-constants
      for (int i = 0; i < size; i++) {
        values[i] = null;
      }

      return ret;
    }

  }

  @Override
  public boolean isConstant() {
    for (ExpressionOp op : ops) {
      if (!op.isConstant()) return false;
    }

    return true;
  }

  @Override
  public ExpressionOp specialize() {
    return new ListOp(node);
  }


  @Override
  public ExpressionOp refresh() {
    return new ListOp(node);
  }


}
