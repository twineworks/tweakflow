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

package com.twineworks.tweakflow.interpreter.calls;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.values.*;
import com.twineworks.tweakflow.interpreter.EvaluationContext;
import com.twineworks.tweakflow.interpreter.Stack;
import com.twineworks.tweakflow.interpreter.StackEntry;
import com.twineworks.tweakflow.interpreter.memory.Cell;
import com.twineworks.tweakflow.interpreter.memory.LocalMemorySpace;
import com.twineworks.tweakflow.interpreter.memory.MemorySpaceType;
import com.twineworks.tweakflow.interpreter.ops.ExpressionOp;

public class Arity1CallSiteToStandard implements Arity1CallSite {

  private final StandardFunctionValue f;
  private final FunctionSignature signature;
  private final Stack stack;
  private final EvaluationContext context;
  private final ExpressionOp op;
  private final ConstShapeMap<Cell> argsFrame;

  private final ConstShapeMap.Accessor<Cell> p0a;
  private final FunctionParameter p0;
  private final Type p0Type;

  private final LocalMemorySpace argSpace;
  private final StackEntry stackEntry;

  @SuppressWarnings("unchecked")
  public Arity1CallSiteToStandard(StandardFunctionValue f, Node at, Stack stack, EvaluationContext context) {
    this.f = f;
    this.signature = f.getSignature();
    this.stack = stack;
    this.context = context;
    this.op = f.getBody().getOp();

    // fill args frame with default values
    this.argsFrame = new ConstShapeMap<>(signature.getParameterShapeMap());
    FunctionParameter[] parameterArray = signature.getParameterArray();
    int paramsSize = parameterArray.length;

    for (FunctionParameter param : parameterArray) {
      argsFrame.seta(param.getShapeAccessor(), new Cell().setValue(param.getDefaultValue()));
    }

    p0 = parameterArray[0];
    p0a = p0.getShapeAccessor();
    p0Type = p0.getDeclaredType();

    argSpace = new LocalMemorySpace(
        stack.peek().getSpace(),
        f.getBody().getScope(),
        MemorySpaceType.CALL_ARGUMENTS,
        argsFrame
    );

    stackEntry = new StackEntry(at, argSpace, f.getClosures());

  }


  @Override
  public Value call(Value arg0) {

    argsFrame.geta(p0a).setValue(arg0.castTo(p0Type));

    stack.push(stackEntry);
    Value retValue = op.eval(stack, context);
    stack.pop();
    return retValue;
  }
}
