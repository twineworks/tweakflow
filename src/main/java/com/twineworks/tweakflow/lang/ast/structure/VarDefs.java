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

import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class VarDefs implements Node {

  private LinkedHashMap<String, VarDefNode> map = new LinkedHashMap<>();

  private VarDefNode[] array;
  private SourceInfo sourceInfo;
  private Scope scope;
  private Set<ShapeKey> shapeKeys;

  public LinkedHashMap<String, VarDefNode> getMap() {
    return map;
  }

  public VarDefs setMap(LinkedHashMap<String, VarDefNode> map) {
    this.map = map;
    return this;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public VarDefs setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    List<Node> ret = new ArrayList<>();
    ret.addAll(map.values());
    return ret;
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public VarDefs setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public VarDefs accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public VarDefs copy() {
    VarDefs copy = new VarDefs();
    copy.sourceInfo = sourceInfo;
    for (String s : map.keySet()) {
      copy.map.put(s, map.get(s).copy());
    }
    copy.cook();
    return copy;
  }

  public void cook(){
    // make alternative representations
    array = new VarDefNode[map.size()];
    map.values().toArray(array);
    shapeKeys = ShapeKey.getAll(map.keySet());
  }

  public VarDefNode[] getArray(){
    return array;
  }

  public VarDefNode[]  getDependencyOrderedArray(){
    VarDefNode[] a = new VarDefNode[map.size()];

    List<Symbol> dependencyOrderedSymbols = scope.getDependencyOrderedSymbols();

    for (int i = 0; i < dependencyOrderedSymbols.size(); i++) {
      Symbol symbol = dependencyOrderedSymbols.get(i);
      a[i] = map.get(symbol.getName());
    }
    return a;

  }

  public Set<ShapeKey> getShapeKeys() {
    return shapeKeys;
  }
}
