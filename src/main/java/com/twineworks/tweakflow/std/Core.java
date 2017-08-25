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

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.values.*;

public final class Core {

  // function inspect (x) -> string via {:class "com.twineworks.tweakflow.std.Core$inspect"}
  public static final class inspect implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      return Values.make(ValueInspector.inspect(x));
    }
  }

  // function present? (x) -> boolean via {:class "com.twineworks.tweakflow.std.Core$present"}
  public static final class present implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()){
        return Values.FALSE;
      }
      else {
        return Values.TRUE;
      }

    }
  }

  // function nil? (x) -> boolean via {:class "com.twineworks.tweakflow.std.Core$isNil"}
  public static final class isNil implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()){
        return Values.TRUE;
      }
      else {
        return Values.FALSE;
      }
    }
  }

  // function hash (x) -> long via {:class "com.twineworks.tweakflow.std.Core$hash"}
  public static final class hash implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      return Values.make(x.hashCode());
    }
  }

  // function eval (string x) -> any
  public static final class eval implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return TweakFlow.evaluate(x.string(), false);
    }
  }

}
