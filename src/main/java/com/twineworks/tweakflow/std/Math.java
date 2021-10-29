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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Random;

public final class Math {

  // abs: (x)
  public static final class abs implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;

      if (x.isLongNum()){
        Long v = x.longNum();
        if (v < 0){
          if (v.equals(Long.MIN_VALUE)) throw new LangException(LangError.NUMBER_OUT_OF_BOUNDS, "cannot represent magnitude as long");
          return Values.make(-v);
        }
        else {
          return x;
        }
      }

      if (x.isDoubleNum()){
        Double v = x.doubleNum();
        if (v < 0.0){
          return Values.make(-v);
        }
        else {
          return x;
        }
      }

      if (x.isDecimal()){
        BigDecimal d = x.decimal();
        if (d.compareTo(BigDecimal.ZERO) < 0){
          return Values.make(d.negate());
        }
        else{
          return x;
        }
      }

      throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot determine magnitude of type "+x.type().name());

    }
  }

  // compare: (a, b) -> long
  public static final class compare implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value a, Value b) {

      if (a == b) return Values.LONG_ZERO; // short circuit for identity

      if (!a.isDoubleNum() && !a.isLongNum() && !a.isDecimal() && !(a == Values.NIL)) throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare non-numeric: "+ValueInspector.inspect(a));
      if (!b.isDoubleNum() && !b.isLongNum() && !b.isDecimal() && !(b == Values.NIL)) throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare non-numeric: "+ValueInspector.inspect(b));

      // nil case
      // a == nil && b != nil
      if (a == Values.NIL){
        return Values.LONG_NEG_ONE;
      }
      // a != nil && b == nil
      if (b == Values.NIL){
        return Values.LONG_ONE;
      }

      // a == NaN && b != NaN
      if (a == Values.NAN){
        return Values.LONG_NEG_ONE;
      }
      // a != NaN && b == NaN
      if (b == Values.NAN){
        return Values.LONG_ONE;
      }

      if (a.isDoubleNum()){

        // both double case
        if (b.isDoubleNum()){
          double ad = a.doubleNum();
          double bd = b.doubleNum();
          if (ad < bd) return Values.LONG_NEG_ONE;
          if (ad > bd) return Values.LONG_ONE;
          return Values.LONG_ZERO;
        }
        else if (b.isLongNum()){
          // b long
          double ad = a.doubleNum();
          long bl = b.longNum();
          if (ad < bl) return Values.LONG_NEG_ONE;
          if (ad > bl) return Values.LONG_ONE;
          return Values.LONG_ZERO;
        }
        else { // b is decimal
          double d = a.doubleNum();
          if (Double.isFinite(d)){
            // b decimal
            BigDecimal ad = BigDecimal.valueOf(d);
            BigDecimal bd = b.decimal();
            int cmp = ad.compareTo(bd);
            if (cmp < 0) return Values.LONG_NEG_ONE;
            if (cmp > 0) return Values.LONG_ONE;
            return Values.LONG_ZERO;
          }
          else{
            // only +-Infinity cases to handle
            if (d < 0) return Values.LONG_NEG_ONE;
            return Values.LONG_ONE;
          }
        }
      }
      else if(a.isLongNum()){
        // both long case
        if (b.isLongNum()){
          long al = a.longNum();
          long bl = b.longNum();
          if (al == bl) return Values.LONG_ZERO;
          if (al < bl) return Values.LONG_NEG_ONE;
          return Values.LONG_ONE;
        }
        else if (b.isDoubleNum()){
          // a long, b double
          long al = a.longNum();
          double bd = b.doubleNum();
          if (al < bd) return Values.LONG_NEG_ONE;
          if (al > bd) return Values.LONG_ONE;
          return Values.LONG_ZERO;
        }
        else {
          // a long, b decimal
          BigDecimal ad = BigDecimal.valueOf(a.longNum());
          BigDecimal bd = b.decimal();
          int cmp = ad.compareTo(bd);
          if (cmp < 0) return Values.LONG_NEG_ONE;
          if (cmp > 0) return Values.LONG_ONE;
          return Values.LONG_ZERO;
        }
      }
      else { /* a decimal */
        if (b.isLongNum()){
          BigDecimal ad = a.decimal();
          BigDecimal bd = BigDecimal.valueOf(b.longNum());

          int cmp = ad.compareTo(bd);
          if (cmp < 0) return Values.LONG_NEG_ONE;
          if (cmp > 0) return Values.LONG_ONE;
          return Values.LONG_ZERO;
        }
        else if (b.isDoubleNum()){
          double d = b.doubleNum();
          if (Double.isFinite(d)){
            BigDecimal ad = a.decimal();
            BigDecimal bd = BigDecimal.valueOf(d);
            int cmp = ad.compareTo(bd);
            if (cmp < 0) return Values.LONG_NEG_ONE;
            if (cmp > 0) return Values.LONG_ONE;
            return Values.LONG_ZERO;
          }
          else{
            // only +-Infinity cases to handle
            if (d < 0) return Values.LONG_ONE;
            return Values.LONG_NEG_ONE;
          }
        }
        else {
          /* b is decimal */
          BigDecimal ad = a.decimal();
          BigDecimal bd = b.decimal();
          int cmp = ad.compareTo(bd);
          if (cmp < 0) return Values.LONG_NEG_ONE;
          if (cmp > 0) return Values.LONG_ONE;
          return Values.LONG_ZERO;
        }
      }

    }
  }

  // inc: (x)
  public static final class inc implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      if (x.isLongNum()){
        Long v = x.longNum();
        return Values.make(v+1L);
      }

      if (x.isDoubleNum()){
        Double v = x.doubleNum();
        return Values.make(v+1.0);
      }

      if (x.isDecimal()){
        BigDecimal v = x.decimal();
        return Values.make(v.add(BigDecimal.ONE));
      }

      throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot increment type "+x.type().name());

    }
  }

  // dec: (x)
  public static final class dec implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x.isNil()) return Values.NIL;

      if (x.isLongNum()){
        Long v = x.longNum();
        return Values.make(v-1L);
      }

      if (x.isDoubleNum()){
        Double v = x.doubleNum();
        return Values.make(v-1.0);
      }

      if (x.isDecimal()){
        BigDecimal v = x.decimal();
        return Values.make(v.subtract(BigDecimal.ONE));
      }

      throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot decrement type "+x.type().name());

    }
  }

  // rand: (seed) -> double
  public static final class rand implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value seed) {
      return Values.make(new Random(seed.hashCode()).nextDouble());
    }
  }

  // rands: (long count, seed) -> list
  public static final class rands implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value count, Value seed) {
      if (count == Values.NIL) return Values.NIL;
      long longCount = count.longNum();
      if (longCount < 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "count cannot be negative, found: "+longCount);
      if (longCount == 0) return Values.EMPTY_LIST;

      Random rnd = new Random(seed.hashCode());
      ListValue ret = new ListValue();
      for (long i=0;i<longCount;i++){
        ret = ret.append(Values.make(rnd.nextDouble()));
      }

      return Values.make(ret);
    }
  }

  // min: (list xs) ->
  public static final class min implements UserFunction, Arity1UserFunction {

    private int compareTo(double a, BigDecimal b){
      if (Double.isFinite(a)){
        return BigDecimal.valueOf(a).compareTo(b);
      }
      if (a > 0) return 1;
      return -1;
    }

    @Override
    public Value call(UserCallContext context, Value xs) {

      if (xs.isNil()) return Values.NIL;

      ListValue list = xs.list();
      if (list.isEmpty()) return Values.NIL;

      // only one element?
      if (list.size() == 1) {
        Value v = list.get(0);
        if (v.isNil() || v.isLongNum() || v.isDecimal()){
          return v;
        }
        else if (v.isDoubleNum()){
          if (v.doubleNum().isNaN()) return Values.NIL;
          return v;
        }
        else{
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+v.type().name());
        }
      }

      byte MODE_LONG=0;
      byte MODE_DOUBLE=1;
      byte MODE_DECIMAL=2;

      byte mode;

      long longMin = 0L;
      double doubleMin = 0.0d;
      BigDecimal decimalMin = BigDecimal.ZERO;

      // first value
      Value min = list.get(0);
      if (min.isLongNum()){
        mode = MODE_LONG;
        longMin = min.longNum();
      }
      else if (min.isDoubleNum()) {
        doubleMin = min.doubleNum();
        if (Double.isNaN(doubleMin)){
          return Values.NIL;
        }
        mode = MODE_DOUBLE;
      }
      else if (min.isDecimal()){
        mode = MODE_DECIMAL;
        decimalMin = min.decimal();
      }
      else if (min.isNil()){
        return Values.NIL;
      }
      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+min.type().name());
      }

      for (Value v : list) {
        if (v.isNil()) return Values.NIL;
        if (mode == MODE_LONG){
          // long mode
          if (v.isLongNum()){
            long num = v.longNum();
            if (num < longMin){
              longMin = num;
              min = v;
            }
          }
          else if (v.isDoubleNum()){
            doubleMin = longMin;
            double num = v.doubleNum();
            if (Double.isNaN(num)) return Values.NIL;
            if (num < doubleMin){
              doubleMin = num;
              min = v;
            }
            mode = MODE_DOUBLE;
          }
          else if (v.isDecimal()){
            decimalMin = BigDecimal.valueOf(longMin);
            BigDecimal num = v.decimal();
            if (num.compareTo(decimalMin) < 0){
              decimalMin = num;
              min = v;
            }
            mode = MODE_DECIMAL;
          }
          else {
            throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+v.type().name());
          }
        }
        else if (mode == MODE_DOUBLE){
          // double mode
          if (v.isLongNum()){
            long num = v.longNum();
            if (num < doubleMin){
              doubleMin = num;
              min = v;
            }
          }
          else if (v.isDoubleNum()){
            double num = v.doubleNum();
            if (Double.isNaN(num)) return Values.NIL;
            if (num < doubleMin){
              doubleMin = num;
              min = v;
            }
          }
          else if (v.isDecimal()){
            BigDecimal num = v.decimal();
            if (compareTo(doubleMin, num) > 0){
              decimalMin = num;
              min = v;
              mode = MODE_DECIMAL;
            }
          }
          else {
            throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+v.type().name());
          }
        }
        else {
          // decimal mode
          if (v.isLongNum()){
            BigDecimal num = BigDecimal.valueOf(v.longNum());
            if (num.compareTo(decimalMin) < 0){
              decimalMin = num;
              min = v;
            }
          }
          else if (v.isDoubleNum()){
            double d = v.doubleNum();
            if (Double.isFinite(d)){
              BigDecimal num = BigDecimal.valueOf(d);
              if (num.compareTo(decimalMin) < 0){
                decimalMin = num;
                min = v;
              }
            }
            else {
              if (Double.isNaN(d)) return Values.NIL;
              if (d < 0){ // d = -Infinity
                doubleMin = d;
                min = v;
                mode = MODE_DOUBLE;
              }
            }

          } else if (v.isDecimal()) {
            BigDecimal num = v.decimal();
            if (num.compareTo(decimalMin) < 0){
              decimalMin = num;
              min = v;
            }
          }
          else {
            throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+v.type().name());
          }
        }

      }

      return min;
    }
  }

  // max: (list xs) ->
  public static final class max implements UserFunction, Arity1UserFunction {

    private int compareTo(double a, BigDecimal b){
      if (Double.isFinite(a)){
        return BigDecimal.valueOf(a).compareTo(b);
      }
      if (a > 0) return 1;
      return -1;
    }

    @Override
    public Value call(UserCallContext context, Value xs) {
      if (xs.isNil()) return Values.NIL;

      ListValue list = xs.list();
      if (list.isEmpty()) return Values.NIL;

      // only one element?
      if (list.size() == 1) {
        Value v = list.get(0);
        if (v.isNil() || v.isLongNum() || v.isDecimal()){
          return v;
        }
        else if (v.isDoubleNum()){
          if (v.doubleNum().isNaN()) return Values.NIL;
          return v;
        }
        else{
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+v.type().name());
        }
      }

      byte MODE_LONG=0;
      byte MODE_DOUBLE=1;
      byte MODE_DECIMAL=2;

      byte mode;

      long longMax = 0L;
      double doubleMax = 0.0d;
      BigDecimal decimalMax = BigDecimal.ZERO;

      // first value
      Value max = list.get(0);
      if (max.isLongNum()){
        mode = MODE_LONG;
        longMax = max.longNum();
      }
      else if (max.isDoubleNum()) {
        doubleMax = max.doubleNum();
        if (Double.isNaN(doubleMax)){
          return Values.NIL;
        }
        mode = MODE_DOUBLE;
      }
      else if (max.isDecimal()){
        mode = MODE_DECIMAL;
        decimalMax = max.decimal();
      }
      else if (max.isNil()){
        return Values.NIL;
      }
      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+max.type().name());
      }

      for (Value v : list) {
        if (v.isNil()) return Values.NIL;
        if (mode == MODE_LONG){
          // long mode
          if (v.isLongNum()){
            long num = v.longNum();
            if (num > longMax){
              longMax = num;
              max = v;
            }
          }
          else if (v.isDoubleNum()){
            doubleMax = longMax;
            double num = v.doubleNum();
            if (Double.isNaN(num)) return Values.NIL;
            if (num > doubleMax){
              doubleMax = num;
              max = v;
            }
            mode = MODE_DOUBLE;
          }
          else if (v.isDecimal()){
            decimalMax = BigDecimal.valueOf(longMax);
            BigDecimal num = v.decimal();
            if (num.compareTo(decimalMax) > 0){
              decimalMax = num;
              max = v;
            }
            mode = MODE_DECIMAL;
          }
          else {
            throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+v.type().name());
          }
        }
        else if (mode == MODE_DOUBLE){
          // double mode
          if (v.isLongNum()){
            long num = v.longNum();
            if (num > doubleMax){
              doubleMax = num;
              max = v;
            }
          }
          else if (v.isDoubleNum()){
            double num = v.doubleNum();
            if (Double.isNaN(num)) return Values.NIL;
            if (num > doubleMax){
              doubleMax = num;
              max = v;
            }
          }
          else if (v.isDecimal()){
            BigDecimal num = v.decimal();
            if (compareTo(doubleMax, num) < 0){
              decimalMax = num;
              max = v;
              mode = MODE_DECIMAL;
            }
          }
          else {
            throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+v.type().name());
          }
        }
        else {
          // decimal mode
          if (v.isLongNum()){
            BigDecimal num = BigDecimal.valueOf(v.longNum());
            if (num.compareTo(decimalMax) > 0){
              decimalMax = num;
              max = v;
            }
          }
          else if (v.isDoubleNum()){
            double d = v.doubleNum();
            if (Double.isFinite(d)){
              BigDecimal num = BigDecimal.valueOf(d);
              if (num.compareTo(decimalMax) > 0){
                decimalMax = num;
                max = v;
              }
            }
            else {
              if (Double.isNaN(d)) return Values.NIL;
              if (d > 0){ // d = Infinity
                doubleMax = d;
                max = v;
                mode = MODE_DOUBLE;
              }
            }

          } else if (v.isDecimal()) {
            BigDecimal num = v.decimal();
            if (num.compareTo(decimalMax) > 0){
              decimalMax = num;
              max = v;
            }
          }
          else {
            throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot compare type "+v.type().name());
          }
        }

      }

      return max;
    }
  }

  // round: (x) ->
  public static final class round implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x == Values.NIL) return Values.NIL;
      return Values.make(java.lang.Math.round(x.doubleNum()));

    }
  }

  // ceil: (x) ->
  public static final class ceil implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.ceil(x.doubleNum())
      );
    }
  }

  // floor: (x) ->
  public static final class floor implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.floor(x.doubleNum())
      );
    }
  }

  // nan?: (any x) -> boolean
  public static final class nan implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.FALSE;
      if (!x.isDoubleNum()) return Values.FALSE;
      return Double.isNaN(x.doubleNum()) ? Values.TRUE : Values.FALSE;
    }
  }

  // finite?: (any x) -> boolean
  public static final class finite implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.FALSE;
      if (!x.isNumeric()) throw new LangException(LangError.ILLEGAL_ARGUMENT, "x must be numeric, found: "+x);
      if (!x.isDoubleNum()) return Values.TRUE;
      return Double.isFinite(x.doubleNum()) ? Values.TRUE : Values.FALSE;
    }
  }

  // sqrt: (x) ->
  public static final class sqrt implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.sqrt(x.doubleNum())
      );
    }
  }

  // bit_count: (long x) ->
  public static final class bitCount implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      return Values.make(Long.bitCount(x.longNum()));
    }
  }

  // factorial: (long n) -> decimal
  public static final class factorial implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value n) {
      if (n == Values.NIL) return Values.NIL;
      long x = n.longNum();
      if (x < 0){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot calculate factorial of negative value: "+x);
      }
      BigDecimal acc = BigDecimal.ONE;
      for (long i=2;i<=x;i++){
        acc = acc.multiply(BigDecimal.valueOf(i));
      }

      return Values.make(acc);
    }
  }

  // sin: (double x) -> double
  public static final class sin implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.sin(x.doubleNum())
      );
    }
  }

  // cos: (double x) -> double
  public static final class cos implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.cos(x.doubleNum())
      );
    }
  }

  // tan: (double x) -> double
  public static final class tan implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.tan(x.doubleNum())
      );
    }
  }

  // asin: (double x) -> double
  public static final class asin implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.asin(x.doubleNum())
      );
    }
  }

  // acos: (double x) -> double
  public static final class acos implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.acos(x.doubleNum())
      );
    }
  }

  // atan: (double x) -> double
  public static final class atan implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.atan(x.doubleNum())
      );
    }
  }

  // log: (double x) -> double
  public static final class log implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.log(x.doubleNum())
      );
    }
  }

  // log10: (double x) -> double
  public static final class log10 implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;

      return Values.make(
          java.lang.Math.log10(x.doubleNum())
      );
    }
  }

  public static final class formatter_impl implements UserFunction, Arity1UserFunction {

    private final DecimalFormat formatter;

    formatter_impl(DecimalFormat formatter) {
      this.formatter = formatter;
    }

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x == Values.NIL) return Values.NIL;

      String out;
      if (x.isDoubleNum()){
        out = formatter.format(x.doubleNum());
      }
      else if (x.isLongNum()){
        out = formatter.format(x.longNum());
      }
      else if (x.isDecimal()){
        out = formatter.format(x.decimal());
      }
      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot format value of type: "+x.type().name());
      }
      return Values.make(out);
    }
  }


  // (string pattern="0.##", dict decimal_symbols=nil, string rounding_mode="up", boolean always_show_decimal_separator=false) -> function
  // https://docs.oracle.com/javase/8/docs/api/java/math/RoundingMode.html
  public static final class formatter implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value pattern, Value decimalSymbols, Value roundingMode, Value alwaysShowDecimalSeparator) {

      // pattern
      if (pattern == Values.NIL){
        throw new LangException(LangError.NIL_ERROR, "pattern cannot be nil");
      }

      // symbols
      DecimalFormatSymbols symbols;
      if (decimalSymbols == Values.NIL){
        symbols = DecimalFormatSymbols.getInstance(java.util.Locale.US);
      }
      else{
        symbols = Locale.decimalSymbolsFromDict(decimalSymbols.dict());
      }

      RoundingMode rounding;
      // rounding mode
      if (roundingMode == Values.NIL){
        throw new LangException(LangError.NIL_ERROR, "rounding_mode cannot be nil");
      }
      try {
        rounding = RoundingMode.valueOf(roundingMode.string().toUpperCase(java.util.Locale.US));
      } catch (IllegalArgumentException e){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "unknown rounding_mode: "+roundingMode.string());
      }

      // always show decimal dot
      if (alwaysShowDecimalSeparator == Values.NIL){
        throw new LangException(LangError.NIL_ERROR, "always_show_decimal_separator cannot be nil");
      }

      try {
        DecimalFormat formatter = new DecimalFormat(pattern.string(), symbols);
        formatter.setRoundingMode(rounding);
        formatter.setDecimalSeparatorAlwaysShown(alwaysShowDecimalSeparator == Values.TRUE);

        return Values.make(
            new UserFunctionValue(
                new FunctionSignature(Collections.singletonList(
                    new FunctionParameter(0, "x", Types.ANY, Values.NIL)),
                    Types.STRING),
                new formatter_impl(formatter)));

      }
      catch (IllegalArgumentException e){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid decimal number format pattern: "+e.getMessage());
      }

    }
  }


  public static final class parser_impl implements UserFunction, Arity1UserFunction {

    private final DecimalFormat formatter;
    private final boolean allowPartial;

    public parser_impl(DecimalFormat formatter, boolean allowPartial) {
      this.formatter = formatter;
      this.allowPartial = allowPartial;
    }

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x == Values.NIL) return Values.NIL;

      ParsePosition parsePosition = new ParsePosition(0);
      Number parsed = formatter.parse(x.string(), parsePosition);

      if (parsed == null){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "Error at index: "+parsePosition.getErrorIndex()+". Cannot parse: "+x.string());
      }

      if (parsePosition.getIndex() < x.string().length() && !allowPartial){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "Partial match not allowed. Parsing ended at index: "+parsePosition.getIndex());
      }

      if (parsed instanceof Long){
        return Values.make(parsed.longValue());
      }
      else if (parsed instanceof Double){
        return Values.make(parsed.doubleValue());
      }
      else if (parsed instanceof BigDecimal){
        return Values.make((BigDecimal) parsed);
      }
      else if (parsed instanceof BigInteger){
        return Values.make(new BigDecimal((BigInteger)parsed));
      }

      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "unexpected parse result type: "+parsed.getClass().getCanonicalName());
      }
    }
  }

  // (string pattern='#.#', dict decimal_symbols=nil, boolean lenient=false, boolean parse_decimal=false) -> function
  public static final class parser implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value pattern, Value decimalSymbols, Value lenient, Value parseDecimal) {

      // pattern
      if (pattern == Values.NIL){
        throw new LangException(LangError.NIL_ERROR, "pattern cannot be nil");
      }

      // symbols
      DecimalFormatSymbols symbols;
      if (decimalSymbols == Values.NIL){
        symbols = DecimalFormatSymbols.getInstance(java.util.Locale.US);
      }
      else{
        symbols = Locale.decimalSymbolsFromDict(decimalSymbols.dict());
      }

      if (lenient == Values.NIL){
        throw new LangException(LangError.NIL_ERROR, "lenient cannot be nil");
      }

      if (parseDecimal == Values.NIL){
        throw new LangException(LangError.NIL_ERROR, "parse_decimal cannot be nil");
      }

      boolean allowPartial = lenient == Values.TRUE;
      boolean useDecimal = parseDecimal == Values.TRUE;
      try {
        DecimalFormat formatter = new DecimalFormat(pattern.string(), symbols);
        formatter.setParseBigDecimal(useDecimal);

        return Values.make(
            new UserFunctionValue(
                new FunctionSignature(Collections.singletonList(
                    new FunctionParameter(0, "x", Types.STRING, Values.NIL)),
                    Types.ANY),
                    new parser_impl(formatter, allowPartial)));

      }
      catch (IllegalArgumentException e){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid number format pattern: "+e.getMessage());
      }

    }
  }

}
