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

package com.twineworks.tweakflow.lang.ast.args;

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;

import java.util.ArrayList;
import java.util.List;

public class Arguments implements Node {

  private List<ArgumentNode> list = new ArrayList<>();
  private SourceInfo sourceInfo;
  private Scope scope;
  private boolean allPositional;

  public List<ArgumentNode> getList() {
    return list;
  }

  public Arguments setList(List<ArgumentNode> list) {
    this.list = list;
    cook();
    return this;
  }

  public boolean allPositional() {
    return allPositional;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public Arguments setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    return list;
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public Arguments setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public Arguments accept(Visitor visitor) {
    return visitor.visit(this);
  }

  private void cook() {
    allPositional = true;
    for (ArgumentNode argumentNode : list) {
      if (!argumentNode.isPositional()){
        allPositional = false;
        break;
      }
    }
  }
}
