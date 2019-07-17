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
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.spec.runner.SpecContext;

import java.util.HashSet;
import java.util.Set;

public class ItNode implements SpecNode, TaggableSpecNode {

  private String name = "unknown";
  private boolean selected = true;
  private Value spec;

  private NodeLocation at;
  private DescribeNode parent;
  private String fullName;

  private long startedMillis;
  private long endedMillis;

  private boolean success = true;
  private boolean didRun = false;

  private String errorMessage;
  private String errorLocation;
  private Value thrownValue;
  private Throwable cause;
  private Value source;

  private Set<String> tags;
  private Set<String> fullTags;

  public ItNode setSpec(Value value) {
    this.spec = value;
    return this;
  }

  public ItNode setAt(NodeLocation at) {
    this.at = at;
    return this;
  }

  @Override
  public Value getSource() {
    return source;
  }

  public ItNode setSource(Value source) {
    this.source = source;
    return this;
  }

  public DescribeNode getParent() {
    return parent;
  }

  public ItNode setParent(DescribeNode parent) {
    this.parent = parent;
    return this;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getFullName() {
    if (fullName == null) {
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

  public ItNode setName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public void run(SpecContext context) {
    startedMillis = System.currentTimeMillis();
    context.onEnterIt(this);

    if (success) {
      didRun = true;
      if (spec == Values.NIL) {
        // pending
      } else {
        CallContext cc = context.getRuntime().createCallContext();
        int paramCount = spec.function().getSignature().getParameterList().size();

        try {
          if (paramCount == 0) {
            cc.call(spec);
          } else {
            cc.call(spec, context.getSubject());
          }
        } catch (LangException e) {
          fail(e.getMessage(), e);
        }
      }
    }

    endedMillis = System.currentTimeMillis();
    context.onLeaveIt(this);

  }

  @Override
  public void fail(String errorMessage, Throwable cause) {
    success = false;

    // extract useful info
    this.cause = cause;

    if (cause instanceof LangException) {
      LangException e = (LangException) cause;

      thrownValue = (Value) e.getProperties().getOrDefault("value", Values.NIL);

      if (thrownValue.isDict()) {
        DictValue dict = thrownValue.dict();
        Value code = dict.get("code");
        if (code.isString() && code.string().equals("ASSERTION_ERROR")) {
          // construct a matcher-specific error message
          this.errorMessage = "expected " + dict.get("x") + " " + dict.get("semantic").string() + " " + dict.get("expected");
          errorLocation = dict.get("location").string();
        } else if (code.isString() && code.string().equals("ERROR_EXPECTED")) {
          // construct a matcher-specific error message
          this.errorMessage = "expected an error, but no error was thrown";
          errorLocation = dict.get("location").string();
        }
      }

      if (this.errorMessage == null) {
        this.errorMessage = e.getDigestMessage();
        if (e.getSourceInfo() != null){
          errorLocation = e.getSourceInfo().getFullLocation();
        }
      }
    } else {
      this.errorMessage = errorMessage;
    }

  }

  public String getBody(){
    return ValueInspector.inspect(spec, true).trim();
  }

  @Override
  public boolean didRun() {
    return didRun;
  }

  public boolean isPending() {
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

  @Override
  public long getDurationMillis() {
    return endedMillis - startedMillis;
  }

  @Override
  public Set<String> getTags() {

    if (fullTags == null) {
      if (parent == null) return tags;
      fullTags = new HashSet<>(parent.getTags());
      fullTags.addAll(tags);
    }
    return fullTags;
  }

  @Override
  public Set<String> getOwnTags() {
    return tags;
  }

  public ItNode setTags(Set<String> tags) {
    this.tags = tags;
    return this;
  }
}
