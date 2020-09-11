/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Twineworks GmbH
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

package com.twineworks.tweakflow.lang.analysis;

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.structure.LibraryNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.ScopeType;
import com.twineworks.tweakflow.lang.scope.Symbol;
import org.assertj.core.api.StrictAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnalysisInspectorTest {

  private final String VALID_MODULE_PATH = "fixtures/tweakflow/analysis/scopes/items_in_scope.tf";
  private final String INVALID_MODULE_PATH = "fixtures/tweakflow/analysis/scopes/items_in_scope_invalid.tf";
  private AnalysisResult validModule;
  private AnalysisResult invalidModule;

  private AnalysisResult analyze(String... paths) {

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();

    List<String> filePaths = new ArrayList<>(paths.length);
    filePaths.addAll(Arrays.asList(paths));

    return Analysis.recoveryAnalysis(filePaths, loadPath, false);
  }

  private AnalysisResult getValidModuleAnalysis() {
    if (validModule == null) {
      validModule = analyze(VALID_MODULE_PATH);
      StrictAssertions.assertThat(validModule.hasRecoveryErrors()).isFalse();
    }
    return validModule;
  }

  private AnalysisResult getInvalidModuleAnalysis() {
    if (invalidModule == null) {
      invalidModule = analyze(INVALID_MODULE_PATH);
      StrictAssertions.assertThat(invalidModule.hasRecoveryErrors()).isTrue();
    }
    return invalidModule;
  }

  @Test
  void finds_module_node_from_position_in_valid_module() {
    AnalysisResult a = getValidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Node foundNode = AnalysisInspector.getNodeAt(s, VALID_MODULE_PATH, 1, 1);
    assertThat(foundNode).isInstanceOf(ModuleNode.class);
  }

  @Test
  void finds_library_node_from_position_in_valid_module() {
    AnalysisResult a = getValidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Node foundNode = AnalysisInspector.getNodeAt(s, VALID_MODULE_PATH, 6, 1);
    assertThat(foundNode).isInstanceOf(LibraryNode.class);
    LibraryNode lib = (LibraryNode) foundNode;
    assertThat(lib.getSymbolName()).isEqualTo("lib_a");
  }

  @Test
  void finds_var_node_from_position_in_valid_module() {
    AnalysisResult a = getValidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Node foundNode = AnalysisInspector.getNodeAt(s, VALID_MODULE_PATH, 23, 4);
    assertThat(foundNode).isInstanceOf(VarDefNode.class);
    VarDefNode varDef = (VarDefNode) foundNode;
    assertThat(varDef.getSymbolName()).isEqualTo("c");
  }

  @Test
  void finds_expression_node_from_position_in_valid_module() {
    AnalysisResult a = getValidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Node foundNode = AnalysisInspector.getNodeAt(s, VALID_MODULE_PATH, 23, 18);
    assertThat(foundNode).isInstanceOf(ReferenceNode.class);
    ReferenceNode refNode = (ReferenceNode) foundNode;
    assertThat(refNode.getAnchor()).isEqualTo(ReferenceNode.Anchor.GLOBAL);
    assertThat(refNode.getElements()).contains("scope_module", "lib_a", "a");
  }

  @Test
  void finds_lib_scope_at_position_in_valid_module() {
    AnalysisResult a = getValidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Scope scope = AnalysisInspector.getScopeAt(s, VALID_MODULE_PATH, 23, 18);
    assertThat(scope).isNotNull();
    // it should find lib_c scope
    assertThat(scope.getScopeType()).isEqualTo(ScopeType.SYMBOL);
    assertThat(scope.isLibrary()).isTrue();
    Symbol scopeSymbol = (Symbol) scope;
    assertThat(scopeSymbol.getName()).isEqualTo("lib_c");
  }

  @Test
  void finds_local_scope_at_position_in_valid_module() {
    AnalysisResult a = getValidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Scope scope = AnalysisInspector.getScopeAt(s, VALID_MODULE_PATH, 26, 5);
    assertThat(scope).isNotNull();
    // it should find local scope of let
    assertThat(scope.getScopeType()).isEqualTo(ScopeType.LOCAL);
    assertThat(scope.getSymbols().containsKey("foo")).isTrue();
  }

  @Test
  void finds_module_node_from_position_in_invalid_module() {
    AnalysisResult a = getInvalidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Node foundNode = AnalysisInspector.getNodeAt(s, INVALID_MODULE_PATH, 1, 1);
    assertThat(foundNode).isInstanceOf(ModuleNode.class);
  }

  @Test
  void finds_library_node_from_position_in_invalid_module() {
    AnalysisResult a = getInvalidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Node foundNode = AnalysisInspector.getNodeAt(s, INVALID_MODULE_PATH, 6, 1);
    assertThat(foundNode).isInstanceOf(LibraryNode.class);
    LibraryNode lib = (LibraryNode) foundNode;
    assertThat(lib.getSymbolName()).isEqualTo("lib_a");
  }

  @Test
  void finds_var_node_from_position_in_invalid_module() {
    AnalysisResult a = getInvalidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Node foundNode = AnalysisInspector.getNodeAt(s, INVALID_MODULE_PATH, 23, 4);
    assertThat(foundNode).isInstanceOf(VarDefNode.class);
    VarDefNode varDef = (VarDefNode) foundNode;
    assertThat(varDef.getSymbolName()).isEqualTo("c");
  }

  @Test
  void finds_expression_node_from_position_in_invalid_module() {
    AnalysisResult a = getInvalidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Node foundNode = AnalysisInspector.getNodeAt(s, INVALID_MODULE_PATH, 23, 18);
    assertThat(foundNode).isInstanceOf(ReferenceNode.class);
    ReferenceNode refNode = (ReferenceNode) foundNode;
    assertThat(refNode.getAnchor()).isEqualTo(ReferenceNode.Anchor.GLOBAL);
    assertThat(refNode.getElements()).contains("scope_module", "lib_a", "a");
  }

  @Test
  void finds_lib_scope_at_position_in_invalid_module() {
    AnalysisResult a = getInvalidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Scope scope = AnalysisInspector.getScopeAt(s, INVALID_MODULE_PATH, 23, 18);
    assertThat(scope).isNotNull();
    // it should find lib_c scope
    assertThat(scope.getScopeType()).isEqualTo(ScopeType.SYMBOL);
    assertThat(scope.isLibrary()).isTrue();
    Symbol scopeSymbol = (Symbol) scope;
    assertThat(scopeSymbol.getName()).isEqualTo("lib_c");
  }

  @Test
  void finds_local_scope_at_position_in_invalid_module() {
    AnalysisResult a = getInvalidModuleAnalysis();
    AnalysisSet s = a.getAnalysisSet();
    Scope scope = AnalysisInspector.getScopeAt(s, INVALID_MODULE_PATH, 26, 5);
    assertThat(scope).isNotNull();
    // it should find local scope of let
    assertThat(scope.getScopeType()).isEqualTo(ScopeType.LOCAL);
    assertThat(scope.getSymbols().containsKey("foo")).isTrue();
  }

}