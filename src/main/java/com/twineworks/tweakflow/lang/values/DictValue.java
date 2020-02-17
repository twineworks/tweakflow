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


import com.twineworks.collections.champ.ChampEntry;
import com.twineworks.collections.champ.ChampMap;
import com.twineworks.collections.champ.TransientChampMap;

import java.util.*;

final public class DictValue {

  final ChampMap<String, Value> map;

  DictValue(ChampMap<String, Value> map) {
    this.map = map;
  }

  public DictValue() {
    this(ChampMap.empty());
  }

  public DictValue(Map<String, Value> in) {
    TransientChampMap<String, Value> t = new TransientChampMap<>();
    t.setAll(in);
    map = t.freeze();
  }

  public DictValue(Map.Entry<String, Value>[] entries) {
    TransientChampMap<String, Value> t = new TransientChampMap<>();
    t.setAll(Arrays.asList(entries));
    map = t.freeze();
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
    return map.containsValue(value);
  }

  public HashMap<String, Value> toHashMap() {
    HashMap<String, Value> ret = new HashMap<>();
    Iterator<Map.Entry<String, Value>> iter = map.entryIterator();
    while(iter.hasNext()){
      Map.Entry<String, Value> e = iter.next();
      ret.put(e.getKey(), e.getValue());
    }
    return ret;
  }

  public DictValue put(String key, Value value) {
    return new DictValue(map.set(key, value));
  }

  public DictValue putAll(Map<String, Value> entries) {
    return new DictValue(map.setAll(entries));
  }

  public DictValue delete(String key) {
    return new DictValue(map.remove(key));
  }

  public DictValue deleteAll(Iterable<? extends String> keys) {

    TransientChampMap<String, Value> t = new TransientChampMap<>(map);
    for (String key : keys) {
      t.remove(key);
    }
    return new DictValue(t.freeze());

  }

  public DictValue deleteAll(DictValue dict) {
    return new DictValue(map.removeAll(dict.keys()));
  }

  public DictValue putAll(DictValue dict) {
    return new DictValue(map.setAll(dict.map));
  }

  public Value get(String key) {
    Value v = map.get(key);
    if (v == null) return Values.NIL;
    return v;
  }

  public ListValue values() {
    return new ListValue(map.values().toArray());
  }

  public Set<String> keys() {
    return map.keySet();
  }

  public void getAll(String[] keys, Value[] values){
    Iterator<ChampEntry<String, Value>> iter = map.champEntryIterator();
    int i=0;
    while(iter.hasNext()){
      ChampEntry<String, Value> entry = iter.next();
      keys[i] = entry.key;
      values[i] = entry.value;
      i++;
    }
  }

  public Iterator<Map.Entry<String, Value>> entryIterator(){
    return map.entryIterator();
  }

  public Iterator<String> keyIterator(){
    return map.keyIterator();
  }

  public Iterator<Value> valueIterator(){
    return map.valueIterator();
  }

  public boolean equals(Object o) {

    if (this == o) return true;
    if (o == null) return false;
    if (o.getClass() == getClass()) {
      DictValue that = (DictValue) o;
      return map.equals(that.map);
    } else {
      return false;
    }

  }

  public int hashCode() {
    return map.hashCode();
  }
}
