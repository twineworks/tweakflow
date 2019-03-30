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

import com.twineworks.tweakflow.lang.ast.expressions.TryCatchNode;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.interpreter.Stack;

final public class TryCatchOp implements ExpressionOp {

  private final TryCatchNode node;

  public TryCatchOp(TryCatchNode node) {
    this.node = node;
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {
    return Interpreter.evaluateTryCatchNode(node, stack, context);
  }

  @Override
  public boolean isConstant() {

    // NOTE:
    // if try throws, evaluation results in result of catch block
    // but constant traces are not supported
    if (node.getCaughtTrace() == null){
      return node.getTryExpression().getOp().isConstant();
    }
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new TryCatchOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new TryCatchOp(node);
  }

}
