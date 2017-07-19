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

package com.twineworks.tweakflow.lang.scope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalScope implements Scope {

  private final Map<String, Symbol> symbols = new HashMap<>();
  private List<Symbol> dependencyOrderedSymbols;

  private final LocalScope unitScope = new LocalScope(this, ScopeType.UNIT);

  public LocalScope getUnitScope() {
    return unitScope;
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
  public Scope getEnclosingScope() {
    return null;
  }

  @Override
  public Scope setEnclosingScope(Scope scope) {
    if (scope != null){
      throw new AssertionError("Cannot set enclosing scope of global scope");
    }
    return this;
  }

  @Override
  public ScopeType getScopeType() {
    return ScopeType.GLOBAL;
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
  public void setDependencyOrderedSymbols(List<Symbol> dependencyOrderedSymbols) {
    this.dependencyOrderedSymbols = dependencyOrderedSymbols;
  }

  @Override
  public boolean isOrdered() {
    return false;
  }
}
