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

package com.twineworks.tweakflow.lang.analysis.scope;

import com.twineworks.tweakflow.lang.analysis.Analysis;
import com.twineworks.tweakflow.lang.analysis.AnalysisResult;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.scope.SymbolTarget;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkerTest {

  public AnalysisResult compile(String path) throws Exception {

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();

    return Analysis.analyze(Collections.singletonList(path), loadPath);

  }

  @Test
  public void links_simple_local_module_alias() throws Exception {

    String src = "fixtures/tweakflow/analysis/aliases/simple_mod_alias.tf";

    AnalysisResult result = compile(src);
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(src).getUnit();
    Symbol alias = module.getUnitScope().getSymbols().get("m");
    assertThat(alias.isAlias()).isTrue();
    assertThat(alias.isRefResolved()).isTrue();
    assertThat(alias.getTarget()).isSameAs(SymbolTarget.MODULE);
  }

  @Test
  public void error_on_unknown_module_alias() throws Exception {

    String src = "fixtures/tweakflow/analysis/aliases/unknown_simple_mod_alias.tf";

    AnalysisResult result = compile(src);
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);

  }

  @Test
  public void error_on_cyclic_module_alias() throws Exception {

    String src = "fixtures/tweakflow/analysis/aliases/cyclic_mod_alias.tf";

    AnalysisResult result = compile(src);
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isSameAs(LangError.ALREADY_DEFINED);

  }

  @Test
  public void links_chained_local_module_alias() throws Exception {

    String src = "fixtures/tweakflow/analysis/aliases/chained_mod_alias.tf";

    AnalysisResult result = compile(src);
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(src).getUnit();
    Symbol alias = module.getUnitScope().getSymbols().get("p");
    assertThat(alias.isAlias()).isTrue();
    assertThat(alias.isRefResolved()).isTrue();
    assertThat(alias.getTarget()).isSameAs(SymbolTarget.MODULE);
  }


  @Test
  public void links_simple_local_library_alias() throws Exception {

    String src = "fixtures/tweakflow/analysis/aliases/simple_lib_alias.tf";

    AnalysisResult result = compile(src);
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(src).getUnit();
    Symbol alias = module.getUnitScope().getSymbols().get("a_lib");
    assertThat(alias.isAlias()).isTrue();
    assertThat(alias.isRefResolved()).isTrue();
    assertThat(alias.getTarget()).isSameAs(SymbolTarget.LIBRARY);
  }

  @Test
  public void links_chained_local_library_alias() throws Exception {

    String src = "fixtures/tweakflow/analysis/aliases/chained_lib_alias.tf";

    AnalysisResult result = compile(src);
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(src).getUnit();
    Symbol alias = module.getUnitScope().getSymbols().get("d_lib");

    assertThat(alias.isAlias()).isTrue();
    assertThat(alias.isRefResolved()).isTrue();
    assertThat(alias.getTarget()).isSameAs(SymbolTarget.LIBRARY);
  }

  @Test
  public void links_simple_exports() throws Exception {

    String src = "fixtures/tweakflow/analysis/exports/import_simple_export.tf";

    AnalysisResult result = compile(src);
    if (result.isError()) result.getException().printDigestMessageAndStackTrace();
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(src).getUnit();
    Map<String, Symbol> symbols = module.getUnitScope().getSymbols();

    Symbol a = symbols.get("a");
    assertThat(a.isImport()).isTrue();
    assertThat(a.isRefResolved()).isTrue();
    assertThat(a.getTarget()).isSameAs(SymbolTarget.LIBRARY);

    Symbol b = symbols.get("b");
    assertThat(b.isImport()).isTrue();
    assertThat(b.isRefResolved()).isTrue();
    assertThat(b.getTarget()).isSameAs(SymbolTarget.LIBRARY);

    Symbol m_a = symbols.get("m_a");
    assertThat(m_a.isImport()).isTrue();
    assertThat(m_a.isRefResolved()).isTrue();
    assertThat(m_a.getTarget()).isSameAs(SymbolTarget.MODULE);

    Symbol m_b = symbols.get("m_b");
    assertThat(m_b.isImport()).isTrue();
    assertThat(m_b.isRefResolved()).isTrue();
    assertThat(m_b.getTarget()).isSameAs(SymbolTarget.MODULE);

    // verify that a.a and m_a.library_a.a point to the same thing
    Symbol a_a = a.getSymbols().get("a");
    Symbol m_a_library_a_a = m_a.getSymbols().get("library_a").getSymbols().get("a");

    assertThat(a_a).isSameAs(m_a_library_a_a);

  }

  @Test
  public void links_reexports() throws Exception {

    String src = "fixtures/tweakflow/analysis/exports/import_reexports.tf";

    AnalysisResult result = compile(src);
    if (result.isError()) result.getException().printDigestMessageAndStackTrace();
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(src).getUnit();
    Map<String, Symbol> symbols = module.getUnitScope().getSymbols();

    Symbol conf_a = symbols.get("conf_a");
    assertThat(conf_a.isImport()).isTrue();
    assertThat(conf_a.isRefResolved()).isTrue();
    assertThat(conf_a.getTarget()).isSameAs(SymbolTarget.LIBRARY);

    Symbol conf_b = symbols.get("conf_b");
    assertThat(conf_b.isImport()).isTrue();
    assertThat(conf_b.isRefResolved()).isTrue();
    assertThat(conf_b.getTarget()).isSameAs(SymbolTarget.LIBRARY);

    // verify conf_a.e0 is present
    assertThat(conf_a.getSymbols().get("e0")).isNotNull();

  }

  @Test
  public void error_on_cyclic_import() throws Exception {

    String src = "fixtures/tweakflow/analysis/exports/cycle/cycle_a.tf";

    AnalysisResult result = compile(src);
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isSameAs(LangError.CYCLIC_REFERENCE);

  }


  @Test
  public void links_mutual_imports() throws Exception {

    String src = "fixtures/tweakflow/analysis/exports/mutual/mutual_a.tf";

    AnalysisResult result = compile(src);
    if (result.isError()) result.getException().printDigestMessageAndStackTrace();
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(src).getUnit();
    Map<String, Symbol> symbols = module.getUnitScope().getSymbols();

    Symbol b = symbols.get("b");
    assertThat(b.isImport()).isTrue();
    assertThat(b.isRefResolved()).isTrue();
    assertThat(b.getTarget()).isSameAs(SymbolTarget.LIBRARY);

    Symbol a = symbols.get("a");
    assertThat(a.isLocal()).isTrue();
    assertThat(a.getTarget()).isSameAs(SymbolTarget.LIBRARY);

  }


}