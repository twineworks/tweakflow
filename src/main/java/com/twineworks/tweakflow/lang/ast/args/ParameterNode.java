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

package com.twineworks.tweakflow.lang.ast.args;

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.types.Type;

import java.util.Collections;
import java.util.List;

public class ParameterNode implements Node, SymbolNode {

  private SourceInfo sourceInfo;
  private String name;
  private int index;
  private Type declaredType;
  private ExpressionNode defaultValue;
  private Scope scope;

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public ParameterNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    if (defaultValue != null) return Collections.singletonList(defaultValue);
    return Collections.emptyList();
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public ParameterNode setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public ParameterNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  public String getSymbolName() {
    return name;
  }

  public ParameterNode setSymbolName(String name) {
    this.name = name;
    return this;
  }

  public int getIndex() {
    return index;
  }

  public ParameterNode setIndex(int index) {
    this.index = index;
    return this;
  }

  public Type getDeclaredType() {
    return declaredType;
  }

  public ParameterNode setDeclaredType(Type declaredType) {
    this.declaredType = declaredType;
    return this;
  }

  public ExpressionNode getDefaultValue() {
    return defaultValue;
  }

  public ParameterNode setDefaultValue(ExpressionNode defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }
}
