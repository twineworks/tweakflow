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

package com.twineworks.tweakflow.lang.parse;

import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.imports.ModuleImportNode;
import com.twineworks.tweakflow.lang.ast.imports.NameImportNode;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest {

  private HashMap<String, Map<String, VarDefNode>> moduleCache = new HashMap<>();

  private synchronized Map<String, VarDefNode> getVars(String ofModule) {
    if (!moduleCache.containsKey(ofModule)){
      Parser p = new Parser(
          new ResourceParseUnit(new ResourceLocation.Builder().build(), ofModule)
      );
      ParseResult result = p.parseUnit();

      if (result.isError()){
        result.getException().printDigestMessageAndStackTrace();
      }
      // parse is successful
      assertThat(result.isSuccess()).isTrue();

      // get the variable map
      Map<String, VarDefNode> varMap = ((ModuleNode) result.getNode()).getLibraries().get(0).getVars().getMap();
      moduleCache.put(ofModule, varMap);
    }
    return moduleCache.get(ofModule);

  }

  @Test
  public void parses_empty_module() throws Exception {

    Parser p = new Parser(new MemoryLocation.Builder().add("", "").build().getParseUnit(""));
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    // module node is empty
    ModuleNode module = (ModuleNode) result.getNode();
    assertThat(module).isNotNull();
    assertThat(module.hasMeta()).isFalse();
    assertThat(module.hasDoc()).isFalse();

  }

  @Test
  public void parses_module_metadata() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), "fixtures/tweakflow/analysis/parsing/module.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();
    ModuleNode module = (ModuleNode) result.getNode();

    // module has a doc string
    assertThat(module.hasDoc()).isTrue();
    ExpressionNode expression = module.getDoc().getExpression();
    assertThat(expression).isInstanceOf(StringNode.class);

    String docString = ((StringNode) expression).getStringVal();
    assertThat(docString).isEqualTo("Module documentation string");

    // module has meta map
    assertThat(module.hasMeta()).isTrue();
    assertThat(module.getMeta().getExpression()).isInstanceOf(DictNode.class);

    // module always starts at 1,1
    assertThat(module.getSourceInfo().getLine()).isEqualTo(1);
    assertThat(module.getSourceInfo().getCharWithinLine()).isEqualTo(1);

  }

  @Test
  public void parses_module_imports() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), "fixtures/tweakflow/analysis/parsing/module.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();
    ModuleNode module = (ModuleNode) result.getNode();

    assertThat(module.getImports()).hasSize(3);

    // module import as x
    // import * as x from "other/module"
    ImportNode i0 = module.getImports().get(0);
    assertThat(i0.getModulePath()).isInstanceOf(StringNode.class);
    String i0_path = ((StringNode)i0.getModulePath()).getStringVal();
    assertThat(i0_path).isEqualTo("other/module");

    assertThat(i0.getMembers()).hasSize(1);
    assertThat(i0.getMembers().get(0)).isInstanceOf(ModuleImportNode.class);
    ModuleImportNode i0_m0 = (ModuleImportNode) i0.getMembers().get(0);
    assertThat(i0_m0.getImportName()).isEqualTo("x");

    // component import as x
    // import my_lib as x from "other/module"
    ImportNode i1 = module.getImports().get(1);
    assertThat(i1.getModulePath()).isInstanceOf(StringNode.class);
    String i1_path = ((StringNode)i1.getModulePath()).getStringVal();
    assertThat(i1_path).isEqualTo("other/module");

    assertThat(i1.getMembers()).hasSize(1);
    assertThat(i1.getMembers().get(0)).isInstanceOf(NameImportNode.class);
    NameImportNode i1_m1 = (NameImportNode) i1.getMembers().get(0);
    assertThat(i1_m1.getExportName()).isEqualTo("lib_x");
    assertThat(i1_m1.getImportName()).isEqualTo("x");

    // 1 module import, 1 component import with explicit import name, 1 component import with implicit import name
    // import * as m, lib_x as x, lib_y as y from "other/module"
    ImportNode i2 = module.getImports().get(2);
    assertThat(i2.getModulePath()).isInstanceOf(StringNode.class);
    String i2_path = ((StringNode)i2.getModulePath()).getStringVal();
    assertThat(i2_path).isEqualTo("other/module");

    assertThat(i2.getMembers()).hasSize(3);
    assertThat(i2.getMembers().get(0)).isInstanceOf(ModuleImportNode.class);
    ModuleImportNode i2_m0 = (ModuleImportNode) i2.getMembers().get(0);
    assertThat(i2_m0.getImportName()).isEqualTo("m");

    assertThat(i2.getMembers().get(1)).isInstanceOf(NameImportNode.class);
    NameImportNode i2_m1 = (NameImportNode) i2.getMembers().get(1);
    assertThat(i2_m1.getExportName()).isEqualTo("lib_x");
    assertThat(i2_m1.getImportName()).isEqualTo("x");

    assertThat(i2.getMembers().get(2)).isInstanceOf(NameImportNode.class);
    NameImportNode i2_m2 = (NameImportNode) i2.getMembers().get(2);
    assertThat(i2_m2.getExportName()).isEqualTo("lib_y");
    assertThat(i2_m2.getImportName()).isEqualTo("lib_y");

  }

  @Test
  public void parses_module_aliases() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), "fixtures/tweakflow/analysis/parsing/module.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();
    ModuleNode module = (ModuleNode) result.getNode();

    assertThat(module.getAliases()).hasSize(1);

    // alias m as q
    AliasNode i0 = module.getAliases().get(0);
    assertThat(i0.getSymbolName()).isEqualTo("q");
    assertThat(i0.getSource().getElements()).hasSize(1);
    String ref = i0.getSource().getElements().get(0);
    assertThat(ref).isEqualTo("m");

  }

  @Test
  public void parses_module_explicitly_named_exports() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), "fixtures/tweakflow/analysis/parsing/module.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();
    ModuleNode module = (ModuleNode) result.getNode();

    assertThat(module.getExports()).hasSize(2);

    // export m as my_mod
    ExportNode i1 = module.getExports().get(1);
    assertThat(i1.getSymbolName()).isEqualTo("my_mod");
    assertThat(i1.getSource().getElements()).hasSize(1);
    String ref = i1.getSource().getElements().get(0);
    assertThat(ref).isEqualTo("m");

  }

  @Test
  public void parses_module_implicitly_named_exports() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), "fixtures/tweakflow/analysis/parsing/module.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();
    ModuleNode module = (ModuleNode) result.getNode();

    assertThat(module.getExports()).hasSize(2);

    // export m as my_mod
    ExportNode i1 = module.getExports().get(0);
    assertThat(i1.getSymbolName()).isEqualTo("m");
    assertThat(i1.getSource().getElements()).hasSize(1);
    String ref = i1.getSource().getElements().get(0);
    assertThat(ref).isEqualTo("m");

  }

  @Test
  public void parses_library_metadata() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), "fixtures/tweakflow/analysis/parsing/library.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    // library exists

    assertThat(((ModuleNode) result.getNode()).getComponents()).hasSize(1);

    // library has correct nameo
    LibraryNode lib = (LibraryNode) ((ModuleNode) result.getNode()).getComponents().get(0);
    assertThat(lib.getSymbolName()).isEqualTo("lib");

    // library is exported
    assertThat(lib.isExport()).isTrue();

    // library has correct doc-string
    assertThat(lib.getDoc().getExpression()).isInstanceOf(StringNode.class);
    StringNode docStringNode = (StringNode) lib.getDoc().getExpression();
    assertThat(docStringNode.getStringVal()).isEqualTo("lib doc string");

    // library has meta map
    assertThat(lib.hasMeta()).isTrue();
    assertThat(lib.getMeta().getExpression()).isInstanceOf(DictNode.class);

  }

  @Test
  public void parses_vardef_metadata() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/vardef.tf");

    // vardef exists
    assertThat(varDefMap.containsKey("x"));

    // vardef has correct name
    VarDefNode v = varDefMap.get("x");
    assertThat(v.getSymbolName()).isEqualTo("x");

    // vardef has correct doc-string
    assertThat(v.getDoc().getExpression()).isInstanceOf(StringNode.class);
    StringNode docStringNode = (StringNode) v.getDoc().getExpression();
    assertThat(docStringNode.getStringVal()).isEqualTo("var x doc string");

    // vardef has meta map
    assertThat(v.hasMeta()).isTrue();
    assertThat(v.getMeta().getExpression()).isInstanceOf(DictNode.class);

  }

  @Test
  public void parses_typed_vardefs() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/typed_vardefs.tf");

    VarDefNode m = varDefMap.get("m");
    assertThat(m.getDeclaredType().isAny()).isTrue();

    VarDefNode x0 = varDefMap.get("x0");
    assertThat(x0.getDeclaredType().isAny()).isTrue();

    VarDefNode x1 = varDefMap.get("x1");
    assertThat(x1.getDeclaredType().isString()).isTrue();

    VarDefNode x2 = varDefMap.get("x2");
    assertThat(x2.getDeclaredType().isLong()).isTrue();

    VarDefNode x3 = varDefMap.get("x3");
    assertThat(x3.getDeclaredType().isDict()).isTrue();

    VarDefNode x4 = varDefMap.get("x4");
    assertThat(x4.getDeclaredType().isList()).isTrue();

    VarDefNode x5 = varDefMap.get("x5");
    assertThat(x5.getDeclaredType().isFunction()).isTrue();

    VarDefNode x6 = varDefMap.get("x6");
    assertThat(x6.getDeclaredType().isDouble()).isTrue();

    VarDefNode x7 = varDefMap.get("x7");
    assertThat(x7.getDeclaredType().isBoolean()).isTrue();

    VarDefNode x8 = varDefMap.get("x8");
    assertThat(x8.getDeclaredType().isDateTime()).isTrue();

  }

  @Test
  public void parses_empty_interactive_unit() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), "fixtures/tweakflow/analysis/parsing/empty_interactive.tf")
    );
    ParseResult result = p.parseUnit();

    assertThat(result.isSuccess()).isTrue();

    InteractiveNode node = (InteractiveNode) result.getNode();
    assertThat(node.getSourceInfo()).isNotNull();

    // unit always starts at 1,1
    assertThat(node.getSourceInfo().getLine()).isEqualTo(1);
    assertThat(node.getSourceInfo().getCharWithinLine()).isEqualTo(1);

    assertThat(node.getSections()).hasSize(0);
  }

  @Test
  public void parses_interactive_unit() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), "fixtures/tweakflow/analysis/parsing/interactive.tf")
    );
    ParseResult result = p.parseUnit();

    assertThat(result.isSuccess()).isTrue();

    InteractiveNode node = (InteractiveNode) result.getNode();
    assertThat(node.getSourceInfo()).isNotNull();

    // unit always starts at 1,1
    assertThat(node.getSourceInfo().getLine()).isEqualTo(1);
    assertThat(node.getSourceInfo().getCharWithinLine()).isEqualTo(1);

    /*
     interactive
      in_scope `some.tf`
        f: (long x) -> x+1
        b: f(2)
        `<interactive>`: 2+2

      in_scope `some_other.tf`
        x: 3

    */

    assertThat(node.getSections()).hasSize(2);

    InteractiveSectionNode in_some = node.getSections().get(0);
    assertThat(in_some).isNotNull();
    assertThat(in_some.getInScopeRef()).hasSameStructureAs(
        new ReferenceNode()
            .setAnchor(ReferenceNode.Anchor.LOCAL)
            .setElements(Collections.singletonList("some.tf")));

    LinkedHashMap<String, VarDefNode> in_some_vars = in_some.getVars().getMap();
    assertThat(in_some_vars).hasSize(3);
    assertThat(in_some_vars.get("f").getSymbolName()).isEqualTo("f");
    assertThat(in_some_vars.get("f").getValueExpression()).isInstanceOf(FunctionNode.class);

    assertThat(in_some_vars.get("b").getSymbolName()).isEqualTo("b");
    assertThat(in_some_vars.get("b").getValueExpression()).isInstanceOf(CallNode.class);

    assertThat(in_some_vars.get("<interactive>").getSymbolName()).isEqualTo("<interactive>");
    assertThat(in_some_vars.get("<interactive>").getValueExpression()).isInstanceOf(PlusNode.class);

    InteractiveSectionNode in_some_other = node.getSections().get(1);
    assertThat(in_some_other).isNotNull();
    assertThat(in_some_other.getInScopeRef()).hasSameStructureAs(
        new ReferenceNode().
          setAnchor(ReferenceNode.Anchor.LOCAL)
          .setElements(Collections.singletonList("some_other.tf")));

    LinkedHashMap<String, VarDefNode> in_some_other_vars = in_some_other.getVars().getMap();
    assertThat(in_some_other_vars).hasSize(1);

    assertThat(in_some_other_vars.get("x").getSymbolName()).isEqualTo("x");
    assertThat(in_some_other_vars.get("x").getValueExpression()).isInstanceOf(LongNode.class);

  }


}