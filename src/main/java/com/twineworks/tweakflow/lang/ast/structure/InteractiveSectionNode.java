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

import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;

import java.util.Arrays;
import java.util.List;

public class InteractiveSectionNode implements SymbolNode {

  private SourceInfo sourceInfo;
  private ReferenceNode inScopeRef;
  private VarDefs vars = new VarDefs();
  private Scope scope;
  private String symbolName;
  private Symbol symbol;

  @Override
  public InteractiveSectionNode copy() {
    InteractiveSectionNode copy = new InteractiveSectionNode();
    copy.sourceInfo = sourceInfo;
    copy.inScopeRef = inScopeRef.copy();
    copy.vars = vars.copy();
    copy.symbolName = symbolName;
    return copy;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public InteractiveSectionNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    return Arrays.asList(inScopeRef, vars);
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public InteractiveSectionNode setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  public ReferenceNode getInScopeRef() {
    return inScopeRef;
  }

  public InteractiveSectionNode setInScopeRef(ReferenceNode inScopeRef) {
    this.inScopeRef = inScopeRef;
    return this;
  }

  @Override
  public InteractiveSectionNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  public VarDefs getVars() {
    return vars;
  }

  @Override
  public String getSymbolName() {
    return symbol.getName();
  }

  @Override
  public InteractiveSectionNode setSymbolName(String name) {
    throw new AssertionError("cannot set symbol name on interactive section node");
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public InteractiveSectionNode setSymbol(Symbol symbol) {
    this.symbol = symbol;
    return this;
  }
}
