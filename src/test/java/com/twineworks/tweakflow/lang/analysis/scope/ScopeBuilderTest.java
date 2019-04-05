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

package com.twineworks.tweakflow.lang.analysis.scope;

import com.twineworks.tweakflow.lang.analysis.AnalysisSet;
import com.twineworks.tweakflow.lang.ast.expressions.FunctionNode;
import com.twineworks.tweakflow.lang.ast.expressions.LetNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.Loader;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.scope.LocalScope;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.scope.SymbolTarget;
import com.twineworks.tweakflow.lang.types.Types;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopeBuilderTest {

  private ModuleNode module;

  public ScopeBuilderTest() {

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();


    AnalysisSet space = new AnalysisSet(loadPath);
    String path = "fixtures/tweakflow/analysis/imports/scope.tf";
    Loader.load(loadPath, path, space.getUnits(), true);
    module = (ModuleNode) space.getUnits().get(path).getUnit();
    ScopeBuilder.buildScope(space, module);

  }

  @Test
  public void makes_module_local_scope() throws Exception {

    Scope moduleScope = module.getUnitScope();
    assertThat(moduleScope).isNotNull();
    assertThat(moduleScope).isInstanceOf(Symbol.class);
    Symbol symbol = (Symbol) moduleScope;

    assertThat(symbol.isScoped()).isTrue();
    assertThat(symbol.getTarget()).isSameAs(SymbolTarget.MODULE);

  }

  @Test
  public void module_local_scope_has_components() throws Exception {

    Scope moduleScope = module.getUnitScope();
    Map<String, Symbol> symbols = moduleScope.getSymbols();

    assertThat(symbols).containsKey("lib");
    assertThat(symbols.get("lib")).isInstanceOf(Symbol.class);

  }

  @Test
  public void makes_library_scoped_symbol() throws Exception {

    Scope moduleScope = module.getUnitScope();
    Map<String, Symbol> symbols = moduleScope.getSymbols();

    Symbol lib = symbols.get("lib");
    assertThat(lib.getName()).isEqualTo("lib");

    assertThat(lib.getEnclosingScope()).isSameAs(moduleScope);
    assertThat(lib.getScope()).isSameAs(moduleScope);
    assertThat(lib.isScoped()).isTrue();

  }

  @Test
  public void library_symbol_has_vars() throws Exception {

    Symbol lib = module.getUnitScope().getSymbols().get("lib");
    Map<String, Symbol> symbols = lib.getSymbols();

    // check that e0, e1, ... exist
    int maxVar = 2;

    for (int i = 0; i < maxVar; i++) {
      String n = "e"+i;
      Symbol e = symbols.get(n);
      assertThat(e.getName()).isEqualTo(n);
      assertThat(e.getScope()).isSameAs(lib);
      assertThat(e.getTarget()).isSameAs(SymbolTarget.VAR);
    }

  }

  /*
    e1: let {
        string a: "foo"
      }
      a;
  */

  @Test
  public void creates_local_scope_for_let() throws Exception {

    Symbol lib = module.getUnitScope().getSymbols().get("lib");

    VarDefNode e1 = module.getLibraries().get(0).getVars().getMap().get("e1");
    LetNode letNode = (LetNode) e1.getValueExpression();

    // these should be the same
    Scope letBindingsScope = letNode.getBindings().getScope();
    Scope letExpScope = letNode.getExpression().getScope();

    assertThat(letExpScope).isNotSameAs(lib);
    assertThat(letExpScope.getEnclosingScope()).isSameAs(lib);
    assertThat(letExpScope).isInstanceOf(LocalScope.class);

    assertThat(letBindingsScope).isSameAs(letExpScope);
    assertThat(letBindingsScope.getSymbols()).hasSize(1);
    assertThat(letBindingsScope.getSymbols().get("a").getTarget()).isSameAs(SymbolTarget.VAR);

    Symbol aVarSymbol = letBindingsScope.getSymbols().get("a");
    assertThat(aVarSymbol.getName()).isEqualTo("a");
    assertThat(aVarSymbol.getVarType()).isSameAs(Types.STRING);
    assertThat(aVarSymbol.getScope()).isSameAs(letBindingsScope);

  }

  // e2: (string a = "foo") -> a

  @Test
  public void creates_local_scope_for_function_def() throws Exception {

    Symbol lib = module.getUnitScope().getSymbols().get("lib");

    VarDefNode e2 = module.getLibraries().get(0).getVars().getMap().get("e2");
    FunctionNode functionNode = (FunctionNode) e2.getValueExpression();

    // these should be the same
    Scope paramScope = functionNode.getParameters().getScope();
    Scope bodyScope = functionNode.getExpression().getScope();

    assertThat(bodyScope).isNotSameAs(lib);
    assertThat(bodyScope.getEnclosingScope()).isSameAs(lib);
    assertThat(bodyScope).isInstanceOf(LocalScope.class);

    assertThat(paramScope).isSameAs(bodyScope);
    assertThat(paramScope.getSymbols()).hasSize(1);
    assertThat(paramScope.getSymbols().get("a").getTarget()).isSameAs(SymbolTarget.VAR);

    Symbol aVarSymbol = paramScope.getSymbols().get("a");
    assertThat(aVarSymbol.getName()).isEqualTo("a");
    assertThat(aVarSymbol.getVarType()).isSameAs(Types.STRING);
    assertThat(aVarSymbol.getScope()).isSameAs(paramScope);

  }


}