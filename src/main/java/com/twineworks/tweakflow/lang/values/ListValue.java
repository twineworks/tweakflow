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

import com.twineworks.collections.trie.TrieList;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;

import java.util.*;

final public class ListValue implements Iterable<Value> {

  private final TrieList vec;

  private ListValue(TrieList vec) {
    this.vec = vec;
  }

  public ListValue() {
    this(TrieList.empty());
  }

  public ListValue(List<Value> values) {
    this.vec = TrieList.empty().addAll(values.toArray());
  }

  public ListValue(Value[] values) {
    this.vec = TrieList.empty().addAll(values);
  }

  ListValue(Object[] values) {
    this.vec = TrieList.empty().addAll(values);
  }

  public ListValue(Collection<Value> values) {
    this.vec = TrieList.empty().addAll(values.toArray());
  }

  public Value get(long index) {
    if (index >= 0 && index < vec.size()){
      Value value = (Value) vec.get((int)index);
      if (value == null) return Values.NIL;
      return value;
    }

    return Values.NIL;
  }

  public ListValue set(long index, Value value) {

    if (index > Integer.MAX_VALUE || index < 0L) throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "cannot set index "+index);

    TrieList ret = vec;

    if (index >= vec.size()){
      ret = ret.padTo((int)index, Values.NIL).add(value);
    }
    else{
      ret = ret.set((int)index, value);
    }

    return new ListValue(ret);
  }

  public boolean isEmpty() {
    return vec.isEmpty();
  }

  public ListValue append(Value v) {
    return new ListValue(vec.add(v));
  }

  public ListValue appendAll(List<? extends Value> values) {
    return new ListValue(vec.addAll(values.toArray()));
  }

  public ListValue appendAll(Value[] values) {
    return new ListValue(vec.addAll(values));
  }

  public ListValue appendAll(ListValue values) {
    return new ListValue(vec.addAll(values.vec));
  }

  public ListValue prepend(Value x) {
    return new ListValue(vec.insert(0, x));
  }

  public ListValue padTo(long length, Value withValue) {
    if (length > Integer.MAX_VALUE) throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "cannot pad to length "+length);
    return new ListValue(vec.padTo((int)length, withValue));
  }
  
  public ListValue insert(long idx, Value value) {
    if (idx >= Integer.MAX_VALUE) throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "cannot insert at index "+idx);
    TrieList ret = vec;
    if (idx > vec.size()){
      ret = ret.padTo((int)idx, Values.NIL);
    }

    ret = ret.insert((int)idx, value);

    return new ListValue(ret);
  }

  public ListValue delete(long idx) {
    if (idx >= vec.size()) return this;
    if (idx < 0) return this;
    return new ListValue(vec.remove((int)idx));
  }

  public ListValue take(int n) {
    return new ListValue(vec.slice(0, Math.min(n, vec.size())));
  }

  public ListValue drop(int n) {
    if (n == 0) return this;
    return new ListValue(vec.slice(Math.min(n, vec.size()), vec.size()));
  }

  public ListValue init() {
    if (vec.size() == 0){
      return this;
    }
    if (vec.size() == 1){
      return new ListValue(TrieList.empty());
    }
    return new ListValue(vec.slice(0, vec.size()-1));
  }

  public ListValue tail() {
    if (vec.size() == 0){
      return this;
    }
    if (vec.size() == 1){
      return new ListValue(TrieList.empty());
    }
    return new ListValue(vec.slice(1, vec.size()));
  }

  public ListValue reverse() {

    if (vec.isEmpty()) return this;

    TrieList ret = TrieList.empty();
    for(Iterator iterator = vec.reverseIterator(); iterator.hasNext();){
      ret = ret.add(iterator.next());
    }
    return new ListValue(ret);
  }

  public Value head() {
    return (Value) vec.get(0);
  }

  public Value last() {
    return (Value) vec.get(vec.size()-1);
  }

  public ListValue slice(int startIndex, int endIndex) {
    return new ListValue(vec.slice(startIndex, endIndex));
  }

  public Value indexOf(Value x) {
    return Values.make(vec.indexOf(x));
  }

  public Value indexOf(Value x, long from) {
    if (from > Integer.MAX_VALUE) return Values.LONG_NEG_ONE;
    return Values.make(vec.indexOf(x, Math.max((int) from, 0)));
  }
  
  public Value lastIndexOf(Value x) {
    return Values.make(vec.lastIndexOf(x));
  }

  public Value lastIndexOf(Value x, long end) {
    if (end >= Integer.MAX_VALUE) return Values.LONG_NEG_ONE;
    return Values.make(vec.lastIndexOf(x, Math.min(vec.size()-1, (int)end)));
  }

  public int size() {
    return vec.size();
  }

  public boolean containsValue(Value value) {
    return vec.contains(value);
  }

  @SuppressWarnings("unchecked")
  public ListValue sort(Comparator comparator){
    Object[] values = vec.toArray();
    Arrays.sort(values, comparator);
    return new ListValue(values);
  }

  public Iterator<Value> iterator() {

    // basic iterator implementation that allows basic traversal only
    Iterator internalIterator = vec.iterator();

    return new Iterator<Value>(){
      
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      public Value next() {
        Value next = (Value) internalIterator.next();
        if (next == null) return Values.NIL;
        return next;
      }
    };
  }

  public Value[] toArray(){
    Value[] ret = new Value[size()];

    int i=0;
    for(Value item: this){
      ret[i++] = item;
    }
    return ret;
  }

  public List<Value> toList(){
    ArrayList<Value> ret = new ArrayList<>(size());
    for(Value item: this){
      ret.add(item);
    }
    return ret;
  }

  public boolean equals(Object o) {

    if (this == o) return true;
    if (o == null) return false;

    if (!(o instanceof ListValue)) return false;
    ListValue values = (ListValue) o;
    return vec.equals(values.vec);

  }

  public int hashCode() {
    return vec.hashCode();
  }
}
