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

package com.twineworks.tweakflow.lang.errors;

import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class LangException extends RuntimeException {

  private ErrorCode code;
  private Stack stack;
  private SourceInfo sourceInfo;
  private Map<String, Object> properties = new HashMap<>();


  private LangException(Throwable t, ErrorCode code, String message, Stack stack, SourceInfo sourceInfo) {
    super(message, t);
    this.code = code;
    this.stack = stack == null ? null : stack.copy();
    this.sourceInfo = sourceInfo;
  }

  private LangException(Throwable t, ErrorCode code) {
    super(t);
    this.code = code;
  }

  public LangException(ErrorCode code, String message, Stack stack, SourceInfo sourceInfo) {
    super(message);
    this.code = code;
    this.stack = stack == null ? null : stack.copy();
    this.sourceInfo = sourceInfo;
  }

  public LangException(ErrorCode code, String message, SourceInfo sourceInfo) {
    super(message);
    this.code = code;
    this.sourceInfo = sourceInfo;
  }

  public LangException(ErrorCode code, SourceInfo sourceInfo) {
    this.code = code;
    this.sourceInfo = sourceInfo;
  }

  public LangException(ErrorCode code, String message, Stack stack) {
    super(message);
    this.code = code;
    this.stack = stack == null ? null : stack.copy();
  }

  public LangException(ErrorCode code, String message) {
    super(message);
    this.code = code;
  }

  public LangException(ErrorCode code) {
    this.code = code;
  }

  public LangException(ErrorCode code, Stack stack) {
    this.code = code;
    this.stack = stack == null ? null : stack.copy();
  }

  public LangException(Throwable t, ErrorCode code, Stack stack) {
    super(t);
    this.code = code;
    this.stack = stack == null ? null : stack.copy();
  }

  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  public LangException setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  public static LangException wrap(Throwable reason){
    return wrap(reason, null, null);
  }

  public static LangException wrap(Throwable reason, ErrorCode code, Stack stack){

    // if reason is already a Eval exception and already has requested code
    // return it directly
    if (reason instanceof LangException){

      LangException e = (LangException) reason;
      if (code == e.code || code == null){
        return e;
      }

    }

    // wrap otherwise
    if (code == null){
      return new LangException(reason, LangError.UNKNOWN_ERROR, stack);
    }
    else{
      return new LangException(reason, code, stack);
    }

  }

  public static LangException wrap(Throwable reason, ErrorCode code){

    // if reason is already a Eval exception and already has requested code
    // return it directly
    if (reason instanceof LangException){

      LangException e = (LangException) reason;
      if (code == e.code || code == null){
        return e;
      }

    }

    // wrap otherwise
    if (code == null){
      return new LangException(reason, LangError.UNKNOWN_ERROR);
    }
    else{
      return new LangException(reason, code);
    }

  }

  public Stack getStack() {
    return stack;
  }

  public void setStack(Stack stack) {
    this.stack = stack.copy();
  }

  public ErrorCode getCode() {
    return code;
  }

  public void printDigestMessageAndStackTrace(){
    printDigestMessage();
    System.err.flush();
    printStackTrace();
    System.err.flush();
  }

  public void printDigestMessage(){
    System.err.println(getDigestMessage());
  }

  public String getDigestMessage(){

    if (getCause() instanceof LangException && getCause() != this){
      return ((LangException) getCause()).getDigestMessage();
    }

    if (getCause() instanceof LangException){
      return ((LangException) getCause()).getDigestMessage();
    }

//    System.out.println(getSourceInfo().getParseUnit().getProgramText());
    return makeTraceString(getStack());
  }

  private String makeTraceString(Stack stack) {
    StringBuilder trace = new StringBuilder();

    trace.append("ERROR: \n");
    trace.append("  code: ").append(code.getName()).append("\n");
    trace.append("  message: ").append(getMessage() == null? code.getName() : getMessage()).append("\n");

    if (getSourceInfo() != null){

      String source = getSourceInfo().getSourceCode();
      if (source != null){
        trace.append("  source: ");
        if (source.length() < 250){
          trace.append(source);
        }
        else{
          trace.append(source.substring(0, 250));
        }
        trace.append("\n");
      }

      String line = getSourceInfo().getSourceCodeLine();
      if (line != null){
        trace.append("  line: ");
        if (line.length() < 250){
          trace.append(line);
        }
        else{
          trace.append(line.substring(0, 250));
        }
        trace.append("\n");
      }

      trace.append("  at: ").append(getSourceInfo().toString()).append("\n");

    }

    for (Map.Entry<String, Object> entry : properties.entrySet()) {

      try {
        String traceLine = "";
        if (entry.getValue() instanceof Value){
          traceLine = ValueInspector.inspect((Value) entry.getValue());
        }
        else{
          traceLine = ValueInspector.inspect(Values.make(entry.getValue()));
        }
        trace.append("  ").append(entry.getKey()).append(": ").append(traceLine).append("\n");
      }
      catch (RuntimeException ignore){
      }
    }

    trace.append("TRACE:\n");

    if (stack == null){
      trace.append("  none");
    }
    else{

      stack.forEach(
          (x) -> trace
              .append("  ")
              .append(x.getNode().getSourceInfo().getFullLocation())
              .append("\n")
      );

    }

    return trace.toString();
  }


  public Value toTraceValue(){
    //    printStackTrace();

    // custom properties
    DictValue dict = new DictValue();
    for (Map.Entry<String, Object> entry : properties.entrySet()) {
      try {
        if (entry.getValue() instanceof Value){
          dict = dict.put(entry.getKey(), (Value) entry.getValue());
        }
        else{
          dict = dict.put(entry.getKey(), Values.make(entry.getValue()));
        }
      }
      catch (RuntimeException ignore){
      }
    }

    dict = dict.put("code", Values.make(code.getName()));

    if (getMessage() != null){
      dict = dict.put("message", Values.make(getMessage()));
    }
    else{
      dict = dict.put("message", Values.make(code.getName()));
    }

    if (getSourceInfo() != null){
      String source = getSourceInfo().getSourceCode();
      if (source != null){
        if (source.length() < 250){
          dict = dict.put("source", Values.make(source));
        }
        else{
          dict = dict.put("source", Values.make(source.substring(0, 250)));
        }
      }
      String line = getSourceInfo().getSourceCodeLine();
      if (line != null){
        if (line.length() < 250){
          dict = dict.put("line", Values.make(line));
        }
        else{
          dict = dict.put("line", Values.make(line.substring(0, 250)));
        }
      }
      dict = dict.put("at", Values.make(getSourceInfo().toString()));

    }

    if (stack != null){
      dict = dict.put("stack", stack.toValue());
    }

    return Values.make(dict);
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  // delegate convenience methods for populating context map
  public boolean containsKey(Object key) {
    return properties.containsKey(key);
  }

  public Object get(Object key) {
    return properties.get(key);
  }

  public LangException put(String key, Object value) {
    properties.put(key, value);
    return this;
  }

  public LangException putAll(Map<? extends String, ?> m) {
    properties.putAll(m);
    return this;
  }

  public Set<String> keySet() {
    return properties.keySet();
  }

  public Set<Map.Entry<String, Object>> entrySet() {
    return properties.entrySet();
  }

  public Object getOrDefault(Object key, Object defaultValue) {
    return properties.getOrDefault(key, defaultValue);
  }

  public void forEach(BiConsumer<? super String, ? super Object> action) {
    properties.forEach(action);
  }

  public LangException putIfAbsent(String key, Object value) {
    properties.putIfAbsent(key, value);
    return this;
  }

  public Value toErrorValue() {
    // catchable value given explicitly?
    Object explicitValue = get("value");
    if (explicitValue instanceof Value) return (Value) explicitValue;

    // construct an error value
    DictValue dict = new DictValue();
    dict = dict.put("code", Values.make(code.getName()));

    if (getMessage() != null){
      dict = dict.put("message", Values.make(getMessage()));
    }
    return Values.make(dict);

  }
}
