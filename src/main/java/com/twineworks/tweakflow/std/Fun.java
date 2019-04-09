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

package com.twineworks.tweakflow.std;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

public final class Fun {

  // function times: (long n, any x, function f) ->  any
  public static final class times implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value n, Value x, Value f) {

      if (f == Values.NIL) throw new LangException(LangError.NIL_ERROR, "f cannot be nil");

      int paramCount = f.function().getSignature().getParameterList().size();
      if (paramCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "f must accept at least one argument");

      if (n == Values.NIL) return Values.NIL;

      long t = n.longNum();
      if (t < 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "n must not be negative, found: "+t);

      Arity1CallSite fcs = context.createArity1CallSite(f);

      while(t > 0){
        x = fcs.call(x);
        t--;
      }

      return x;
    }
  }

  // function until: (function p, x, function f) ->                via {:class "com.twineworks.tweakflow.std.Fun$until"}
  public static final class until implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value p, Value x, Value f) {

      if (p == Values.NIL) throw new LangException(LangError.NIL_ERROR, "p cannot be nil");
      if (f == Values.NIL) throw new LangException(LangError.NIL_ERROR, "f cannot be nil");

      int fParamCount = f.function().getSignature().getParameterList().size();
      if (fParamCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "f must accept at least one argument");

      int pParamCount = p.function().getSignature().getParameterList().size();
      if (pParamCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "p must accept at least one argument");

      Arity1CallSite fcs = context.createArity1CallSite(f);
      Arity1CallSite pcs = context.createArity1CallSite(p);

      // keep checking predicate and call function until it is true
      while(pcs.call(x).castTo(Types.BOOLEAN) != Values.TRUE){
        x = fcs.call(x);
      }

      return x;
    }
  }

  //   function while: (function p, x, function f) ->                via {:class "com.twineworks.tweakflow.std.Fun$doWhile"}
  public static final class doWhile implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value p, Value x, Value f) {

      if (p == Values.NIL) throw new LangException(LangError.NIL_ERROR, "p cannot be nil");
      if (f == Values.NIL) throw new LangException(LangError.NIL_ERROR, "f cannot be nil");

      int fParamCount = f.function().getSignature().getParameterList().size();
      if (fParamCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "f must accept at least one argument");

      int pParamCount = p.function().getSignature().getParameterList().size();
      if (pParamCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "p must accept at least one argument");

      Arity1CallSite fcs = context.createArity1CallSite(f);
      Arity1CallSite pcs = context.createArity1CallSite(p);

      // keep checking predicate and call function while it is true
      while(pcs.call(x).castTo(Types.BOOLEAN) == Values.TRUE){
        x = fcs.call(x);
      }

      return x;

    }
  }

  // function iterate: (long start, long end, x, function f) ->      via {:class "com.twineworks.tweakflow.std.Fun$iterate"}
  public static final class iterate implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value start, Value end, Value x, Value f) {

      if (f == Values.NIL) throw new LangException(LangError.NIL_ERROR, "f cannot be nil");

      int fParamCount = f.function().getSignature().getParameterList().size();
      if (fParamCount < 2) throw new LangException(LangError.ILLEGAL_ARGUMENT, "f must accept at least two arguments");

      Long startLong = start.longNum();
      Long endLong = end.longNum();

      if (startLong == null || endLong == null) return Values.NIL;

      if (endLong - startLong < 0) return x;

      Arity2CallSite fcs = context.createArity2CallSite(f);

      long i = startLong;
      while(i <= endLong){
        x = fcs.call(x, Values.make(i));
        i++;
      }

      return x;
    }
  }

  // function signature: (function f) -> dict
  public static final class signature implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value f) {

      if (f == Values.NIL) return Values.NIL;
      FunctionValue function = f.function();
      return function.getSignature().toValue();

    }
  }


}
