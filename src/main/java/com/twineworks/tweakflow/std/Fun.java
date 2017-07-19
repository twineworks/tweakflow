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

package com.twineworks.tweakflow.std;

import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

public final class Fun {

  // function times: (long times, x, function f) ->                via {:class "com.twineworks.tweakflow.std.Fun$times"}
  public static final class times implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value times, Value x, Value f) {

      if (f == Values.NIL) return Values.NIL;

      if (times == Values.NIL) return Values.NIL;
      long t = times.longNum();
      if (t < 0) return Values.NIL;

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

      if (f == Values.NIL) return Values.NIL;
      if (p == Values.NIL) return Values.NIL;

      Arity1CallSite fcs = context.createArity1CallSite(f);
      Arity1CallSite pcs = context.createArity1CallSite(p);

      // keep checking predicate and call function until it is true
      while(!pcs.call(x).castTo(Types.BOOLEAN).bool()){
        x = fcs.call(x);
      }

      return x;
    }
  }

  //   function while: (function p, x, function f) ->                via {:class "com.twineworks.tweakflow.std.Fun$doWhile"}
  public static final class doWhile implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value p, Value x, Value f) {
      if (f.isNil()) return Values.NIL;
      if (p.isNil()) return Values.NIL;

      Arity1CallSite fcs = context.createArity1CallSite(f);
      Arity1CallSite pcs = context.createArity1CallSite(p);

      // keep checking predicate and call function while it is true
      while(pcs.call(x).castTo(Types.BOOLEAN).bool()){
        x = fcs.call(x);
      }

      return x;

    }
  }

  // function iterate: (long from, long to, x, function f) ->      via {:class "com.twineworks.tweakflow.std.Fun$iterate"}
  public static final class iterate implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value from, Value to, Value x, Value f) {

      if (f == Values.NIL) return Values.NIL;

      Long fromLong = from.longNum();
      Long toLong = to.longNum();

      if (fromLong == null || toLong == null) return Values.NIL;

      if (toLong - fromLong < 0) return x;

      Arity2CallSite fcs = context.createArity2CallSite(f);

      long i = fromLong;
      while(i <= toLong){
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
