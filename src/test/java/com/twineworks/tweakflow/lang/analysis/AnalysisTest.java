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

import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.scope.GlobalScope;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.types.Types;
import org.junit.Test;

import java.util.*;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class AnalysisTest {

  private AnalysisResult analyze(String ... paths){

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();

    List<String> filePaths = new ArrayList<>(paths.length);
    filePaths.addAll(Arrays.asList(paths));

    return Analysis.analyze(filePaths, loadPath);
  }

  @Test
  public void analyzes_empty_module() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/empty.tf");

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    // module node is empty
    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get("fixtures/tweakflow/analysis/empty.tf").getUnit();
    assertThat(module).isNotNull();
    assertThat(module.hasMeta()).isFalse();
    assertThat(module.hasDoc()).isFalse();

  }

  @Test
  public void fails_on_non_literal_docs() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/meta/errors/non_literal_docs.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.LITERAL_VALUE_REQUIRED);

  }


  @Test
  public void fails_on_non_literal_meta_data() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/meta/errors/non_literal_metadata.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.LITERAL_VALUE_REQUIRED);

  }


  @Test
  public void fails_on_deep_non_literal_docs() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/meta/errors/deep_non_literal_docs.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.LITERAL_VALUE_REQUIRED);

  }


  @Test
  public void fails_on_deep_non_literal_meta_data() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/meta/errors/deep_non_literal_metadata.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.LITERAL_VALUE_REQUIRED);

  }

  @Test
  public void fails_on_non_constant_default_parameter() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/non_const_default_parameter.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.LITERAL_VALUE_REQUIRED);

  }

  @Test
  public void fails_on_redefined_library() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/libraries/errors/already_defined_lib.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.ALREADY_DEFINED);

  }

  @Test
  public void fails_on_unresolved_variable_reference() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/unresolved_var_reference.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);

  }

  @Test
  public void fails_on_variable_referencing_a_non_var() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/invalid_var_reference.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.INVALID_REFERENCE_TARGET);

  }

  @Test
  public void fails_on_for_unordered_scope() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/for_unordered_scope.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);

  }

  @Test
  public void fails_on_for_shadowed_variable() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/for_shadowing.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.ALREADY_DEFINED);

  }

  @Test
  public void fails_on_multiple_default_match_patterns() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/match_multiple_default_patterns.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.DEFAULT_PATTERN_NOT_LAST);

  }

  @Test
  public void fails_on_default_match_pattern_not_last() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/match_default_pattern_not_last.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.DEFAULT_PATTERN_NOT_LAST);

  }

  @Test
  public void fails_on_match_pattern_referencing_binding() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/match_pattern_referencing_binding.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);

  }

  @Test
  public void fails_on_match_pattern_invalid_open_dict_capture() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/match_pattern_invalid_open_dict_capture.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.PARSE_ERROR);

  }

  @Test
  public void fails_on_call_referencing_a_non_function() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/invalid_call_reference.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.INVALID_REFERENCE_TARGET);

  }

  @Test
  public void fails_on_unresolved_variable_reference_in_import() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/unresolved_var_reference_in_import.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);

  }

  @Test
  public void fails_on_unresolved_multi_element_variable_reference() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/unresolved_multi_element_var_reference.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);

  }

  @Test
  public void fails_on_redefined_library_variable() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/libraries/errors/already_defined_lib_var.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.ALREADY_DEFINED);

  }

  @Test
  public void fails_on_redefined_function_parameter() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/already_defined_function_parameter.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.ALREADY_DEFINED);

  }

  @Test
  public void fails_on_redefined_import() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/imports/errors/already_defined_import.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.ALREADY_DEFINED);

  }

  @Test
  public void analyzes_basic_library() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/libraries/basic_library.tf");

    if (!result.isSuccess()){
      result.getException().printDigest();
      result.getException().printStackTrace();
    }

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get("fixtures/tweakflow/analysis/libraries/basic_library.tf").getUnit();

    // module has a local scope
    assertThat(module.getScope()).isNotNull();

    // library exists
    assertThat(module.getComponents()).hasSize(1);

  }

  @Test
  public void analyzes_library_with_imports() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/imports/main.tf");

    if (!result.isSuccess()){
      result.getException().printDigest();
      result.getException().printStackTrace();
    }
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void analyzes_conditionals() throws Exception {

    String path = "fixtures/tweakflow/analysis/expressions/conditionals.tf";
    AnalysisResult result = analyze(path);
    if (!result.isSuccess()) result.getException().printStackTrace();
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode lib = (LibraryNode) module.getComponentsMap().get("lib");
    Map<String, VarDefNode> vars = lib.getVars().getMap();

    IfNode cond = (IfNode) vars.get("cond").getValueExpression();

    assertThat(cond.getCondition()).hasSameStructureAs(
        new CastNode()
        .setTargetType(Types.BOOLEAN)
        .setExpression(new StringNode("a")));
    assertThat(cond.getThenExpression()).hasSameStructureAs(new StringNode("b"));
    assertThat(cond.getElseExpression()).hasSameStructureAs(new StringNode("c"));

    // ensure all conditionals resulted in the exact same structure
    IfNode cond_short = (IfNode) vars.get("cond_short").getValueExpression();

    assertThat(cond).hasSameStructureAs(cond_short);

  }

  @Test
  public void analyzes_bindings() throws Exception {

    String path = "fixtures/tweakflow/analysis/expressions/bindings.tf";
    AnalysisResult result = analyze(path);
    if (!result.isSuccess()) result.getException().printStackTrace();
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode lib = (LibraryNode) module.getComponentsMap().get("lib");
    Map<String, VarDefNode> vars = lib.getVars().getMap();

    // e0: let {a: 1} a + a
    VarDefNode e0 = vars.get("e0");
    ExpressionNode valueExpression = e0.getValueExpression();
    assertThat(valueExpression).isInstanceOf(LetNode.class);
    LetNode letNode = (LetNode) valueExpression;
    assertThat(letNode.getExpression()).isInstanceOf(PlusNode.class);

  }

  @Test
  public void analyzes_closures() throws Exception {

    String path = "fixtures/tweakflow/analysis/expressions/closures.tf";
    AnalysisResult result = analyze(path);
    if (!result.isSuccess()) result.getException().printStackTrace();
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode lib = (LibraryNode) module.getComponentsMap().get("lib");
    Map<String, VarDefNode> vars = lib.getVars().getMap();

    // a: 100
    // f: () -> a
    VarDefNode f = vars.get("f");
    ExpressionNode f_valueExpression = f.getValueExpression();
    assertThat(f_valueExpression).isInstanceOf(FunctionNode.class);
    FunctionNode f_functionNode = (FunctionNode) f_valueExpression;
    Set<ReferenceNode> f_closures = f_functionNode.getClosedOverReferences();
    assertThat(f_closures).hasSize(1);
    ReferenceNode f_closure_a = f_closures.iterator().next();
    assertThat(f_closure_a.getElements()).containsExactly("a");
    assertThat(f_closure_a.isClosure()).isTrue();

    // g: (x) -> [a x]
    VarDefNode g = vars.get("g");
    ExpressionNode g_valueExpression = g.getValueExpression();
    assertThat(g_valueExpression).isInstanceOf(FunctionNode.class);
    FunctionNode g_functionNode = (FunctionNode) g_valueExpression;
    Set<ReferenceNode> g_closures = g_functionNode.getClosedOverReferences();
    assertThat(g_closures).hasSize(1);
    ReferenceNode g_closure_a = g_closures.iterator().next();
    assertThat(g_closure_a.getElements()).containsExactly("a");
    assertThat(g_closure_a.isClosure()).isTrue();

    // h: (c) ->
    //    (x) -> [x c a]
    // verify outer function h closes over a
    VarDefNode h = vars.get("h");
    ExpressionNode h_valueExpression = h.getValueExpression();
    assertThat(h_valueExpression).isInstanceOf(FunctionNode.class);
    FunctionNode h_functionNode = (FunctionNode) h_valueExpression;
    Set<ReferenceNode> h_closures = h_functionNode.getClosedOverReferences();
    assertThat(h_closures).hasSize(1);
    ReferenceNode h_closure_a = h_closures.iterator().next();
    assertThat(h_closure_a.getElements()).containsExactly("a");
    assertThat(h_closure_a.isClosure()).isTrue();

    // verify inner function anonymous closes over a and c
    FunctionNode inner_functionNode = (FunctionNode) h_functionNode.getExpression();
    Set<ReferenceNode> inner_closures = inner_functionNode.getClosedOverReferences();
    assertThat(inner_closures).hasSize(2);

    // verify inner_closures contains a,c (order undefined)
    Iterator<ReferenceNode> iterator = inner_closures.iterator();
    ReferenceNode first = iterator.next();
    ReferenceNode second = iterator.next();
    assertThat(first.isClosure()).isTrue();
    assertThat(second.isClosure()).isTrue();

    if (first.getElements().get(0).equals("a")){
      assertThat(first.getElements()).containsExactly("a");
      assertThat(second.getElements()).containsExactly("c");
    }
    else {
      assertThat(first.getElements()).containsExactly("c");
      assertThat(second.getElements()).containsExactly("a");
    }

  }

  @Test
  public void analyzes_implicit_casts() throws Exception {

    String path = "fixtures/tweakflow/analysis/expressions/implicit_casts.tf";
    AnalysisResult result = analyze(path);
    if (!result.isSuccess()) result.getException().printStackTrace();
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode lib = (LibraryNode) module.getComponentsMap().get("lib");
    Map<String, VarDefNode> vars = lib.getVars().getMap();

    // string string_direct: "hello"
    ExpressionNode string_direct = vars.get("string_direct").getValueExpression();
    assertThat(string_direct).hasSameStructureAs(new StringNode("hello"));

    // string string_cast: 1
    ExpressionNode string_cast = vars.get("string_cast").getValueExpression();
    assertThat(string_cast).isInstanceOf(CastNode.class);
    CastNode string_cast_node = (CastNode) string_cast;
    assertThat(string_cast_node.getTargetType()).isSameAs(Types.STRING);
    assertThat(string_cast_node.getExpression()).hasSameStructureAs(new LongNode(1L));
    // implicit casts inherit source info from their expression
    assertThat(string_cast_node.getSourceInfo()).isSameAs(string_cast_node.getExpression().getSourceInfo());

    // boolean boolean_direct: false
    ExpressionNode boolean_direct = vars.get("boolean_direct").getValueExpression();
    assertThat(boolean_direct).hasSameStructureAs(new BooleanNode(Boolean.FALSE));

    // boolean boolean_cast: 1
    ExpressionNode boolean_cast = vars.get("boolean_cast").getValueExpression();
    assertThat(boolean_cast).isInstanceOf(CastNode.class);
    CastNode boolean_cast_node = (CastNode) boolean_cast;
    assertThat(boolean_cast_node.getTargetType()).isSameAs(Types.BOOLEAN);
    assertThat(boolean_cast_node.getExpression()).hasSameStructureAs(new LongNode(1L));
    assertThat(boolean_cast_node.getSourceInfo()).isSameAs(boolean_cast_node.getExpression().getSourceInfo());

    // long long_direct: 1
    ExpressionNode long_direct = vars.get("long_direct").getValueExpression();
    assertThat(long_direct).hasSameStructureAs(new LongNode(1L));

    // long long_cast: "123"
    ExpressionNode long_cast = vars.get("long_cast").getValueExpression();
    assertThat(long_cast).isInstanceOf(CastNode.class);
    CastNode long_cast_node = (CastNode) long_cast;
    assertThat(long_cast_node.getTargetType()).isSameAs(Types.LONG);
    assertThat(long_cast_node.getExpression()).hasSameStructureAs(new StringNode("123"));
    assertThat(long_cast_node.getSourceInfo()).isSameAs(long_cast_node.getExpression().getSourceInfo());

    // double double_direct: 1.0
    ExpressionNode double_direct = vars.get("double_direct").getValueExpression();
    assertThat(double_direct).hasSameStructureAs(new DoubleNode(1.0d));

    // double double_cast: "123.0"
    ExpressionNode double_cast = vars.get("double_cast").getValueExpression();
    assertThat(double_cast).isInstanceOf(CastNode.class);
    CastNode double_cast_node = (CastNode) double_cast;
    assertThat(double_cast_node.getTargetType()).isSameAs(Types.DOUBLE);
    assertThat(double_cast_node.getExpression()).hasSameStructureAs(new StringNode("123.0"));
    assertThat(double_cast_node.getSourceInfo()).isSameAs(double_cast_node.getExpression().getSourceInfo());

    // list list_direct: []
    ExpressionNode list_direct = vars.get("list_direct").getValueExpression();
    assertThat(list_direct).hasSameStructureAs(new ListNode().setElements(Collections.emptyList()));

    // list list_cast: "hello"
    ExpressionNode list_cast = vars.get("list_cast").getValueExpression();
    assertThat(list_cast).isInstanceOf(CastNode.class);
    CastNode list_cast_node = (CastNode) list_cast;
    assertThat(list_cast_node.getTargetType()).isSameAs(Types.LIST);
    assertThat(list_cast_node.getExpression()).hasSameStructureAs(new StringNode("hello"));
    assertThat(list_cast_node.getSourceInfo()).isSameAs(list_cast_node.getExpression().getSourceInfo());

    // map map_direct: {}
    ExpressionNode map_direct = vars.get("map_direct").getValueExpression();
    assertThat(map_direct).hasSameStructureAs(new DictNode().setEntries(Collections.emptyList()));

    // map map_cast:  ["key" "value"]
    ExpressionNode map_cast = vars.get("map_cast").getValueExpression();
    assertThat(map_cast).isInstanceOf(CastNode.class);
    CastNode map_cast_node = (CastNode) map_cast;
    assertThat(map_cast_node.getTargetType()).isSameAs(Types.DICT);
    assertThat(map_cast_node.getExpression()).hasSameStructureAs(
        new ListNode()
            .setElements(Arrays.asList(
                new StringNode("key"),
                new StringNode("value"))));
    assertThat(map_cast_node.getSourceInfo()).isSameAs(map_cast_node.getExpression().getSourceInfo());

    // map_keys: {1 "one" 2 "two"}
    // all map keys must cast to strings
    DictNode map_keys = (DictNode) vars.get("map_keys").getValueExpression();
    for (DictEntryNode dictEntryNode : map_keys.getEntries()) {
      assertThat(dictEntryNode.getKey().getValueType()).isSameAs(Types.STRING);
    }

    // function f_direct: () -> long 1 # returning a long
    FunctionNode f_direct = (FunctionNode) vars.get("f_direct").getValueExpression();
    ExpressionNode f_direct_expression = f_direct.getExpression();
    assertThat(f_direct_expression).hasSameStructureAs(new LongNode(1L));

    // function f_cast: () -> string 1 # returning a long that must be cast to string
    FunctionNode f_cast = (FunctionNode) vars.get("f_cast").getValueExpression();
    ExpressionNode f_cast_expression = f_cast.getExpression();
    assertThat(f_cast_expression).isInstanceOf(CastNode.class);
    CastNode f_cast_expression_cast_node = (CastNode) f_cast_expression;
    assertThat(f_cast_expression_cast_node.getTargetType()).isSameAs(Types.STRING);
    assertThat(f_cast_expression_cast_node.getExpression()).hasSameStructureAs(new LongNode(1L));

  }

  @Test
  public void analyzes_explicit_casts() throws Exception {

    String path = "fixtures/tweakflow/analysis/expressions/explicit_casts.tf";
    AnalysisResult result = analyze(path);
    if (!result.isSuccess()){
      result.getException().printDetails();
    }
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode lib = (LibraryNode) module.getComponentsMap().get("lib");
    Map<String, VarDefNode> vars = lib.getVars().getMap();

    // verify types of expressions
    assertThat(vars.get("boolean_to_long").getValueExpression().getValueType()).isSameAs(Types.LONG);
    assertThat(vars.get("boolean_to_double").getValueExpression().getValueType()).isSameAs(Types.DOUBLE);
    assertThat(vars.get("boolean_to_string").getValueExpression().getValueType()).isSameAs(Types.STRING);
    assertThat(vars.get("string_to_boolean").getValueExpression().getValueType()).isSameAs(Types.BOOLEAN);
    assertThat(vars.get("string_to_long").getValueExpression().getValueType()).isSameAs(Types.LONG);
    assertThat(vars.get("string_to_double").getValueExpression().getValueType()).isSameAs(Types.DOUBLE);
    assertThat(vars.get("string_to_list").getValueExpression().getValueType()).isSameAs(Types.LIST);
    assertThat(vars.get("long_to_boolean").getValueExpression().getValueType()).isSameAs(Types.BOOLEAN);
    assertThat(vars.get("long_to_double").getValueExpression().getValueType()).isSameAs(Types.DOUBLE);
    assertThat(vars.get("long_to_string").getValueExpression().getValueType()).isSameAs(Types.STRING);
    assertThat(vars.get("double_to_boolean").getValueExpression().getValueType()).isSameAs(Types.BOOLEAN);
    assertThat(vars.get("double_to_long").getValueExpression().getValueType()).isSameAs(Types.LONG);
    assertThat(vars.get("double_to_string").getValueExpression().getValueType()).isSameAs(Types.STRING);
    assertThat(vars.get("list_to_boolean").getValueExpression().getValueType()).isSameAs(Types.BOOLEAN);
    assertThat(vars.get("list_to_map").getValueExpression().getValueType()).isSameAs(Types.DICT);
    assertThat(vars.get("map_to_boolean").getValueExpression().getValueType()).isSameAs(Types.BOOLEAN);
    assertThat(vars.get("map_to_list").getValueExpression().getValueType()).isSameAs(Types.LIST);
    assertThat(vars.get("nil_to_boolean").getValueExpression().getValueType()).isSameAs(Types.BOOLEAN);

  }

  @Test
  public void analyzes_simple_interactive_session() throws Exception {

    String modulePath = "fixtures/tweakflow/analysis/interactive/module_a.tf";
    String interactivePath = "fixtures/tweakflow/analysis/interactive/simple_interactive_session.tf";
    AnalysisResult result = analyze(modulePath, interactivePath);
    if (!result.isSuccess()){
      result.getException().printDetails();
    }
    assertThat(result.isSuccess()).isTrue();

    GlobalScope globalScope = result.getAnalysisSet().getGlobalScope();
    InteractiveNode interactiveNode = (InteractiveNode) result.getAnalysisSet().getUnits().get(interactivePath).getUnit();
    ModuleNode moduleNode = (ModuleNode) result.getAnalysisSet().getUnits().get(modulePath).getUnit();

    // interactive node lives in unit scope
    assertThat(interactiveNode.getScope()).isSameAs(globalScope.getUnitScope());

    // interactive
    // in_scope `fixtures/tweakflow/analysis/interactive/module_a.tf`
    //  x: lib.a

    InteractiveSectionNode sectionNode = interactiveNode.getSections().get(0);

    // section node lives in same scope as interactive node
    assertThat(sectionNode.getScope()).isSameAs(interactiveNode.getUnitScope());

    // vars live in bindings scope
    VarDefs vars = sectionNode.getVars();
    assertThat(vars.getScope()).isNotSameAs(sectionNode.getScope());
    assertThat(vars.getScope().getEnclosingScope()).isSameAs(sectionNode.getInScopeRef().getReferencedSymbol());
    assertThat(vars.getScope().getSymbols()).containsOnlyKeys("x");

    // reference to module_a resolves
    Symbol sectionSymbol = sectionNode.getInScopeRef().getReferencedSymbol();
    assertThat(sectionSymbol).isNotNull();

    // reference x: a resolves
    ReferenceNode a = (ReferenceNode) vars.getMap().get("x").getValueExpression();
    assertThat(a.getReferencedSymbol()).isNotNull();

    LibraryNode lib = (LibraryNode) moduleNode.getComponentsMap().get("lib");
    assertThat(a.getReferencedSymbol()).isSameAs(lib.getVars().getMap().get("a").getSymbol());

  }

  @Test
  public void analyzes_interactive_closure() throws Exception {

    String modulePath = "fixtures/tweakflow/analysis/interactive/module_a.tf";
    String interactivePath = "fixtures/tweakflow/analysis/interactive/interactive_closure.tf";
    AnalysisResult result = analyze(modulePath, interactivePath);
    if (!result.isSuccess()){
      result.getException().printDetails();
    }
    assertThat(result.isSuccess()).isTrue();

    GlobalScope globalScope = result.getAnalysisSet().getGlobalScope();
    InteractiveNode interactiveNode = (InteractiveNode) result.getAnalysisSet().getUnits().get(interactivePath).getUnit();

    // interactive node lives in unit scope
    assertThat(interactiveNode.getScope()).isSameAs(globalScope.getUnitScope());

    // interactive
    // in_scope `fixtures/tweakflow/analysis/interactive/module_a.tf`
    //  f: () -> lib.a

    InteractiveSectionNode sectionNode = interactiveNode.getSections().get(0);

    Map<String, VarDefNode> vars = sectionNode.getVars().getMap();

    VarDefNode f = vars.get("f");
    ExpressionNode f_valueExpression = f.getValueExpression();
    assertThat(f_valueExpression).isInstanceOf(FunctionNode.class);
    FunctionNode f_functionNode = (FunctionNode) f_valueExpression;
    Set<ReferenceNode> f_closures = f_functionNode.getClosedOverReferences();
    assertThat(f_closures).hasSize(1);
    ReferenceNode f_closure_a = f_closures.iterator().next();
    assertThat(f_closure_a.getElements()).containsExactly("lib", "a");
    assertThat(f_closure_a.isClosure()).isTrue();

  }


  @Test
  public void analyzes_interactive_session() throws Exception {

    String module_a_path = "fixtures/tweakflow/analysis/interactive/module_a.tf";
    String module_b_path = "fixtures/tweakflow/analysis/interactive/module_b.tf";
    String interactive_path = "fixtures/tweakflow/analysis/interactive/interactive_session.tf";
    AnalysisResult result = analyze(module_a_path, module_b_path, interactive_path);
    if (!result.isSuccess()){
      result.getException().printDetails();
    }
    assertThat(result.isSuccess()).isTrue();

    GlobalScope globalScope = result.getAnalysisSet().getGlobalScope();
    InteractiveNode interactive_node = (InteractiveNode) result.getAnalysisSet().getUnits().get(interactive_path).getUnit();
    ModuleNode module_a_node = (ModuleNode) result.getAnalysisSet().getUnits().get(module_a_path).getUnit();
    ModuleNode module_b_node = (ModuleNode) result.getAnalysisSet().getUnits().get(module_b_path).getUnit();

    // interactive node lives in unit scope
    assertThat(interactive_node.getScope()).isSameAs(globalScope.getUnitScope());

    /*
    interactive
      in_scope `fixtures/tweakflow/analysis/interactive/module_a.tf`
        x: lib.a
      in_scope `fixtures/tweakflow/analysis/interactive/module_b.tf`
        y: lib.b
    */

    InteractiveSectionNode section_a_node = interactive_node.getSections().get(0);

    // section node lives in same scope as interactive node
    assertThat(section_a_node.getScope()).isSameAs(interactive_node.getUnitScope());

    // vars live in bindings scope
    VarDefs section_a_vars = section_a_node.getVars();
    assertThat(section_a_vars.getScope()).isNotSameAs(section_a_node.getScope());
    assertThat(section_a_vars.getScope().getEnclosingScope()).isSameAs(section_a_node.getInScopeRef().getReferencedSymbol());
    assertThat(section_a_vars.getScope().getSymbols()).containsOnlyKeys("x");

    // reference to module_a resolves
    Symbol section_a_symbol = section_a_node.getInScopeRef().getReferencedSymbol();
    assertThat(section_a_symbol).isNotNull();

    // reference x: lib.a resolves
    ReferenceNode a = (ReferenceNode) section_a_vars.getMap().get("x").getValueExpression();
    assertThat(a.getReferencedSymbol()).isNotNull();

    LibraryNode lib_a = (LibraryNode) module_a_node.getComponentsMap().get("lib");
    assertThat(a.getReferencedSymbol()).isSameAs(lib_a.getVars().getMap().get("a").getSymbol());

    InteractiveSectionNode section_b_node = interactive_node.getSections().get(1);
    // section node lives in same scope as interactive node
    assertThat(section_b_node.getScope()).isSameAs(interactive_node.getUnitScope());

    // vars live in bindings scope
    VarDefs section_b_vars = section_b_node.getVars();
    assertThat(section_b_vars.getScope()).isNotSameAs(section_b_node.getScope());
    assertThat(section_b_vars.getScope().getEnclosingScope()).isSameAs(section_b_node.getInScopeRef().getReferencedSymbol());
    assertThat(section_b_vars.getScope().getSymbols()).containsOnlyKeys("y");

    // reference to module_b resolves
    Symbol section_b_symbol = section_b_node.getInScopeRef().getReferencedSymbol();
    assertThat(section_b_symbol).isNotNull();

    // reference x: lib.a resolves
    ReferenceNode b = (ReferenceNode) section_b_vars.getMap().get("y").getValueExpression();
    assertThat(b.getReferencedSymbol()).isNotNull();

    LibraryNode lib_b = (LibraryNode) module_b_node.getComponentsMap().get("lib");
    assertThat(b.getReferencedSymbol()).isSameAs(lib_b.getVars().getMap().get("b").getSymbol());

  }

  @Test
  public void fails_on_redefined_interactive_var() throws Exception {

    String module_a_path = "fixtures/tweakflow/analysis/interactive/module_a.tf";
    String interactive_path = "fixtures/tweakflow/analysis/interactive/errors/redefined_interactive_var.tf";
    AnalysisResult result = analyze(module_a_path, interactive_path);

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.ALREADY_DEFINED);

  }

  @Test
  public void fails_on_explicit_cast_of_boolean_to_list() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/casts/explicit_cast_boolean_to_list.tf");
    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.INCOMPATIBLE_TYPES);

  }

  @Test
  public void fails_on_implicit_cast_of_boolean_to_list() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/casts/implicit_cast_boolean_to_list.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.INCOMPATIBLE_TYPES);

  }

  @Test
  public void fails_on_invalid_long() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/values/invalid_long.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.NUMBER_OUT_OF_BOUNDS);

  }

  @Test
  public void analyzes_desugaring_string_operations() throws Exception {

    String path = "fixtures/tweakflow/analysis/desugar/strings.tf";
    AnalysisResult result = analyze(path);
    if (!result.isSuccess()) result.getException().printStackTrace();
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode lib = (LibraryNode) module.getComponentsMap().get("lib");
    Map<String, VarDefNode> vars = lib.getVars().getMap();

    ExpressionNode interpolation = vars.get("interpolation").getValueExpression();
    ExpressionNode interpolation_expected = vars.get("interpolation_expected").getValueExpression();

    assertThat(interpolation).hasSameStructureAs(interpolation_expected);

    ExpressionNode simple_interpolation = vars.get("simple_interpolation").getValueExpression();
    ExpressionNode simple_interpolation_expected = vars.get("simple_interpolation_expected").getValueExpression();

    assertThat(simple_interpolation).hasSameStructureAs(simple_interpolation_expected);

  }

  @Test
  public void analyzes_desugaring_function_operations() throws Exception {

    String path = "fixtures/tweakflow/analysis/desugar/fun.tf";
    AnalysisResult result = analyze(path);
    if (!result.isSuccess()){
      result.getException().printStackTrace();
      System.out.flush();
      System.err.flush();
      result.getException().printDigest();
    }
    assertThat(result.isSuccess()).isTrue();

    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode lib = (LibraryNode) module.getComponentsMap().get("lib");
    Map<String, VarDefNode> vars = lib.getVars().getMap();

    // ensure the desugaring resulted in expected equivalent trees
    ExpressionNode thread = vars.get("thread").getValueExpression();
    ExpressionNode thread_expected = vars.get("thread_expected").getValueExpression();

    assertThat(thread).hasSameStructureAs(thread_expected);

  }


  @Test
  public void fails_on_cyclic_dependency_in_expression() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/cyclic_dependency_in_expression.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.CYCLIC_REFERENCE);

  }

  @Test
  public void fails_on_cyclic_dependency_in_expression_across_modules() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/imports/errors/cycle/cycle_expression_module_a.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.CYCLIC_REFERENCE);

  }

  @Test
  public void fails_on_cyclic_dependency_in_let_definition() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/cyclic_dependency_in_let_definition.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.CYCLIC_REFERENCE);

  }


  @Test
  public void fails_on_cyclic_dependency_in_bindings_definition() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/cyclic_dependency_in_bindings_definition.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.CYCLIC_REFERENCE);

  }

  @Test
  public void fails_on_cyclic_dependency_in_sub_expression() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/cyclic_dependency_in_sub_expression.tf");

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.CYCLIC_REFERENCE);

  }

  @Test
  public void fails_on_cyclic_dependency_in_interactive() throws Exception {

    AnalysisResult result = analyze(
        "fixtures/tweakflow/analysis/interactive/module_a.tf",
        "fixtures/tweakflow/analysis/interactive/errors/cyclic_dependency_in_interactive.tf"
    );

    assertThat(result.isError()).isTrue();
    assertThat(result.getException().getCode()).isSameAs(LangError.CYCLIC_REFERENCE);

  }

  // TODO: re-enable below test once constants are folded during analysis again
//  @Test
//  public void fails_on_invalid_cast_to_long() throws Exception {
//
//    String path = "fixtures/tweakflow/analysis/expressions/errors/casts/invalid_cast_to_long.tf";
//
//    AnalysisResult result = analyze(path);
//
//    assertThat(result.isError()).isTrue();
//    assertThat(result.getException().getCode()).isSameAs(LangError.CAST_ERROR);
//
//  }


}