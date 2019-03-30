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

import com.twineworks.tweakflow.lang.ast.*;
import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportMemberNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.meta.DocNode;
import com.twineworks.tweakflow.lang.ast.meta.MetaNode;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleNode implements MetaDataNode, SymbolNode, UnitNode {

  private DocNode docNode;
  private MetaNode metaNode;
  private List<ImportNode> imports = new ArrayList<>();
  private List<ExportNode> exports = new ArrayList<>();
  private List<AliasNode> aliases = new ArrayList<>();
  private List<ComponentNode> components = new ArrayList<>();
  private String globalName;

  private SourceInfo sourceInfo;
  private Scope scope;
  private Symbol unitSymbol;

  public List<ExportNode> getExports() {
    return exports;
  }

  public ModuleNode setExports(List<ExportNode> exports) {
    this.exports = exports;
    return this;
  }

  public String getGlobalName() {
    return globalName;
  }

  public ModuleNode setGlobalName(String globalName) {
    this.globalName = globalName;
    return this;
  }

  public boolean isGlobal(){
    return this.globalName != null;
  }

  public List<ImportNode> getImports() {
    return imports;
  }

  public ModuleNode setImports(List<ImportNode> imports) {
    this.imports = imports;
    return this;
  }

  public List<AliasNode> getAliases() {
    return aliases;
  }

  public ModuleNode setAliases(List<AliasNode> aliases) {
    this.aliases = aliases;
    return this;
  }

  @Override
  public DocNode getDoc() {
    return docNode;
  }

  @Override
  public MetaNode getMeta() {
    return metaNode;
  }

  @Override
  public ModuleNode setDoc(DocNode docNode) {
    this.docNode = docNode;
    return this;
  }

  @Override
  public ModuleNode setMeta(MetaNode metaNode) {
    this.metaNode = metaNode;
    return this;
  }

  @Override
  public boolean hasDoc() {
    return docNode != null;
  }

  @Override
  public boolean hasMeta() {
    return metaNode != null;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public ModuleNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    List<Node> ret = new ArrayList<>();
    if (metaNode != null) ret.add(metaNode);
    if (docNode != null) ret.add(docNode);
    ret.addAll(imports);
    ret.addAll(aliases);
    ret.addAll(exports);
    ret.addAll(components);
    return ret;
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public Node setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  public ModuleNode setUnitSymbol(Symbol unitSymbol) {
    this.unitSymbol = unitSymbol;
    return this;
  }

  public Symbol getSymbol(){
    return unitSymbol;
  }

  public Scope getUnitScope(){
    return unitSymbol;
  }

  @Override
  public Scope getPublicScope() {
    return unitSymbol.getPublicScope();
  }

  @Override
  public ModuleNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  public List<ComponentNode> getComponents() {
    return components;
  }

  public Map<String, ComponentNode> getComponentsMap(){

    HashMap<String, ComponentNode> result = new HashMap<>();
    for (ComponentNode component : components) {
      result.put(component.getSymbolName(), component);
    }
    return result;
  }

  public Map<String, ImportMemberNode> getImportsMap(){
    HashMap<String, ImportMemberNode> result = new HashMap<>();
    for (ImportNode importNode : getImports()) {
      for (ImportMemberNode importMemberNode : importNode.getMembers()) {
        result.put(importMemberNode.getSymbolName(), importMemberNode);
      }
    }
    return result;

  }

  public ModuleNode setComponents(List<ComponentNode> components) {
    this.components = components;
    return this;
  }

  public List<LibraryNode> getLibraries() {

    return components.stream()
        .filter(component -> component instanceof LibraryNode)
        .map(component -> (LibraryNode) component)
        .collect(Collectors.toList());
  }

  @Override
  public String getSymbolName() {
    return "";
  }

  public Symbol getExportSymbol(){
    return unitSymbol;
  }

  @Override
  public SymbolNode setSymbolName(String name) {
    throw new AssertionError("cannot set symbol name");
  }


}
