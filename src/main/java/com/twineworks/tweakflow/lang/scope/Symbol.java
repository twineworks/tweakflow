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

import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.types.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Symbol implements Scope {

  private String name;
  private Scope scope;
  private Scope enclosingScope;
  private Map<String, Symbol> symbols;
  private LocalScope exports;
  private List<Symbol> dependencyOrderedSymbols;
//  private Map<String, Symbol> exportedSymbols;

  private boolean isExport;

  private String refName;
  private ReferenceNode refNode;
  private Symbol ref;

  private SymbolTarget target;
  private SymbolNode node;
  private SymbolType type;

  private Type varType;

  public String getName() {
    return name;
  }

  public Symbol setName(String name) {
    this.name = name;
    return this;
  }

  public SymbolType getSymbolType() {
    return type;
  }

  public Symbol setType(SymbolType type) {
    this.type = type;
    return this;
  }

  public Scope getScope() {
    return scope;
  }

  public Symbol setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  public ScopeType getScopeType(){
    return ScopeType.SYMBOL;
  }

  @Override
  public Scope getPublicScope() {
    if (isRef()) return ref.getPublicScope();
    if (exports != null) return exports;
    return this;
  }

  @Override
  public boolean isLibrary() {
    return target == SymbolTarget.LIBRARY;
  }

  public Scope getEnclosingScope() {
    return enclosingScope;
  }

  public Symbol setEnclosingScope(Scope enclosingScope) {
    this.enclosingScope = enclosingScope;
    return this;
  }

  private boolean isRef(){
    return type == SymbolType.EXPORT ||
        type == SymbolType.ALIAS ||
        type == SymbolType.MODULE_IMPORT ||
        type == SymbolType.NAME_IMPORT;
  }

  public Map<String, Symbol> getSymbols() {

    if (isRef()){
      return ref.getPublicScope().getSymbols();
    }
    else if (isScoped()){
      return symbols;
    }
    else {
      return Collections.emptyMap();
    }

  }

  public ReferenceNode getRefNode() {
    return refNode;
  }

  public Symbol setRefNode(ReferenceNode refNode) {
    this.refNode = refNode;
    return this;
  }

  public Symbol setSymbols(Map<String, Symbol> symbols) {
    this.symbols = symbols;
    return this;
  }

  public boolean isExport() {
    return isExport;
  }

  public Symbol setExport(boolean export) {
    isExport = export;
    return this;
  }

  public boolean isImport() {
    return type == SymbolType.NAME_IMPORT|| type == SymbolType.MODULE_IMPORT;
  }

  public boolean isNameImport(){
    return type == SymbolType.NAME_IMPORT;
  }

  public boolean isModuleImport(){
    return type == SymbolType.MODULE_IMPORT;
  }

  public boolean isAlias() {
    return type == SymbolType.ALIAS;
  }

  public String getRefName() {
    return refName;
  }

  public Symbol setRefName(String refName) {
    this.refName = refName;
    return this;
  }

  public Symbol getRef() {
    return ref;
  }

  public Symbol setRef(Symbol ref) {
    this.ref = ref;
    return this;
  }

  public boolean isRefResolved(){
    return this.ref != null;
  }

  public boolean isScoped() {
    return target == SymbolTarget.MODULE ||
        target == SymbolTarget.LIBRARY ||
        target == SymbolTarget.INTERACTIVE ||
        target == SymbolTarget.INTERACTIVE_SECTION;

  }

  public SymbolTarget getTarget() {
    return target;
  }

  public Symbol setTarget(SymbolTarget target) {
    this.target = target;

    // make sure there's child-symbols for a symbol acting as a scope
    if (isScoped() && symbols == null){
      symbols = new HashMap<>();

      // modules have separate exports as well
      if (target == SymbolTarget.MODULE){
        exports = new LocalScope(enclosingScope, ScopeType.EXPORTS);
      }

    }

    if (!isScoped()){
      symbols = null;
      exports = null;
    }
    return this;
  }

  public SymbolNode getNode(){
    return node;
  }

  public SymbolNode getTargetNode(){
    if (isRef()){
      return ref.getTargetNode();
    }
    else{
      return node;
    }
  }

  public Symbol setNode(SymbolNode node){
    this.node = node;
    return this;
  }

  public Type getVarType() {
    return varType;
  }

  public Symbol setVarType(Type varType) {
    this.varType = varType;
    return this;
  }

  public boolean isLocal() {
    return type == SymbolType.LOCAL;
  }

  public boolean isLibraryVar(){
    return type == SymbolType.LOCAL && scope.getScopeType() == ScopeType.SYMBOL && ((Symbol)scope).getTarget() == SymbolTarget.LIBRARY;
  }

  @Override
  public List<Symbol> getDependencyOrderedSymbols() {
    return dependencyOrderedSymbols;
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
