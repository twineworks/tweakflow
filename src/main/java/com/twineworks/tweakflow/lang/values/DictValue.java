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


import io.vavr.collection.HashMap;

import java.util.Map;

final public class DictValue {

  private final HashMap<String, Value> map;

  private DictValue(HashMap<String, Value> map) {
    this.map = map;
  }

  public DictValue() {
    this(HashMap.empty());
  }

  public DictValue(Map<String, Value> in) {
    map = HashMap.ofAll(in);
  }

  @SuppressWarnings("unchecked")
  public DictValue(Map.Entry<String, Value>[] entries){
    map  = HashMap.ofEntries((Map.Entry[]) entries);
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(String key) {
    return map.containsKey(key);
  }

  public boolean containsValue(Value value) {
    for (String s : map.keySet()) {
      if (map.get(s).get().equals(value)){
        return true;
      }
    }
    return false;
  }

  
  public DictValue put(String key, Value value) {
    return new DictValue(map.put(key, value));
  }

  public DictValue putAll(Map<String, Value> entries){
    return new DictValue(HashMap.ofAll(entries).merge(map));
  }

  
  public DictValue delete(String key) {
    return new DictValue(map.remove(key));
  }

  
  public DictValue deleteAll(Iterable<? extends String> keys) {
    return new DictValue(map.removeAll(keys));
  }

  public DictValue putAll(DictValue dict){

    if (dict.getClass() == getClass()){
      DictValue otherDict = (DictValue) dict;
      return new DictValue(otherDict.map.merge(this.map));
    }

    HashMap<String, Value> out = this.map;
    for (String k : dict.keys()) {
      out = out.put(k, dict.get(k));
    }
    return new DictValue(out);
  }

  
  public Value get(String key) {
    return map.get(key).getOrElse(Values.NIL);
  }

  
  public ListValue values() {
    return new ListValue(map.values().toJavaList());
  }

  
  public Iterable<String> keys() {
    return map.keySet();
  }

  
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (o.getClass() == getClass()){
      DictValue that = (DictValue) o;
      return map.equals(that.map);
    }
    // another dict value type
    else if (o instanceof DictValue){
      DictValue other = (DictValue) o;
      // another dict implementation, must compare keys and values
      if (other.size() != this.size()) return false;
      for (String k : this.keys()) {
        if (!other.containsKey(k)) return false;
        Value v = get(k);
        Value ov = other.get(k);
        if (!v.equals(ov)) return false;
      }
      return true;
    }
    else{
      return false;
    }

  }

  
  public int hashCode() {
    return map.hashCode();
  }
}
