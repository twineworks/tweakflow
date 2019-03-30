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

import com.twineworks.tweakflow.lang.ast.args.Arguments;
import com.twineworks.tweakflow.lang.ast.expressions.CallNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.FunctionValue;
import com.twineworks.tweakflow.lang.values.StandardFunctionValue;
import com.twineworks.tweakflow.lang.values.UserFunctionValue;
import com.twineworks.tweakflow.lang.values.Value;

import static com.twineworks.tweakflow.lang.interpreter.Interpreter.evalArguments;

final public class CallOp implements ExpressionOp {

  private final CallNode node;
  private final ExpressionOp callableOp;

  public CallOp(CallNode node) {
    this.node = node;
    callableOp = node.getExpression().getOp();
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    Value callableValue = callableOp.eval(stack, context);

    if (callableValue.type() != Types.FUNCTION){
      throw new LangException(LangError.CANNOT_CALL, "Cannot call "+callableValue.toString()+". Not a function.", stack, node.getSourceInfo());
    }
    FunctionValue function = callableValue.function();
    Arguments arguments = node.getArguments();

    if (function.isStandard()){
      return evaluateStandard((StandardFunctionValue) function, arguments, stack, context);
    }
    else{
      return evaluateUser((UserFunctionValue) function, arguments, stack, context);
    }
  }

  private Value evaluateUser(UserFunctionValue userFunction, Arguments arguments, Stack stack, EvaluationContext context) {

    Value[] argValues = evalArguments(arguments, stack, context);
    if (arguments.allPositional()){
      return Interpreter.evaluateUserFunctionCall(userFunction, Interpreter.argumentsForPositionalUserCall(argValues, userFunction.getSignature()), node, stack, context);
    }
    else {
      return Interpreter.evaluateUserFunctionCall(userFunction, Interpreter.argumentsForUserCall(arguments, argValues, userFunction.getSignature()), node, stack, context);
    }

  }

  private Value evaluateStandard(StandardFunctionValue standardFunction, Arguments arguments, Stack stack, EvaluationContext context){
    return Interpreter.evaluateStandardFunctionCall(node, standardFunction, arguments, stack, context);
  }

  @Override
  public boolean isConstant() {

    // calls are never constant
    // they might turn out to not terminate
    // and that's impossible to detect reliably
    return false;

//    if (callableOp.isConstant()){
//      for (ArgumentNode argumentNode : node.getArguments().getList()) {
//        if (!argumentNode.getExpression().getOp().isConstant()) return false;
//      }
//      // function is constant, all args are constant, => result is constant
//      return true;
//    }
//    return false;
  }

  @Override
  public ExpressionOp specialize() {

    if (callableOp.isConstant()){
      try {
        Value callable = Interpreter.evaluateInEmptyScope(node.getExpression());
        if (node.getArguments().allPositional()){
          int argCount = node.getArguments().getList().size();
          switch (argCount){
            case 1: return new FixedFunArity1CallOp(node);
            case 2: return new FixedFunArity2CallOp(node);
            case 3: return new FixedFunArity3CallOp(node);
          }
        }
      }
      catch (LangException ignored){}
    }
    return new CallOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new CallOp(node);
  }


}
