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

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.HashMap;

final public class DictType implements Type {

  @Override
  public String name() {
    return "dict";
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
    return false;
  }

  @Override
  public boolean isBinary() {
    return false;
  }

  @Override
  public boolean isDict() {
    return true;
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
    return type == Types.DICT
        || type == Types.ANY
        || type == Types.VOID
        || type == Types.LIST
        || type == Types.DATETIME;
  }

  @Override
  public Value castFrom(Value x) {

    if (x == Values.NIL) return x;

    Type srcType = x.type();
    if (srcType == Types.DICT){
      return x;
    }
    else if (srcType == Types.DATETIME){
      ZonedDateTime dt = x.dateTime().getZoned();

      return Values.makeDict(
          "year", Values.make(dt.getYear()),
          "month", Values.make(dt.getMonthValue()),
          "day_of_month", Values.make(dt.getDayOfMonth()),
          "hour", Values.make(dt.getHour()),
          "minute", Values.make(dt.getMinute()),
          "second", Values.make(dt.getSecond()),
          "nano_of_second", Values.make(dt.getNano()),
          "day_of_year", Values.make(dt.getDayOfYear()),
          "day_of_week", Values.make(dt.getDayOfWeek().getValue()),
          "week_of_year", Values.make(dt.getLong(WeekFields.ISO.weekOfWeekBasedYear())),
          "offset_seconds", Values.make(dt.getOffset().get(ChronoField.OFFSET_SECONDS)),
          "zone", Values.make(dt.getZone().getId())
      );
    }
    else if (srcType == Types.LIST){
      ListValue list = x.list();
      if ((list.size() & 1) == 1){
        throw new LangException(LangError.CAST_ERROR, "Cannot cast list with odd number of items to dict");
      }
      HashMap<String, Value> map = new HashMap<>();
      for (int i = 0, listSize = list.size(); i < listSize; i+=2) {
        String key = Types.STRING.castFrom(list.get(i)).string();
        if (key == null){
          throw new LangException(LangError.CAST_ERROR, "Cannot cast list to dict with nil key at index: "+i);
        }
        Value value = list.get(i+1);
        map.put(key, value);
      }
      return Values.make(new DictValue(map));

    }

    throw new LangException(LangError.CAST_ERROR, "Cannot cast "+srcType.name()+" to "+this.name());
  }

  @Override
  public int valueHash(Value x) {
    return x.dict().hashCode();
  }

  @Override
  public boolean valueEquals(Value x, Value o) {
    return o.type() == this &&
        valueHash(x) == valueHash(o) &&
        x.dict().equals(o.dict());
  }

  @Override
  public boolean valueIdentical(Value x, Value o) {
    if (o.type() != this) return false;
    DictValue xDict = x.dict();
    DictValue oDict = o.dict();
    if (xDict.size() != oDict.size()) return false;
    for (String k : xDict.keys()) {
      if (!oDict.containsKey(k)) return false;
      Value xValue = xDict.get(k);
      Value oValue = oDict.get(k);
      if (!xValue.identical(oValue)) return false;
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
