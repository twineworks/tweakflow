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

package com.twineworks.tweakflow.std;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

import java.util.*;

public final class Data {

  // function get_in: (xs, list keys, not_found=nil)
  public static final class getIn implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value keys, Value notFound) {

      if (xs.isNil()) return Values.NIL;
      if (keys.isNil()) return Values.NIL;
      ListValue keysList = keys.list();

      Value current = xs;

      for (Value index : keysList) {

        // map navigation
        if (current.type() == Types.DICT){
          DictValue map = current.dict();
          Value key = index.castTo(Types.STRING);
          if (key.isNil()){
            return Values.NIL;
          }

          String strKey = key.string();

          if (map.containsKey(strKey)){
            current = map.get(strKey);
            if (current == Values.NIL) return Values.NIL;
          }
          else{
            return notFound;
          }

        }

        // list navigation
        else if(current.type() == Types.LIST){

          ListValue list = current.list();
          Value idx = index.castTo(Types.LONG);
          if (idx.isNil()){
            return Values.NIL;
          }

          Long idxLong = idx.longNum();
          if (idxLong < 0 || idxLong >= list.size()){
            return notFound;
          }

          Value next = list.get(idxLong.intValue());

          if (next == Values.NIL){
            return Values.NIL;
          }
          else{
            current = next;
          }

        }
        else {
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "get_in is not defined for type "+current.type().name());
        }
      }

      return current;
    }
  }

  // function select: (xs, list keys, not_found=nil)
  public static final class select implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value keys, Value notFound) {
      if (xs.isNil()) return Values.NIL;
      if (keys.isNil()) return Values.NIL;
      ListValue keysList = keys.list();

      // map version
      if (xs.type() == Types.DICT) {
        DictValue map = xs.dict();
        HashMap<String, Value> items = new HashMap<>();

        for (Value keyValue : keysList) {
          String k = keyValue.castTo(Types.STRING).string();

          if (k == null) {
            throw new LangException(LangError.NIL_ERROR, "cannot select nil key");
          }

          if (map.containsKey(k)) {
            items.put(k, map.get(k));
          } else {
            items.put(k, notFound);
          }

        }

        return Values.make(new DictValue(items));

      }

      // list version
      else if (xs.type() == Types.LIST) {

        ListValue list = xs.list();
        ArrayList<Value> items = new ArrayList<>(list.size());
        for (Value keyValue : keysList) {
          Long idxLong = keyValue.castTo(Types.LONG).longNum();
          if (idxLong == null) {
            throw new LangException(LangError.NIL_ERROR, "cannot select nil key");
          }
          if (idxLong < 0 || idxLong >= list.size()) {
            items.add(notFound);
          } else {
            items.add(list.get(idxLong.intValue()));
          }
        }
        return Values.make(new ListValue(items));
      } else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "select is not defined for type " + xs.type().name());
      }
    }
  }

  // function put_in: (xs, list keys, value)
  public static final class putIn implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value keys, Value value) {

      if (xs.isNil()) return Values.NIL;

      if (keys.isNil()){
        throw new LangException(LangError.NIL_ERROR, "nil keys not supported");
      }

      ListValue keysList = keys.list();
      int keyCount = keysList.size();

      // empty sequence returns thing to set
      if (keysList.size() == 0) return value;

      Value current = xs;
      ArrayList<Value> castKeys = new ArrayList<>(keysList.size());
      ArrayList<Value> traversed = new ArrayList<>(keysList.size());

      for (int i=0; i < keyCount; i++) {

        Value index = keysList.get(i);

        // need to create an intermediate
        if (current == Values.NIL){
          if (index.isLongNum()){
            current = Values.makeList();
          }
          else{
            current = Values.makeDict();
          }
        }

        traversed.add(current);

        // map navigation
        if (current.type() == Types.DICT){
          DictValue map = current.dict();
          Value key = index.castTo(Types.STRING);

          if (key.isNil()){
            throw new LangException(LangError.NIL_ERROR, "nil keys not supported");
          }

          castKeys.add(key);
          current = map.get(key.string());

        }
        else if (current.type() == Types.LIST){
          ListValue list = current.list();
          Value key = index.castTo(Types.LONG);
          if (key.isNil()){
            throw new LangException(LangError.NIL_ERROR, "nil keys not supported");
          }
          castKeys.add(key);
          current = list.get(key.longNum().intValue());
        }
        else {
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot navigate into type "+current.type().name());
        }
      }

      // add the to be placed at the key in
      traversed.add(value);

      // we now have:
      // cast: {:characters {:main {:name "Sherlock Holmes"} :side {:name "Dr. Watson"}}}
      // put_in(cast, [:characters :side :name], "Joan Watson")
      //
      // traversed = [cast,       cast[:characters] cast[:characters :side], "Joan Watson"]
      // keys      = [:characters :side             :name]

      // traversed contains all values that need to be stitched back together
      for (int j=keyCount;j > 0;j--){
        Value v = traversed.get(j);
        Value c = traversed.get(j-1);
        Value key = castKeys.get(j-1);

        if (c.isDict()){
          DictValue cDict = c.dict().put(key.string(), v);
          traversed.set(j-1, Values.make(cDict));
        }
        else {
          ListValue cList = c.list().set(key.longNum().intValue(), v);
          traversed.set(j-1, Values.make(cList));
        }
      }

      return traversed.get(0);
    }
  }

  // function update_in: (xs, list keys, function f)
  public static final class updateIn implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value keys, Value f) {

      if (xs.isNil()) return Values.NIL;

      if (keys.isNil()){
        throw new LangException(LangError.NIL_ERROR, "nil keys not supported");
      }

      ListValue keysList = keys.list();
      int keyCount = keysList.size();

      if (f.isNil()){
        throw new LangException(LangError.NIL_ERROR, "f cannot be nil");
      }

      // empty sequence operates main item
      if (keysList.size() == 0){
        return context.call(f, xs);
      }

      Value current = xs;
      ArrayList<Value> castKeys = new ArrayList<>(keysList.size());
      ArrayList<Value> traversed = new ArrayList<>(keysList.size());

      for (int i=0; i < keyCount; i++) {

        Value index = keysList.get(i);

        // need to create an intermediate
        if (current == Values.NIL){
          if (index.isLongNum()){
            current = Values.makeList();
          }
          else{
            current = Values.makeDict();
          }
        }

        traversed.add(current);

        // map navigation
        if (current.type() == Types.DICT){
          DictValue map = current.dict();
          Value key = index.castTo(Types.STRING);

          if (key.isNil()){
            throw new LangException(LangError.NIL_ERROR, "nil keys not supported");
          }

          castKeys.add(key);
          current = map.get(key.string());

        }
        else if (current.type() == Types.LIST){
          ListValue list = current.list();
          Value key = index.castTo(Types.LONG);
          if (key.isNil()){
            throw new LangException(LangError.NIL_ERROR, "nil keys not supported");
          }
          castKeys.add(key);
          current = list.get(key.longNum().intValue());
        }
        else {
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot navigate into type "+current.type().name());
        }
      }

      // add the last item transformed
      traversed.add(context.call(f, current));

      // we now have:
      // cast: {:characters {:main {:name "Sherlock Holmes"} :side {:name "Dr. Watson"}}}
      // update_in(cast, [:characters :side :name], (x) -> "Dr. Joan Watson")
      //
      // traversed = [cast,       cast[:characters] cast[:characters :side], "Dr. Joan Watson"]
      // keys      = [:characters :side             :name]

      // traversed contains all values that need to be stitched back together
      for (int j=keyCount;j > 0;j--){
        Value v = traversed.get(j);
        Value c = traversed.get(j-1);
        Value key = castKeys.get(j-1);

        if (c.isDict()){
          DictValue cDict = c.dict().put(key.string(), v);
          traversed.set(j-1, Values.make(cDict));
        }
        else {
          ListValue cList = c.list().set(key.longNum().intValue(), v);
          traversed.set(j-1, Values.make(cList));
        }
      }

      return traversed.get(0);

    }
  }

  // function get: (xs, key, not_found=nil)
  public static final class get implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value key, Value notFound) {

      if (xs.isNil()) return Values.NIL;
      if (key.isNil()) return Values.NIL;

      // map navigation
      if (xs.type() == Types.DICT) {
        DictValue map = xs.dict();
        Value k = key.castTo(Types.STRING);
        if (k.isNil()) {
          return notFound;
        }

        String strKey = k.string();
        if (notFound == Values.NIL){
          return map.get(strKey);
        }
        else{
          if (map.containsKey(strKey)){
            return map.get(strKey);
          }
          else{
            return notFound;
          }
        }

      }

      // list navigation
      else if(xs.type() == Types.LIST){

        ListValue list = xs.list();
        Value idx = key.castTo(Types.LONG);
        if (idx.isNil()){
          return notFound;
        }

        int index = idx.longNum().intValue();
        if (notFound == Values.NIL){
          return list.get(index);
        }
        else {
          if (index < 0 || index >= list.size()){
            return notFound;
          }
          else{
            return list.get(index);
          }
        }

      }
      else{
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "get is not defined for type "+xs.type().name());
      }

    }
  }

  // function contains: (xs, x)
  public static final class contains implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value x) {
      if (xs.isNil()) return Values.NIL;

      // map navigation
      if (xs.type() == Types.DICT) {
        DictValue map = xs.dict();
        return map.containsValue(x) ? Values.TRUE : Values.FALSE;
      }

      // list navigation
      else if(xs.type() == Types.LIST){
        ListValue list = xs.list();
        return list.containsValue(x) ? Values.TRUE : Values.FALSE;
      }
      else{
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "contains? is not defined for type "+xs.type().name());
      }
    }
  }

  // function put: (xs, key, value)
  public static final class put implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value key, Value value) {

      if (xs.isNil()) return Values.NIL;

      if (key.isNil()){
        throw new LangException(LangError.NIL_ERROR, "cannot set nil key");
      }

      // map navigation
      if (xs.type() == Types.DICT) {
        DictValue map = xs.dict();
        Value k = key.castTo(Types.STRING);
        return Values.make(map.put(k.string(), value));
      }

      // list navigation
      else if(xs.type() == Types.LIST){

        ListValue list = xs.list();
        Long idx = key.castTo(Types.LONG).longNum();

        // need to be sure index is within bounds
        if (idx < 0 || idx > Integer.MAX_VALUE) {
          throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "cannot set index "+idx);
        }

        return Values.make(list.set(idx.intValue(), value));

      }
      else{
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "put is not defined for type "+xs.type().name());
      }
    }
  }

  // function delete: (xs, key)
  public static final class delete implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value key) {
      if (xs.isNil()) return Values.NIL;

      // map navigation
      if (xs.type() == Types.DICT) {

        if (key.isNil()){
          return xs;
        }

        DictValue map = xs.dict();
        Value k = key.castTo(Types.STRING);
        return Values.make(map.delete(k.string()));
      }

      // list navigation
      else if(xs.type() == Types.LIST){

        if (key.isNil()){
          return xs;
        }

        ListValue list = xs.list();
        Long idx = key.castTo(Types.LONG).longNum();

        // need to be sure index is within bounds
        if (idx < 0 || idx > Integer.MAX_VALUE) {
          throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "cannot delete index "+idx);
        }

        return Values.make(list.delete(idx.intValue()));

      }
      else{
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "delete is not defined for type "+xs.type().name());
      }
    }
  }

  // function insert: (list xs, long i, v)
  public static final class insert implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value i, Value v) {

      if (xs.isNil()) return Values.NIL;

      if (i.isNil()){
        throw new LangException(LangError.NIL_ERROR, "cannot insert at nil index");
      }

      ListValue list = xs.list();
      Long idx = i.castTo(Types.LONG).longNum();

      // need to be sure index is within bounds
      if (idx < 0 || idx > Integer.MAX_VALUE) {
        throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "cannot insert at index "+idx);
      }

      return Values.make(list.insert(idx.intValue(), v));

    }
  }

  // function update: (xs, key, function f)
  public static final class update implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value key, Value f) {

      if (xs.isNil()) return Values.NIL;

      if (key.isNil()){
        throw new LangException(LangError.NIL_ERROR, "cannot update nil key");
      }

      if (f.isNil()){
        throw new LangException(LangError.NIL_ERROR, "update function cannot be nil");
      }

      // map navigation
      if (xs.type() == Types.DICT) {
        DictValue map = xs.dict();
        String k = key.castTo(Types.STRING).string();
        Value v = map.get(k);
        return Values.make(map.put(k, context.call(f, v)));
      }

      // list navigation
      else if(xs.type() == Types.LIST){

        ListValue list = xs.list();
        Long idx = key.castTo(Types.LONG).longNum();

        // need to be sure index is within bounds
        if (idx < 0 || idx > Integer.MAX_VALUE) {
          throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "cannot update index "+idx);
        }

        Value v = list.get(idx.intValue());
        return Values.make(list.set(idx.intValue(), context.call(f, v)));

      }
      else{
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "update is not defined for type "+xs.type().name());
      }

    }
  }

  // function concat: (list lists)
  public static final class concat implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value lists) {
      ListValue listsList = lists.list();

      if (listsList == null){
        return Values.NIL;
      }

      ListValue result = new ListValue();

      for (Value list : listsList) {
        if (list.isList()){
          ListValue listValue = list.list();
          result = result.appendAll(listValue);
        }
        else if (list.isNil()){ // trying to concat with nil results in nil
          return Values.NIL;
        }
        else{
          // trying to concat a non-list
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot concat type "+list.type().name());
        }
      }

      return Values.make(result);
    }
  }

  // function append: (list xs, x)
  public static final class append implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value x) {

      if (xs == Values.NIL){
        return Values.NIL;
      }

      return Values.make(xs.list().append(x));
    }
  }

  // function prepend: (x, list xs)
  public static final class prepend implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value xs) {

      if (xs == Values.NIL){
        return Values.NIL;
      }

      return Values.make(xs.list().prepend(x));
    }
  }

  // function take: (long n, list xs)
  public static final class take implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value n, Value xs) {
      if (n.isNil()) return Values.NIL;
      if (xs.isNil()) return Values.NIL;
      ListValue list = xs.list();

      long num = n.longNum();
      if (num < 0) return Values.EMPTY_LIST;
      if (num > list.size()) return xs;

      return Values.make(list.take((int) num));
    }
  }

  // function drop: (long n, list xs)
  public static final class drop implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value n, Value xs) {
      if (n.isNil()) return Values.NIL;
      if (xs.isNil()) return Values.NIL;

      long num = n.longNum();
      ListValue list = xs.list();
      if (num >= list.size()) return Values.EMPTY_LIST;
      if (num <= 0) return xs;
      return Values.make(list.drop((int)num));
    }
  }

  // function init: (list xs) -> list
  public static final class init implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xss) {

      ListValue xs = xss.list();
      if (xs == null){
        return Values.NIL;
      }

      if (xs.isEmpty()) throw new LangException(LangError.ILLEGAL_ARGUMENT, "list must not be empty");

      return Values.make(xs.init());
    }
  }

  // function reverse: (list xs) -> list
  public static final class reverse implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xss) {

      ListValue xs = xss.list();

      if (xs == null){
        return Values.NIL;
      }

      return Values.make(xs.reverse());
    }
  }

  // function index_of: (list xs, x, long start=0) -> list
  public static final class indexOf implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value x, Value start) {

      ListValue xsList = xs.list();
      Long startLong = start.longNum();

      if (xsList == null){
        return Values.NIL;
      }

      if (startLong == null){
        return Values.NIL;
      }

      if (startLong >= xsList.size()) return Values.LONG_NEG_ONE;

      return Values.make(xsList.indexOf(x, startLong));
    }
  }

  // function key_of: (dict xs, x) -> string
  public static final class keyOf implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value x) {

      if (xs == Values.NIL){
        return Values.NIL;
      }

      DictValue dict = xs.dict();
      for (String s : dict.keys()) {
        if (dict.get(s).equals(x)){
          return Values.make(s);
        }
      }

      return Values.NIL;
    }
  }

  // function last_index_of: (list xs, x, long end) -> list
  public static final class lastIndexOf implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value x, Value end) {
      ListValue xsList = xs.list();

      Long endLong = end.longNum();

      if (xsList == null){
        return Values.NIL;
      }

      int endInt;
      if (endLong == null){
        endInt = xsList.size();
      }
      else{
        if (endLong < 0) return Values.LONG_NEG_ONE;
        endInt = (int) java.lang.Math.min(endLong, xsList.size());
      }

      return Values.make(xsList.lastIndexOf(x, endInt));
    }
  }

  // function repeat: (long n=0, x) -> list
  public static final class repeat implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value n, Value x) {

      if (n == Values.NIL) return Values.NIL;

      long num = n.longNum();
      if (num < 0 || num > Integer.MAX_VALUE) throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "Cannot repeat "+num+" times");

      return Values.make(new ListValue().padTo(n.longNum().intValue(), x));

    }
  }

  // function merge: (list maps)
  public static final class merge implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value dicts) {

      ListValue dictList = dicts.list();

      if (dictList == null){
        return Values.NIL;
      }

      DictValue result = new DictValue();

      for (Value dict : dictList) {
        if (dict.isDict()){
          DictValue dictValue = dict.dict();
          result = result.putAll(dictValue);
        }
        else if (dict.isNil()){ // trying to merge with nil
          return Values.NIL;
        }
        else{
          // trying to merge a non-dict
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "cannot merge type "+dict.type().name());
        }
      }

      return Values.make(result);
    }
  }

  // function filter: (xs, function p)
  public static final class filter implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value p) {

      if (xs.isNil()) return Values.NIL;

      if (p.isNil()) throw new LangException(LangError.NIL_ERROR, "predicate function cannot be nil");

      if (xs.isList()){

        int paramCount = p.function().getSignature().getParameterList().size();
        if (paramCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "predicate function must accept at least one argument");

        boolean withIndex = paramCount >= 2;
        ListValue retVal = new ListValue();
        ListValue list = xs.list();

        if (withIndex){
          Arity2CallSite pcs = context.createArity2CallSite(p);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Value x = list.get(i);
            if (pcs.call(x, Values.make(i)).castTo(Types.BOOLEAN) == Values.TRUE){
              retVal = retVal.append(x);
            }
          }
        }
        else{
          Arity1CallSite pcs = context.createArity1CallSite(p);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Value x = list.get(i);
            if (pcs.call(x).castTo(Types.BOOLEAN) == Values.TRUE){
              retVal = retVal.append(x);
            }
          }

        }

        return Values.make(retVal);
      }
      else if (xs.isDict()){

        int paramCount = p.function().getSignature().getParameterList().size();
        if (paramCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "predicate function must accept at least one argument");

        boolean withKey = paramCount >= 2;
        DictValue retVal = new DictValue();

        DictValue map = xs.dict();
        if (withKey){
          Arity2CallSite pcs = context.createArity2CallSite(p);
          for (String key : map.keys()) {
            Value x = map.get(key);
            if (pcs.call(x, Values.make(key)).castTo(Types.BOOLEAN) == Values.TRUE){
              retVal = retVal.put(key, x);
            }
          }
        }
        else{
          Arity1CallSite pcs = context.createArity1CallSite(p);
          for (String key : map.keys()) {
            Value x = map.get(key);
            if (pcs.call(x).castTo(Types.BOOLEAN) == Values.TRUE){
              retVal = retVal.put(key, x);
            }
          }
        }

        return Values.make(retVal);

      }
      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "filter is not defined for type "+xs.type().name());
      }

    }
  }

  // function find: (list xs, function p)
  public static final class find implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value p) {

      if (xs == Values.NIL) return Values.NIL;
      if (p == Values.NIL) throw new LangException(LangError.NIL_ERROR, "predicate function cannot be nil");

      int paramCount = p.function().getSignature().getParameterList().size();
      if (paramCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "predicate function must accept at least one argument");

      boolean withIndex = paramCount > 1;

      ListValue list = xs.list();

      if (withIndex){
        Arity2CallSite pcs = context.createArity2CallSite(p);
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          if (pcs.call(x, Values.make(i)).castTo(Types.BOOLEAN) == Values.TRUE) {
            return x;
          }
        }
      }
      else {
        Arity1CallSite pcs = context.createArity1CallSite(p);
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          if (pcs.call(x).castTo(Types.BOOLEAN) == Values.TRUE) {
            return x;
          }
        }
      }

      return Values.NIL;
    }
  }

  // function find_index: (list xs, function p) -> long
  public static final class findIndex implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value p) {


      if (xs.isNil()) return Values.NIL;

      if (p == Values.NIL) throw new LangException(LangError.NIL_ERROR, "predicate function cannot be nil");

      int paramCount = p.function().getSignature().getParameterList().size();
      if (paramCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "predicate function must accept at least one argument");

      boolean withIndex = paramCount > 1;

      ListValue list = xs.list();

      if (withIndex){
        Arity2CallSite pcs = context.createArity2CallSite(p);
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          Value result = pcs.call(x, Values.make(i)).castTo(Types.BOOLEAN);
          if (result == Values.TRUE) {
            return Values.make(i);
          }
        }
      }
      else{
        Arity1CallSite pcs = context.createArity1CallSite(p);
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          Value result = pcs.call(x).castTo(Types.BOOLEAN);

          if (result == Values.TRUE) {
            return Values.make(i);
          }
        }

      }

      return Values.NIL;
    }
  }

  // function shuffle: (list xs, seed)
  public static final class shuffle implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value seed) {

      int seedInt = seed.hashCode();

      if (xs == Values.NIL) return Values.NIL;
      ListValue list = xs.list();
      if (list.size() < 2) return xs;

      Random r = new Random(seedInt);

      for(int i=0, size = list.size();i < size; i++){
        Value item = list.get(i);
        int swapIdx = r.nextInt(size);
        Value swapItem = list.get(swapIdx);
        list = list.set(i, swapItem);
        list = list.set(swapIdx, item);
      }
      return Values.make(list);

    }

  }

  // function unique: (list xs) -> list
  public static final class unique implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {

      if (xs.isNil()) return Values.NIL;
      ListValue list = xs.list();
      if (list.size() < 2) return xs;

      // put everything into a linked hash set, to retrieve unique results in order
      LinkedHashSet<Value> set = new LinkedHashSet<>(list.size());

      for (Value value : list) {
        set.add(value);
      }
      return Values.make(new ListValue(set));

    }
  }

  // function head: (list xs)
  public static final class head implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {
      if (xs.isNil()) return Values.NIL;
      ListValue list = xs.list();
      if (list.size() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "list must not be empty");
      return Values.make(list.head());
    }
  }

  // function last: (list xs)
  public static final class last implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {

      if (xs.isNil()) return Values.NIL;
      ListValue list = xs.list();
      if (list.size() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "list must not be empty");
      return Values.make(list.last());
    }
  }

  // function tail: (list xs)
  public static final class tail implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {

      if (xs.isNil()) return Values.NIL;
      ListValue list = xs.list();
      if (list.size() == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "list must not be empty");
      return Values.make(list.tail());
    }
  }

  // function slice: (list xs, long start=0, long end) -> list
  public static final class slice implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value start, Value end) {

      if (xs.isNil()) return Values.NIL;
      if (start.isNil()) throw new LangException(LangError.NIL_ERROR, "Cannot slice starting at nil");
      if (start.longNum() < 0) throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "Cannot slice starting at: "+start.longNum());

      ListValue list = xs.list();
      int size = list.size();
      long startIndex = start.longNum();
      long endIndex = end.isNil() ? size : end.longNum();

      if (endIndex <= startIndex) return Values.EMPTY_LIST;
      if (startIndex >= size) return Values.EMPTY_LIST;

      if (endIndex >= size) endIndex = size;
      if (startIndex == 0L && endIndex >= size) return xs;

      return Values.make(list.slice((int)startIndex, (int)endIndex));

    }
  }

  // function entries: (dict xs) -> list
  public static final class entries implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {

      if (xs.isNil()) return Values.NIL;
      DictValue dict = xs.dict();
      if (dict.isEmpty()) return Values.EMPTY_LIST;

      ListValue listValue = new ListValue();
      for (String k : dict.keys()) {
        listValue = listValue.append(Values.makeDict("key", k, "value", dict.get(k)));
      }

      return Values.make(listValue);
    }
  }

  // function any?: (list xs, function p) -> boolean
  public static final class any implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value p) {

      if (xs == Values.NIL) return Values.NIL;

      if (p == Values.NIL) throw new LangException(LangError.NIL_ERROR, "predicate function cannot be nil");

      int paramCount = p.function().getSignature().getParameterList().size();
      if (paramCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "predicate function must accept at least one argument");

      boolean withIndex = paramCount > 1;

      if (withIndex){
        Arity2CallSite pcs = context.createArity2CallSite(p);
        ListValue list = xs.list();
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          Boolean satisfied = pcs.call(x, Values.make(i)).castTo(Types.BOOLEAN).bool();
          if (satisfied) return Values.TRUE;
        }
      }
      else{
        Arity1CallSite pcs = context.createArity1CallSite(p);
        ListValue list = xs.list();
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          Boolean satisfied = pcs.call(x).castTo(Types.BOOLEAN).bool();
          if (satisfied) return Values.TRUE;
        }
      }

      return Values.FALSE;
    }
  }

  // function all?: (list xs, function p) -> boolean
  public static final class all implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value p) {

      if (xs == Values.NIL) return Values.NIL;

      if (p == Values.NIL) throw new LangException(LangError.NIL_ERROR, "predicate function cannot be nil");

      int paramCount = p.function().getSignature().getParameterList().size();
      if (paramCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "predicate function must accept at least one argument");

      boolean withIndex = paramCount > 1;

      if (withIndex){
        Arity2CallSite pcs = context.createArity2CallSite(p);

        ListValue list = xs.list();
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          Boolean satisfied = pcs.call(x, Values.make(i)).castTo(Types.BOOLEAN).bool();
          if (!satisfied) return Values.FALSE;
        }
      }
      else{
        Arity1CallSite pcs = context.createArity1CallSite(p);

        ListValue list = xs.list();
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          Boolean satisfied = pcs.call(x).castTo(Types.BOOLEAN).bool();
          if (!satisfied) return Values.FALSE;
        }
      }

      return Values.TRUE;
    }
  }

  // function none?: (list xs, function p) -> boolean
  public static final class none implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value p) {

      if (xs == Values.NIL) return Values.NIL;

      if (p == Values.NIL) throw new LangException(LangError.NIL_ERROR, "predicate function cannot be nil");

      int paramCount = p.function().getSignature().getParameterList().size();
      if (paramCount == 0) throw new LangException(LangError.ILLEGAL_ARGUMENT, "predicate function must accept at least one argument");

      boolean withIndex = paramCount > 1;

      if (withIndex){
        ListValue list = xs.list();
        Arity2CallSite pcs = context.createArity2CallSite(p);

        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          Boolean satisfied = pcs.call(x, Values.make(i)).castTo(Types.BOOLEAN).bool();
          if (satisfied) return Values.FALSE;
        }
      }
      else{
        ListValue list = xs.list();
        Arity1CallSite pcs = context.createArity1CallSite(p);

        for (int i = 0, listSize = list.size(); i < listSize; i++) {
          Value x = list.get(i);
          Boolean satisfied = pcs.call(x).castTo(Types.BOOLEAN).bool();
          if (satisfied) return Values.FALSE;
        }
      }

      return Values.TRUE;
    }
  }

  // function map: (xs, function f)
  public static final class map implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value f) {

      if (f == Values.NIL) return Values.NIL;
      if (xs == Values.NIL) return Values.NIL;

      if (xs.isList()){

        boolean withIndex = f.function().getSignature().getParameterList().size() >= 2;
        ListValue retVal = new ListValue();

        ListValue list = xs.list();

        if (withIndex){
          Arity2CallSite fcs = context.createArity2CallSite(f);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Value x = list.get(i);
            Value mapped = fcs.call(x, Values.make(i));
            retVal = retVal.append(mapped);
          }
        }
        else{
          Arity1CallSite fcs = context.createArity1CallSite(f);

          for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Value x = list.get(i);
            Value mapped = fcs.call(x);
            retVal = retVal.append(mapped);
          }
        }

        return Values.make(retVal);
      }
      else if (xs.isDict()){

        boolean withKey = f.function().getSignature().getParameterList().size() >= 2;
        DictValue retVal = new DictValue();

        DictValue map = xs.dict();
        if (withKey){
          Arity2CallSite fcs = context.createArity2CallSite(f);
          for (String key : map.keys()) {
            Value x = map.get(key);
            Value mapped = fcs.call(x, Values.make(key));
            retVal = retVal.put(key, mapped);
          }

        }
        else{

          for (String key : map.keys()) {
            Arity1CallSite fcs = context.createArity1CallSite(f);
            Value x = map.get(key);
            Value mapped = fcs.call(x);
            retVal = retVal.put(key, mapped);
          }

        }

        return Values.make(retVal);

      }
      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "map is not defined for type "+xs.type().name());
      }

    }
  }

  // function has?: (xs, key)
  public static final class has implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value key) {

      if (xs == Values.NIL) return Values.NIL;
      if (key == Values.NIL) return Values.FALSE;

      if (xs.isDict()){
        DictValue dictValue = xs.dict();
        String strKey = key.castTo(Types.STRING).string();
        if (dictValue.containsKey(strKey)){
          return Values.TRUE;
        }
        else{
          return Values.FALSE;
        }
      }
      else if (xs.isList()){
        ListValue listValue = xs.list();
        Long idx = key.castTo(Types.LONG).longNum();
        if (idx >= 0 && idx < listValue.size()){
          return Values.TRUE;
        }
        else{
          return Values.FALSE;
        }
      }
      else{
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "has? is not defined for type "+xs.type().name());
      }

    }

  }

  // function keys: (xs) -> list
  public static final class keys implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {
      if (xs.isNil()) return Values.NIL;

      if (xs.isDict()){
        DictValue dictValue = xs.dict();
        return Values.makeList(dictValue.keys());
      }
      else if (xs.isList()){
        ListValue listValue = xs.list();
        if (listValue.size() == 0){
          return Values.makeList();
        }
        else{
          return Values.makeRange(0, listValue.size()-1);
        }

      }
      else throw new LangException(LangError.ILLEGAL_ARGUMENT, "keys is not defined for type "+xs.type().name());

    }
  }

  // function values: (xs) -> list
  public static final class values implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {
      if (xs.isNil()) return Values.NIL;

      if (xs.isDict()){
        DictValue dictValue = xs.dict();
        return Values.makeList(dictValue.values());
      }
      else if (xs.isList()){
        return xs;
      }
      else throw new LangException(LangError.ILLEGAL_ARGUMENT, "values is not defined for type "+xs.type().name());

    }
  }

  // function reduce: (xs, init, function f)
  public static final class reduce implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value init, Value f) {

      if (f.isNil()) return Values.NIL;
      if (xs.isNil()) return Values.NIL;

      Value ret = init;

      if (xs.isList()){

        boolean withIndex = f.function().getSignature().getParameterList().size() >= 3;

        ListValue list = xs.list();

        if (withIndex){
          Arity3CallSite fcs = context.createArity3CallSite(f);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Value x = list.get(i);
            ret = fcs.call(ret, x, Values.make(i));
          }

        }
        else{
          Arity2CallSite fcs = context.createArity2CallSite(f);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Value x = list.get(i);
            ret = fcs.call(ret, x);
          }
        }

        return ret;
      }
      else if (xs.isDict()){

        boolean withKey = f.function().getSignature().getParameterList().size() >= 3;

        DictValue map = xs.dict();

        if (withKey){
          Arity3CallSite fcs = context.createArity3CallSite(f);
          for (String key : map.keys()) {
            Value x = map.get(key);
            ret = fcs.call(ret, x, Values.make(key));
          }
        }
        else{
          Arity2CallSite fcs = context.createArity2CallSite(f);
          for (Value x : map.values()) {
            ret = fcs.call(ret, x);
          }
        }
        return ret;

      }
      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "reduce is not defined for type "+xs.type().name());
      }
    }
  }

  // function reduce_until: (xs, init, function p, function f)
  public static final class reduce_until implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value init, Value p, Value f) {

      if (f.isNil()) return Values.NIL;
      if (p.isNil()) return Values.NIL;
      if (xs.isNil()) return Values.NIL;

      Value ret = init;
      Arity1CallSite pcs = context.createArity1CallSite(p);

      if (!xs.isList() && !xs.isDict()){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "reduce_until is not defined for type "+xs.type().name());
      }

      boolean done = pcs.call(ret).castTo(Types.BOOLEAN).bool();
      if (done) return ret;

      if (xs.isList()){

        boolean withIndex = f.function().getSignature().getParameterList().size() >= 3;
        ListValue list = xs.list();

        if (withIndex){
          Arity3CallSite fcs = context.createArity3CallSite(f);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Value x = list.get(i);
            ret = fcs.call(ret, x, Values.make(i));
            done = pcs.call(ret).castTo(Types.BOOLEAN).bool();
            if (done) return ret;
          }
        }
        else{
          Arity2CallSite fcs = context.createArity2CallSite(f);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Value x = list.get(i);
            ret = fcs.call(ret, x);
            done = pcs.call(ret).castTo(Types.BOOLEAN).bool();
            if (done) return ret;
          }
        }


        return ret;
      }
      else if (xs.isDict()){

        boolean withKey = f.function().getSignature().getParameterList().size() >= 3;
        DictValue map = xs.dict();

        if (withKey){
          Arity3CallSite fcs = context.createArity3CallSite(f);
          for (String key : map.keys()) {
            Value x = map.get(key);
            ret = fcs.call(ret, x, Values.make(key));
            done = pcs.call(ret).castTo(Types.BOOLEAN).bool();
            if (done) return ret;
          }
        }
        else{
          Arity2CallSite fcs = context.createArity2CallSite(f);
          for (Value x : map.values()) {
            ret = fcs.call(ret, x);
            done = pcs.call(ret).castTo(Types.BOOLEAN).bool();
            if (done) return ret;
          }
        }
        return ret;

      }
      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "reduce_until is not defined for type "+xs.type().name());
      }

    }
  }

  // function reduce_while: (xs, init, function p, function f)
  public static final class reduce_while implements UserFunction, Arity4UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value init, Value p, Value f) {

      if (f.isNil()) return Values.NIL;
      if (p.isNil()) return Values.NIL;
      if (xs.isNil()) return Values.NIL;

      Value ret = init;
      boolean keepGoing;

      Arity1CallSite pcs = context.createArity1CallSite(p);
      if (xs.isList()){

        boolean withIndex = f.function().getSignature().getParameterList().size() >= 3;
        ListValue list = xs.list();

        if (withIndex){
          Arity3CallSite fcs = context.createArity3CallSite(f);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {

            keepGoing = pcs.call(ret).castTo(Types.BOOLEAN).bool();
            if (!keepGoing) return ret;

            Value x = list.get(i);
            ret = fcs.call(ret, x, Values.make(i));

          }
        }
        else{
          Arity2CallSite fcs = context.createArity2CallSite(f);
          for (int i = 0, listSize = list.size(); i < listSize; i++) {

            keepGoing = pcs.call(ret).castTo(Types.BOOLEAN).bool();
            if (!keepGoing) return ret;

            Value x = list.get(i);
            ret = fcs.call(ret, x);

          }
        }

        return ret;
      }
      else if (xs.isDict()){

        boolean withKey = f.function().getSignature().getParameterList().size() >= 3;
        DictValue map = xs.dict();

        if (withKey){
          Arity3CallSite fcs = context.createArity3CallSite(f);
          for (String key : map.keys()) {

            keepGoing = pcs.call(ret).castTo(Types.BOOLEAN).bool();
            if (!keepGoing) return ret;

            Value x = map.get(key);
            ret = fcs.call(ret, x, Values.make(key));

          }
        }
        else{
          Arity2CallSite fcs = context.createArity2CallSite(f);
          for (Value x: map.values()) {

            keepGoing = pcs.call(ret).castTo(Types.BOOLEAN).bool();
            if (!keepGoing) return ret;

            ret = fcs.call(ret, x);

          }
        }

        return ret;

      }
      else {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "reduce_while is not defined for type "+xs.type().name());
      }
    }
  }

  // function size: (xs) -> long
  public static final class size implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {

      if (xs.isNil()) return Values.NIL;

//      if (xs.isString()){
//        String s = xs.string();
//        return Values.make(s.codePointCount(0, s.length()));
//      }

      if (xs.isList()){
        return Values.make(xs.list().size());
      }

      if (xs.isDict()){
        return Values.make(xs.dict().size());
      }

      throw new LangException(LangError.ILLEGAL_ARGUMENT, "size is not defined for type "+xs.type().name());

    }
  }

  // function range: (long start, long end) -> long
  public static final class range implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value start, Value end) {

      if (start == Values.NIL) return Values.NIL;
      if (end == Values.NIL) return Values.NIL;

      return Values.makeRange(start.longNum(), end.longNum());
    }
  }

  // function empty?: (xs) -> boolean
  public static final class empty implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {

      if (xs.isNil()) return Values.NIL;

//      if (xs.isString()){
//        String s = xs.string();
//        if (s.isEmpty()){
//          return Values.TRUE;
//        }
//        else{
//          return Values.FALSE;
//        }
//
//      }

      if (xs.isList()){
        return xs.list().isEmpty() ? Values.TRUE : Values.FALSE;
      }

      if (xs.isDict()){
        return xs.dict().isEmpty() ? Values.TRUE : Values.FALSE;
      }

      throw new LangException(LangError.ILLEGAL_ARGUMENT, "empty? is not defined for type "+xs.type().name());

    }
  }

  public static final class sortComparator implements Comparator<Value> {

    private final Arity2CallSite f;

    public sortComparator(Arity2CallSite f) {
      this.f = f;
    }

    @Override
    public int compare(Value a, Value b) {
      Value v = f.call(a, b);

      if (v.isLongNum()){
        long longNum = v.longNum();
        if (longNum < 0) return -1;
        if (longNum > 0) return 1;
        return 0;
      }
      else  if (v.isDoubleNum()){
        double doubleNum = v.doubleNum();
        if (doubleNum < 0.0) return -1;
        if (doubleNum > 0.0) return 1;
        if (doubleNum == 0.0) return 0;
      }

      throw new LangException(LangError.ILLEGAL_ARGUMENT, "comparator function must return a number, but returned "+ValueInspector.inspect(v));
    }
  }

  // function sort: (list xs, function f) -> list
  public static final class sort implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value f) {

      if (xs == Values.NIL) return Values.NIL;
      if (f == Values.NIL) return Values.NIL;

      Comparator<Value> cmp = new sortComparator(context.createArity2CallSite(f));
      ListValue sorted = xs.list().sort(cmp);
      return Values.make(sorted);

    }
  }


}
