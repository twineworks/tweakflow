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

    Value f = callableOp.eval(stack, context);
    if (f.type() != Types.FUNCTION) {
      throw new LangException(LangError.CANNOT_CURRY, "Cannot curry " + f.toString() + ". Not a function.", stack, node.getSourceInfo());
    }

    FunctionValue function = f.function();
    FunctionSignature signature = function.getSignature();

    ArrayList<FunctionParameter> newParams = new ArrayList<>();
    Map<String, FunctionParameter> fParams = signature.getParameterMap();

    // ensure every curried param is present in host
    for (String name : curriedArgs.keySet()) {
      if (!fParams.containsKey(name)) {
        throw new LangException(LangError.UNEXPECTED_ARGUMENT, "Cannot curry undeclared parameter " + name + ".", stack, node.getSourceInfo());
      }
    }

    Value[] fixedArgs = new Value[signature.getParameterList().size()];
    int[] argsMap = new int[signature.getParameterList().size() - curriedArgs.size()];
    Arrays.fill(argsMap, -1);

    // inherit any params from host that are not curried, in order
    List<FunctionParameter> parameterList = signature.getParameterList();
    int j = 0;
    for (int i = 0; i < parameterList.size(); i++) {
      FunctionParameter p = parameterList.get(i);

      if (curriedArgs.containsKey(p.getName())) {
        fixedArgs[i] = curriedArgs.get(p.getName()).eval(stack, context);
      } else {
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


    UserFunction curriedFunction = null;
    // f(x=a) -> g()
    if (fixedArgs.length == 1 && newParams.size() == 0) {
      curriedFunction = new curry_impl_1_total_0_fixed(f, fixedArgs[0]);
    } else if (fixedArgs.length == 2) {
      if (newParams.size() == 1) {
        // f(x=a,y) -> g(y)
        if (argsMap[0] == 1) {
          curriedFunction = new curry_impl_2_total_0_fixed(f, fixedArgs[0]);
        }
        // f(x,y=a) -> g(x)
        else if (argsMap[0] == 0) {
          curriedFunction = new curry_impl_2_total_1_fixed(f, fixedArgs[1]);
        }
      }
      if (newParams.size() == 0) {
        // f(x=a,y=a) -> g()
        curriedFunction = new curry_impl_2_total_0_1_fixed(f, fixedArgs[0], fixedArgs[1]);
      }
    } else if (fixedArgs.length == 3) {
      if (newParams.size() == 2) {
        // f(x=a,y,z) -> g(y,z)
        if (argsMap[0] == 1 && argsMap[1] == 2) {
          curriedFunction = new curry_impl_3_total_0_fixed(f, fixedArgs[0]);
        }
        // f(x,y=a,z) -> g(x,z)
        else if (argsMap[0] == 0 && argsMap[1] == 2) {
          curriedFunction = new curry_impl_3_total_1_fixed(f, fixedArgs[1]);
        }
        // f(x,y,z=a) -> g(x,y)
        else if (argsMap[0] == 0 && argsMap[1] == 1) {
          curriedFunction = new curry_impl_3_total_2_fixed(f, fixedArgs[2]);
        }
      } else if (newParams.size() == 1) {
        // f(x=a,y=a,z) -> g(z)
        if (argsMap[0] == 2) {
          curriedFunction = new curry_impl_3_total_0_1_fixed(f, fixedArgs[0], fixedArgs[1]);
        }
        // f(x=a,y,z=a) -> g(y)
        else if (argsMap[0] == 1) {
          curriedFunction = new curry_impl_3_total_0_2_fixed(f, fixedArgs[0], fixedArgs[2]);
        }
        // f(x,y=a,z=a) -> g(x)
        else if (argsMap[0] == 0) {
          curriedFunction = new curry_impl_3_total_1_2_fixed(f, fixedArgs[1], fixedArgs[2]);
        }
      }
      else if (newParams.size() == 0) {
        // f(x=a,y=a,z=a) -> g()
        curriedFunction = new curry_impl_3_total_0_1_2_fixed(f, fixedArgs[0], fixedArgs[1], fixedArgs[2]);
      }
    }


    if (curriedFunction == null) {
      curriedFunction = new curry_impl_generic(f, fixedArgs, argsMap);
    }

    return Values.make(new UserFunctionValue(curriedSignature, curriedFunction));

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


  private static final class curry_impl_1_total_0_fixed implements UserFunction, Arity0UserFunction {

    private final Value f;
    private final Value fixedArg;
    private Arity1CallSite cs;

    curry_impl_1_total_0_fixed(Value f, Value fixedArg) {
      this.f = f;
      this.fixedArg = fixedArg;
    }

    @Override
    public Value call(UserCallContext context) {
      if (cs == null) {
        cs = context.createArity1CallSite(f);
      }
      return cs.call(fixedArg);
    }

  }

  private static final class curry_impl_2_total_0_fixed implements UserFunction, Arity1UserFunction {

    private final Value f;
    private final Value fixedArg;
    private Arity2CallSite cs;

    curry_impl_2_total_0_fixed(Value f, Value fixedArg) {
      this.f = f;
      this.fixedArg = fixedArg;
    }

    @Override
    public Value call(UserCallContext context, Value arg0) {
      if (cs == null) {
        cs = context.createArity2CallSite(f);
      }
      return cs.call(fixedArg, arg0);
    }

  }

  private static final class curry_impl_2_total_1_fixed implements UserFunction, Arity1UserFunction {

    private final Value f;
    private final Value fixedArg;
    private Arity2CallSite cs;

    curry_impl_2_total_1_fixed(Value f, Value fixedArg) {
      this.f = f;
      this.fixedArg = fixedArg;
    }

    @Override
    public Value call(UserCallContext context, Value arg0) {
      if (cs == null) {
        cs = context.createArity2CallSite(f);
      }
      return cs.call(arg0, fixedArg);
    }

  }

  private static final class curry_impl_2_total_0_1_fixed implements UserFunction, Arity0UserFunction {

    private final Value f;
    private final Value fixedArg0;
    private final Value fixedArg1;
    private Arity2CallSite cs;

    curry_impl_2_total_0_1_fixed(Value f, Value fixedArg0, Value fixedArg1) {
      this.f = f;
      this.fixedArg0 = fixedArg0;
      this.fixedArg1 = fixedArg1;
    }

    @Override
    public Value call(UserCallContext context) {
      if (cs == null) {
        cs = context.createArity2CallSite(f);
      }
      return cs.call(fixedArg0, fixedArg1);
    }

  }

  private static final class curry_impl_3_total_0_fixed implements UserFunction, Arity2UserFunction {

    private final Value f;
    private final Value fixedArg0;
    private Arity3CallSite cs;

    curry_impl_3_total_0_fixed(Value f, Value fixedArg0) {
      this.f = f;
      this.fixedArg0 = fixedArg0;
    }

    @Override
    public Value call(UserCallContext context, Value arg0, Value arg1) {
      if (cs == null) {
        cs = context.createArity3CallSite(f);
      }
      return cs.call(fixedArg0, arg0, arg1);
    }

  }

  private static final class curry_impl_3_total_1_fixed implements UserFunction, Arity2UserFunction {

    private final Value f;
    private final Value fixedArg1;
    private Arity3CallSite cs;

    curry_impl_3_total_1_fixed(Value f, Value fixedArg1) {
      this.f = f;
      this.fixedArg1 = fixedArg1;
    }

    @Override
    public Value call(UserCallContext context, Value arg0, Value arg1) {
      if (cs == null) {
        cs = context.createArity3CallSite(f);
      }
      return cs.call(arg0, fixedArg1, arg1);
    }

  }

  private static final class curry_impl_3_total_2_fixed implements UserFunction, Arity2UserFunction {

    private final Value f;
    private final Value fixedArg2;
    private Arity3CallSite cs;

    curry_impl_3_total_2_fixed(Value f, Value fixedArg2) {
      this.f = f;
      this.fixedArg2 = fixedArg2;
    }

    @Override
    public Value call(UserCallContext context, Value arg0, Value arg1) {
      if (cs == null) {
        cs = context.createArity3CallSite(f);
      }
      return cs.call(arg0, arg1, fixedArg2);
    }

  }

  private static final class curry_impl_3_total_0_1_fixed implements UserFunction, Arity1UserFunction {

    private final Value f;
    private final Value fixedArg0;
    private final Value fixedArg1;
    private Arity3CallSite cs;

    curry_impl_3_total_0_1_fixed(Value f, Value fixedArg0, Value fixedArg1) {
      this.f = f;
      this.fixedArg0 = fixedArg0;
      this.fixedArg1 = fixedArg1;
    }

    @Override
    public Value call(UserCallContext context, Value arg0) {
      if (cs == null) {
        cs = context.createArity3CallSite(f);
      }
      return cs.call(fixedArg0, fixedArg1, arg0);
    }

  }

  private static final class curry_impl_3_total_1_2_fixed implements UserFunction, Arity1UserFunction {

    private final Value f;
    private final Value fixedArg1;
    private final Value fixedArg2;
    private Arity3CallSite cs;

    curry_impl_3_total_1_2_fixed(Value f, Value fixedArg1, Value fixedArg2) {
      this.f = f;
      this.fixedArg1 = fixedArg1;
      this.fixedArg2 = fixedArg2;
    }

    @Override
    public Value call(UserCallContext context, Value arg0) {
      if (cs == null) {
        cs = context.createArity3CallSite(f);
      }
      return cs.call(arg0, fixedArg1, fixedArg2);
    }

  }

  private static final class curry_impl_3_total_0_2_fixed implements UserFunction, Arity1UserFunction {

    private final Value f;
    private final Value fixedArg0;
    private final Value fixedArg2;
    private Arity3CallSite cs;

    curry_impl_3_total_0_2_fixed(Value f, Value fixedArg0, Value fixedArg2) {
      this.f = f;
      this.fixedArg0 = fixedArg0;
      this.fixedArg2 = fixedArg2;
    }

    @Override
    public Value call(UserCallContext context, Value arg0) {
      if (cs == null) {
        cs = context.createArity3CallSite(f);
      }
      return cs.call(fixedArg0, arg0, fixedArg2);
    }

  }

  private static final class curry_impl_3_total_0_1_2_fixed implements UserFunction, Arity0UserFunction {

    private final Value f;
    private final Value fixedArg0;
    private final Value fixedArg1;
    private final Value fixedArg2;
    private Arity3CallSite cs;

    curry_impl_3_total_0_1_2_fixed(Value f, Value fixedArg0, Value fixedArg1, Value fixedArg2) {
      this.f = f;
      this.fixedArg0 = fixedArg0;
      this.fixedArg1 = fixedArg1;
      this.fixedArg2 = fixedArg2;
    }

    @Override
    public Value call(UserCallContext context) {
      if (cs == null) {
        cs = context.createArity3CallSite(f);
      }
      return cs.call(fixedArg0, fixedArg1, fixedArg2);
    }

  }

  private static final class curry_impl_generic implements UserFunction, Arity0UserFunction, Arity1UserFunction, Arity2UserFunction, Arity3UserFunction, Arity4UserFunction, ArityNUserFunction {

    private final Value f;
    private final ThreadLocal<Value[]> fixedArgs;
    private final int[] argsMap;

    curry_impl_generic(Value f, Value[] fixedArgs, int[] argsMap) {
      this.f = f;
      this.argsMap = argsMap;
      Value[] args = new Value[fixedArgs.length];
      System.arraycopy(fixedArgs, 0, args, 0, fixedArgs.length);
      this.fixedArgs = ThreadLocal.withInitial(() -> args);

    }


    @Override
    public Value call(UserCallContext context) {
      Value[] args = fixedArgs.get();
      return context.call(f, args);
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
      Value  ret = context.call(f, args);
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
    public Value call(UserCallContext context, Value arg0, Value arg1, Value arg2, Value arg3) {
      Value[] args = fixedArgs.get();
      int idx0 = argsMap[0];
      int idx1 = argsMap[1];
      int idx2 = argsMap[2];
      int idx3 = argsMap[3];
      args[idx0] = arg0;
      args[idx1] = arg1;
      args[idx2] = arg2;
      args[idx3] = arg3;
      Value ret = context.call(f, args);
      args[idx0] = null;
      args[idx1] = null;
      args[idx2] = null;
      args[idx3] = null;
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
