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

package com.twineworks.tweakflow.spec.nodes;

import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.CallContext;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.spec.runner.SpecContext;

public class ItNode implements SpecNode {

  private String name = "unknown";
  private boolean selected = true;
  private Value spec;

  private NodeLocation at;
  private DescribeNode parent;
  private String fullName;

  private boolean success = true;
  private boolean didRun = false;

  private String errorMessage;
  private String errorLocation;
  private Value thrownValue;
  private Throwable cause;


  public ItNode setName(String name){
    this.name = name;
    return this;
  }

  public ItNode setSpec(Value value){
    this.spec = value;
    return this;
  }

  public ItNode setAt(NodeLocation at){
    this.at = at;
    return this;
  }

  public ItNode setParent(DescribeNode parent){
    this.parent = parent;
    return this;
  }

  public DescribeNode getParent() {
    return parent;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getFullName() {
    if (fullName == null){
      if (parent == null) return name;
      fullName = parent.getFullName() + " " + name;
    }
    return fullName;
  }

  @Override
  public SpecNodeType getType() {
    return SpecNodeType.IT;
  }

  public String getName() {
    return name;
  }

  @Override
  public void run(SpecContext context) {

    context.onEnterIt(this);

    if (success){
      didRun = true;
      if (spec == Values.NIL) {
        // pending
      }
      else {
        CallContext cc = context.getRuntime().createCallContext();
        int paramCount = spec.function().getSignature().getParameterList().size();

        try {
          if (paramCount == 0){
            cc.call(spec);
          }
          else {
            cc.call(spec, context.getSubject());
          }
        } catch (LangException e){
          fail(e.getMessage(), e);
        }
      }
    }

    context.onLeaveIt(this);

  }

  @Override
  public void fail(String errorMessage, Throwable cause) {
    success = false;

    // extract useful info
    this.cause = cause;

    if (cause instanceof LangException){
      LangException e = (LangException) cause;

      thrownValue = (Value) e.getProperties().getOrDefault("value", Values.NIL);

      if (thrownValue.isDict()){
        DictValue dict = thrownValue.dict();
        Value code = dict.get("code");
        if (code.isString() && code.string().equals("ASSERTION_ERROR")){
          // construct a matcher-specific error message
          this.errorMessage = "expected " + dict.get("x") + " " + dict.get("semantic").string() + " "+ dict.get("expected");
          errorLocation = dict.get("location").string();
        }
        else if (code.isString() && code.string().equals("ERROR_EXPECTED")){
          // construct a matcher-specific error message
          this.errorMessage = "expected an error, but no error was thrown";
          errorLocation = dict.get("location").string();
        }
      }

      if (this.errorMessage == null){
        this.errorMessage = e.getDigestMessage();
        errorLocation = e.getSourceInfo().getFullLocation();
      }
    }
    else{
      this.errorMessage = errorMessage;
    }

  }

  @Override
  public boolean didRun() {
    return didRun;
  }

  public boolean isPending(){
    return spec == Values.NIL;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public Throwable getCause() {
    return cause;
  }

  public String getErrorLocation() {
    return errorLocation;
  }

  public NodeLocation at() {
    return at;
  }
}
