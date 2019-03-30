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

package com.twineworks.tweakflow.lang.scope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalScope implements Scope {

  private final Map<String, Symbol> symbols = new HashMap<>();
  private List<Symbol> dependencyOrderedSymbols;
  private Scope enclosingScope;
  private final ScopeType scopeType;
  private final boolean ordered;

  public LocalScope(Scope enclosingScope) {
    this.enclosingScope = enclosingScope;
    scopeType = ScopeType.LOCAL;
    ordered = false;
  }

  public LocalScope(Scope enclosingScope, ScopeType scopeType) {
    this.enclosingScope = enclosingScope;
    this.scopeType = scopeType;
    ordered = false;
  }

  public LocalScope(Scope enclosingScope, ScopeType scopeType, boolean ordered) {
    this.enclosingScope = enclosingScope;
    this.scopeType = scopeType;
    this.ordered = ordered;
  }

  @Override
  public boolean isOrdered() {
    return ordered;
  }

  public Scope getEnclosingScope() {
    return enclosingScope;
  }

  public LocalScope setEnclosingScope(Scope enclosingScope) {
    this.enclosingScope = enclosingScope;
    return this;
  }

  @Override
  public ScopeType getScopeType() {
    return scopeType;
  }

  @Override
  public Scope getPublicScope() {
    return this;
  }

  @Override
  public boolean isLibrary() {
    return false;
  }

  @Override
  public Map<String, Symbol> getSymbols() {
    return symbols;
  }


  @Override
  public List<Symbol> getDependencyOrderedSymbols() {
    return dependencyOrderedSymbols;
  }

  @Override
  public void setDependencyOrderedSymbols(List<Symbol> dependencyOrderedSymbols) {
    this.dependencyOrderedSymbols = dependencyOrderedSymbols;
  }
}
