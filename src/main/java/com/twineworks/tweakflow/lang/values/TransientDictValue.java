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

import com.twineworks.collections.champ.TransientChampMap;

import java.util.Collection;
import java.util.Map;

public class TransientDictValue {

  TransientChampMap<String, Value> t;

  public TransientDictValue(){
    this(new TransientChampMap<>());
  }

  public TransientDictValue(DictValue d){
    this(new TransientChampMap<>(d.map));
  }

  private TransientDictValue(TransientChampMap<String, Value> t){
    this.t = t;
  }

  public void put(String key, Value v){
    t.set(key, v);
  }

  public Value get(String key){
    return t.get(key);
  }

  public boolean containsKey(String key){
    return t.containsKey(key);
  }

  public void remove(String key){
    t.remove(key);
  }

  public void removeAll(Collection<String> keys){
    t.removeAll(keys);
  }

  public void putAll(Map<String, Value> m){
    t.setAll(m);
  }
  public void putAll(DictValue d){
    t.setAll(d.map);
  }
  public void putAll(String[] keys, Value[] values){
    t.setAll(keys, values);
  }

  public DictValue persistent(){
    return new DictValue(t.freeze());
  }

  public TransientDictValue dup(){
    return new TransientDictValue(t.dup());
  }

}
