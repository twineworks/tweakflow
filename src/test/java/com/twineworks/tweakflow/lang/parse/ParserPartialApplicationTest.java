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

import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.PartialApplicationNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.expressions.StringNode;
import com.twineworks.tweakflow.lang.ast.partial.PartialArgumentNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserPartialApplicationTest {

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
  public void parses_1_arg_partial_application() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/partial_application.tf");

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

  }

  @Test
  public void parses_2_arg_partial_application() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/partial_application.tf");

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

  }

}