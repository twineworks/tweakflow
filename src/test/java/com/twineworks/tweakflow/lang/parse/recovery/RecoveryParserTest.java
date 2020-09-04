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

package com.twineworks.tweakflow.lang.parse.recovery;

import com.twineworks.tweakflow.lang.ast.NodeStructureAssert;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.StringNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.structure.LibraryNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.Parser;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Set;

import static org.assertj.core.api.StrictAssertions.assertThat;

class RecoveryParserTest {

  @Test
  void parses_empty_module() {
    Parser p = new Parser(
        new MemoryLocation.Builder().add("", "").build().getParseUnit(""),
        true
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    // module node is empty
    ModuleNode module = (ModuleNode) result.getNode();
    NodeStructureAssert.assertThat(module).isNotNull();
    assertThat(module.hasMeta()).isFalse();
    assertThat(module.hasDoc()).isFalse();

  }

  @Test
  public void parses_faulty_module_header() throws Exception {

/*
doc "Module documentation string"
module;

import * as m, lib_x as x, lib_y from "other/module" # missing semicolon gets repaired
import something lib_x as x from "other/module";     # bad syntax generates error nodes - import is not repairable
*/

    Parser p = new Parser(
        new ResourceParseUnit(
            new ResourceLocation.Builder().build(),
            "fixtures/tweakflow/analysis/recoveryparsing/module.tf"
        ),
        true
    );
    ParseResult result = p.parseUnit();

    // parse has errors
    assertThat(result.hasRecoveryErrors()).isTrue();
    ModuleNode module = (ModuleNode) result.getNode();

    // module has a doc string
    assertThat(module.hasDoc()).isTrue();
    ExpressionNode expression = module.getDoc().getExpression();
    NodeStructureAssert.assertThat(expression).isInstanceOf(StringNode.class);

    String docString = ((StringNode) expression).getStringVal();
    assertThat(docString).isEqualTo("Module documentation string");

    // module has no meta map
    assertThat(module.hasMeta()).isFalse();

    // module has a single import - the other one got lost
    assertThat(module.getImports().size()).isEqualTo(1);
    ImportNode importNode = module.getImports().get(0);

    // the repaired input node
    assertThat(importNode.getMembers().size()).isEqualTo(3);
    assertThat(importNode.getModulePath()).isInstanceOf(StringNode.class);
    StringNode modulePath = (StringNode) importNode.getModulePath();
    assertThat(modulePath.getStringVal()).isEqualTo("other/module");

    // module always starts at 1,1
    assertThat(module.getSourceInfo().getLine()).isEqualTo(1);
    assertThat(module.getSourceInfo().getCharWithinLine()).isEqualTo(1);

  }

  @Test
  void parses_faulty_var_expressions() {

    Parser p = new Parser(
        new ResourceParseUnit(
            new ResourceLocation.Builder().build(),
            "fixtures/tweakflow/analysis/recoveryparsing/faulty_vars.tf"
        ),
        true
    );
    ParseResult result = p.parseUnit();

    // parse has errors
    assertThat(result.hasRecoveryErrors()).isTrue();
    ModuleNode module = (ModuleNode) result.getNode();

    // but contains all variables
    assertThat(module.getLibraries().size()).isEqualTo(1);
    LibraryNode vars = module.getLibraries().get(0);
    LinkedHashMap<String, VarDefNode> varsMap = vars.getVars().getMap();

    assertThat(varsMap.size()).isEqualTo(9);

    Set<String> varNames = varsMap.keySet();
    assertThat("a").isIn(varNames);
    assertThat("b").isIn(varNames);
    assertThat("c").isIn(varNames);
    assertThat("d").isIn(varNames);
    assertThat("e").isIn(varNames);
    assertThat("f").isIn(varNames);
    assertThat("g").isIn(varNames);
    assertThat("h").isIn(varNames);
    assertThat("i").isIn(varNames);
  }
}