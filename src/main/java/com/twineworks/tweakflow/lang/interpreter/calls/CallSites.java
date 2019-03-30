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

package com.twineworks.tweakflow.lang.interpreter.calls;

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.*;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;

public class CallSites {

  private CallSites(){}

  public static Arity1CallSite createArity1CallSite(Value f, Node at, Stack stack, EvaluationContext evaluationContext, UserCallContext userCallContext) {
    if (!f.isFunction()){
      throw new LangException(LangError.CANNOT_CALL, "not a function", stack, stack.peek().getNode().getSourceInfo());
    }
    FunctionValue function = f.function();

    int paramCount = function.getSignature().getParameterList().size();
    // function must have at least one parameter
    if (paramCount < 1){
      throw new LangException(LangError.UNEXPECTED_ARGUMENT, "cannot call 0-arity function with 1 argument", stack, stack.peek().getNode().getSourceInfo());
    }

    if (function.isUser()){
      UserFunctionValue uf = (UserFunctionValue) function;
      switch (paramCount){
        case 1:
          return new Arity1CallSiteToArity1User(uf, at, stack, userCallContext);
        case 2:
          return new Arity1CallSiteToArity2User(uf, at, stack, userCallContext);
        case 3:
          return new Arity1CallSiteToArity3User(uf, at, stack, userCallContext);
        case 4:
          return new Arity1CallSiteToArity4User(uf, at, stack, userCallContext);
        default:
          return new Arity1CallSiteToArityNUser(uf, at, stack, userCallContext);
      }

    }
    else{
      return new Arity1CallSiteToStandard((StandardFunctionValue) function, at, stack, evaluationContext);
    }

  }

  public static Arity2CallSite createArity2CallSite(Value f, Node at, Stack stack, EvaluationContext evaluationContext, UserCallContext userCallContext) {
    if (!f.isFunction()){
      throw new LangException(LangError.CANNOT_CALL, "not a function", stack, stack.peek().getNode().getSourceInfo());
    }
    FunctionValue function = f.function();

    // function must have at least two parameters
    int paramCount = function.getSignature().getParameterList().size();
    if (paramCount < 2){
      throw new LangException(LangError.UNEXPECTED_ARGUMENT, "cannot call function with 2 arguments", stack, stack.peek().getNode().getSourceInfo());
    }

    if (function.isUser()){
      UserFunctionValue uf = (UserFunctionValue) function;
      switch (paramCount){
        case 2:
          return new Arity2CallSiteToArity2User(uf, at, stack, userCallContext);
        case 3:
          return new Arity2CallSiteToArity3User(uf, at, stack, userCallContext);
        case 4:
          return new Arity2CallSiteToArity4User(uf, at, stack, userCallContext);
        default:
          return new Arity2CallSiteToArityNUser(uf, at, stack, userCallContext);
      }
    }
    else{
      return new Arity2CallSiteToStandard((StandardFunctionValue) function, at, stack, evaluationContext);
    }


  }

  public static Arity3CallSite createArity3CallSite(Value f, Node at, Stack stack, EvaluationContext evaluationContext, UserCallContext userCallContext) {
    if (!f.isFunction()){
      throw new LangException(LangError.CANNOT_CALL, "not a function", stack, stack.peek().getNode().getSourceInfo());
    }
    FunctionValue function = f.function();

    // function must have at least 3 parameters
    int paramCount = function.getSignature().getParameterList().size();
    if (paramCount < 3){
      throw new LangException(LangError.UNEXPECTED_ARGUMENT, "cannot call function with 3 arguments", stack, stack.peek().getNode().getSourceInfo());
    }

    if (function.isUser()){
      UserFunctionValue uf = (UserFunctionValue) function;
      switch (paramCount){
        case 3:
          return new Arity3CallSiteToArity3User(uf, at, stack, userCallContext);
        case 4:
          return new Arity3CallSiteToArity4User(uf, at, stack, userCallContext);
        default:
          return new Arity3CallSiteToArityNUser(uf, at, stack, userCallContext);
      }

    }
    else{
      return new Arity3CallSiteToStandard((StandardFunctionValue) function, at, stack, evaluationContext);
    }
  }

}
