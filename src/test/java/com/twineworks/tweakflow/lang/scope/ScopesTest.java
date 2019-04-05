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

import com.twineworks.tweakflow.lang.analysis.Analysis;
import com.twineworks.tweakflow.lang.analysis.AnalysisResult;
import com.twineworks.tweakflow.lang.ast.ComponentNode;
import com.twineworks.tweakflow.lang.ast.expressions.LetNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.structure.LibraryNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopesTest {

  private AnalysisResult result;
  private ModuleNode module;

  public ScopesTest() {

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();

    result = Analysis.analyze(Collections.singletonList("fixtures/tweakflow/analysis/scopes/scope.tf"), loadPath);

    if (result.isSuccess()){
      module = (ModuleNode) result.getAnalysisSet().getUnits().get("fixtures/tweakflow/analysis/scopes/scope.tf").getUnit();
    }

  }

  @Test
  public void test_harness_is_valid() throws Exception {
    if (result.isError()){
      result.getException().printDigestMessageAndStackTrace();
    }
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void calculates_visible_symbols_in_module() throws Exception {

    Map<String, Symbol> visibleSymbols = Scopes.getVisibleSymbols(module.getUnitScope());

    assertThat(visibleSymbols).hasSize(5);
    assertThat(visibleSymbols.get("lib_a").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_b").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_c").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_empty").getTarget()).isSameAs(SymbolTarget.LIBRARY);

    assertThat(visibleSymbols.get("m").getTarget()).isSameAs(SymbolTarget.MODULE);

  }

  @Test
  public void calculates_visible_symbols_in_empty_library() throws Exception {

    LibraryNode lib_empty = (LibraryNode) module.getComponentsMap().get("lib_empty");
    Map<String, Symbol> visibleSymbols = Scopes.getVisibleSymbols(lib_empty.getScope());

    // sees everything the module declares, no own contributions
    assertThat(visibleSymbols).hasSize(5);
    assertThat(visibleSymbols.get("lib_a").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_b").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_c").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_empty").getTarget()).isSameAs(SymbolTarget.LIBRARY);

    assertThat(visibleSymbols.get("m").getTarget()).isSameAs(SymbolTarget.MODULE);
  }

//  library lib_b
//  {
//    a:     lib_a.a
//    b:     "b"
//    lib_c: "shadow"
//    d:     scope_module.lib_a.a
//  }

  @Test
  public void calculates_visible_symbols_shadowing_library() throws Exception {
    Symbol lib_b = module.getUnitScope().getSymbols().get("lib_b");
    Map<String, Symbol> visibleSymbols = Scopes.getVisibleSymbols(lib_b);

    // sees local vars, var "lib_c" shadowing the actual lib, and lib_a, lib_b, lib_empty
    assertThat(visibleSymbols).hasSize(8);
    assertThat(visibleSymbols.get("lib_a").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_b").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_empty").getTarget()).isSameAs(SymbolTarget.LIBRARY);

    assertThat(visibleSymbols.get("lib_c").getTarget()).isSameAs(SymbolTarget.VAR);
    assertThat(visibleSymbols.get("a").getTarget()).isSameAs(SymbolTarget.VAR);
    assertThat(visibleSymbols.get("b").getTarget()).isSameAs(SymbolTarget.VAR);
    assertThat(visibleSymbols.get("d").getTarget()).isSameAs(SymbolTarget.VAR);

    assertThat(visibleSymbols.get("m").getTarget()).isSameAs(SymbolTarget.MODULE);


  }

//  library lib_a
//  {
//    a: let {
//             a: "foo"
//           }
//           a;
//  }

  @Test
  public void calculates_visible_symbols_shadowing_var() throws Exception {

    LibraryNode lib_a = (LibraryNode) module.getComponentsMap().get("lib_a");
    LetNode letNode = (LetNode) lib_a.getVars().getMap().get("a").getValueExpression();

    Map<String, Symbol> visibleSymbols = Scopes.getVisibleSymbols(letNode.getExpression().getScope());

    // sees a as shadowed var, and lib_a, lib_b, lib_c, lib_empty
    assertThat(visibleSymbols).hasSize(6);
    assertThat(visibleSymbols.get("lib_a").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_b").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_c").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("lib_empty").getTarget()).isSameAs(SymbolTarget.LIBRARY);
    assertThat(visibleSymbols.get("m").getTarget()).isSameAs(SymbolTarget.MODULE);

    assertThat(visibleSymbols.get("a").getTarget()).isSameAs(SymbolTarget.VAR);

    // verify that a seen is shadowed
    Symbol a = visibleSymbols.get("a");
    assertThat(a.getNode()).isSameAs(letNode.getBindings().getVars().getMap().get("a"));
    assertThat(a.getScope()).isSameAs(letNode.getBindings().getScope());

  }

  @Test
  public void resolves_module_reference() throws Exception {

    Map<String, ComponentNode> componentsMap = module.getComponentsMap();
    LibraryNode lib_c = (LibraryNode) componentsMap.get("lib_c");

    VarDefNode a = lib_c.getVars().getMap().get("a");
    ReferenceNode aRef = (ReferenceNode) a.getValueExpression();
    assertThat(aRef.getAnchor()).isSameAs(ReferenceNode.Anchor.MODULE);
    assertThat(aRef.getReferencedSymbol()).isNotNull();

    // verify ::lib_a.a points to lib_a.a
    Symbol symbol_lib_a = module.getUnitScope().getSymbols().get("lib_a");
    assertThat(aRef.getReferencedSymbol()).isSameAs(symbol_lib_a.getSymbols().get("a"));

  }

  @Test
  public void resolves_state_reference() throws Exception {

    Map<String, ComponentNode> componentsMap = module.getComponentsMap();
    LibraryNode lib_c = (LibraryNode) componentsMap.get("lib_c");

    VarDefNode b = lib_c.getVars().getMap().get("b");
    ReferenceNode bRef = (ReferenceNode) b.getValueExpression();
    assertThat(bRef.getAnchor()).isSameAs(ReferenceNode.Anchor.LIBRARY);
    assertThat(bRef.getReferencedSymbol()).isNotNull();

    // verify @a points to lib_c.a
    Symbol symbol_lib_c = module.getUnitScope().getSymbols().get("lib_c");
    assertThat(bRef.getReferencedSymbol()).isSameAs(symbol_lib_c.getSymbols().get("a"));

  }

  @Test
  public void resolves_aliased_reference() throws Exception {

    Map<String, ComponentNode> componentsMap = module.getComponentsMap();
    LibraryNode lib_c = (LibraryNode) componentsMap.get("lib_c");

    VarDefNode m_a = lib_c.getVars().getMap().get("m_a");
    ReferenceNode m_aRef = (ReferenceNode) m_a.getValueExpression();
    assertThat(m_aRef.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    assertThat(m_aRef.getReferencedSymbol()).isNotNull();

    // verify @m_a points to lib_a.a
    Symbol symbol_lib_a = module.getUnitScope().getSymbols().get("lib_a");
    assertThat(m_aRef.getReferencedSymbol()).isSameAs(symbol_lib_a.getSymbols().get("a"));

  }

}