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
import com.twineworks.tweakflow.lang.ast.args.NamedArgumentNode;
import com.twineworks.tweakflow.lang.ast.args.PositionalArgumentNode;
import com.twineworks.tweakflow.lang.ast.args.SplatArgumentNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.imports.ModuleImportNode;
import com.twineworks.tweakflow.lang.ast.imports.NameImportNode;
import com.twineworks.tweakflow.lang.ast.partial.PartialArgumentNode;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.ast.structure.match.DefaultPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.ExpressionPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLineNode;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import com.twineworks.tweakflow.lang.types.Types;
import org.junit.jupiter.api.Test;

import java.util.*;

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
  public void parses_semantic_expressions() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic_expressions.tf");

    // nest_exp: (1)
    ExpressionNode nest_exp = varDefMap.get("nest_exp").getValueExpression();
    assertThat(nest_exp).isInstanceOf(LongNode.class);
    LongNode next_exp_value = (LongNode) nest_exp;
    assertThat(next_exp_value.getLongNum()).isEqualTo(1L);

    // bindings: let {a: 1} true
    ExpressionNode bindingsNode = varDefMap.get("bindings").getValueExpression();
    assertThat(bindingsNode).isInstanceOf(LetNode.class);
    LetNode bindingsLetNode = (LetNode) bindingsNode;

    LinkedHashMap<String, VarDefNode> bindings = bindingsLetNode.getBindings().getVars().getMap();
    assertThat(bindings).hasSize(1);
    assertThat(bindings.get("a").getDeclaredType()).isEqualTo(Types.ANY);
    assertThat(bindings.get("a").getSymbolName()).isEqualTo("a");

    BooleanNode bindingsValue = (BooleanNode) bindingsLetNode.getExpression();
    assertThat(bindingsValue.getBoolVal()).isEqualTo(Boolean.TRUE);

    // try_catch_e: try 0 catch e false
    ExpressionNode try_catch_e = varDefMap.get("try_catch_e").getValueExpression();
    assertThat(try_catch_e).isInstanceOf(TryCatchNode.class);
    TryCatchNode try_catch_e_node = (TryCatchNode) try_catch_e;
    assertThat(try_catch_e_node.getCaughtException().getSymbolName()).isEqualTo("e");
    assertThat(try_catch_e_node.getCaughtTrace()).isNull();

    assertThat(try_catch_e_node.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode try_catch_e_try_exp = (LongNode) try_catch_e_node.getTryExpression();
    assertThat(try_catch_e_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(try_catch_e_node.getCatchExpression()).isInstanceOf(BooleanNode.class);
    BooleanNode try_catch_e_catch = (BooleanNode) try_catch_e_node.getCatchExpression();
    assertThat(try_catch_e_catch.getBoolVal()).isFalse();

    // try_catch: try 0 catch false
    ExpressionNode try_catch = varDefMap.get("try_catch").getValueExpression();
    assertThat(try_catch).isInstanceOf(TryCatchNode.class);
    TryCatchNode try_catch_node = (TryCatchNode) try_catch;
    assertThat(try_catch_node.getCaughtException()).isNull();
    assertThat(try_catch_node.getCaughtTrace()).isNull();

    assertThat(try_catch_node.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode try_catch_try_exp = (LongNode) try_catch_node.getTryExpression();
    assertThat(try_catch_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(try_catch_node.getCatchExpression()).isInstanceOf(BooleanNode.class);
    BooleanNode try_catch_catch = (BooleanNode) try_catch_node.getCatchExpression();
    assertThat(try_catch_catch.getBoolVal()).isFalse();

    // throw_nil: throw nil
    ExpressionNode throw_nil = varDefMap.get("throw_nil").getValueExpression();
    assertThat(throw_nil).isInstanceOf(ThrowNode.class);
    ThrowNode throw_nil_node = (ThrowNode) throw_nil;
    assertThat(throw_nil_node.getExceptionExpression()).isInstanceOf(NilNode.class);

    // cast_str_as_long: "0001" as long
    ExpressionNode cast_str_as_long = varDefMap.get("cast_str_as_long").getValueExpression();
    assertThat(cast_str_as_long).isInstanceOf(CastNode.class);
    CastNode cast_str_as_long_cast = (CastNode) cast_str_as_long;
    assertThat(cast_str_as_long_cast.getExpression()).isInstanceOf(StringNode.class);
    StringNode cast_str_as_long_exp = (StringNode) cast_str_as_long_cast.getExpression();
    assertThat(cast_str_as_long_exp.getStringVal()).isEqualTo("0001");
    assertThat(cast_str_as_long_cast.getTargetType()).isEqualTo(Types.LONG);

    // if_then_else: if true then 1 else 0
    ExpressionNode if_then_else_node = varDefMap.get("if_then_else").getValueExpression();
    assertThat(if_then_else_node).isInstanceOf(IfNode.class);
    IfNode if_then_else = (IfNode) if_then_else_node;

    assertThat(if_then_else.getCondition()).isInstanceOf(BooleanNode.class);
    BooleanNode if_then_else_cond = (BooleanNode) if_then_else.getCondition();
    assertThat(if_then_else_cond.getBoolVal()).isTrue();

    assertThat(if_then_else.getThenExpression()).isInstanceOf(LongNode.class);
    LongNode if_then_else_then = (LongNode) if_then_else.getThenExpression();
    assertThat(if_then_else_then.getLongNum()).isEqualTo(1L);

    assertThat(if_then_else.getElseExpression()).isInstanceOf(LongNode.class);
    LongNode if_then_else_else = (LongNode) if_then_else.getElseExpression();
    assertThat(if_then_else_else.getLongNum()).isEqualTo(0L);

    // if_else: if true 1 else 0
    ExpressionNode if_else_node = varDefMap.get("if_else").getValueExpression();
    assertThat(if_else_node).isInstanceOf(IfNode.class);
    IfNode if_else = (IfNode) if_else_node;

    assertThat(if_else.getCondition()).isInstanceOf(BooleanNode.class);
    BooleanNode if_else_if_cond = (BooleanNode) if_else.getCondition();
    assertThat(if_else_if_cond.getBoolVal()).isTrue();

    assertThat(if_else.getThenExpression()).isInstanceOf(LongNode.class);
    LongNode if_else_if_then = (LongNode) if_else.getThenExpression();
    assertThat(if_else_if_then.getLongNum()).isEqualTo(1L);

    assertThat(if_else.getElseExpression()).isInstanceOf(LongNode.class);
    LongNode if_else_if_else = (LongNode) if_else.getElseExpression();
    assertThat(if_else_if_else.getLongNum()).isEqualTo(0L);

    // try_catch_e_t: try 0 catch e, trace nil # try evaluating 0, catch exception and trace and return nil

    ExpressionNode try_catch_e_t_node = varDefMap.get("try_catch_e_t").getValueExpression();
    assertThat(try_catch_e_t_node).isInstanceOf(TryCatchNode.class);
    TryCatchNode try_catch_e_t = (TryCatchNode) try_catch_e_t_node;
    assertThat(try_catch_e_t.getCaughtException().getSymbolName()).isEqualTo("e");
    assertThat(try_catch_e_t.getCaughtTrace().getSymbolName()).isEqualTo("trace");

    assertThat(try_catch_e_t.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode try_catch_e_t_try_exp = (LongNode) try_catch_e_t.getTryExpression();
    assertThat(try_catch_e_t_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(try_catch_e_t.getCatchExpression()).isInstanceOf(NilNode.class);

    // type_check: "foo" is string
    ExpressionNode type_check_node = varDefMap.get("type_check").getValueExpression();
    assertThat(type_check_node).isInstanceOf(IsNode.class);
    IsNode type_check = (IsNode) type_check_node;
    assertThat(type_check.getCompareType()).isEqualTo(Types.STRING);
    assertThat(type_check.getExpression()).isInstanceOf(StringNode.class);
    StringNode type_check_exp = (StringNode) type_check.getExpression();
    assertThat(type_check_exp.getStringVal()).isEqualTo("foo");

    // reference: import_name.lib.x
    ExpressionNode reference_node = varDefMap.get("reference").getValueExpression();
    assertThat(reference_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode reference = (ReferenceNode) reference_node;
    assertThat(reference.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> elements = reference.getElements();
    assertThat(elements).hasSize(3);
    assertThat(elements).containsExactly("import_name", "lib", "x");

    // f_partial_a: f(a="foo")
    ExpressionNode f_partial_a_node = varDefMap.get("f_partial_a").getValueExpression();
    assertThat(f_partial_a_node).isInstanceOf(PartialApplicationNode.class);
    PartialApplicationNode f_partial_a = (PartialApplicationNode) f_partial_a_node;
    assertThat(f_partial_a.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_partial_a.getArguments().getList()).hasSize(1);
    PartialArgumentNode f_partial_a_arg_a = f_partial_a.getArguments().getList().get(0);
    assertThat(f_partial_a_arg_a.getExpression()).isInstanceOf(StringNode.class);
    StringNode f_partial_a_arg_a_v = (StringNode) f_partial_a_arg_a.getExpression();
    assertThat(f_partial_a_arg_a_v.getStringVal()).isEqualTo("foo");

    // f_partial_a_b: f(a="foo", b="bar")
    ExpressionNode f_partial_a_b_node = varDefMap.get("f_partial_a_b").getValueExpression();
    assertThat(f_partial_a_b_node).isInstanceOf(PartialApplicationNode.class);
    PartialApplicationNode f_partial_a_b = (PartialApplicationNode) f_partial_a_b_node;
    assertThat(f_partial_a_b.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_partial_a_b.getArguments().getList()).hasSize(2);
    PartialArgumentNode f_partial_a_b_arg_a = f_partial_a_b.getArguments().getList().get(0);
    assertThat(f_partial_a_b_arg_a.getExpression()).isInstanceOf(StringNode.class);
    StringNode f_partial_a_b_arg_a_v = (StringNode) f_partial_a_b_arg_a.getExpression();
    assertThat(f_partial_a_b_arg_a_v.getStringVal()).isEqualTo("foo");
    PartialArgumentNode f_partial_a_b_arg_b = f_partial_a_b.getArguments().getList().get(1);
    assertThat(f_partial_a_b_arg_b.getExpression()).isInstanceOf(StringNode.class);
    StringNode f_partial_a_b_arg_b_v = (StringNode) f_partial_a_b_arg_b.getExpression();
    assertThat(f_partial_a_b_arg_b_v.getStringVal()).isEqualTo("bar");

    // f_call: f()
    ExpressionNode f_call_node = varDefMap.get("f_call").getValueExpression();
    assertThat(f_call_node).isInstanceOf(CallNode.class);
    CallNode f_call = (CallNode) f_call_node;
    assertThat(f_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call.getArguments().getList()).isEmpty();

    // f_call_1: f(1)
    ExpressionNode f_call_1_node = varDefMap.get("f_call_1").getValueExpression();
    assertThat(f_call_1_node).isInstanceOf(CallNode.class);
    CallNode f_call_1 = (CallNode) f_call_1_node;
    assertThat(f_call_1.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call_1.getArguments().getList()).hasSize(1);
    PositionalArgumentNode f_call_1_arg_0 = (PositionalArgumentNode) f_call_1.getArguments().getList().get(0);
    assertThat(f_call_1_arg_0.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_1_arg_0_v = (LongNode) f_call_1_arg_0.getExpression();
    assertThat(f_call_1_arg_0_v.getLongNum()).isEqualTo(1);

    // f_call_a1: f(:a 1)
    ExpressionNode f_call_a1_node = varDefMap.get("f_call_a1").getValueExpression();
    assertThat(f_call_a1_node).isInstanceOf(CallNode.class);
    CallNode f_call_a1 = (CallNode) f_call_a1_node;
    assertThat(f_call_a1.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call_a1.getArguments().getList()).hasSize(1);
    NamedArgumentNode f_call_a1_a = (NamedArgumentNode) f_call_a1.getArguments().getList().get(0);
    assertThat(f_call_a1_a.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_a1_a_v = (LongNode) f_call_a1_a.getExpression();
    assertThat(f_call_a1_a_v.getLongNum()).isEqualTo(1);

    // f_call_1_2: f(1, 2)
    ExpressionNode f_call_1_2_node = varDefMap.get("f_call_1_2").getValueExpression();
    assertThat(f_call_1_2_node).isInstanceOf(CallNode.class);
    CallNode f_call_1_2 = (CallNode) f_call_1_2_node;
    assertThat(f_call_1_2.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call_1_2.getArguments().getList()).hasSize(2);

    PositionalArgumentNode f_call_1_2_arg_0 = (PositionalArgumentNode) f_call_1_2.getArguments().getList().get(0);
    assertThat(f_call_1_2_arg_0.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_1_2_arg_0_v = (LongNode) f_call_1_2_arg_0.getExpression();
    assertThat(f_call_1_2_arg_0_v.getLongNum()).isEqualTo(1);

    PositionalArgumentNode f_call_1_2_arg_1 = (PositionalArgumentNode) f_call_1_2.getArguments().getList().get(1);
    assertThat(f_call_1_2_arg_1.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_1_2_arg_1_v = (LongNode) f_call_1_2_arg_1.getExpression();
    assertThat(f_call_1_2_arg_1_v.getLongNum()).isEqualTo(2);

    // f_call_a1_b2: f(:a 1, :b 2)
    ExpressionNode f_call_a1_b2_node = varDefMap.get("f_call_a1_b2").getValueExpression();
    assertThat(f_call_a1_b2_node).isInstanceOf(CallNode.class);
    CallNode f_call_a1_b2 = (CallNode) f_call_a1_b2_node;
    assertThat(f_call_a1_b2.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call_a1_b2.getArguments().getList()).hasSize(2);

    NamedArgumentNode f_call_a1_b2_arg_a = (NamedArgumentNode) f_call_a1_b2.getArguments().getList().get(0);
    assertThat(f_call_a1_b2_arg_a.getName()).isEqualTo("a");
    assertThat(f_call_a1_b2_arg_a.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_a1_b2_arg_a_v = (LongNode) f_call_a1_b2_arg_a.getExpression();
    assertThat(f_call_a1_b2_arg_a_v.getLongNum()).isEqualTo(1);

    NamedArgumentNode f_call_a1_b2_arg_b = (NamedArgumentNode) f_call_a1_b2.getArguments().getList().get(1);
    assertThat(f_call_a1_b2_arg_b.getName()).isEqualTo("b");
    assertThat(f_call_a1_b2_arg_b.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_a1_b2_arg_b_v = (LongNode) f_call_a1_b2_arg_b.getExpression();
    assertThat(f_call_a1_b2_arg_b_v.getLongNum()).isEqualTo(2);

    // f_call_1_2_c3: f(1, 2, :c 3)
    ExpressionNode f_call_1_2_c3_node = varDefMap.get("f_call_1_2_c3").getValueExpression();
    assertThat(f_call_1_2_c3_node).isInstanceOf(CallNode.class);
    CallNode f_call_1_2_c3 = (CallNode) f_call_1_2_c3_node;
    assertThat(f_call_1_2_c3.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call_1_2_c3.getArguments().getList()).hasSize(3);

    PositionalArgumentNode f_call_1_2_c3_arg_0 = (PositionalArgumentNode) f_call_1_2_c3.getArguments().getList().get(0);
    assertThat(f_call_1_2_c3_arg_0.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_1_2_c3_arg_0_v = (LongNode) f_call_1_2_c3_arg_0.getExpression();
    assertThat(f_call_1_2_c3_arg_0_v.getLongNum()).isEqualTo(1);

    PositionalArgumentNode f_call_1_2_c3_arg_1 = (PositionalArgumentNode) f_call_1_2_c3.getArguments().getList().get(1);
    assertThat(f_call_1_2_c3_arg_1.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_1_2_c3_arg_1_v = (LongNode) f_call_1_2_c3_arg_1.getExpression();
    assertThat(f_call_1_2_c3_arg_1_v.getLongNum()).isEqualTo(2);

    NamedArgumentNode f_call_1_2_c3_arg_c = (NamedArgumentNode) f_call_1_2_c3.getArguments().getList().get(2);
    assertThat(f_call_1_2_c3_arg_c.getName()).isEqualTo("c");
    assertThat(f_call_1_2_c3_arg_c.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_1_2_c3_arg_c_v = (LongNode) f_call_1_2_c3_arg_c.getExpression();
    assertThat(f_call_1_2_c3_arg_c_v.getLongNum()).isEqualTo(3);

    // f_call_1_sp_a1: f(1, ...{:a 1})
    ExpressionNode f_call_1_sp_a1_node = varDefMap.get("f_call_1_sp_a1").getValueExpression();
    assertThat(f_call_1_sp_a1_node).isInstanceOf(CallNode.class);
    CallNode f_call_1_sp_a1 = (CallNode) f_call_1_sp_a1_node;
    assertThat(f_call_1_sp_a1.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call_1_sp_a1.getArguments().getList()).hasSize(2);

    PositionalArgumentNode f_call_1_sp_a1_arg_0 = (PositionalArgumentNode) f_call_1_sp_a1.getArguments().getList().get(0);
    assertThat(f_call_1_sp_a1_arg_0.getExpression()).isInstanceOf(LongNode.class);
    LongNode f_call_1_sp_a1_arg_0_v = (LongNode) f_call_1_sp_a1_arg_0.getExpression();
    assertThat(f_call_1_sp_a1_arg_0_v.getLongNum()).isEqualTo(1);

    SplatArgumentNode f_call_1_sp_a1_arg_1 = (SplatArgumentNode) f_call_1_sp_a1.getArguments().getList().get(1);
    assertThat(f_call_1_sp_a1_arg_1.getExpression()).isInstanceOf(DictNode.class);
    DictNode f_call_1_sp_a1_arg_1_v = (DictNode) f_call_1_sp_a1_arg_1.getExpression();
    assertThat(f_call_1_sp_a1_arg_1_v.getEntries()).hasSize(1);

    // lib_ref: library::e0            # library reference
    ExpressionNode lib_ref_node = varDefMap.get("lib_ref").getValueExpression();
    assertThat(lib_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode lib_ref = (ReferenceNode) lib_ref_node;
    assertThat(lib_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.LIBRARY);
    List<String> lib_ref_elements = lib_ref.getElements();
    assertThat(lib_ref_elements).hasSize(1);
    assertThat(lib_ref_elements).containsExactly("e0");

    // mod_short_ref: ::lib.e0               # module reference
    ExpressionNode mod_short_ref_node = varDefMap.get("mod_short_ref").getValueExpression();
    assertThat(mod_short_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode mod_short_ref = (ReferenceNode) mod_short_ref_node;
    assertThat(mod_short_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.MODULE);
    List<String> mod_short_ref_elements = mod_short_ref.getElements();
    assertThat(mod_short_ref_elements).hasSize(2);
    assertThat(mod_short_ref_elements).containsExactly("lib", "e0");

    // mod_ref: module::lib.e0         # module reference
    ExpressionNode mod_ref_node = varDefMap.get("mod_ref").getValueExpression();
    assertThat(mod_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode mod_ref = (ReferenceNode) mod_ref_node;
    assertThat(mod_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.MODULE);
    List<String> mod_ref_elements = mod_ref.getElements();
    assertThat(mod_ref_elements).hasSize(2);
    assertThat(mod_ref_elements).containsExactly("lib", "e0");

//    global_short_ref: $global_var            # global reference
    ExpressionNode global_short_ref_node = varDefMap.get("global_short_ref").getValueExpression();
    assertThat(global_short_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode global_short_ref = (ReferenceNode) global_short_ref_node;
    assertThat(global_short_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.GLOBAL);
    List<String> global_short_ref_elements = global_short_ref.getElements();
    assertThat(global_short_ref_elements).hasSize(1);
    assertThat(global_short_ref_elements).containsExactly("global_var");

//    global_ref: global::global_var     # global reference
    ExpressionNode global_ref_node = varDefMap.get("global_ref").getValueExpression();
    assertThat(global_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode global_ref = (ReferenceNode) global_ref_node;
    assertThat(global_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.GLOBAL);
    List<String> global_ref_elements = global_ref.getElements();
    assertThat(global_ref_elements).hasSize(1);
    assertThat(global_ref_elements).containsExactly("global_var");

//    local_ref: e0                     # local reference
    ExpressionNode local_ref_node = varDefMap.get("local_ref").getValueExpression();
    assertThat(local_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode local_ref = (ReferenceNode) local_ref_node;
    assertThat(local_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> local_ref_elements = local_ref.getElements();
    assertThat(local_ref_elements).hasSize(1);
    assertThat(local_ref_elements).containsExactly("e0");

//  string_inter: "string ${e0}"         # string reference interpolation
    ExpressionNode string_inter_node = varDefMap.get("string_inter").getValueExpression();
    assertThat(string_inter_node).isInstanceOf(StringConcatNode.class);
    StringConcatNode string_inter = (StringConcatNode) string_inter_node;
    assertThat(string_inter.getLeftExpression()).isInstanceOf(StringNode.class);
    StringNode string_inter_left = (StringNode) string_inter.getLeftExpression();
    assertThat(string_inter_left.getStringVal()).isEqualTo("string ");

    assertThat(string_inter.getRightExpression()).isInstanceOf(ReferenceNode.class);
    ReferenceNode string_inter_right = (ReferenceNode) string_inter.getRightExpression();
    assertThat(string_inter_right.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> string_inter_right_elements = string_inter_right.getElements();
    assertThat(string_inter_right_elements).hasSize(1);
    assertThat(string_inter_right_elements).containsExactly("e0");

    // f_call_a_b: f(:a, :b)
    ExpressionNode f_call_a_b_node = varDefMap.get("f_call_a_b").getValueExpression();
    assertThat(f_call_a_b_node).isInstanceOf(CallNode.class);
    CallNode f_call_a_b = (CallNode) f_call_a_b_node;
    assertThat(f_call_a_b.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call_a_b.getArguments().getList()).hasSize(2);

    PositionalArgumentNode f_call_a_b_arg_0 = (PositionalArgumentNode) f_call_a_b.getArguments().getList().get(0);
    assertThat(f_call_a_b_arg_0.getExpression()).isInstanceOf(StringNode.class);
    StringNode f_call_a_b_arg_0_v = (StringNode) f_call_a_b_arg_0.getExpression();
    assertThat(f_call_a_b_arg_0_v.getStringVal()).isEqualTo("a");

    PositionalArgumentNode f_call_a_b_arg_1 = (PositionalArgumentNode) f_call_a_b.getArguments().getList().get(1);
    assertThat(f_call_a_b_arg_1.getExpression()).isInstanceOf(StringNode.class);
    StringNode f_call_a_b_arg_1_v = (StringNode) f_call_a_b_arg_1.getExpression();
    assertThat(f_call_a_b_arg_1_v.getStringVal()).isEqualTo("b");

    // f_call_a_bfoo: f(:a, :b "foo")
    ExpressionNode f_call_a_bfoo_node = varDefMap.get("f_call_a_bfoo").getValueExpression();
    assertThat(f_call_a_bfoo_node).isInstanceOf(CallNode.class);
    CallNode f_call_a_bfoo = (CallNode) f_call_a_bfoo_node;
    assertThat(f_call_a_bfoo.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call_a_bfoo.getArguments().getList()).hasSize(2);

    PositionalArgumentNode f_call_a_bfoo_arg_0 = (PositionalArgumentNode) f_call_a_bfoo.getArguments().getList().get(0);
    assertThat(f_call_a_bfoo_arg_0.getExpression()).isInstanceOf(StringNode.class);
    StringNode f_call_a_bfoo_arg_0_v = (StringNode) f_call_a_bfoo_arg_0.getExpression();
    assertThat(f_call_a_bfoo_arg_0_v.getStringVal()).isEqualTo("a");

    NamedArgumentNode f_call_a_bfoo_arg_b = (NamedArgumentNode) f_call_a_bfoo.getArguments().getList().get(1);
    assertThat(f_call_a_bfoo_arg_b.getName()).isEqualTo("b");
    assertThat(f_call_a_bfoo_arg_b.getExpression()).isInstanceOf(StringNode.class);
    StringNode f_call_a_bfoo_arg_b_v = (StringNode) f_call_a_bfoo_arg_b.getExpression();
    assertThat(f_call_a_bfoo_arg_b_v.getStringVal()).isEqualTo("foo");

    // e32: ->> (8)
    //   (n) -> n*4
    //   (n) -> n+3
    //   (n) -> n*n

    ExpressionNode thread = varDefMap.get("thread").getValueExpression();
    assertThat(thread).isInstanceOf(CallNode.class);

    // thread_expected: ((n) -> n*n)(((n) -> n+3)(((n) -> n*4)(8)))
    ExpressionNode thread_expected = varDefMap.get("thread_expected").getValueExpression();
    assertThat(thread).hasSameStructureAs(thread_expected);

    //    match_42: match 42
    //        10:      "ten"
    //        20:      "twenty"
    //        42:      "the answer to everything"
    //        default: "unknown"
    ExpressionNode match_42_node = varDefMap.get("match_42").getValueExpression();
    assertThat(match_42_node).isInstanceOf(MatchNode.class);

    MatchNode match_42 = (MatchNode) match_42_node;
    assertThat(match_42.getSubject() instanceof LongNode);
    assertThat(((LongNode) match_42.getSubject()).getLongNum()).isEqualTo(42L);
    List<MatchLineNode> match_42_lines = match_42.getMatchLines().getElements();

    assertThat(match_42_lines).hasSize(4);

    MatchLineNode match_42_line_0 = match_42_lines.get(0);
    assertThat(match_42_line_0.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode match_42_line_0_pattern = (ExpressionPatternNode) match_42_line_0.getPattern();
    assertThat(match_42_line_0_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode) match_42_line_0_pattern.getExpression()).getLongNum()).isEqualTo(10L);
    assertThat(match_42_line_0.getExpression() instanceof StringNode);
    assertThat(((StringNode) match_42_line_0.getExpression()).getStringVal()).isEqualTo("ten");

    MatchLineNode match_42_line_1 = match_42_lines.get(1);
    assertThat(match_42_line_1.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode match_42_line_1_pattern = (ExpressionPatternNode) match_42_line_1.getPattern();
    assertThat(match_42_line_1_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode) match_42_line_1_pattern.getExpression()).getLongNum()).isEqualTo(20L);
    assertThat(match_42_line_1.getExpression() instanceof StringNode);
    assertThat(((StringNode) match_42_line_1.getExpression()).getStringVal()).isEqualTo("twenty");

    MatchLineNode match_42_line_2 = match_42_lines.get(2);
    assertThat(match_42_line_2.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode match_42_line_2_pattern = (ExpressionPatternNode) match_42_line_2.getPattern();
    assertThat(match_42_line_2_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode)match_42_line_2_pattern.getExpression()).getLongNum()).isEqualTo(42L);
    assertThat(match_42_line_2.getExpression() instanceof StringNode);
    assertThat(((StringNode) match_42_line_2.getExpression()).getStringVal()).isEqualTo("the answer to everything");

    MatchLineNode match_42_line_3 = match_42_lines.get(3);
    assertThat(match_42_line_3.getPattern() instanceof ExpressionPatternNode);
    DefaultPatternNode match_42_line_3_pattern = (DefaultPatternNode) match_42_line_3.getPattern();
    assertThat(match_42_line_3.getExpression() instanceof StringNode);
    assertThat(((StringNode) match_42_line_3.getExpression()).getStringVal()).isEqualTo("unknown");

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