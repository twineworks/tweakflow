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

package com.twineworks.tweakflow.lang.ast.imports;

import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;

import java.util.Collections;
import java.util.List;

public class NameImportNode implements ImportMemberNode {

  private String importName;
  private String exportName;
  private SourceInfo sourceInfo;
  private AnalysisUnit importedCompilationUnit;
  private Scope scope;

  @Override
  public NameImportNode copy() {
    NameImportNode copy = new NameImportNode();
    copy.importName = importName;
    copy.exportName = exportName;
    copy.sourceInfo = sourceInfo;
    return copy;
  }


  public AnalysisUnit getImportedCompilationUnit() {
    return importedCompilationUnit;
  }

  public NameImportNode setImportedCompilationUnit(AnalysisUnit importedCompilationUnit) {
    this.importedCompilationUnit = importedCompilationUnit;
    return this;
  }

  public String getImportName() {
    return importName;
  }

  public NameImportNode setImportName(String importName) {
    this.importName = importName;
    return this;
  }

  public String getExportName() {
    return exportName;
  }

  public NameImportNode setExportName(String exportName) {
    this.exportName = exportName;
    return this;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public NameImportNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public NameImportNode setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public NameImportNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public String getSymbolName() {
    return importName;
  }

  @Override
  public Symbol getSymbol(){
    return scope.getSymbols().get(importName);
  }

  @Override
  public NameImportNode setSymbolName(String name) {
    return setImportName(name);
  }
}
