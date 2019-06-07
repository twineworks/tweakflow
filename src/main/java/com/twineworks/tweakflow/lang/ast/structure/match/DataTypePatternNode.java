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

package com.twineworks.tweakflow.lang.ast.structure.match;

import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.interpreter.ops.PatternOp;

import java.util.Collections;
import java.util.List;

public class DataTypePatternNode implements Node, MatchPatternNode {

  private Type type;
  private PatternOp patternOp;
  private SourceInfo sourceInfo;
  private Scope scope;
  private CapturePatternNode capture;

  @Override
  public DataTypePatternNode copy() {
    DataTypePatternNode copy = new DataTypePatternNode();
    copy.type = type;
    copy.sourceInfo = sourceInfo;
    copy.capture = capture == null ? null : capture.copy();
    return copy;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public DataTypePatternNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    if (capture != null) return Collections.singletonList(capture);
    return Collections.emptyList();
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public DataTypePatternNode setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public DataTypePatternNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  public Type getType() {
    return type;
  }

  public DataTypePatternNode setType(Type type) {
    this.type = type;
    return this;
  }

  @Override
  public PatternOp getPatternOp() {
    return patternOp;
  }

  public DataTypePatternNode setPatternOp(PatternOp patternOp) {
    this.patternOp = patternOp;
    return this;
  }

  public CapturePatternNode getCapture() {
    return capture;
  }

  public DataTypePatternNode setCapture(CapturePatternNode capture) {
    this.capture = capture;
    return this;
  }
}
