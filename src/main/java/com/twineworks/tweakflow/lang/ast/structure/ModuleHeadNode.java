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
import com.twineworks.tweakflow.lang.ast.MetaDataNode;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.meta.DocNode;
import com.twineworks.tweakflow.lang.ast.meta.MetaNode;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleHeadNode implements MetaDataNode {

  private DocNode docNode;
  private MetaNode metaNode;
  private List<ImportNode> imports = new ArrayList<>();
  private List<ExportNode> exports = new ArrayList<>();
  private List<AliasNode> aliases = new ArrayList<>();
  private String globalName;

  private SourceInfo sourceInfo;

  public List<ExportNode> getExports() {
    return exports;
  }

  public ModuleHeadNode setExports(List<ExportNode> exports) {
    this.exports = exports;
    return this;
  }

  public String getGlobalName() {
    return globalName;
  }

  public ModuleHeadNode setGlobalName(String globalName) {
    this.globalName = globalName;
    return this;
  }

  public boolean isGlobal(){
    return this.globalName != null;
  }

  public List<ImportNode> getImports() {
    return imports;
  }

  public ModuleHeadNode setImports(List<ImportNode> imports) {
    this.imports = imports;
    return this;
  }

  public List<AliasNode> getAliases() {
    return aliases;
  }

  public ModuleHeadNode setAliases(List<AliasNode> aliases) {
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
  public ModuleHeadNode setDoc(DocNode docNode) {
    this.docNode = docNode;
    return this;
  }

  @Override
  public ModuleHeadNode setMeta(MetaNode metaNode) {
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
  public ModuleHeadNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public Scope getScope() {
    return null;
  }

  @Override
  public Node setScope(Scope scope) {
    return this;
  }

  @Override
  public ModuleHeadNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public ModuleHeadNode copy() {
    ModuleHeadNode copy = new ModuleHeadNode();
    copy.sourceInfo = sourceInfo;
    copy.docNode = docNode == null ? null : docNode.copy();
    copy.metaNode = metaNode == null ? null : metaNode.copy();
    copy.globalName = globalName;

    for (AliasNode alias : aliases) {
      copy.aliases.add(alias.copy());
    }

    for (ImportNode anImport : imports) {
      copy.imports.add(anImport.copy());
    }

    for (ExportNode export : exports) {
      copy.exports.add(export.copy());
    }

    return copy;
  }


}
