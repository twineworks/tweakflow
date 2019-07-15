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
  private Value spec;
  private boolean success;
  private String errorMessage;
  private String errorLocation;
  private Value thrownValue;
  private LangException error;

  public ItNode setName(String name){
    this.name = name;
    return this;
  }

  public ItNode setSpec(Value value){
    this.spec = value;
    return this;
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

    context.getReporter().onEnterIt(this);
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
          // TODO: pass current subject
          cc.call(spec, Values.NIL);
        }
        success = true;
      } catch (LangException e){
        // extract useful info
        success = false;
        error = e;
        thrownValue = (Value) e.getProperties().getOrDefault("value", Values.NIL);

        if (thrownValue.isDict()){
          DictValue dict = thrownValue.dict();
          Value code = dict.get("code");
          if (code.isString() && code.string().equals("ASSERTION_ERROR")){
            // construct a matcher-specific error message
            errorMessage = "expected: " + dict.get("x") + " " + dict.get("semantic").string() + ": "+ dict.get("expected");
            errorLocation = dict.get("location").string();
          }
          else if (code.isString() && code.string().equals("ERROR_EXPECTED")){
            // construct a matcher-specific error message
            errorMessage = "expected an error, but no error was thrown";
            errorLocation = dict.get("location").string();
          }
        }

        if (errorMessage == null){
          errorMessage = e.getDigestMessage();
          errorLocation = e.getSourceInfo().getFullLocation();
        }
      }
    }
    context.getReporter().onLeaveIt(this);

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

  public String getErrorLocation() {
    return errorLocation;
  }
}
