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

import java.util.regex.Pattern;

final public class LongType implements Type {

  private String parseRegexPattern = "[+-]?[0-9]+";
  private Pattern parseRegex = Pattern.compile(parseRegexPattern);

  @Override
  public String name() {
    return "long";
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
    return true;
  }

  @Override
  public boolean isDouble() {
    return false;
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
    return type == Types.LONG
        || type == Types.ANY
        || type == Types.VOID
        || type == Types.BOOLEAN
        || type == Types.DOUBLE
        || type == Types.DECIMAL
        || type == Types.STRING;
  }

  @Override
  public Value castFrom(Value x) {

    if (x == Values.NIL) return x;
    if (x.type() == this) return x;
    Type srcType = x.type();

    if (srcType == Types.DOUBLE){
      return Values.make(x.doubleNum().longValue());
    }

    if (srcType == Types.DECIMAL){
      return Values.make(x.decimal().longValue());
    }

    if (srcType == Types.STRING){
      String s = x.string().trim();

      if (parseRegex.matcher(s).matches()){
        try {
          return Values.make(Long.parseLong(s, 10));
        } catch(NumberFormatException e){
          throw new LangException(LangError.CAST_ERROR, "Cannot cast "+ValueInspector.inspect(x)+" to long. Number out of bounds.");
        }
      }
      else {
        throw new LangException(LangError.CAST_ERROR, "Cannot cast "+ValueInspector.inspect(x)+" to "+name());
      }

    }

    if (srcType == Types.BOOLEAN){
      return (x == Values.TRUE) ? Values.LONG_ONE : Values.LONG_ZERO;
    }

    throw new LangException(LangError.CAST_ERROR, "Cannot cast "+ValueInspector.inspect(x)+" to "+name());
  }

  @Override
  public int valueHash(Value x) {
    // always share the hashcode with doubles
    return Double.hashCode((double)x.longNum());
  }

  @Override
  public boolean valueEquals(Value x, Value o) {
    if (o.type() == this){
      return x.value().equals(o.longNum());
    }
    if (o.type() == Types.DOUBLE){
      // the double type implements equality check between doubles and longs
      return Types.DOUBLE.valueEquals(o, x);
    }
    if (o.type() == Types.DECIMAL){
      // the decimal type implements equality check between decimals and longs
      return Types.DECIMAL.valueEquals(o, x);
    }
    return false;
  }

  @Override
  public boolean valueAndTypeEquals(Value x, Value o) {
    return (o.type() == this) && x.longNum().equals(o.longNum());
  }

  @Override
  public boolean valueIdentical(Value x, Value o) {
    return valueAndTypeEquals(x, o);
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
