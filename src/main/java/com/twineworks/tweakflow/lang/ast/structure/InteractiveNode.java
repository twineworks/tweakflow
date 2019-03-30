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

package com.twineworks.tweakflow.lang.ast.structure;

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.ast.UnitNode;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;

import java.util.ArrayList;
import java.util.List;

public class InteractiveNode implements UnitNode, SymbolNode {

  private SourceInfo sourceInfo;
  private Scope scope;
  private Symbol unitSymbol;
  private List<InteractiveSectionNode> sections = new ArrayList<>();

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public InteractiveNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    return sections;
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public InteractiveNode setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public InteractiveNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  public List<InteractiveSectionNode> getSections() {
    return sections;
  }

  @Override
  public String getSymbolName() {
    return "";
  }

  @Override
  public InteractiveNode setSymbolName(String name) {
    throw new AssertionError("cannot set symbol name");
  }

  public InteractiveNode setUnitSymbol(Symbol unitSymbol) {
    this.unitSymbol = unitSymbol;
    return this;
  }

  public Symbol getSymbol(){
    return unitSymbol;
  }

  public Symbol getExportSymbol(){
    return unitSymbol;
  }

  public Scope getUnitScope(){
    return unitSymbol;
  }

  @Override
  public Scope getPublicScope() {
    return unitSymbol;
  }



}
