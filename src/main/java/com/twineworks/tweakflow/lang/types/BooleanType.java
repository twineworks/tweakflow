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

package com.twineworks.tweakflow.lang.types;

import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

final public class BooleanType implements Type {

  @Override
  public String name() {
    return "boolean";
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
  public boolean isBigInteger() {
    return false;
  }

  @Override
  public boolean isBigDecimal() {
    return false;
  }

  @Override
  public boolean isBoolean() {
    return true;
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
  public boolean isSet() {
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
    return true; // everything casts to boolean somehow
  }

  @Override
  public Value castFrom(Value x) {

    if (x == Values.NIL) return Values.FALSE;

    Type srcType = x.type();

    if (srcType == Types.BOOLEAN){
      return x;
    }
    else if (srcType == Types.LIST){
      return x.list().size() == 0 ? Values.FALSE : Values.TRUE;
    }
    else if (srcType == Types.DICT){
      return x.dict().size() == 0 ? Values.FALSE : Values.TRUE;
    }
    else if (srcType == Types.DOUBLE){
      Double d = x.doubleNum();
      if (Double.isNaN(d)) return Values.FALSE;
      if (d.equals(0.0d)) return Values.FALSE;
      if (d.equals(-0.0d)) return Values.FALSE;
      return Values.TRUE;
    }
    else if (srcType == Types.LONG){
      Long n = x.longNum();
      return (n == 0L) ? Values.FALSE : Values.TRUE;
    }
    else if (srcType == Types.FUNCTION){
      return Values.TRUE;
    }
    else if (srcType == Types.DATETIME){
      return Values.TRUE;
    }
    else if (srcType == Types.STRING){
      return (x.string().length() == 0) ? Values.FALSE : Values.TRUE;
    }

    throw new AssertionError("Unknown type "+srcType);

  }

  @Override
  public int valueHash(Value x) {
    return x == Values.FALSE ? 0 : 1;
  }

  @Override
  public boolean valueEquals(Value x, Value o) {
    // boolean values are singletons
    return x == o;
  }

  @Override
  public boolean valueIdentical(Value x, Value o) {
    // boolean values are singletons
    return x == o;
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
