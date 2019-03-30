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

import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.ast.expressions.CallNode;
import com.twineworks.tweakflow.lang.values.Arity1CallSite;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.CallContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.calls.CallSites;

final public class FixedFunArity1CallOp implements ExpressionOp {

  private final CallNode node;
  private Arity1CallSite cs;
  private final ExpressionOp arg0Op;
  private final Value f;

  public FixedFunArity1CallOp(CallNode node) {
    this.node = node;
    this.f = Interpreter.evaluateInEmptyScope(node.getExpression());
    this.arg0Op = node.getArguments().getList().get(0).getExpression().getOp();
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    if (cs == null){
      cs = CallSites.createArity1CallSite(f, node, stack, context, new CallContext(stack, context));
    }
    return cs.call(arg0Op.eval(stack, context));
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new FixedFunArity1CallOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new FixedFunArity1CallOp(node);
  }

}
