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

package com.twineworks.tweakflow.lang.values;

import com.twineworks.tweakflow.lang.types.Types;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Struct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

final public class Values {

  /* factory and static methods */
  public static final Value NIL = new Value(Types.VOID, null);

  public static final Value TRUE = new Value(Types.BOOLEAN, true);
  public static final Value FALSE = new Value(Types.BOOLEAN, false);

  public static final Value EMPTY_BINARY = new Value(Types.BINARY, new byte[0]);

  public static final Value EPOCH = new Value(Types.DATETIME, new DateTimeValue(Instant.EPOCH));

  public static final Value NAN = new Value(Types.DOUBLE, Double.NaN);
  public static final Value INFINITY = new Value(Types.DOUBLE, Double.POSITIVE_INFINITY);
  public static final Value NEG_INFINITY = new Value(Types.DOUBLE, Double.NEGATIVE_INFINITY);

  public static final Value EMPTY_LIST = new Value(Types.LIST, new ListValue());
  public static final Value EMPTY_DICT = new Value(Types.DICT, new DictValue());
  public static final Value EMPTY_STRING = new Value(Types.STRING, "");

  public static final Value LONG_ZERO = new Value(Types.LONG, 0L);
  public static final Value LONG_ONE = new Value(Types.LONG, 1L);
  public static final Value LONG_NEG_ONE = new Value(Types.LONG, -1L);

  public static final Value DECIMAL_ZERO = new Value(Types.DECIMAL, BigDecimal.ZERO);
  public static final Value DECIMAL_ONE = new Value(Types.DECIMAL, BigDecimal.ONE);

  private static final Value[] LONGS = new Value[255];

  static {
    // init interned longs
    LONGS[0] = LONG_ZERO;
    LONGS[1] = LONG_ONE;
    for (int i = 2; i < LONGS.length; i++) {
      LONGS[i] = new Value(Types.LONG, (long) i);
    }
  }

  public static Value make(String s) {
    if (s == null) return NIL;
    if (s.length() == 0) return EMPTY_STRING;
    return new Value(Types.STRING, s);
  }

  public static Value make(byte[] bin) {
    if (bin == null) return NIL;
    if (bin.length == 0) return EMPTY_BINARY;
    return new Value(Types.BINARY, bin);
  }

  public static Value make(Long n) {
    if (n == null) return NIL;
    if (n >= 0 && n < LONGS.length) {
      return LONGS[n.intValue()];
    }
    return new Value(Types.LONG, n);
  }

  public static Value make(Integer i) {
    if (i == null) return NIL;
    return make(i.longValue());
  }

  public static Value make(Byte i) {
    if (i == null) return NIL;
    return make(i.longValue());
  }

  public static Value make(Short i) {
    if (i == null) return NIL;
    return make(i.longValue());
  }

  public static Value make(BigDecimal d) {
    if (d == null) return NIL;
    return new Value(Types.DECIMAL, d);
  }

  public static Value make(BigInteger d) {
    if (d == null) return NIL;
    return new Value(Types.DECIMAL, new BigDecimal(d));
  }

  public static Value make(Double d) {
    if (d == null) return NIL;
    if (Double.isNaN(d)) return NAN;
    if (Double.isInfinite(d)) {
      if (d == Double.POSITIVE_INFINITY) {
        return INFINITY;
      } else {
        return NEG_INFINITY;
      }
    }
    return new Value(Types.DOUBLE, d);
  }

  public static Value make(Float f) {
    if (f == null) return NIL;
    if (Float.isNaN(f)) return NAN;
    if (Float.isInfinite(f)) {
      if (f == Float.POSITIVE_INFINITY) {
        return INFINITY;
      } else {
        return NEG_INFINITY;
      }
    }
    return new Value(Types.DOUBLE, f.doubleValue());
  }

  public static Value make(Character c) {
    if (c == null) return NIL;
    return new Value(Types.STRING, c.toString());
  }

  public static Value make(Boolean b) {
    if (b == null) return NIL;
    return b ? Values.TRUE : Values.FALSE;
  }

  public static Value make(FunctionValue f) {
    if (f == null) return NIL;
    return new Value(Types.FUNCTION, f);
  }

  public static Value make(ListValue list) {
    if (list == null) return NIL;
    return new Value(Types.LIST, list);
  }

  public static Value make(DictValue map) {
    if (map == null) return NIL;
    return new Value(Types.DICT, map);
  }

  public static Value make(DateTimeValue dt) {
    if (dt == null) return NIL;
    return new Value(Types.DATETIME, dt);
  }

  public static Value make(Instant instant) {
    if (instant == null) return NIL;
    return new Value(Types.DATETIME, new DateTimeValue(instant));
  }

  public static Value make(LocalDateTime ldt) {
    if (ldt == null) return NIL;
    return new Value(Types.DATETIME, new DateTimeValue(ldt));
  }

  public static Value make(OffsetDateTime odt) {
    if (odt == null) return NIL;
    return new Value(Types.DATETIME, new DateTimeValue(odt));
  }

  public static Value make(ZonedDateTime zdt) {
    if (zdt == null) return NIL;
    return new Value(Types.DATETIME, new DateTimeValue(zdt));
  }

  public static Value make(Number o){
    if (o instanceof Byte) return Values.make((Byte) o);
    if (o instanceof Short) return Values.make((Short) o);
    if (o instanceof Long) return make((Long) o);
    if (o instanceof Integer) return make(((Integer) o).longValue());
    if (o instanceof Float) return Values.make((Float) o);
    if (o instanceof Double) return make((Double) o);
    if (o instanceof BigDecimal) return make((BigDecimal) o);
    if (o instanceof BigInteger) return make((BigInteger) o);

    return make((Object)o);
  }

  // convenience factory

  public static Value make(Object o) {
    if (o == null) return NIL;
    if (o instanceof Value) return (Value) o;
    if (o instanceof Boolean) return o == Boolean.TRUE ? Values.TRUE : Values.FALSE;

    if (o instanceof Character) return Values.make((Character) o);
    if (o instanceof String) return make((String) o);

    if (o instanceof Byte) return Values.make((Byte) o);
    if (o instanceof Short) return Values.make((Short) o);
    if (o instanceof Long) return make((Long) o);
    if (o instanceof Integer) return make(((Integer) o).longValue());
    if (o instanceof Float) return Values.make((Float) o);
    if (o instanceof Double) return make((Double) o);
    if (o instanceof BigDecimal) return make((BigDecimal) o);
    if (o instanceof BigInteger) return make((BigInteger) o);

    if (o instanceof Instant) return make(new DateTimeValue((Instant) o));
    if (o instanceof LocalDateTime) return Values.make((LocalDateTime) o);
    if (o instanceof OffsetDateTime) return Values.make((OffsetDateTime) o);
    if (o instanceof ZonedDateTime) return Values.make((ZonedDateTime) o);
    if (o instanceof DateTimeValue) return make((DateTimeValue) o);

    if (o instanceof FunctionValue) return make((FunctionValue) o);
    if (o instanceof ListValue) return make((ListValue) o);
    if (o instanceof DictValue) return make((DictValue) o);
    if (o instanceof List) return makeList((List) o);

    if (o instanceof Date) return Values.make(((Date) o).toInstant());
    if (o instanceof byte[]) return Values.make((byte[]) o);

    if (o instanceof Struct){
      Struct struct = (Struct) o;
      try {
        ListValue list = new ListValue();
        for (Object attribute : struct.getAttributes()) {
          list = list.append(Values.make(attribute));
        }
        return Values.make(list);
      } catch (SQLException e) {
        return Values.NIL;
      }
    }

    if (o.getClass().isArray()){

      ListValue list = new ListValue();

      if (o instanceof int[]){
        int[] a = (int[]) o;
        for (int value : a) {
          list = list.append(Values.make(value));
        }
      }
      else if (o instanceof long[]){
        long[] a = (long[]) o;
        for (long value : a) {
          list = list.append(Values.make(value));
        }
      }
      else if (o instanceof boolean[]){
        boolean[] a = (boolean[]) o;
        for (boolean value : a) {
          list = list.append(Values.make(value));
        }
      }
      else if (o instanceof double[]){
        double[] a = (double[]) o;
        for (double value : a) {
          list = list.append(Values.make(value));
        }
      }
      else if (o instanceof char[]){
        char[] a = (char[]) o;
        for (char value : a) {
          list = list.append(Values.make(value));
        }
      }
      if (o instanceof String[]){
        String[] a = (String[]) o;
        for (String value : a) {
          list = list.append(Values.make(value));
        }
      }
      else {
        // https://stackoverflow.com/questions/2725533/how-to-see-if-an-object-is-an-array-without-using-reflection
        for(int i = 0; i< Array.getLength(o); i++){
          list = list.append(Values.make(Array.get(o, i)));
        }
      }
      return Values.make(list);
    }

    if (o instanceof Map){
      TransientDictValue t = new TransientDictValue();
      Map map = (Map) o;
      for (Object k : map.keySet()) {
        t.put(k.toString(), Values.make(map.get(k)));
      }
      return Values.make(t.persistent());
    }

    if (o instanceof Collection){
      Collection collection = (Collection) o;
      ListValue list = new ListValue();
      for (Object item : collection) {
        list = list.append(Values.make(item));
      }
      return Values.make(list);
    }

    return Values.make(o.toString());

  }

  public static Value makeDict() {
    return EMPTY_DICT;
  }

  public static Value makeList() {
    return EMPTY_LIST;
  }

  public static Value makeRange(long from, long to) {
    long size = to - from + 1;

    if (size <= 0) {
      return EMPTY_LIST;
    }

    if (size >= Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Cannot make range exceeding: " + Integer.MAX_VALUE + " items");
    }

    ListValue range = new ListValue();
    long idx = from;
    while (idx <= to) {
      range = range.append(make(idx));
      idx++;
    }
    return new Value(Types.LIST, range);
  }

  public static Value makeList(Value... values) {
    ListValue list = new ListValue();
    return new Value(Types.LIST, list.appendAll(values));
  }

  public static Value makeDict(Map<String, Value> map) {
    return new Value(Types.DICT, new DictValue(map));
  }

  public static Value makeDict(Object... keysAndValues) {

    if ((keysAndValues.length % 2) != 0) {
      throw new RuntimeException("Must provide a sequence of string keys and corresponding values. Got " + keysAndValues.length + " arguments.");
    }

    HashMap<String, Value> map = new HashMap<>();
    for (int i = 0; i < keysAndValues.length; i += 2) {
      String key = (String) keysAndValues[i];
      Value value = make(keysAndValues[i + 1]);
      map.put(key, value);
    }

    return new Value(Types.DICT, new DictValue(map));

  }

  public static Value makeList(Object... items) {

    ArrayList<Value> list = new ArrayList<>();
    for (Object item : items) {
      list.add(Values.make(item));
    }
    return new Value(Types.LIST, new ListValue(list));

  }

  public static Value makeList(List items) {
    ArrayList<Value> list = new ArrayList<>();
    for (Object item : items) {
      list.add(Values.make(item));
    }
    return new Value(Types.LIST, new ListValue(list));
  }

  public static Value makeList(Iterable items) {
    ArrayList<Value> list = new ArrayList<>();
    for (Object item : items) {
      list.add(Values.make(item));
    }
    return new Value(Types.LIST, new ListValue(list));
  }

}


