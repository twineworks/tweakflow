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


import io.usethesource.capsule.Map.Immutable;
import io.usethesource.capsule.Map.Transient;
import io.usethesource.capsule.core.PersistentTrieMap;

import java.util.Map;
import java.util.Set;

final public class DictValue {

  final Immutable<String, Value> map;

  DictValue(Immutable<String, Value> map) {
    this.map = map;
  }

  public DictValue() {
    this(PersistentTrieMap.of());
  }

  public DictValue(Map<String, Value> in) {
    Transient<String, Value> m = PersistentTrieMap.transientOf();
    m.__putAll(in);
    map = m.freeze();
  }

  public DictValue(Map.Entry<String, Value>[] entries) {
    Immutable<String, Value> m = PersistentTrieMap.of();
    for (Map.Entry<String, Value> entry : entries) {
      m = m.__put(entry.getKey(), entry.getValue());
    }
    map = m;
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

  public java.util.HashMap<String, Value> toHashMap() {
    java.util.HashMap<String, Value> ret = new java.util.HashMap<>();
    for (String k : keys()) {
      ret.put(k, get(k));
    }
    return ret;
  }

  public DictValue put(String key, Value value) {
    return new DictValue(map.__put(key, value));
  }

  public DictValue putAll(Map<String, Value> entries) {
    return new DictValue(map.__putAll(entries));
  }

  public DictValue delete(String key) {
    return new DictValue(map.__remove(key));
  }

  public DictValue deleteAll(Iterable<? extends String> keys) {
    Immutable<String, Value> m = this.map;
    for (String key : keys) {
      m = m.__remove(key);
    }
    return new DictValue(m);
  }

  public DictValue putAll(DictValue dict) {
    return new DictValue(map.__putAll(dict.map));
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
