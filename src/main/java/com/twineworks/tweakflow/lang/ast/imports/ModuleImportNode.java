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

import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;

import java.util.Collections;
import java.util.List;

public class ModuleImportNode implements ImportMemberNode {

  private String importName;
  private SourceInfo sourceInfo;
  private AnalysisUnit importedCompilationUnit;
  private Scope scope;

  @Override
  public ModuleImportNode copy() {
    ModuleImportNode copy = new ModuleImportNode();
    copy.sourceInfo = sourceInfo;
    copy.importName = importName;
    return copy;
  }

  public AnalysisUnit getImportedCompilationUnit() {
    return importedCompilationUnit;
  }

  public ModuleImportNode setImportedCompilationUnit(AnalysisUnit importedCompilationUnit) {
    this.importedCompilationUnit = importedCompilationUnit;
    return this;
  }

  public String getImportName() {
    return importName;
  }

  public ModuleImportNode setImportName(String importName) {
    this.importName = importName;
    return this;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public ModuleImportNode setSourceInfo(SourceInfo sourceInfo) {
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
  public ModuleImportNode setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public ModuleImportNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public String getSymbolName() {
    return getImportName();
  }

  @Override
  public ModuleImportNode setSymbolName(String name) {
    return setImportName(name);
  }

  @Override
  public Symbol getSymbol(){
    return scope.getSymbols().get(getSymbolName());
  }
}
