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

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class Regex {

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

  public static final class capture_impl implements UserFunction, Arity1UserFunction {

    private final Pattern pattern;

    public capture_impl(Pattern pattern) {
      this.pattern = pattern;
    }

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      String str = x.string();
      Matcher matcher = pattern.matcher(str);
      ListValue groups = new ListValue();

      if (matcher.find() && matcher.end() == str.length()){
        int groupCount = matcher.groupCount();

        for (int i=0; i<=groupCount; i++){
          String group = matcher.group(i);
          if (group == null){
            groups = groups.append(Values.NIL);
          }
          else {
            groups = groups.append(Values.make(group));
          }
        }
      }
      return Values.make(groups);
    }
  }

  // function capture (string pattern) -> function
  public static final class capturing implements UserFunction, Arity1UserFunction {

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
                    Types.LIST),
                new capture_impl(p)));

      }
      catch (PatternSyntaxException e){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid regex pattern: "+e.getMessage());
      }

    }
  }

  public static final class replace_impl implements UserFunction, Arity1UserFunction {

    private final Pattern pattern;
    private final String replace;

    public replace_impl(Pattern pattern, String replace) {
      this.pattern = pattern;
      this.replace = replace;
    }

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x == Values.NIL) return Values.NIL;

      try {
        Matcher matcher = pattern.matcher(x.string());
        String s = matcher.replaceAll(replace);
        return Values.make(s);
      } catch(IndexOutOfBoundsException e){
        throw new LangException(LangError.INDEX_OUT_OF_BOUNDS, e.getMessage());
      }
    }

  }

  public static final class replacing implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value pattern, Value replace) {


      if (pattern == Values.NIL)
        throw new LangException(LangError.NIL_ERROR, "pattern cannot be nil");

      if (replace == Values.NIL)
        throw new LangException(LangError.NIL_ERROR, "replace cannot be nil");

      try {
        Pattern p = Pattern.compile(pattern.string());

        return Values.make(
            new UserFunctionValue(
                new FunctionSignature(Collections.singletonList(
                    new FunctionParameter(0, "x", Types.STRING, Values.NIL)),
                    Types.STRING),
                new replace_impl(p, replace.string())));

      }
      catch (PatternSyntaxException e){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid regex pattern: "+e.getMessage());
      }

    }
  }

  public static final class scanning_impl implements UserFunction, Arity1UserFunction {

    private final Pattern pattern;

    public scanning_impl(Pattern pattern) {
      this.pattern = pattern;
    }

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      String str = x.string();
      Matcher matcher = pattern.matcher(str);

      ListValue result = new ListValue();

      while (matcher.find()){
        ListValue groups = new ListValue();
        int groupCount = matcher.groupCount();

        for (int i=0; i<=groupCount; i++){
          String group = matcher.group(i);
          if (group == null){
            groups = groups.append(Values.NIL);
          }
          else {
            groups = groups.append(Values.make(group));
          }
        }
        result = result.append(Values.make(groups));
      }

      return Values.make(result);
    }
  }

  // function scanning (string pattern) -> function
  public static final class scanning implements UserFunction, Arity1UserFunction {

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
                    Types.LIST),
                new scanning_impl(p)));

      }
      catch (PatternSyntaxException e){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid regex pattern: "+e.getMessage());
      }

    }
  }

  public static final class splitting_impl implements UserFunction, Arity1UserFunction {

    private final Pattern pattern;
    private final int limit;

    public splitting_impl(Pattern pattern, int limit) {
      this.pattern = pattern;
      this.limit = limit;
    }

    @Override
    public Value call(UserCallContext context, Value x) {
      if (x == Values.NIL) return Values.NIL;
      String str = x.string();
      String[] out = pattern.split(str, limit);

      ListValue result = new ListValue();

      for (String s : out) {
        result = result.append(Values.make(s));
      }

      return Values.make(result);
    }
  }

  // function splitting (string pattern, long limit=nil) -> function
  public static final class splitting implements UserFunction, Arity2UserFunction {

    @Override
    public Value call(UserCallContext context, Value pattern, Value limit) {

      if (pattern == Values.NIL)
        throw new LangException(LangError.NIL_ERROR, "pattern cannot be nil");

      int intLimit = 0;
      if (limit != Values.NIL){
        long longLimit = limit.longNum();
        if (longLimit < 0){
          intLimit = -1;
        }
        else if (longLimit > Integer.MAX_VALUE){
          intLimit = Integer.MAX_VALUE;
        }
        else {
          intLimit = (int) longLimit;
        }
      }

      try {
        Pattern p = Pattern.compile(pattern.string());

        return Values.make(
            new UserFunctionValue(
                new FunctionSignature(Collections.singletonList(
                    new FunctionParameter(0, "x", Types.STRING, Values.NIL)),
                    Types.LIST),
                new splitting_impl(p, intLimit)));

      }
      catch (PatternSyntaxException e){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "invalid regex pattern: "+e.getMessage());
      }

    }
  }

  // function quote (string x) -> string
  public static final class quote implements UserFunction, Arity1UserFunction {

    @Override
    public Value call(UserCallContext context, Value x) {

      if (x == Values.NIL) return Values.NIL;
      return Values.make(Pattern.quote(x.string()));

    }
  }

}
