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

package com.twineworks.tweakflow.lang.interpreter.memory;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.ast.structure.InteractiveSectionNode;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.scope.SymbolTarget;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueProvider;

public class Cell implements MemorySpace, ValueProvider {

  private ConstShapeMap<Cell> cells;                     // in case the cell is a space/scope itself
  private MemorySpace enclosingSpace;                  // in case the cell is a space/scope itself
  private Scope scope;                                 // in case the cell is a space/scope itself

  private Symbol symbol;
  private Value value;
  private boolean dirty = true;
  private boolean evaluating = false;

  public Symbol getSymbol() {
    return symbol;
  }

  public Cell setSymbol(Symbol symbol) {
    return setSymbol(symbol, false);
  }

  public Cell setSymbol(Symbol symbol, boolean publicScope) {
    this.symbol = symbol;
    if (symbol.isScoped()){

      if (publicScope){
        cells = new ConstShapeMap<>(ShapeKey.getAll(symbol.getPublicScope().getSymbols().keySet()));
      }
      else{
        if (symbol.getTarget() == SymbolTarget.INTERACTIVE_SECTION){
          InteractiveSectionNode n = (InteractiveSectionNode) symbol.getTargetNode();
          cells = new ConstShapeMap<>(ShapeKey.getAll(n.getVars().getMap().keySet()));
        }
        else{
          cells = new ConstShapeMap<>(ShapeKey.getAll(symbol.getSymbols().keySet()));
        }

      }
    }
    return this;
  }

  public Cell setLeafSymbol(Symbol symbol) {
    this.symbol = symbol;
    return this;
  }

  public boolean isEvaluating() {
    return evaluating;
  }

  public Cell setEvaluating(boolean evaluating) {
    this.evaluating = evaluating;
    return this;
  }

//  public MemorySpace getSpace() {
//    return space;
//  }
//
//  public Cell setSpace(MemorySpace space) {
//    this.space = space;
//    return this;
//  }

  @Override
  public ConstShapeMap<Cell> getCells() {
    return cells;
  }

  @Override
  public MemorySpace getEnclosingSpace() {
    return enclosingSpace;
  }

  @Override
  public Cell setEnclosingSpace(MemorySpace space) {
    enclosingSpace = space;
    return this;
  }

  // MemorySpace scope
  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public MemorySpaceType getMemorySpaceType() {
    if (isModule()) return MemorySpaceType.MODULE;
    if (isLibrary()) return MemorySpaceType.LIBRARY;
    if (isInteractiveUnit()) return MemorySpaceType.INTERACTIVE;
    if (isInteractiveSection()) return MemorySpaceType.INTERACTIVE_SECTION;
    if (isVar()) return MemorySpaceType.VAR;
    return MemorySpaceType.LOCAL;
  }

  public Cell setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  public boolean isModule(){
    return symbol.getTarget() == SymbolTarget.MODULE;
  }

  public boolean isLibrary(){
    return symbol.getTarget() == SymbolTarget.LIBRARY;
  }

  public boolean isInteractiveUnit() {
    return symbol.getTarget() == SymbolTarget.INTERACTIVE;
  }

  public boolean isInteractiveSection() {
    return symbol.getTarget() == SymbolTarget.INTERACTIVE_SECTION;
  }

  public boolean isVar(){
    return symbol.getTarget() == SymbolTarget.VAR;
  }

  @Override
  public Value getValue() {
    return value;
  }

  public Cell setValue(Value value) {
    this.value = value;
    dirty = false;
    return this;
  }

  public boolean isDirty() {
    return dirty;
  }

  public Cell setDirty(boolean dirty) {
    this.dirty = dirty;
    return this;
  }
}
