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

import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.ast.expressions.IfNode;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

final public class IfOp implements ExpressionOp {

  private final ExpressionOp conditionOp;
  private final ExpressionOp thenOp;
  private final ExpressionOp elseOp;
  private final IfNode node;

  public IfOp(IfNode node){
    this.node = node;
    conditionOp = node.getCondition().getOp();
    thenOp = node.getThenExpression().getOp();
    elseOp = node.getElseExpression().getOp();
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {
    Value conditionValue = conditionOp.eval(stack, context);
    if (conditionValue != Values.NIL && conditionValue.bool()){
      return thenOp.eval(stack, context);
    }
    else{
      return elseOp.eval(stack, context);
    }
  }

  @Override
  public boolean isConstant() {
    if (conditionOp.isConstant()){
      try {
        Value cond = Interpreter.evaluateInEmptyScope(node.getCondition());
        if (cond != Values.NIL && cond.bool()){
          return thenOp.isConstant();
        }
        else {
          return elseOp.isConstant();
        }
      }
      catch (LangException ignored){}
    }
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new IfOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new IfOp(node);
  }

}
