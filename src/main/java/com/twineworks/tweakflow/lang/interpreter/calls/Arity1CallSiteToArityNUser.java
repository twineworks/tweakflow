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

package com.twineworks.tweakflow.lang.interpreter.calls;

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.values.*;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.StackEntry;
import com.twineworks.tweakflow.lang.interpreter.memory.LocalMemorySpace;

import java.util.Collections;

public class Arity1CallSiteToArityNUser implements Arity1CallSite {

  private final ArityNUserFunction f;
  private final Stack stack;
  private final UserCallContext userCallContext;
  private final StackEntry stackEntry;

  private final Type p0Type;
  private final Value[] args;
  private final Type retType;

  public Arity1CallSiteToArityNUser(UserFunctionValue f, Node at, Stack stack, UserCallContext userCallContext) {

    this.f = (ArityNUserFunction) f.getUserFunction();
    FunctionSignature signature = f.getSignature();

    this.stack = stack;
    this.userCallContext = userCallContext;
    stackEntry = new StackEntry(at, LocalMemorySpace.EMPTY, Collections.emptyMap());

    // fill args with default values
    FunctionParameter[] parameterArray = signature.getParameterArray();
    p0Type = parameterArray[0].getDeclaredType();

    int paramsSize = parameterArray.length;
    this.args = new Value[paramsSize];

    for (int i = 0; i < parameterArray.length; i++) {
      args[i] = parameterArray[i].getDefaultValue();
    }

    retType = f.getSignature().getReturnType();

  }

  @Override
  public Value call(Value arg0) {

    stack.push(stackEntry);

    args[0] = arg0.castTo(p0Type);
    Value retValue = f.callVariadic(userCallContext, args).castTo(retType);

    stack.pop();
    return retValue;
  }
}
