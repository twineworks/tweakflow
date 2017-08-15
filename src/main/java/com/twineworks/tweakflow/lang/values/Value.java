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

package com.twineworks.tweakflow.lang.values;

import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;

import java.util.Objects;

import static com.twineworks.tweakflow.lang.values.Values.NIL;

final public class Value implements ValueProvider {

  private final Type type;
  private final Object value;

  private int hashCode;

  Value(Type type, Object value) {

    Objects.requireNonNull(type, "type cannot be null");

    if (type != Types.VOID){
      Objects.requireNonNull(value, "value cannot be null");
    }

    this.type = type;
    this.value = value;
  }

  public Type type() {
    return type;
  }

  public Object value() {
    return value;
  }

  public boolean isNil() {return this == NIL;}

  public String string() {
    return (String) value;
  }

  public DictValue dict() {
    return (DictValue) value;
  }

  public ListValue list() {
    return (ListValue) value;
  }

  public FunctionValue function() {
    return (FunctionValue) value;
  }

  public Long longNum() {
    return (Long) value;
  }

  public Double doubleNum() {
    return (Double) value;
  }

  public DateTimeValue dateTime() {
    return (DateTimeValue) value;
  }

  public Boolean bool() {
    return (Boolean) value;
  }

  @Override
  public boolean equals(Object o) {
    // break out early if this is a function
    // identity does not mean equality for functions
    if (type == Types.FUNCTION) return false;
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Value other = (Value) o;
    return type.valueEquals(this, other);
  }

  @Override
  public int hashCode() {
    // assuming a legitimate 0 hash code is cheap to calculate
    // a 0 hash code is effectively not cached
    if (hashCode == 0){
      hashCode = type.valueHash(this);
    }
    return hashCode;
  }

  public boolean identical(Value other){
    return type.valueIdentical(this, other);
  }

  @Override
  public String toString() {
    return ValueInspector.inspect(this);
  }

  public boolean isString() {
    return type == Types.STRING;
  }

  public boolean isLongNum() {
    return this.type == Types.LONG;
  }

  public boolean isDoubleNum(){
    return this.type == Types.DOUBLE;
  }

  public Value castTo(Type type){
    if (type == this.type || type == Types.ANY || this == Values.NIL) return this;     // no cast necessary
    return type.castFrom(this);
  }

  public boolean isList() {
    return this.type == Types.LIST;
  }

  public boolean isFunction() {
    return this.type == Types.FUNCTION;
  }

  public boolean isDict() {
    return this.type == Types.DICT;
  }

  @Override
  public Value getValue() {
    return this;
  }


}