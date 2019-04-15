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

import com.twineworks.tweakflow.lang.ast.args.NamedArgumentNode;
import com.twineworks.tweakflow.lang.ast.args.PositionalArgumentNode;
import com.twineworks.tweakflow.lang.ast.args.SplatArgumentNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserFunctionCallTest {

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
  public void parses_zero_args_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

    // f_call: f()
    ExpressionNode f_call_node = varDefMap.get("f_call").getValueExpression();
    assertThat(f_call_node).isInstanceOf(CallNode.class);
    CallNode f_call = (CallNode) f_call_node;
    assertThat(f_call.getExpression()).isInstanceOf(ReferenceNode.class);
    assertThat(f_call.getArguments().getList()).isEmpty();

  }


  @Test
  public void parses_1_positional_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

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

  }

  @Test
  public void parses_1_named_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

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

  }

  @Test
  public void parses_2_positional_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

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

  }

  @Test
  public void parses_2_named_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

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

  }


  @Test
  public void parses_2_positional_1_named_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

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

  }


  @Test
  public void parses_1_positional_1_splat_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

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

  }


  @Test
  public void parses_2_positional_key_strings_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

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

  }

  @Test
  public void parses_1_positional_key_1_named_call() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/function_call.tf");

    // f_call_a_bfoo: f(:a, b: "foo")
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

  }

}