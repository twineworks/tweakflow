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

package com.twineworks.tweakflow.std;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class Strings {

  // (list xs) -> string
  public static final class concat implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {
      ListValue xsList = xs.list();
      if (xsList == null) return Values.NIL;

      StringBuilder b = new StringBuilder();
      for (Value x : xsList) {

        Value value = x.castTo(Types.STRING);

        if (value.isNil()){
          b.append("nil");
        }
        else{
          b.append(value.string());
        }

      }
      return Values.make(b.toString());
    }
  }

  // (string x) -> long
  public static final class length implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value arg) {
      String x = arg.string();
      if (x == null) return Values.NIL;
      return Values.make(x.codePointCount(0, x.length()));
    }
  }

  // (string x) -> string
  public static final class lowerCase implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      String str = x.string();
      if (str == null) return Values.NIL;
      return Values.make(str.toLowerCase());
    }
  }

  // (string x) -> string
  public static final class upperCase implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      String str = x.string();
      if (str == null) return Values.NIL;
      return Values.make(str.toUpperCase());
    }
  }

  // (string x) -> string
  public static final class trim implements UserFunction, Arity1UserFunction {

    int firstNonWhitespace(String s){

      int size = s.codePointCount(0, s.length());
      for(int i=0;i<size;i++){
        if (!Character.isWhitespace(s.codePointAt(i))){
          return i;
        }
      }
      return -1;
    }

    int lastNonWhitespace(String s){

      int size = s.codePointCount(0, s.length());
      for(int i=size-1;i>=0;i--){
        if (!Character.isWhitespace(s.codePointAt(i))){
          return i;
        }
      }
      return -1;
    }

    @Override
    public Value call(UserCallContext context, Value x) {
      String str = x.string();
      if (str == null) return Values.NIL;
      int first = firstNonWhitespace(str);
      if (first == -1) return Values.EMPTY_STRING;

      int last = lastNonWhitespace(str);
      if (last == -1){
        str = str.substring(str.offsetByCodePoints(0, first));
      }
      else{
        str = str.substring(str.offsetByCodePoints(0, first), str.offsetByCodePoints(0, last+1));
      }

      return Values.make(str);
    }
  }

  // (string x) -> list
  public static final class chars implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;
      return x.castTo(Types.LIST);
    }
  }

  // (string x) -> list
  public static final class codePoints implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x.isNil()) return Values.NIL;
      String s = x.string();
      int[] ints = s.codePoints().toArray();
      ListValue v = new ListValue();
      for (int i : ints) {
        v = v.append(Values.make(i));
      }
      return Values.make(v);

    }
  }

  // (list xs) -> string
  public static final class ofCodePoints implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs) {
      if (xs.isNil()) return Values.NIL;
      ListValue xsList = xs.list();
      if (xsList.isEmpty()) return Values.EMPTY_STRING;

      int[] codePoints = new int[xsList.size()];
      int i = 0;
      for (Value v : xsList) {

        if (v == Values.NIL) {
          throw new LangException(LangError.NIL_ERROR, "illegal nil code point at index: "+i);
        }
        if (!v.isLongNum()){
          v = v.castTo(Types.LONG);
        }
        try {
          int c = java.lang.Math.toIntExact(v.longNum());
          codePoints[i] = c;
        } catch (ArithmeticException e){
          throw LangException.wrap(e, LangError.ILLEGAL_ARGUMENT);
        }
        i++;
      }

      return Values.make(new String(codePoints, 0, codePoints.length));

    }
  }

  // function join: (list xs, string s="") -> string
  public static final class join implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value xs, Value s) {

      if (xs == Values.NIL) return Values.NIL;
      if (s == Values.NIL) return Values.NIL;

      ListValue list = xs.list();
      String str = s.string();

      StringBuilder b = new StringBuilder();
      for (int i = 0, size = list.size(); i < size; i++) {

        if (i > 0) b.append(str);

        Value x = list.get(i);
        Value value = x.castTo(Types.STRING);

        if (value.isNil()) {
          b.append("nil");
        } else {
          b.append(value.string());
        }

      }
      return Values.make(b.toString());
    }
  }

  // function split: (string x, string sep=" ") -> list
  public static final class split implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value sep) {

      if (x == Values.NIL) return Values.NIL;
      if (sep == Values.NIL) return Values.NIL;

      String str = x.string();
      if (str.isEmpty()) return Values.makeList(Values.EMPTY_STRING);

      if (!str.contains(sep.string())){
        return Values.makeList(str);
      }

      String separator = Pattern.quote(sep.string());

      String[] parts = str.split(separator, -1);
      return Values.makeList((Object[]) parts);

    }
  }

  // function lower_case: (string x, string lang="en-US") -> string
  public static final class lower_case implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value lang) {

      if (x == Values.NIL) return Values.NIL;
      if (lang == Values.NIL) return Values.NIL;

      String str = x.string();
      String langId = lang.string();

      java.util.Locale locale = Locale.forLanguageTag(langId);

      return Values.make(str.toLowerCase(locale));
    }
  }

  // function upper_case: (string x, string lang="en-US") -> string
  public static final class upper_case implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value lang) {

      if (x == Values.NIL) return Values.NIL;
      if (lang == Values.NIL) return Values.NIL;

      String str = x.string();
      String langId = lang.string();

      java.util.Locale locale = Locale.forLanguageTag(langId);

      return Values.make(str.toUpperCase(locale));
    }
  }

  public static final class matcher_impl implements UserFunction, Arity1UserFunction {

    private final Pattern pattern;

    public matcher_impl(Pattern pattern) {
      this.pattern = pattern;
    }

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x == Values.NIL) return Values.NIL;

      if (pattern.matcher(x.string()).matches()){
        return Values.TRUE;
      }
      else{
        return Values.FALSE;
      }
    }
  }

  // function matching (string pattern) -> function
  public static final class matching implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value pattern) {

      if (pattern == Values.NIL)
        throw new LangException(LangError.NIL_ERROR, "pattern cannot be nil");

      try {
        Pattern p = Pattern.compile(pattern.string());

        return Values.make(
            new UserFunctionValue(
                new FunctionSignature(Collections.singletonList(
                    new FunctionParameter(0, "x", Types.STRING, Values.NIL)),
                    Types.BOOLEAN),
                new matcher_impl(p)));

      }
      catch (PatternSyntaxException e){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid regex pattern: "+e.getMessage());
      }

    }
  }

  public static final class comparator_impl implements UserFunction, Arity2UserFunction {

    private final java.text.Collator collator;

    private final Value ab = Values.make(-1);
    private final Value ba = Values.make(1);
    private final Value eq = Values.make(0);

    public comparator_impl(Collator collator) {
      this.collator = collator;
    }

    @Override
    public Value call(UserCallContext context, Value a, Value b) {

      if (a == Values.NIL){
        if (b == Values.NIL){
          return eq;
        }
        return ab;
      }

      if (b == Values.NIL){
        return ba;
      }

      int cmp = collator.compare(a.string(), b.string());

      if (cmp < 0) return ab;
      if (cmp > 0) return ba;
      return eq;

    }
  }

  // function comparator: (string lang="en-US", boolean case_sensitive=true) -> function
  public static final class comparator implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value lang, Value case_sensitive) {

      if (lang == Values.NIL) throw new LangException(LangError.NIL_ERROR, "language tag cannot be nil");
      if (case_sensitive == Values.NIL) throw new LangException(LangError.NIL_ERROR, "case_sensitive cannot be nil");

      String langId = lang.string();
      Boolean caseSensitive = case_sensitive.bool();

      java.util.Locale locale = Locale.forLanguageTag(langId);
      Collator collator = Collator.getInstance(locale);

      collator.setDecomposition(Collator.FULL_DECOMPOSITION);

      if (caseSensitive){
        collator.setStrength(Collator.IDENTICAL);
      }
      else{
        collator.setStrength(Collator.SECONDARY);
      }

      return Values.make(
          new UserFunctionValue(
              new FunctionSignature(Arrays.asList(
                  new FunctionParameter(0, "a", Types.STRING, Values.NIL),
                  new FunctionParameter(1, "b", Types.STRING, Values.NIL)),
                  Types.LONG),
              new comparator_impl(collator)));

    }
  }

  // function starts_with?: (string x, string init) -> boolean
  public static final class startsWith implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value init) {

      if (x == Values.NIL) return Values.NIL;
      if (init == Values.NIL) return Values.NIL;

      return Values.make(x.string().startsWith(init.string()));
    }
  }

  // function ends_with?: (string x, string tail) -> boolean
  public static final class endsWith implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value tail) {

      if (x == Values.NIL) return Values.NIL;
      if (tail == Values.NIL) return Values.NIL;

      return Values.make(x.string().endsWith(tail.string()));
    }
  }

  // function index_of: (string x, string sub, long from=0) -> long
  public static final class indexOf implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value sub, Value start) {

      if (x == Values.NIL) return Values.NIL;
      if (sub == Values.NIL) return Values.NIL;
      if (start == Values.NIL) return Values.NIL;

      String xStr = x.string();
      String subStr = sub.string();
      Long startIdx = start.longNum();
      int idx = 0;

      if (startIdx > 0){
        if (startIdx <= Integer.MAX_VALUE){
          idx = startIdx.intValue();
        }
        // from index larger than possible string length
        else{
          return Values.make(-1);
        }
      }

      return Values.make(xStr.indexOf(subStr, idx));
    }
  }

  // function charAt: (string x, long i) -> string
  public static final class charAt implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value i) {

      if (x == Values.NIL) return Values.NIL;
      if (i == Values.NIL) return Values.NIL;

      String xStr = x.string();
      long xIdx = i.longNum();

      int len = xStr.codePointCount(0, xStr.length());
      if (xIdx < 0 || xIdx >= len) return Values.NIL;
      int idx = (int) xIdx;
      int from = xStr.offsetByCodePoints(0, idx);
      int to = xStr.offsetByCodePoints(0, idx+1);
      return Values.make(xStr.substring(from, to));

    }
  }

  // function code_point_at: (string x, long i) -> long
  public static final class codePointAt implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value i) {

      if (x == Values.NIL) return Values.NIL;
      if (i == Values.NIL) return Values.NIL;

      String xStr = x.string();
      long xIdx = i.longNum();

      int len = xStr.codePointCount(0, xStr.length());
      if (xIdx < 0 || xIdx >= len) return Values.NIL;
      int idx = (int) xIdx;
      int from = xStr.offsetByCodePoints(0, idx);
      return Values.make(xStr.codePointAt(from));

    }
  }

  // function last_index_of: (string x, string sub, long end=nil) -> long
  public static final class lastIndexOf implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value sub, Value end) {

      if (x == Values.NIL) return Values.NIL;
      if (sub == Values.NIL) return Values.NIL;

      if (end == Values.NIL) {
        return Values.make(x.string().lastIndexOf(sub.string()));
      }

      Long fromIdx = end.longNum();
      if (fromIdx < 0) return Values.make(-1);

      String xStr = x.string();
      String subStr = sub.string();

      int idx = 0;
      if (fromIdx > xStr.length()){
        idx = xStr.length();
      }
      else {
        idx = fromIdx.intValue();
      }

      return Values.make(xStr.lastIndexOf(subStr, idx));
    }
  }

  // function substring (string x, long start, long to=nil) -> string
  public static final class substring implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value start, Value end) {

      if (x == Values.NIL) return Values.NIL;

      if (start == Values.NIL){
        throw new LangException(LangError.NIL_ERROR, "start must not be nil");
      }

      String xStr = x.string();
      Long startLong = start.longNum();
      Long endLong = end.longNum();

      int codePoints = xStr.codePointCount(0, xStr.length());
      int startIdx = 0;
      int endIdx = codePoints;

      if (end != Values.NIL){
        if (endLong <= 0){
          return Values.EMPTY_STRING;
        }
        if (endLong < codePoints){
          endIdx = endLong.intValue();
        }
      }

      if (startLong >= endIdx || startLong >= codePoints){
        return Values.EMPTY_STRING;
      }

      startIdx = startLong.intValue();

      if (startIdx < 0){
        throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, "start must not be negative: "+ startLong);
      }

      int startCodepoint = xStr.offsetByCodePoints(0, startIdx);
      int endCodepoint = xStr.offsetByCodePoints(0, endIdx);
      return Values.make(xStr.substring(startCodepoint, endCodepoint));
    }
  }

  // function replace: (string x, string search, string replace) -> string
  public static final class searchReplace implements UserFunction, Arity3UserFunction {

    @Override
    public Value call(UserCallContext context, Value x, Value search, Value replace) {

      if (x == Values.NIL) return Values.NIL;
      if (search == Values.NIL) return Values.NIL;
      if (replace == Values.NIL) return Values.NIL;

      return Values.make(x.string().replace(search.string(), replace.string()));
    }
  }




}
