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
import com.twineworks.tweakflow.lang.values.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public final class Decimals {

  // ulp: (decimal x) -> decimal
  public static final class ulp implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;
      BigDecimal d = x.decimal();
      return Values.make(d.ulp());

    }
  }

  // scale: (decimal x) -> long
  public static final class scale implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;
      BigDecimal d = x.decimal();
      return Values.make(d.scale());

    }
  }

  // with_scale: (decimal x, long scale, rounding_mode='half_up') -> long
  public static final class with_scale implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value scale, Value roundingMode) {

      if (x.isNil()) return Values.NIL;
      if (scale.isNil()) throw new LangException(LangError.NIL_ERROR, "scale cannot be nil");
      if (roundingMode.isNil()) throw new LangException(LangError.NIL_ERROR, "rounding_mode cannot be nil");

      long longScale = scale.longNum();
      if (longScale < Integer.MIN_VALUE || longScale > Integer.MAX_VALUE) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "scale must be between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE);
      }

      int intScale = (int) longScale;

      RoundingMode rounding;
      try {
        rounding = RoundingMode.valueOf(roundingMode.string().toUpperCase(java.util.Locale.US));
      } catch (IllegalArgumentException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "unknown rounding_mode: " + roundingMode.string());
      }

      try {
        BigDecimal d = x.decimal();
        return Values.make(d.setScale(intScale, rounding));
      } catch (ArithmeticException e) {
        throw new LangException(LangError.ROUNDING_NECESSARY, e.getMessage());
      }

    }
  }

  // round: (decimal x, long digits, rounding_mode='half_up') -> long
  public static final class round implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value digits, Value roundingMode) {

      if (x.isNil()) return Values.NIL;
      if (digits.isNil()) throw new LangException(LangError.NIL_ERROR, "digits cannot be nil");
      if (roundingMode.isNil()) throw new LangException(LangError.NIL_ERROR, "rounding_mode cannot be nil");

      long longPrecision = digits.longNum();
      if (longPrecision < 0 || longPrecision > Integer.MAX_VALUE) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "digits must be between 0 and " + Integer.MAX_VALUE);
      }

      int intPrecision = (int) longPrecision;

      RoundingMode rounding;
      try {
        rounding = RoundingMode.valueOf(roundingMode.string().toUpperCase(java.util.Locale.US));
      } catch (IllegalArgumentException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "unknown rounding_mode: " + roundingMode.string());
      }

      try {
        BigDecimal d = x.decimal();
        return Values.make(d.round(new MathContext(intPrecision, rounding)));
      } catch (ArithmeticException e) {
        throw new LangException(LangError.ROUNDING_NECESSARY, e.getMessage());
      }

    }
  }

  // plain: (decimal x) -> string
  public static final class plain implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;
      BigDecimal d = x.decimal();
      return Values.make(d.toPlainString());

    }
  }

  // from_double_exact: (double x) -> decimal
  public static final class from_double_exact implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;
      double d = x.doubleNum();
      if (!Double.isFinite(d)){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot convert non-finite double: "+d+" to decimal");
      }
      BigDecimal dec = new BigDecimal(d);
      return Values.make(dec);

    }
  }

  // strip_trailing_zeros: (decimal x) -> string
  public static final class strip_trailing_zeros implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;
      BigDecimal d = x.decimal();
      return Values.make(d.stripTrailingZeros());

    }
  }

  // divide: (decimal x, decimal y, long scale, rounding_mode='half_up') -> long
  public static final class divide implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value y, Value scale, Value roundingMode) {

      if (x.isNil()) return Values.NIL;
      if (y.isNil()) return Values.NIL;
      if (roundingMode.isNil()) throw new LangException(LangError.NIL_ERROR, "rounding_mode cannot be nil");

      int intScale;
      if (scale.isNil()) {
        intScale = x.decimal().scale();
      } else {
        long longScale = scale.longNum();
        if (longScale < Integer.MIN_VALUE || longScale > Integer.MAX_VALUE) {
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "scale must be between " + Integer.MIN_VALUE + " and " + Integer.MAX_VALUE);
        }
        intScale = (int) longScale;
      }

      RoundingMode rounding;
      try {
        rounding = RoundingMode.valueOf(roundingMode.string().toUpperCase(java.util.Locale.US));
      } catch (IllegalArgumentException e) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "unknown rounding_mode: " + roundingMode.string());
      }

      BigDecimal d = x.decimal();
      BigDecimal r = y.decimal();
      if (r.compareTo(BigDecimal.ZERO) == 0) {
        throw new LangException(LangError.DIVISION_BY_ZERO, "division by zero");
      }

      try {
        return Values.make(d.divide(r, intScale, rounding));
      } catch (ArithmeticException e) {
        throw new LangException(LangError.ROUNDING_NECESSARY, e.getMessage());
      }


    }
  }

  // divide: (decimal x, decimal y) -> long
  public static final class divide_integral implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value y) {

      if (x.isNil()) return Values.NIL;
      if (y.isNil()) return Values.NIL;


      BigDecimal d = x.decimal();
      BigDecimal r = y.decimal();
      if (r.compareTo(BigDecimal.ZERO) == 0) {
        throw new LangException(LangError.DIVISION_BY_ZERO, "division by zero");
      }
      return Values.make(d.divideToIntegralValue(r));

    }
  }

}
