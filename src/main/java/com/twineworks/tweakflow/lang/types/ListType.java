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
import com.twineworks.tweakflow.lang.values.*;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

final public class ListType implements Type {

  @Override
  public String name() {
    return "list";
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
    return true;
  }

  @Override
  public boolean isContainer() {
    return true;
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
    return type == Types.LIST
        || type == Types.ANY
        || type == Types.VOID
        || type == Types.DICT
        || type == Types.STRING;
  }

  @Override
  public Value castFrom(Value x) {

    if (x == Values.NIL) return x;

    Type srcType = x.type();

    if (srcType == Types.LIST || srcType == Types.ANY){
      return x;
    }
    else if (srcType == Types.DICT){
      DictValue map = x.dict();
      ListValue out = new ListValue();

      for (String key : map.keys()) {
        Value value = map.get(key);
        out = out.append(Values.make(key));
        out = out.append(value);
      }

      return Values.make(out);
    }
    else if (srcType == Types.STRING){

      String str = x.string();
      ListValue out = new ListValue();

      List<Value> charValues = str.codePoints().mapToObj(
          c -> Values.make(new String(Character.toChars(c)))).collect(Collectors.toList());

      return Values.make(out.appendAll(charValues));
    }
    else{
      throw new LangException(LangError.CAST_ERROR, "Cannot cast "+ValueInspector.inspect(x)+" to "+this.name());
    }
  }

  @Override
  public int valueHash(Value x) {
    return x.list().hashCode();
  }

  @Override
  public boolean valueEquals(Value x, Value o) {

    if (x.type() != this) return false;
    if (o.type() != this) return false;
    ListValue xList = x.list();
    ListValue oList = o.list();
    if (xList.size() != oList.size()) return false;
    Iterator<Value> xi = xList.iterator();
    Iterator<Value> oi = oList.iterator();
    // lists have same size, only one iterator need be queried for
    // hasNext
    while(xi.hasNext()){
      if (!xi.next().valueEquals(oi.next())) return false;
    }
    return true;

  }

  @Override
  public boolean valueAndTypeEquals(Value x, Value o) {
    if (x.type() != this) return false;
    if (o.type() != this) return false;
    ListValue xList = x.list();
    ListValue oList = o.list();
    if (xList.size() != oList.size()) return false;
    Iterator<Value> xi = xList.iterator();
    Iterator<Value> oi = oList.iterator();
    // lists have same size, only one iterator need be queried for
    // hasNext
    while(xi.hasNext()){
      if (!xi.next().valueAndTypeEquals(oi.next())) return false;
    }
    return true;

  }

  @Override
  public boolean valueIdentical(Value x, Value o) {
    if (x == o) return true;
    if (x.type() != this) return false;
    if (o.type() != this) return false;
    ListValue xList = x.list();
    ListValue oList = o.list();
    if (xList.size() != oList.size()) return false;
    Iterator<Value> xi = xList.iterator();
    Iterator<Value> oi = oList.iterator();
    // lists have same size, only one iterator need be queried for
    // hasNext
    while(xi.hasNext()){
      if (!xi.next().valueIdentical(oi.next())) return false;
    }
    return true;
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
