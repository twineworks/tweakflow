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

package com.twineworks.tweakflow.lang.analysis;

import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.scope.SymbolTarget;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AnalysisRelativeResourceImportsTest {

  private AnalysisResult result;
  private AnalysisSet analysisSet;
  private ModuleNode module;

  public AnalysisRelativeResourceImportsTest() {

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();

    List<String> paths = Collections.singletonList("fixtures/tweakflow/analysis/imports/main.tf");
    result = Analysis.analyze(paths, loadPath);

    if (result.isSuccess()){
      analysisSet = result.getAnalysisSet();
      module = (ModuleNode) analysisSet.getUnits().get("fixtures/tweakflow/analysis/imports/main.tf").getUnit();
    }

  }

  @Test
  public void analysis_successful() throws Exception {

    if (result.isError()){
      result.getException().printDigest();
      System.err.flush();
      result.getException().printStackTrace();
      System.err.flush();
    }
    // compilation is successful
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void creates_compilation_units_for_imports() throws Exception {

    // library in main exists
    assertThat(module.getComponents()).hasSize(1);

    // main and imported modules exist in module space
    Map<String, AnalysisUnit> modules = analysisSet.getUnits();
    assertThat(modules).hasSize(4);
    assertThat(modules).containsKey("fixtures/tweakflow/analysis/imports/main.tf");
    assertThat(modules).containsKey("fixtures/tweakflow/analysis/imports/libs/module_a.tf");
    assertThat(modules).containsKey("fixtures/tweakflow/analysis/imports/libs/module_b.tf");
    assertThat(modules).containsKey("fixtures/tweakflow/analysis/imports/libs/module_c.tf");

    AnalysisUnit aUnit = modules.get("fixtures/tweakflow/analysis/imports/libs/module_a.tf");
    AnalysisUnit bUnit = modules.get("fixtures/tweakflow/analysis/imports/libs/module_b.tf");
    AnalysisUnit cUnit = modules.get("fixtures/tweakflow/analysis/imports/libs/module_c.tf");

    // main imports are module_a, module_a and module_b
    List<ImportNode> imports = module.getImports();
    assertThat(imports.get(0).getImportedUnit()).isSameAs(aUnit);
    assertThat(imports.get(1).getImportedUnit()).isSameAs(aUnit);
    assertThat(imports.get(2).getImportedUnit()).isSameAs(bUnit);

    // module_b imports module_c
    assertThat(((ModuleNode)bUnit.getUnit()).getImports().get(0).getImportedUnit()).isSameAs(cUnit);

    // module_c imports module_b (syntactic cyclic dependency allowed)
    assertThat(((ModuleNode)cUnit.getUnit()).getImports().get(0).getImportedUnit()).isSameAs(bUnit);

  }

  @Test
  public void module_has_imported_items_in_scope() throws Exception {

    Map<String, Symbol> importedSymbols = module.getUnitScope().getSymbols();

    assertThat(importedSymbols).containsKeys("module_a", "l_a", "lib_b");

    // verify module_a.lib_a is present
    assertThat(importedSymbols.get("module_a").isImport()).isTrue();
    Symbol module_a = importedSymbols.get("module_a");
    assertThat(module_a.getRef().getTarget()).isSameAs(SymbolTarget.MODULE);
    assertThat(module_a.getSymbols()).hasSize(1);
    assertThat(module_a.getSymbols().get("lib_a")).isInstanceOf(Symbol.class);

    // verify l_a is present
    assertThat(importedSymbols.get("l_a").getRef()).isNotNull();

    // verify lib_b is present
    assertThat(importedSymbols.get("lib_b").getRef()).isNotNull();

  }

}