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
import com.twineworks.tweakflow.lang.interpreter.ops.PatternOp;

import java.util.ArrayList;
import java.util.List;

public class InitLastListPatternNode implements Node, MatchPatternNode {

  private ArrayList<MatchPatternNode> elements = new ArrayList<>();
  private CapturePatternNode initCapture;
  private CapturePatternNode capture;
  private PatternOp patternOp;
  private SourceInfo sourceInfo;
  private Scope scope;

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public InitLastListPatternNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    ArrayList<Node> ret = new ArrayList<>();
    ret.add(initCapture);
    ret.addAll(elements);
    if (capture != null){
      ret.add(capture);
    }
    return ret;
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public InitLastListPatternNode setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public InitLastListPatternNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public PatternOp getPatternOp() {
    return patternOp;
  }

  public InitLastListPatternNode setPatternOp(PatternOp patternOp) {
    this.patternOp = patternOp;
    return this;
  }

  public ArrayList<MatchPatternNode> getElements() {
    return elements;
  }

  public CapturePatternNode getInitCapture() {
    return initCapture;
  }

  public InitLastListPatternNode setInitCapture(CapturePatternNode initCapture) {
    this.initCapture = initCapture;
    return this;
  }

  public CapturePatternNode getCapture() {
    return capture;
  }

  public InitLastListPatternNode setCapture(CapturePatternNode capture) {
    this.capture = capture;
    return this;
  }
}
