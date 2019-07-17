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

import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.spec.runner.SpecContext;

public class SubjectNode implements SpecNode, SubjectSpecNode {

  private String name = "subject";
  private Value data = Values.NIL;
  private DescribeNode parent;

  private String errorMessage = "OK";
  private Throwable cause;
  private boolean success = true;
  private boolean didRun = false;
  private Value source;

  private long startedMillis;
  private long endedMillis;
  private NodeLocation at;

  public SubjectNode setData(Value value) {
    this.data = value;
    return this;
  }

  @Override
  public SpecNodeType getType() {
    return SpecNodeType.SUBJECT;
  }

  public String getName() {
    return name;
  }

  @Override
  public void run(SpecContext context) {
    startedMillis = System.currentTimeMillis();
    context.onEnterSubject(this);
    if (success) {
      try {
        didRun = true;
        context.setSubject(data);
      } catch (Throwable e) {
        fail(e.getMessage(), e);
      }
    }
    endedMillis = System.currentTimeMillis();
    context.onLeaveSubject(this);
  }

  @Override
  public Value getSource() {
    return source;
  }

  public SubjectNode setSource(Value source) {
    this.source = source;
    return this;
  }

  @Override
  public void fail(String errorMessage, Throwable cause) {
    this.success = false;
    this.errorMessage = errorMessage;
    this.cause = cause;
  }

  @Override
  public boolean didRun() {
    return didRun;
  }

  @Override
  public boolean isSuccess() {
    return success;
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public Throwable getCause() {
    return cause;
  }

  @Override
  public long getDurationMillis() {
    return endedMillis - startedMillis;
  }


  @Override
  public DescribeNode getParent() {
    return parent;
  }

  @Override
  public void setParent(DescribeNode parent) {
    this.parent = parent;
  }

  @Override
  public String getErrorLocation() {
    return at.file+":"+at.line+":"+at.charInLine;
  }

  public NodeLocation at(){
    return at;
  }

  public SubjectNode setAt(NodeLocation at) {
    this.at = at;
    return this;
  }
}
