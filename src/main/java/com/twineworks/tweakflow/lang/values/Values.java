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

import com.twineworks.tweakflow.lang.ast.expressions.FunctionNode;
import com.twineworks.tweakflow.lang.types.Types;

import java.time.Instant;
import java.util.*;

final public class Values {

  /* factory and static methods */
  public static final Value NIL = new Value(Types.VOID, null);

  public static final Value TRUE = new Value(Types.BOOLEAN, true);
  public static final Value FALSE = new Value(Types.BOOLEAN, false);

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

  public static final Value[] LONGS = new Value[255];

  static {
    // init interned longs
    LONGS[0] = LONG_ZERO;
    LONGS[1] = LONG_ONE;
    for(int i=2; i<LONGS.length; i++){
      LONGS[i] = new Value(Types.LONG, (long)i);
    }
  }

  public static Value make(String s){
    if (s.length() == 0) return EMPTY_STRING;
    return new Value(Types.STRING, s);
  }

  public static Value make(Long n){
    if (n >= 0 && n < LONGS.length){
      return LONGS[n.intValue()];
    }
    return new Value(Types.LONG, n);
  }

  public static Value make(Integer i){
    return make(i.longValue());
  }

  public static Value make(Double d){
    if (Double.isNaN(d)) return NAN;
    if (Double.isInfinite(d)){
      if (d == Double.POSITIVE_INFINITY){
        return INFINITY;
      }
      else{
        return NEG_INFINITY;
      }
    }
    return new Value(Types.DOUBLE, d);
  }

  public static Value make(Character c){
    return new Value(Types.STRING, c.toString());
  }

  public static Value make(Boolean b){
    return b ? Values.TRUE : Values.FALSE;
  }

  public static Value make(FunctionValue f){
    return new Value(Types.FUNCTION, f);
  }

  public static Value make(ListValue list){
    return new Value(Types.LIST, list);
  }

  public static Value make(DictValue map) {
    return new Value(Types.DICT, map);
  }

  public static Value make(DateTimeValue dt) {return new Value(Types.DATETIME, dt);}

  // convenience factory

  public static Value make(Object o){
    if (o == null) return NIL;
    if (o instanceof Value) return (Value) o;
    if (o instanceof Boolean) return o == Boolean.TRUE ? Values.TRUE : Values.FALSE;
    if (o instanceof String) return make((String) o);
    if (o instanceof Character) return make(((Character) o).toString());
    if (o instanceof Long) return make((Long) o);
    if (o instanceof Integer) return make(((Integer) o).longValue());
    if (o instanceof Double) return make((Double) o);
    if (o instanceof Instant) return make(new DateTimeValue((Instant) o));
    if (o instanceof DateTimeValue) return make((DateTimeValue) o);
    if (o instanceof FunctionValue) return make((FunctionValue) o);
    if (o instanceof ListValue) return make((ListValue) o);
    if (o instanceof DictValue) return make((DictValue) o);
    if (o instanceof List) return makeList((List) o);

    throw new RuntimeException("Cannot make a value from: "+o);
  }

  public static Value makeDict() {
    return EMPTY_DICT;
  }

  public static Value makeList() {
    return EMPTY_LIST;
  }

  public static Value makeRange(long from, long to) {
    long size = to - from + 1;

    if (size <= 0){
      return Values.EMPTY_LIST;
    }

    if (size >= Integer.MAX_VALUE){
      throw new IllegalArgumentException("Cannot make range exceeding: "+Integer.MAX_VALUE+" items");
    }

    ListValue range = new ListValue();
    long idx = from;
    while (idx <= to){
      range = range.append(make(idx));
      idx++;
    }
    return new Value(Types.LIST, range);
  }

  public static Value makeList(Value ... values){
    ListValue list = new ListValue();
    return new Value(Types.LIST, list.appendAll(values));
  }

  public static Value makeDict(Map<String, Value> map){
    return new Value(Types.DICT, new DictValue(map));
  }

  public static Value makeDict(Object ... keysAndValues){

    if ((keysAndValues.length % 2) != 0){
      throw new RuntimeException("Must provide a sequence of string keys and corresponding values. Got "+keysAndValues.length+" arguments.");
    }

    HashMap<String, Value> map = new HashMap<>();
    for (int i = 0; i < keysAndValues.length; i+=2) {
      String key = (String) keysAndValues[i];
      Value value = make(keysAndValues[i+1]);
      map.put(key, value);
    }

    return new Value(Types.DICT, new DictValue(map));

  }

  public static Value makeList(Object ... items){

    ArrayList<Value> list = new ArrayList<>();
    for (Object item : items) {
      list.add(Values.make(item));
    }
    return new Value(Types.LIST, new ListValue(list));

  }

  public static Value makeList(List items){
    ArrayList<Value> list = new ArrayList<>();
    for (Object item : items) {
      list.add(Values.make(item));
    }
    return new Value(Types.LIST, new ListValue(list));
  }

  public static Value makeList(Iterable items){
    ArrayList<Value> list = new ArrayList<>();
    for (Object item : items) {
      list.add(Values.make(item));
    }
    return new Value(Types.LIST, new ListValue(list));
  }

  public static Value makeListOfValues(Collection<Value> items){
    return Values.make(new ListValue(items));
  }

  // convenient stub for testing
  public static Value makeConstantFunctionStub(Value ret) {
    FunctionSignature functionSignature = new FunctionSignature(new LinkedHashMap<>(), ret.type());
    FunctionNode node = new FunctionNode()
        .setDeclaredReturnType(ret.type());

    return make(new StandardFunctionValue(node, functionSignature, Collections.emptyMap()));
  }
}


