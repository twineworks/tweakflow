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

import com.twineworks.tweakflow.lang.ast.curry.CurryArgumentNode;
import com.twineworks.tweakflow.lang.ast.expressions.CurryNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

import java.util.*;

final public class CurryOp implements ExpressionOp {

  private final CurryNode node;
  private final ExpressionOp callableOp;
  private final HashMap<String, ExpressionOp> curriedArgs;

  public CurryOp(CurryNode node) {
    this.node = node;
    callableOp = node.getExpression().getOp();
    curriedArgs = new HashMap<>();
    for (CurryArgumentNode arg : node.getArguments().getList()) {
      curriedArgs.put(arg.getName(), arg.getExpression().getOp());
    }
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    Value callableValue = callableOp.eval(stack, context);
    if (callableValue.type() != Types.FUNCTION) {
      throw new LangException(LangError.CANNOT_CURRY, "Cannot curry " + callableValue.toString() + ". Not a function.", stack, node.getSourceInfo());
    }

    FunctionValue function = callableValue.function();
    FunctionSignature signature = function.getSignature();

    ArrayList<FunctionParameter> newParams = new ArrayList<>();
    Map<String, FunctionParameter> fParams = signature.getParameterMap();

    // ensure every curried param is present in host
    for (String name : curriedArgs.keySet()) {
      if (!fParams.containsKey(name)){
        throw new LangException(LangError.UNEXPECTED_ARGUMENT, "Cannot curry undeclared parameter "+name+".", stack, node.getSourceInfo());
      }
    }

    Value[] fixedArgs = new Value[signature.getParameterList().size()];
    int[] argsMap = new int[signature.getParameterList().size()-curriedArgs.size()];

    // inherit any params from host that are not curried, in order
    List<FunctionParameter> parameterList = signature.getParameterList();
    int j = 0;
    for (int i = 0; i < parameterList.size(); i++) {
      FunctionParameter p = parameterList.get(i);

      if (curriedArgs.containsKey(p.getName())) {
        fixedArgs[i] = curriedArgs.get(p.getName()).eval(stack, context);
      }
      else{
        newParams.add(new FunctionParameter(j, p.getName(), p.getDeclaredType(), p.getDefaultValue()));
        fixedArgs[i] = p.getDefaultValue();
        argsMap[j] = i;
        j++;
      }
    }

    FunctionSignature curriedSignature = new FunctionSignature(
        newParams,
        signature.getReturnType()
    );

    UserFunctionValue curried = new UserFunctionValue(curriedSignature, new curry_impl(callableValue, fixedArgs, argsMap));

    return Values.make(curried);

  }

  @Override
  public boolean isConstant() {
    if (!callableOp.isConstant()) return false;
    for (ExpressionOp value : curriedArgs.values()) {
      if (!value.isConstant()) return false;
    }
    return true;
  }

  @Override
  public ExpressionOp specialize() {
    return new CurryOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new CurryOp(node);
  }

  private static final class curry_impl implements UserFunction, Arity0UserFunction, Arity1UserFunction, Arity2UserFunction, Arity3UserFunction, ArityNUserFunction {

    private final Value f;
    private final ThreadLocal<Value[]> fixedArgs;
    private final int[] argsMap;

    curry_impl(Value f, Value[] fixedArgs, int[] argsMap) {
      this.f = f;
      this.argsMap = argsMap;
      Value[] args = new Value[fixedArgs.length];
      System.arraycopy(fixedArgs, 0, args, 0, fixedArgs.length);
      this.fixedArgs = ThreadLocal.withInitial(() -> args);
    }

    @Override
    public Value call(UserCallContext context) {
      return context.call(f, fixedArgs.get());
    }

    @Override
    public Value call(UserCallContext context, Value arg0) {
      Value[] args = fixedArgs.get();
      int idx = argsMap[0];
      args[idx] = arg0;
      Value ret = context.call(f, args);
      args[idx] = null;
      return ret;
    }

    @Override
    public Value call(UserCallContext context, Value arg0, Value arg1) {
      Value[] args = fixedArgs.get();
      int idx0 = argsMap[0];
      int idx1 = argsMap[1];
      args[idx0] = arg0;
      args[idx1] = arg1;
      Value ret = context.call(f, args);
      args[idx0] = null;
      args[idx1] = null;
      return ret;
    }

    @Override
    public Value call(UserCallContext context, Value arg0, Value arg1, Value arg2) {
      Value[] args = fixedArgs.get();
      int idx0 = argsMap[0];
      int idx1 = argsMap[1];
      int idx2 = argsMap[2];
      args[idx0] = arg0;
      args[idx1] = arg1;
      args[idx2] = arg2;
      Value ret = context.call(f, args);
      args[idx0] = null;
      args[idx1] = null;
      args[idx2] = null;
      return ret;
    }

    @Override
    public Value callVariadic(UserCallContext context, Value... cArgs) {

      Value[] args = fixedArgs.get();
      for (int i = 0; i < cArgs.length; i++) {
        args[argsMap[i]] = cArgs[i];
      }
      Value ret = context.call(f, args);
      for (int i = 0; i < cArgs.length; i++) {
        args[argsMap[i]] = null;
      }
      return ret;
    }
  }


}
