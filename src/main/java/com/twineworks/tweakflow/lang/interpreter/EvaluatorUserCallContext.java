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

package com.twineworks.tweakflow.lang.interpreter;

import com.twineworks.tweakflow.lang.values.*;
import com.twineworks.tweakflow.lang.interpreter.calls.CallSites;

public class EvaluatorUserCallContext implements UserCallContext {

  private final Stack stack;
  private final EvaluationContext evaluationContext;

  public EvaluatorUserCallContext(Stack stack, EvaluationContext evaluationContext) {
    this.stack = stack;
    this.evaluationContext = evaluationContext;
  }

  @Override
  public Value call(Value f, Value ... args) {
    return Evaluator.performUserCall(f, args, stack, evaluationContext);
  }

  @Override
  public Arity1CallSite createArity1CallSite(Value f) {
    return CallSites.createArity1CallSite(f, stack.peek().getNode(), stack, evaluationContext, this);
  }

  @Override
  public Arity2CallSite createArity2CallSite(Value f) {
    return CallSites.createArity2CallSite(f, stack.peek().getNode(), stack, evaluationContext, this);
  }

  @Override
  public Arity3CallSite createArity3CallSite(Value f) {
    return CallSites.createArity3CallSite(f, stack.peek().getNode(), stack, evaluationContext, this);
  }

  @Override
  public void debug(Value v) {
    evaluationContext.getDebugHandler().debug(v);
  }

}
