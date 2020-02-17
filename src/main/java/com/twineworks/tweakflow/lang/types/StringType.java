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

import com.twineworks.tweakflow.io.MagicNumbers;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;

final public class StringType implements Type {

  @Override
  public String name() {
    return "string";
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
    return true;
  }

  @Override
  public boolean isNumeric() {
    return false;
  }

  @Override
  public boolean isLong() {
    return false;
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
    return type == Types.STRING
        || type == Types.ANY
        || type == Types.VOID
        || type == Types.BOOLEAN
        || type == Types.LONG
        || type == Types.DOUBLE
        || type == Types.DECIMAL
        || type == Types.DATETIME;

  }

  @Override
  public Value castFrom(Value x) {

    if (x == Values.NIL) return x;

    Type srcType = x.type();
    if (srcType == this) return x;
    if (srcType == Types.BOOLEAN){
      return (x == Values.TRUE) ? Values.make("true") : Values.make("false");
    }

    if (srcType == Types.LONG){
      return Values.make(x.longNum().toString());
    }

    if (srcType == Types.DOUBLE){
      return Values.make(x.doubleNum().toString());
    }

    if (srcType == Types.DECIMAL){
      return Values.make(x.decimal().toString());
    }

    if (srcType == Types.DATETIME){
      return Values.make(x.dateTime().toString());
    }

    throw new LangException(LangError.CAST_ERROR, "Cannot cast "+ValueInspector.inspect(x) +" to "+name());
  }

  @Override
  public int valueHash(Value x) {
    return x.string().hashCode();
  }

  @Override
  public boolean valueEquals(Value x, Value o) {
    // strings are only equal to strings
    return o.type() == this &&
        valueHash(x) == valueHash(o) &&
        x.string().equals(o.string());
  }

  @Override
  public boolean valueAndTypeEquals(Value x, Value o) {
    return valueEquals(x, o);
  }

  @Override
  public boolean valueIdentical(Value x, Value o) {
    return valueAndTypeEquals(x, o);
  }

  @Override
  public byte getId() {
    return MagicNumbers.Format.STRING;
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
