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

package com.twineworks.tweakflow.lang.parse;

import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.args.NamedArgumentNode;
import com.twineworks.tweakflow.lang.ast.args.ParameterNode;
import com.twineworks.tweakflow.lang.ast.args.PositionalArgumentNode;
import com.twineworks.tweakflow.lang.ast.args.SplatArgumentNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.imports.ModuleImportNode;
import com.twineworks.tweakflow.lang.ast.imports.NameImportNode;
import com.twineworks.tweakflow.lang.ast.meta.ViaNode;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.ast.structure.match.DefaultPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.ExpressionPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLineNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import com.twineworks.tweakflow.lang.types.Types;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest {

  @Test
  public void parses_empty_module() throws Exception {

    Parser p = new Parser(new MemoryLocation().add("", ""));
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
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/module.tf")
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
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/module.tf")
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
  public void fails_on_datetime_invalid_month() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/errors/datetime_invalid_month.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isEqualTo(LangError.INVALID_DATETIME);
  }

  @Test
  public void fails_on_datetime_invalid_day() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/errors/datetime_invalid_day.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isEqualTo(LangError.INVALID_DATETIME);
  }

  @Test
  public void fails_on_datetime_invalid_hour() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/errors/datetime_invalid_hour.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isEqualTo(LangError.INVALID_DATETIME);
  }

  @Test
  public void fails_on_datetime_invalid_minute() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/errors/datetime_invalid_minute.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isEqualTo(LangError.INVALID_DATETIME);
  }

  @Test
  public void fails_on_datetime_invalid_second() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/errors/datetime_invalid_second.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isEqualTo(LangError.INVALID_DATETIME);
  }

  @Test
  public void fails_on_datetime_invalid_second_fraction() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/errors/datetime_invalid_second_fraction.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isEqualTo(LangError.INVALID_DATETIME);
  }

  @Test
  public void fails_on_datetime_invalid_offset_for_tz() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/errors/datetime_invalid_offset_for_tz.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getException().getCode()).isEqualTo(LangError.INVALID_DATETIME);
  }


  @Test
  public void parses_module_aliases() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/module.tf")
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
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/module.tf")
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
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/module.tf")
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
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/library.tf")
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

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/vardef.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    // vardef exists
    Map<String, VarDefNode> varDefMap = ((ModuleNode) result.getNode()).getLibraries().get(0).getVars().getMap();
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

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/typed_vardefs.tf")
    );
    ParseResult result = p.parseUnit();

    if (result.isError()){
      result.getException().printDetails();
    }

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    Map<String, VarDefNode> varDefMap = ((ModuleNode) result.getNode()).getLibraries().get(0).getVars().getMap();

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
  public void parses_literal_expressions() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/literal_expressions.tf")
    );
    ParseResult result = p.parseUnit();

    if (result.isError()){
      result.getException().printDetails();
    }
    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    // get the variable map
    Map<String, VarDefNode> varDefMap = ((ModuleNode) result.getNode()).getLibraries().get(0).getVars().getMap();

    // e0: nil
    ExpressionNode e0 = varDefMap.get("e0").getValueExpression();
    assertThat(e0).isInstanceOf(NilNode.class);

    // e1: "string value"
    ExpressionNode e1 = varDefMap.get("e1").getValueExpression();
    assertThat(e1).isInstanceOf(StringNode.class);
    StringNode e1_value = (StringNode) e1;
    assertThat(e1_value.getStringVal()).isEqualTo("string value");

    // e2: 1
    ExpressionNode e2 = varDefMap.get("e2").getValueExpression();
    assertThat(e2).isInstanceOf(LongNode.class);
    LongNode e2_value = (LongNode) e2;
    assertThat(e2_value.getLongNum()).isEqualTo(1L);

    // e3: 0x01
    ExpressionNode e3 = varDefMap.get("e3").getValueExpression();
    assertThat(e3).isInstanceOf(LongNode.class);
    LongNode e3_value = (LongNode) e3;
    assertThat(e3_value.getLongNum()).isEqualTo(1L);

    // e4: []
    ExpressionNode e4 = varDefMap.get("e4").getValueExpression();
    assertThat(e4).isInstanceOf(ListNode.class);
    ListNode e4_value = (ListNode) e4;
    assertThat(e4_value.getElements()).hasSize(0);

    // e5: [1 2 3]
    ExpressionNode e5 = varDefMap.get("e5").getValueExpression();
    assertThat(e5).isInstanceOf(ListNode.class);
    ListNode e5_value = (ListNode) e5;
    assertThat(e5_value.getElements()).hasSize(3);

    LongNode e5_1 = (LongNode) e5_value.getElements().get(0);
    assertThat(e5_1.getLongNum()).isEqualTo(1L);

    LongNode e5_2 = (LongNode) e5_value.getElements().get(1);
    assertThat(e5_2.getLongNum()).isEqualTo(2L);

    LongNode e5_3 = (LongNode) e5_value.getElements().get(2);
    assertThat(e5_3.getLongNum()).isEqualTo(3L);

    // e6: [1, "a", ["x","y"]]
    ExpressionNode e6 = varDefMap.get("e6").getValueExpression();
    assertThat(e6).isInstanceOf(ListNode.class);
    ListNode e6_value = (ListNode) e6;
    assertThat(e6_value.getElements()).hasSize(3);

    LongNode e6_1 = (LongNode) e6_value.getElements().get(0);
    assertThat(e6_1.getLongNum()).isEqualTo(1L);

    StringNode e6_2 = (StringNode) e6_value.getElements().get(1);
    assertThat(e6_2.getStringVal()).isEqualTo("a");

    ListNode e6_3 = (ListNode) e6_value.getElements().get(2);
    assertThat(e6_3.getElements()).hasSize(2);

    StringNode e6_3_1 = (StringNode) e6_3.getElements().get(0);
    assertThat(e6_3_1.getStringVal()).isEqualTo("x");

    StringNode e6_3_2 = (StringNode) e6_3.getElements().get(1);
    assertThat(e6_3_2.getStringVal()).isEqualTo("y");

    // e7: {}
    ExpressionNode e7 = varDefMap.get("e7").getValueExpression();
    assertThat(e7).isInstanceOf(DictNode.class);
    DictNode e7_value = (DictNode) e7;
    assertThat(e7_value.getEntries()).hasSize(0);

    // e8: {:key "value"}
    ExpressionNode e8 = varDefMap.get("e8").getValueExpression();
    assertThat(e8).isInstanceOf(DictNode.class);
    DictNode e8_value = (DictNode) e8;
    assertThat(e8_value.getEntries()).hasSize(1);

    // :key is parsed into a string key
    DictEntryNode e8_1 = e8_value.getEntries().get(0);
    assertThat(e8_1.getKey()).isInstanceOf(StringNode.class);
    StringNode e8_1_k = (StringNode) e8_1.getKey();
    assertThat(e8_1_k.getStringVal()).isEqualTo("key");

    assertThat(e8_1.getValue()).isInstanceOf(StringNode.class);
    StringNode e8_1_v = (StringNode) e8_1.getValue();
    assertThat(e8_1_v.getStringVal()).isEqualTo("value");

    // e9: {:key1 "value1" :key2 "value2"}
    ExpressionNode e9 = varDefMap.get("e9").getValueExpression();
    assertThat(e9).isInstanceOf(DictNode.class);
    DictNode e9_value = (DictNode) e9;
    assertThat(e9_value.getEntries()).hasSize(2);

    DictEntryNode e9_1 = e9_value.getEntries().get(0);
    assertThat(e9_1.getKey()).isInstanceOf(StringNode.class);
    StringNode e9_1_k = (StringNode) e9_1.getKey();
    assertThat(e9_1_k.getStringVal()).isEqualTo("key1");

    assertThat(e9_1.getValue()).isInstanceOf(StringNode.class);
    StringNode e9_1_v = (StringNode) e9_1.getValue();
    assertThat(e9_1_v.getStringVal()).isEqualTo("value1");

    DictEntryNode e9_2 = e9_value.getEntries().get(1);
    assertThat(e9_2.getKey()).isInstanceOf(StringNode.class);
    StringNode e9_2_k = (StringNode) e9_2.getKey();
    assertThat(e9_2_k.getStringVal()).isEqualTo("key2");

    assertThat(e9_2.getValue()).isInstanceOf(StringNode.class);
    StringNode e9_2_v = (StringNode) e9_2.getValue();
    assertThat(e9_2_v.getStringVal()).isEqualTo("value2");

    // e10: {"k" "v", "sub" {:key "value"}}
    ExpressionNode e10 = varDefMap.get("e10").getValueExpression();
    assertThat(e10).isInstanceOf(DictNode.class);
    DictNode e10_value = (DictNode) e10;
    assertThat(e10_value.getEntries()).hasSize(2);

    DictEntryNode e10_1 = e10_value.getEntries().get(0);
    assertThat(e10_1.getKey()).isInstanceOf(StringNode.class);
    StringNode e10_1_k = (StringNode) e10_1.getKey();
    assertThat(e10_1_k.getStringVal()).isEqualTo("k");

    assertThat(e10_1.getValue()).isInstanceOf(StringNode.class);
    StringNode e10_1_v = (StringNode) e10_1.getValue();
    assertThat(e10_1_v.getStringVal()).isEqualTo("v");

    DictEntryNode e10_2 = e10_value.getEntries().get(1);
    assertThat(e10_2.getKey()).isInstanceOf(StringNode.class);
    StringNode e10_2_k = (StringNode) e10_2.getKey();
    assertThat(e10_2_k.getStringVal()).isEqualTo("sub");

    assertThat(e10_2.getValue()).isInstanceOf(DictNode.class);
    DictNode e10_2_v = (DictNode) e10_2.getValue();
    assertThat(e10_2_v.getEntries()).hasSize(1);

    DictEntryNode e10_2_v_1 = e10_2_v.getEntries().get(0);
    assertThat(e10_2_v_1.getKey()).isInstanceOf(StringNode.class);
    StringNode e10_2_v_1_k = (StringNode) e10_2_v_1.getKey();
    assertThat(e10_2_v_1_k.getStringVal()).isEqualTo("key");
    assertThat(e10_2_v_1.getValue()).isInstanceOf(StringNode.class);
    StringNode e10_2_v_1_v = (StringNode) e10_2_v_1.getValue();
    assertThat(e10_2_v_1_v.getStringVal()).isEqualTo("value");

    // e11: "-\n-"
    ExpressionNode e11 = varDefMap.get("e11").getValueExpression();
    assertThat(e11).isInstanceOf(StringNode.class);
    StringNode e11_value = (StringNode) e11;
    assertThat(e11_value.getStringVal()).isEqualTo("-\n-");

    // e12: true
    ExpressionNode e12 = varDefMap.get("e12").getValueExpression();
    assertThat(e12).isInstanceOf(BooleanNode.class);
    BooleanNode e12_value = (BooleanNode) e12;
    assertThat(e12_value.getBoolVal()).isEqualTo(Boolean.TRUE);

    // e13: false
    ExpressionNode e13 = varDefMap.get("e13").getValueExpression();
    assertThat(e13).isInstanceOf(BooleanNode.class);
    BooleanNode e13_value = (BooleanNode) e13;
    assertThat(e13_value.getBoolVal()).isEqualTo(Boolean.FALSE);

    // e14: () -> true # constant function returning true
    ExpressionNode e14 = varDefMap.get("e14").getValueExpression();
    assertThat(e14).isInstanceOf(FunctionNode.class);
    FunctionNode e14_f = (FunctionNode) e14;
    assertThat(e14_f.getDeclaredReturnType()).isEqualTo(Types.ANY);
    assertThat(e14_f.getParameters().getMap()).isEmpty();
    assertThat(e14_f.getExpression()).isInstanceOf(BooleanNode.class);
    BooleanNode e14_f_value = (BooleanNode) e14_f.getExpression();
    assertThat(e14_f_value.getBoolVal()).isTrue();

    // e15: (double x = 0, double y = 0) -> [x y]
    ExpressionNode e15 = varDefMap.get("e15").getValueExpression();
    assertThat(e15).isInstanceOf(FunctionNode.class);
    FunctionNode e15_f = (FunctionNode) e15;
    assertThat(e15_f.getDeclaredReturnType()).isEqualTo(Types.LIST);

    assertThat(e15_f.getExpression()).isInstanceOf(ListNode.class);
    ListNode e15_f_e = (ListNode) e15_f.getExpression();
    assertThat(e15_f_e.getElements()).hasSize(2);
    assertThat(e15_f_e.getElements().get(0)).isInstanceOf(ReferenceNode.class);
    ReferenceNode e15_f_e_0 = (ReferenceNode) e15_f_e.getElements().get(0);
    assertThat(e15_f_e_0.getElements().get(0)).isEqualTo("x");

    ReferenceNode e15_f_e_1 = (ReferenceNode) e15_f_e.getElements().get(1);
    assertThat(e15_f_e_1.getElements().get(0)).isEqualTo("y");

    assertThat(e15_f.getParameters().getMap()).hasSize(2);

    Iterator<String> iterator = e15_f.getParameters().getMap().keySet().iterator();
    String x = iterator.next();
    assertThat(x).isEqualTo("x");
    String y = iterator.next();
    assertThat(y).isEqualTo("y");

    ParameterNode e15_p_x = e15_f.getParameters().getMap().get(x);
    assertThat(e15_p_x.getDeclaredType()).isEqualTo(Types.DOUBLE);
    assertThat(e15_p_x.getIndex()).isEqualTo(0);
    assertThat(e15_p_x.getSymbolName()).isEqualTo("x");
    assertThat(e15_p_x.getDefaultValue()).isInstanceOf(DoubleNode.class);
    DoubleNode e15_p_x_v = (DoubleNode) e15_p_x.getDefaultValue();
    assertThat(e15_p_x_v.getDoubleNum()).isEqualTo(0.0d);

    ParameterNode e15_p_y = e15_f.getParameters().getMap().get(y);
    assertThat(e15_p_y.getDeclaredType()).isEqualTo(Types.DOUBLE);
    assertThat(e15_p_y.getIndex()).isEqualTo(1);
    assertThat(e15_p_y.getSymbolName()).isEqualTo("y");
    assertThat(e15_p_y.getDefaultValue()).isInstanceOf(DoubleNode.class);
    DoubleNode e15_p_y_v = (DoubleNode) e15_p_y.getDefaultValue();
    assertThat(e15_p_y_v.getDoubleNum()).isEqualTo(0.0d);

    // e16: (list xs) -> any via "native"
    ExpressionNode e16 = varDefMap.get("e16").getValueExpression();
    assertThat(e16).isInstanceOf(FunctionNode.class);
    FunctionNode e16_f = (FunctionNode) e16;
    assertThat(e16_f.getDeclaredReturnType()).isEqualTo(Types.ANY);
    assertThat(e16_f.getParameters().getMap()).hasSize(1);

    ParameterNode e16_p_xs = e16_f.getParameters().getMap().get("xs");
    assertThat(e16_p_xs.getSymbolName()).isEqualTo("xs");
    assertThat(e16_p_xs.getDeclaredType()).isEqualTo(Types.LIST);

    ViaNode e16_f_via = e16_f.getVia();
    assertThat(e16_f_via).isNotNull();

    assertThat(e16_f_via.getExpression()).isInstanceOf(StringNode.class);
    StringNode e16_f_via_str = (StringNode) e16_f_via.getExpression();
    assertThat(e16_f_via_str.getStringVal()).isEqualTo("native");

    // e17: 2e-1
    ExpressionNode e17 = varDefMap.get("e17").getValueExpression();
    assertThat(e17).isInstanceOf(DoubleNode.class);
    DoubleNode e17_value = (DoubleNode) e17;
    assertThat(e17_value.getDoubleNum()).isEqualTo(0.2d);

    // e18: 3.1315
    // e19: 0.31315e1
    // e20: .31315e1
    // e21: 31315e-4

    // all of the above must parse to the same number
    ExpressionNode e18 = varDefMap.get("e18").getValueExpression();
    ExpressionNode e19 = varDefMap.get("e19").getValueExpression();
    ExpressionNode e20 = varDefMap.get("e20").getValueExpression();
    ExpressionNode e21 = varDefMap.get("e21").getValueExpression();

    assertThat(e18).hasSameStructureAs(e19);
    assertThat(e18).hasSameStructureAs(e20);
    assertThat(e18).hasSameStructureAs(e21);

    assertThat(e18.getValueType()).isSameAs(Types.DOUBLE);
    assertThat(e18).isInstanceOf(DoubleNode.class);
    assertThat(((DoubleNode)e18).getDoubleNum()).isEqualTo(3.1315);

    // e22: 'single quoted ''string'''
    ExpressionNode e22 = varDefMap.get("e22").getValueExpression();
    assertThat(e22).isInstanceOf(StringNode.class);
    StringNode e22_value = (StringNode) e22;
    assertThat(e22_value.getStringVal()).isEqualTo("single quoted 'string'");

    // e23: "string with\nescape sequence"
    ExpressionNode e23 = varDefMap.get("e23").getValueExpression();
    assertThat(e23).isInstanceOf(StringNode.class);
    StringNode e23_value = (StringNode) e23;
    assertThat(e23_value.getStringVal()).isEqualTo("string with\nescape sequence");

    // e24: 'single quoted
    // multi
    // line
    // string'
    ExpressionNode e24 = varDefMap.get("e24").getValueExpression();
    assertThat(e24).isInstanceOf(StringNode.class);
    StringNode e24_value = (StringNode) e24;
    assertThat(e24_value.getStringVal()).isEqualTo("single quoted\nmulti\nline\nstring");

    // e25: "double quoted
    // multi
    // line
    // string"
    ExpressionNode e25 = varDefMap.get("e25").getValueExpression();
    assertThat(e25).isInstanceOf(StringNode.class);
    StringNode e25_value = (StringNode) e25;
    assertThat(e25_value.getStringVal()).isEqualTo("double quoted\nmulti\nline\nstring");

    // e26: ''
    ExpressionNode e26 = varDefMap.get("e26").getValueExpression();
    assertThat(e26).isInstanceOf(StringNode.class);
    StringNode e26_value = (StringNode) e26;
    assertThat(e26_value.getStringVal()).isEmpty();

    // e27: ""
    ExpressionNode e27 = varDefMap.get("e27").getValueExpression();
    assertThat(e27).isInstanceOf(StringNode.class);
    StringNode e27_value = (StringNode) e27;
    assertThat(e27_value.getStringVal()).isEmpty();

    // e28: Infinity
    ExpressionNode e28 = varDefMap.get("e28").getValueExpression();
    assertThat(e28).isInstanceOf(DoubleNode.class);
    DoubleNode e28_value = (DoubleNode) e28;
    assertThat(e28_value.getDoubleNum()).isEqualTo(Double.POSITIVE_INFINITY);

    // e29: NaN
    ExpressionNode e29 = varDefMap.get("e29").getValueExpression();
    assertThat(e29).isInstanceOf(DoubleNode.class);
    DoubleNode e29_value = (DoubleNode) e29;
    assertThat(e29_value.getDoubleNum()).isEqualTo(Double.NaN);

    // e30: {:`escaped key` "value"}
    ExpressionNode e30 = varDefMap.get("e30").getValueExpression();
    assertThat(e30).isInstanceOf(DictNode.class);
    DictNode e30_value = (DictNode) e30;
    assertThat(e30_value.getEntries()).hasSize(1);
    DictEntryNode e30_entry_0 = e30_value.getEntries().get(0);

    StringNode e30_entry_0_key = (StringNode) e30_entry_0.getKey();
    assertThat(e30_entry_0_key.getStringVal()).isEqualTo("escaped key");

    StringNode e30_entry_0_value = (StringNode) e30_entry_0.getValue();
    assertThat(e30_entry_0_value.getStringVal()).isEqualTo("value");

    // e31:
    // ~~~
    // Here ~~~ String
    // ~~~
    ExpressionNode e31 = varDefMap.get("e31").getValueExpression();
    assertThat(e31).isInstanceOf(StringNode.class);
    StringNode e31_value = (StringNode) e31;
    assertThat(e31_value.getStringVal()).isEqualTo("Here ~~~ String");

    // e32: 2017-03-17T16:04:02 # local date, implied UTC, second precision
    ExpressionNode e32 = varDefMap.get("e32").getValueExpression();
    assertThat(e32).isInstanceOf(DateTimeNode.class);
    DateTimeNode e32_value = (DateTimeNode) e32;
    assertThat(e32_value.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 0, ZoneOffset.UTC)
    );

    // e33: 2017-03-17T16:04:02.123456789    # local date, implied UTC, nano-second precision
    ExpressionNode e33 = varDefMap.get("e33").getValueExpression();
    assertThat(e33).isInstanceOf(DateTimeNode.class);
    DateTimeNode e33_value = (DateTimeNode) e33;
    assertThat(e33_value.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123_456_789, ZoneOffset.UTC)
    );

    // e34: 2017-03-17T16:04:02+01:00@`Europe/Berlin` # local date in Berlin, second precision
    ExpressionNode e34 = varDefMap.get("e34").getValueExpression();
    assertThat(e34).isInstanceOf(DateTimeNode.class);
    DateTimeNode e34_value = (DateTimeNode) e34;
    assertThat(e34_value.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 0, ZoneId.of("Europe/Berlin"))
    );

    // e35: 2017-03-17T16:04:02.123+01:00@`Europe/Berlin` # local date in Berlin, milli-second precision
    ExpressionNode e35 = varDefMap.get("e35").getValueExpression();
    assertThat(e35).isInstanceOf(DateTimeNode.class);
    DateTimeNode e35_value = (DateTimeNode) e35;
    assertThat(e35_value.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123_000_000, ZoneId.of("Europe/Berlin"))
    );

    // e36: 2017-03-17T16:04:02Z                 # UTC time, second precision
    ExpressionNode e36 = varDefMap.get("e36").getValueExpression();
    assertThat(e36).isInstanceOf(DateTimeNode.class);
    DateTimeNode e36_value = (DateTimeNode) e36;
    assertThat(e36_value.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 0, ZoneOffset.UTC)
    );

    // e37: 2017-03-17T16:04:02+02:00            # UTC+2 time, implied time zone
    ExpressionNode e37 = varDefMap.get("e37").getValueExpression();
    assertThat(e37).isInstanceOf(DateTimeNode.class);
    DateTimeNode e37_value = (DateTimeNode) e37;
    assertThat(e37_value.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 0, ZoneOffset.ofHours(2))
    );

  }

  @Test
  public void parses_semantic_expressions() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/semantic_expressions.tf")
    );
    ParseResult result = p.parseUnit();

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    // get the variable map
    Map<String, VarDefNode> varDefMap = ((ModuleNode) result.getNode()).getLibraries().get(0).getVars().getMap();

    // e0: (1)
    ExpressionNode e0 = varDefMap.get("e0").getValueExpression();
    assertThat(e0).isInstanceOf(LongNode.class);
    LongNode e0_value = (LongNode) e0;
    assertThat(e0_value.getLongNum()).isEqualTo(1L);

    // e1: let {a: 1} true
    ExpressionNode e1 = varDefMap.get("e1").getValueExpression();
    assertThat(e1).isInstanceOf(LetNode.class);
    LetNode e1_let = (LetNode) e1;

    LinkedHashMap<String, VarDefNode> bindings = e1_let.getBindings().getVars().getMap();
    assertThat(bindings).hasSize(1);
    assertThat(bindings.get("a").getDeclaredType()).isEqualTo(Types.ANY);
    assertThat(bindings.get("a").getSymbolName()).isEqualTo("a");

    BooleanNode e1_value = (BooleanNode) e1_let.getExpression();
    assertThat(e1_value.getBoolVal()).isEqualTo(Boolean.TRUE);

    // e2: try 0 catch e false
    ExpressionNode e2 = varDefMap.get("e2").getValueExpression();
    assertThat(e2).isInstanceOf(TryCatchNode.class);
    TryCatchNode e2_try = (TryCatchNode) e2;
    assertThat(e2_try.getCaughtException().getSymbolName()).isEqualTo("e");
    assertThat(e2_try.getCaughtTrace()).isNull();

    assertThat(e2_try.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode e2_try_exp = (LongNode) e2_try.getTryExpression();
    assertThat(e2_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(e2_try.getCatchExpression()).isInstanceOf(BooleanNode.class);
    BooleanNode e2_try_catch = (BooleanNode) e2_try.getCatchExpression();
    assertThat(e2_try_catch.getBoolVal()).isFalse();

    // e3: try 0 catch false
    ExpressionNode e3 = varDefMap.get("e3").getValueExpression();
    assertThat(e3).isInstanceOf(TryCatchNode.class);
    TryCatchNode e3_try = (TryCatchNode) e3;
    assertThat(e3_try.getCaughtException()).isNull();
    assertThat(e3_try.getCaughtTrace()).isNull();

    assertThat(e3_try.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode e3_try_exp = (LongNode) e3_try.getTryExpression();
    assertThat(e3_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(e3_try.getCatchExpression()).isInstanceOf(BooleanNode.class);
    BooleanNode e3_try_catch = (BooleanNode) e3_try.getCatchExpression();
    assertThat(e3_try_catch.getBoolVal()).isFalse();

    // e4: throw nil
    ExpressionNode e4 = varDefMap.get("e4").getValueExpression();
    assertThat(e4).isInstanceOf(ThrowNode.class);
    ThrowNode e4_throw = (ThrowNode) e4;
    assertThat(e4_throw.getExceptionExpression()).isInstanceOf(NilNode.class);

    // e5: "0001" as long
    ExpressionNode e5 = varDefMap.get("e5").getValueExpression();
    assertThat(e5).isInstanceOf(CastNode.class);
    CastNode e5_cast = (CastNode) e5;
    assertThat(e5_cast.getExpression()).isInstanceOf(StringNode.class);
    StringNode e5_cast_exp = (StringNode) e5_cast.getExpression();
    assertThat(e5_cast_exp.getStringVal()).isEqualTo("0001");

    assertThat(e5_cast.getTargetType()).isEqualTo(Types.LONG);

    // e6: if true then 1 else 0
    ExpressionNode e6 = varDefMap.get("e6").getValueExpression();
    assertThat(e6).isInstanceOf(IfNode.class);
    IfNode e6_if = (IfNode) e6;

    assertThat(e6_if.getCondition()).isInstanceOf(BooleanNode.class);
    BooleanNode e6_if_cond = (BooleanNode) e6_if.getCondition();
    assertThat(e6_if_cond.getBoolVal()).isTrue();

    assertThat(e6_if.getThenExpression()).isInstanceOf(LongNode.class);
    LongNode e6_if_then = (LongNode) e6_if.getThenExpression();
    assertThat(e6_if_then.getLongNum()).isEqualTo(1L);

    assertThat(e6_if.getElseExpression()).isInstanceOf(LongNode.class);
    LongNode e6_if_else = (LongNode) e6_if.getElseExpression();
    assertThat(e6_if_else.getLongNum()).isEqualTo(0L);

    // e7: if true 1 else 0
    ExpressionNode e7 = varDefMap.get("e7").getValueExpression();
    assertThat(e7).isInstanceOf(IfNode.class);
    IfNode e7_if = (IfNode) e7;

    assertThat(e7_if.getCondition()).isInstanceOf(BooleanNode.class);
    BooleanNode e7_if_cond = (BooleanNode) e7_if.getCondition();
    assertThat(e7_if_cond.getBoolVal()).isTrue();

    assertThat(e7_if.getThenExpression()).isInstanceOf(LongNode.class);
    LongNode e7_if_then = (LongNode) e7_if.getThenExpression();
    assertThat(e7_if_then.getLongNum()).isEqualTo(1L);

    assertThat(e7_if.getElseExpression()).isInstanceOf(LongNode.class);
    LongNode e7_if_else = (LongNode) e7_if.getElseExpression();
    assertThat(e7_if_else.getLongNum()).isEqualTo(0L);

    // e9: try 0 catch e, trace nil # try evaluating 0, catch exception and trace and return nil

    ExpressionNode e9 = varDefMap.get("e9").getValueExpression();
    assertThat(e9).isInstanceOf(TryCatchNode.class);
    TryCatchNode e9_try = (TryCatchNode) e9;
    assertThat(e9_try.getCaughtException().getSymbolName()).isEqualTo("e");
    assertThat(e9_try.getCaughtTrace().getSymbolName()).isEqualTo("trace");

    assertThat(e9_try.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode e9_try_exp = (LongNode) e9_try.getTryExpression();
    assertThat(e9_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(e9_try.getCatchExpression()).isInstanceOf(NilNode.class);

    // e10: "foo" is string        # type check
    ExpressionNode e10 = varDefMap.get("e10").getValueExpression();
    assertThat(e10).isInstanceOf(IsNode.class);
    IsNode e10_is = (IsNode) e10;
    assertThat(e10_is.getCompareType()).isEqualTo(Types.STRING);
    assertThat(e10_is.getExpression()).isInstanceOf(StringNode.class);
    StringNode e10_is_exp = (StringNode) e10_is.getExpression();
    assertThat(e10_is_exp.getStringVal()).isEqualTo("foo");

    // e11: import_name.lib.x      # reference string
    ExpressionNode e11 = varDefMap.get("e11").getValueExpression();
    assertThat(e11).isInstanceOf(ReferenceNode.class);
    ReferenceNode e11_ref = (ReferenceNode) e11;
    assertThat(e11_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> elements = e11_ref.getElements();
    assertThat(elements).hasSize(3);
    assertThat(elements).containsExactly("import_name", "lib", "x");

    // e12: f()
    ExpressionNode e12 = varDefMap.get("e12").getValueExpression();
    assertThat(e12).isInstanceOf(CallNode.class);
    CallNode e12_call = (CallNode) e12;
    assertThat(e12_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e12_call.getArguments().getList()).isEmpty();

    // e13: f(1)
    ExpressionNode e13 = varDefMap.get("e13").getValueExpression();
    assertThat(e13).isInstanceOf(CallNode.class);
    CallNode e13_call = (CallNode) e13;
    assertThat(e13_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e13_call.getArguments().getList()).hasSize(1);
    PositionalArgumentNode e13_argument_0 = (PositionalArgumentNode) e13_call.getArguments().getList().get(0);
    assertThat(e13_argument_0.getExpression()).isInstanceOf(LongNode.class);
    LongNode e13_argument_0_v = (LongNode) e13_argument_0.getExpression();
    assertThat(e13_argument_0_v.getLongNum()).isEqualTo(1);

    // e14: f(:a 1)
    ExpressionNode e14 = varDefMap.get("e14").getValueExpression();
    assertThat(e14).isInstanceOf(CallNode.class);
    CallNode e14_call = (CallNode) e14;
    assertThat(e14_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e14_call.getArguments().getList()).hasSize(1);
    NamedArgumentNode e14_argument_a = (NamedArgumentNode) e14_call.getArguments().getList().get(0);
    assertThat(e14_argument_a.getExpression()).isInstanceOf(LongNode.class);
    LongNode e14_argument_a_v = (LongNode) e14_argument_a.getExpression();
    assertThat(e14_argument_a_v.getLongNum()).isEqualTo(1);

    // e15: f(1, 2)
    ExpressionNode e15 = varDefMap.get("e15").getValueExpression();
    assertThat(e15).isInstanceOf(CallNode.class);
    CallNode e15_call = (CallNode) e15;
    assertThat(e15_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e15_call.getArguments().getList()).hasSize(2);

    PositionalArgumentNode e15_argument_0 = (PositionalArgumentNode) e15_call.getArguments().getList().get(0);
    assertThat(e15_argument_0.getExpression()).isInstanceOf(LongNode.class);
    LongNode e15_argument_0_v = (LongNode) e15_argument_0.getExpression();
    assertThat(e15_argument_0_v.getLongNum()).isEqualTo(1);

    PositionalArgumentNode e15_argument_1 = (PositionalArgumentNode) e15_call.getArguments().getList().get(1);
    assertThat(e15_argument_1.getExpression()).isInstanceOf(LongNode.class);
    LongNode e15_argument_1_v = (LongNode) e15_argument_1.getExpression();
    assertThat(e15_argument_1_v.getLongNum()).isEqualTo(2);

    // e16: f(:a 1, :b 2)
    ExpressionNode e16 = varDefMap.get("e16").getValueExpression();
    assertThat(e16).isInstanceOf(CallNode.class);
    CallNode e16_call = (CallNode) e16;
    assertThat(e16_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e16_call.getArguments().getList()).hasSize(2);

    NamedArgumentNode e16_argument_a = (NamedArgumentNode) e16_call.getArguments().getList().get(0);
    assertThat(e16_argument_a.getName()).isEqualTo("a");
    assertThat(e16_argument_a.getExpression()).isInstanceOf(LongNode.class);
    LongNode e16_argument_a_v = (LongNode) e16_argument_a.getExpression();
    assertThat(e16_argument_a_v.getLongNum()).isEqualTo(1);

    NamedArgumentNode e16_argument_b = (NamedArgumentNode) e16_call.getArguments().getList().get(1);
    assertThat(e16_argument_b.getName()).isEqualTo("b");
    assertThat(e16_argument_b.getExpression()).isInstanceOf(LongNode.class);
    LongNode e16_argument_b_v = (LongNode) e16_argument_b.getExpression();
    assertThat(e16_argument_b_v.getLongNum()).isEqualTo(2);

    // e17: f(1, 2, :c 3)
    ExpressionNode e17 = varDefMap.get("e17").getValueExpression();
    assertThat(e17).isInstanceOf(CallNode.class);
    CallNode e17_call = (CallNode) e17;
    assertThat(e17_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e17_call.getArguments().getList()).hasSize(3);

    PositionalArgumentNode e17_argument_0 = (PositionalArgumentNode) e17_call.getArguments().getList().get(0);
    assertThat(e17_argument_0.getExpression()).isInstanceOf(LongNode.class);
    LongNode e17_argument_0_v = (LongNode) e17_argument_0.getExpression();
    assertThat(e17_argument_0_v.getLongNum()).isEqualTo(1);

    PositionalArgumentNode e17_argument_1 = (PositionalArgumentNode) e17_call.getArguments().getList().get(1);
    assertThat(e17_argument_1.getExpression()).isInstanceOf(LongNode.class);
    LongNode e17_argument_1_v = (LongNode) e17_argument_1.getExpression();
    assertThat(e17_argument_1_v.getLongNum()).isEqualTo(2);

    NamedArgumentNode e17_argument_c = (NamedArgumentNode) e17_call.getArguments().getList().get(2);
    assertThat(e17_argument_c.getName()).isEqualTo("c");
    assertThat(e17_argument_c.getExpression()).isInstanceOf(LongNode.class);
    LongNode e17_argument_c_v = (LongNode) e17_argument_c.getExpression();
    assertThat(e17_argument_c_v.getLongNum()).isEqualTo(3);

    // e18: f(1, ...{:a 1})
    ExpressionNode e18 = varDefMap.get("e18").getValueExpression();
    assertThat(e18).isInstanceOf(CallNode.class);
    CallNode e18_call = (CallNode) e18;
    assertThat(e18_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e18_call.getArguments().getList()).hasSize(2);

    PositionalArgumentNode e18_argument_0 = (PositionalArgumentNode) e18_call.getArguments().getList().get(0);
    assertThat(e18_argument_0.getExpression()).isInstanceOf(LongNode.class);
    LongNode e18_argument_0_v = (LongNode) e18_argument_0.getExpression();
    assertThat(e18_argument_0_v.getLongNum()).isEqualTo(1);

    SplatArgumentNode e18_argument_1 = (SplatArgumentNode) e18_call.getArguments().getList().get(1);
    assertThat(e18_argument_1.getExpression()).isInstanceOf(DictNode.class);
    DictNode e18_argument_1_v = (DictNode) e18_argument_1.getExpression();
    assertThat(e18_argument_1_v.getEntries()).hasSize(1);

    // e20: library::e0            # library reference
    ExpressionNode e20 = varDefMap.get("e20").getValueExpression();
    assertThat(e20).isInstanceOf(ReferenceNode.class);
    ReferenceNode e20_ref = (ReferenceNode) e20;
    assertThat(e20_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.LIBRARY);
    List<String> e20_elements = e20_ref.getElements();
    assertThat(e20_elements).hasSize(1);
    assertThat(e20_elements).containsExactly("e0");

    // e21: ::lib.e0               # module reference
    ExpressionNode e21 = varDefMap.get("e21").getValueExpression();
    assertThat(e21).isInstanceOf(ReferenceNode.class);
    ReferenceNode e21_ref = (ReferenceNode) e21;
    assertThat(e21_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.MODULE);
    List<String> e21_elements = e21_ref.getElements();
    assertThat(e21_elements).hasSize(2);
    assertThat(e21_elements).containsExactly("lib", "e0");

    // e22: module::lib.e0         # module reference
    ExpressionNode e22 = varDefMap.get("e22").getValueExpression();
    assertThat(e22).isInstanceOf(ReferenceNode.class);
    ReferenceNode e22_ref = (ReferenceNode) e22;
    assertThat(e22_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.MODULE);
    List<String> e22_elements = e22_ref.getElements();
    assertThat(e22_elements).hasSize(2);
    assertThat(e22_elements).containsExactly("lib", "e0");

//    e25: $global_var            # global reference
    ExpressionNode e25 = varDefMap.get("e25").getValueExpression();
    assertThat(e25).isInstanceOf(ReferenceNode.class);
    ReferenceNode e25_ref = (ReferenceNode) e25;
    assertThat(e25_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.GLOBAL);
    List<String> e25_elements = e25_ref.getElements();
    assertThat(e25_elements).hasSize(1);
    assertThat(e25_elements).containsExactly("global_var");

//    e26: global::global_var     # global reference
    ExpressionNode e26 = varDefMap.get("e26").getValueExpression();
    assertThat(e26).isInstanceOf(ReferenceNode.class);
    ReferenceNode e26_ref = (ReferenceNode) e26;
    assertThat(e26_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.GLOBAL);
    List<String> e26_elements = e26_ref.getElements();
    assertThat(e26_elements).hasSize(1);
    assertThat(e26_elements).containsExactly("global_var");

//    e27: e0                     # local reference
    ExpressionNode e27 = varDefMap.get("e27").getValueExpression();
    assertThat(e27).isInstanceOf(ReferenceNode.class);
    ReferenceNode e27_ref = (ReferenceNode) e27;
    assertThat(e27_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> e27_elements = e27_ref.getElements();
    assertThat(e27_elements).hasSize(1);
    assertThat(e27_elements).containsExactly("e0");

//    e28: local::e0              # local reference
    ExpressionNode e28 = varDefMap.get("e28").getValueExpression();
    assertThat(e28).isInstanceOf(ReferenceNode.class);
    ReferenceNode e28_ref = (ReferenceNode) e28;
    assertThat(e28_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> e28_elements = e28_ref.getElements();
    assertThat(e28_elements).hasSize(1);
    assertThat(e28_elements).containsExactly("e0");

//  e29: "string ${e0}"         # string reference interpolation
    ExpressionNode e29 = varDefMap.get("e29").getValueExpression();
    assertThat(e29).isInstanceOf(StringConcatNode.class);

    // e30: f(:a, :b)
    ExpressionNode e30 = varDefMap.get("e30").getValueExpression();
    assertThat(e30).isInstanceOf(CallNode.class);
    CallNode e30_call = (CallNode) e30;
    assertThat(e30_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e30_call.getArguments().getList()).hasSize(2);

    PositionalArgumentNode e30_argument_0 = (PositionalArgumentNode) e30_call.getArguments().getList().get(0);
    assertThat(e30_argument_0.getExpression()).isInstanceOf(StringNode.class);
    StringNode e30_argument_0_v = (StringNode) e30_argument_0.getExpression();
    assertThat(e30_argument_0_v.getStringVal()).isEqualTo("a");

    PositionalArgumentNode e30_argument_1 = (PositionalArgumentNode) e30_call.getArguments().getList().get(1);
    assertThat(e30_argument_1.getExpression()).isInstanceOf(StringNode.class);
    StringNode e30_argument_1_v = (StringNode) e30_argument_1.getExpression();
    assertThat(e30_argument_1_v.getStringVal()).isEqualTo("b");

    // e31: f(:a, :b "foo")
    ExpressionNode e31 = varDefMap.get("e31").getValueExpression();
    assertThat(e31).isInstanceOf(CallNode.class);
    CallNode e31_call = (CallNode) e31;
    assertThat(e31_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(e31_call.getArguments().getList()).hasSize(2);

    PositionalArgumentNode e31_argument_0 = (PositionalArgumentNode) e31_call.getArguments().getList().get(0);
    assertThat(e31_argument_0.getExpression()).isInstanceOf(StringNode.class);
    StringNode e31_argument_0_v = (StringNode) e31_argument_0.getExpression();
    assertThat(e31_argument_0_v.getStringVal()).isEqualTo("a");

    NamedArgumentNode e31_argument_b = (NamedArgumentNode) e31_call.getArguments().getList().get(1);
    assertThat(e31_argument_b.getName()).isEqualTo("b");
    assertThat(e31_argument_b.getExpression()).isInstanceOf(StringNode.class);
    StringNode e31_argument_b_v = (StringNode) e31_argument_b.getExpression();
    assertThat(e31_argument_b_v.getStringVal()).isEqualTo("foo");

    // e32: ->> (8)
    //   (n) -> n*4
    //   (n) -> n+3
    //   (n) -> n*n

    ExpressionNode e32 = varDefMap.get("e32").getValueExpression();
    assertThat(e32).isInstanceOf(CallNode.class);

    // e32_expected: ((n) -> n*n)(((n) -> n+3)(((n) -> n*4)(8)))
    ExpressionNode e32_expected = varDefMap.get("e32_expected").getValueExpression();
    assertThat(e32).hasSameStructureAs(e32_expected);

    //    e33: match 42
    //        10:      "ten"
    //        20:      "twenty"
    //        42:      "the answer to everything"
    //        default: "unknown"
    ExpressionNode e33 = varDefMap.get("e33").getValueExpression();
    assertThat(e33).isInstanceOf(MatchNode.class);

    MatchNode e33_match = (MatchNode) e33;
    assertThat(e33_match.getSubject() instanceof LongNode);
    assertThat(((LongNode) e33_match.getSubject()).getLongNum()).isEqualTo(42L);
    List<MatchLineNode> e33_match_lines = e33_match.getMatchLines().getElements();

    assertThat(e33_match_lines).hasSize(4);

    MatchLineNode e33_line_0 = e33_match_lines.get(0);
    assertThat(e33_line_0.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode e33_line_0_pattern = (ExpressionPatternNode) e33_line_0.getPattern();
    assertThat(e33_line_0_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode) e33_line_0_pattern.getExpression()).getLongNum()).isEqualTo(10L);
    assertThat(e33_line_0.getExpression() instanceof StringNode);
    assertThat(((StringNode) e33_line_0.getExpression()).getStringVal()).isEqualTo("ten");

    MatchLineNode e33_line_1 = e33_match_lines.get(1);
    assertThat(e33_line_1.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode e33_line_1_pattern = (ExpressionPatternNode) e33_line_1.getPattern();
    assertThat(e33_line_1_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode) e33_line_1_pattern.getExpression()).getLongNum()).isEqualTo(20L);
    assertThat(e33_line_1.getExpression() instanceof StringNode);
    assertThat(((StringNode) e33_line_1.getExpression()).getStringVal()).isEqualTo("twenty");

    MatchLineNode e33_line_2 = e33_match_lines.get(2);
    assertThat(e33_line_2.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode e33_line_2_pattern = (ExpressionPatternNode) e33_line_2.getPattern();
    assertThat(e33_line_2_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode)e33_line_2_pattern.getExpression()).getLongNum()).isEqualTo(42L);
    assertThat(e33_line_2.getExpression() instanceof StringNode);
    assertThat(((StringNode) e33_line_2.getExpression()).getStringVal()).isEqualTo("the answer to everything");

    MatchLineNode e33_line_3 = e33_match_lines.get(3);
    assertThat(e33_line_3.getPattern() instanceof ExpressionPatternNode);
    DefaultPatternNode e33_line_3_pattern = (DefaultPatternNode) e33_line_3.getPattern();
    assertThat(e33_line_3.getExpression() instanceof StringNode);
    assertThat(((StringNode) e33_line_3.getExpression()).getStringVal()).isEqualTo("unknown");

  }

  @Test
  public void parses_empty_interactive_unit() throws Exception {

    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/empty_interactive.tf")
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
        new ResourceParseUnit(new ResourceLocation(), "fixtures/tweakflow/analysis/parsing/interactive.tf")
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