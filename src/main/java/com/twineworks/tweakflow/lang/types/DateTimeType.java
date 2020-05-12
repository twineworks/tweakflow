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
import com.twineworks.tweakflow.lang.values.DateTimeValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.util.LangUtil;

import java.time.*;
import java.util.regex.Pattern;

final public class DateTimeType implements Type {

  private final static String REGEX_DATE = "(?:\\+|-)?\\d{1,10}-\\d+-\\d+";
  private final static String REGEX_TIME = "(?:\\d+:\\d+:\\d+(?:\\.\\d+)?)";
  private final static String REGEX_OFFSET = "(?:(?:(?:\\+|-)\\d+:\\d+)|Z)";
  private final static String REGEX_TZ = "@(?:.+)";
  private final static Pattern literalDatetimePattern = Pattern.compile("^"+REGEX_DATE+"T(?:"+REGEX_TIME+"(?:"+REGEX_OFFSET+"|(?:"+REGEX_OFFSET+REGEX_TZ+")|"+REGEX_TZ+")?)?$");

  private final static Pattern isoDatetimePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T?$");
  private final static Pattern isoDateAndTimePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z?$");

  private final static ZoneId UTC_ZONE = ZoneId.of("UTC");
  @Override
  public String name() {
    return "datetime";
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
    return true;
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
    return type == Types.DATETIME
        || type == Types.STRING
        || type == Types.VOID
        || type == Types.ANY;
  }

  @Override
  public Value castFrom(Value x) {

    if (x == Values.NIL) return x;

    Type srcType = x.type();

    if (srcType == Types.DATETIME){
      return x;
    }

    if (srcType == Types.STRING) {
      return Values.make(castFromString(x.string()));
    }

    throw new LangException(LangError.CAST_ERROR, "Cannot cast " + ValueInspector.inspect(x) + " to " + name());
  }

  private DateTimeValue castFromString(String str) {
    // dates of the form 2020-05-30T23:12:34Z
    if (isoDateAndTimePattern.matcher(str).matches()){
      try {
        int year = Integer.parseInt(str.substring(0, 4));
        int month = Integer.parseInt(str.substring(5, 7));
        int dayOfMonth = Integer.parseInt(str.substring(8, 10));
        int hour = Integer.parseInt(str.substring(11, 13));
        int minute = Integer.parseInt(str.substring(14, 16));
        int second = Integer.parseInt(str.substring(17, 19));
        LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
        LocalTime localTime = LocalTime.of(hour, minute, second);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, UTC_ZONE);
        DateTimeValue dateTime = new DateTimeValue(zonedDateTime);
        return dateTime;
      }
      catch(DateTimeException e){
        throw new LangException(LangError.CAST_ERROR, "Cannot cast " + LangUtil.getStringLiteral(str) + " to " + name());
      }
    }
    // dates of the form 2020-05-30
    else if (isoDatetimePattern.matcher(str).matches()){
      try {
        int year = Integer.parseInt(str.substring(0, 4));
        int month = Integer.parseInt(str.substring(5, 7));
        int dayOfMonth = Integer.parseInt(str.substring(8, 10));
        LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
        LocalTime localTime = LocalTime.MIDNIGHT;
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, UTC_ZONE);
        DateTimeValue dateTime = new DateTimeValue(zonedDateTime);
        return dateTime;
      }
      catch(DateTimeException e){
        throw new LangException(LangError.CAST_ERROR, "Cannot cast " + LangUtil.getStringLiteral(str) + " to " + name());
      }
    }
    // everything else
    else if (literalDatetimePattern.matcher(str).matches()){
      try {
        return LangUtil.toDateTime(str, null);
      }
      catch(LangException e){
        throw new LangException(LangError.CAST_ERROR, "Cannot cast " + LangUtil.getStringLiteral(str) + " to " + name()+": "+e.getMessage());
      }
    }
    else {
      throw new LangException(LangError.CAST_ERROR, "Cannot cast " + LangUtil.getStringLiteral(str) + " to " + name());
    }
  }

  @Override
  public int valueHash(Value x) {
    return x.value().hashCode();
  }

  @Override
  public boolean valueEquals(Value x, Value o) {

    //  comparing to a datetime?
    if (o.type() == this) {
      return x.dateTime().getInstant().equals(o.dateTime().getInstant());
    }

    // anything else cannot be equal
    return false;
  }

  @Override
  public boolean valueAndTypeEquals(Value x, Value o) {
    //  comparing to a datetime?
    if (o.type() == this) {
      return x.dateTime().equals(o.dateTime());
    }

    // anything else cannot be equal
    return false;
  }

  @Override
  public boolean valueIdentical(Value x, Value o) {
    return valueAndTypeEquals(x, o);
  }

  @Override
  public byte getId() {
    return MagicNumbers.Format.DATETIME;
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
