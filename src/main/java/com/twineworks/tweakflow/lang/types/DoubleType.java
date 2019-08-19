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

package com.twineworks.tweakflow.lang.types;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;

import java.math.BigDecimal;
import java.util.regex.Pattern;

final public class DoubleType implements Type {

  private String parseRegexPattern =
      "[\\x00-\\x20]*" +                            // optional leading whitespace
      "[-+]?" +                                     // optional sign
      "(" +
      "(NaN)|" +                                    // NaN
      "(Infinity)|" +                               // Infinity
      "([0-9]+(\\.[0-9]+)?([eE][-+]?[0-9]+)?)|" +   // digits.digits(E+-exponent)?
      "(\\.[0-9]+([eE][-+]?[0-9]+)?)" +             // .digits(E+-exponent)?
      ")" +
      "[\\x00-\\x20]*";                             // optional trailing whitespace

  private Pattern parseRegex = Pattern.compile(parseRegexPattern);

  @Override
  public String name() {
    return "double";
  }

  @Override
  public boolean isAny() {
    return false;
  }

  @Override
  public boolean isVoid() {
    return false;
  }

  @Override
  public boolean isString() {
    return false;
  }

  @Override
  public boolean isNumeric() {
    return true;
  }

  @Override
  public boolean isLong() {
    return false;
  }

  @Override
  public boolean isDouble() {
    return true;
  }

  @Override
  public boolean isDateTime() {
    return false;
  }

  @Override
  public boolean isDecimal() {
    return false;
  }

  @Override
  public boolean isBoolean() {
    return false;
  }

  @Override
  public boolean isBinary() {
    return false;
  }

  @Override
  public boolean isDict() {
    return false;
  }

  @Override
  public boolean isList() {
    return false;
  }

  @Override
  public boolean isContainer() {
    return false;
  }

  @Override
  public boolean isFunction() {
    return false;
  }

  @Override
  public boolean canAttemptCastTo(Type type) {
    return type.canAttemptCastFrom(this);
  }

  @Override
  public boolean canAttemptCastFrom(Type type) {
    return type == Types.DOUBLE
        || type == Types.ANY
        || type == Types.BOOLEAN
        || type == Types.LONG
        || type == Types.DECIMAL
        || type == Types.STRING
        || type == Types.VOID;
  }

  @Override
  public Value castFrom(Value x) {

    if (x == Values.NIL) return x;

    Type srcType = x.type();
    if (srcType == Types.DOUBLE) return x;

    if (srcType == Types.BOOLEAN) {
      return (x == Values.TRUE) ? Values.make(1.0d) : Values.make(0.0d);
    }

    if (srcType == Types.STRING){
      String s = x.string();
      if (parseRegex.matcher(s).matches()){
        return Values.make(Double.valueOf(s.trim()));
      }
      else {
        throw new LangException(LangError.CAST_ERROR, "Cannot cast "+ValueInspector.inspect(x)+" to "+name());
      }
    }

    if (srcType == Types.LONG){
      return Values.make((double) x.longNum());
    }

    if (srcType == Types.DECIMAL){
      return Values.make(x.decimal().doubleValue());
    }

    throw new LangException(LangError.CAST_ERROR, "Cannot cast "+srcType.name()+" to "+name());
  }

  @Override
  public int valueHash(Value x) {
      return x.value().hashCode();
  }

  @Override
  public boolean valueEquals(Value x, Value o) {

    // doubles may be equal to other doubles and can equal longs and decimals
    double d = x.doubleNum();

    //  comparing to a double?
    if (o.type() == this){
      return d == o.doubleNum();
    }
    // NaN and Infinities are not equal to any value of any other type
    else if (!Double.isFinite(d)){
      return false;
    }
    // comparing to a long?
    else if (o.type() == Types.LONG){
      return d == (double) o.longNum();
    }
    // comparing to a decimal?
    else if (o.type() == Types.DECIMAL){
      return BigDecimal.valueOf(d).compareTo(o.decimal()) == 0;
    }
    // anything else is not equal to a double
    return false;
  }

  @Override
  public boolean valueAndTypeEquals(Value x, Value o) {
    return o.type() == this && ((double) x.doubleNum() == (double) o.doubleNum());
  }

  @Override
  public boolean valueIdentical(Value x, Value o) {
    if (x == o) return true;
    if (o.type() != this) return false;

    double d_x = x.doubleNum();
    double d_o = o.doubleNum();
    return d_x == d_o || Double.isNaN(d_x) && Double.isNaN(d_o);
  }

  @Override
  public String toString() {
    return name();
  }

  @Override
  public int hashCode() {
    return name().hashCode();
  }
}
