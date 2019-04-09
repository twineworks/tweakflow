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
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.expressions.StringConcatNode;
import com.twineworks.tweakflow.lang.ast.expressions.StringNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserStringLiteralsTest {

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
  void parses_single_quoted_empty(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("single_quoted_empty").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("");

  }

  @Test
  void parses_double_quoted_empty(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("double_quoted_empty").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("");

  }

  @Test
  void parses_single_quoted(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("single_quoted").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("single quoted");

  }

  @Test
  void parses_double_quoted(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("double_quoted").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("double quoted");

  }

  @Test
  void parses_escape_sequence_1(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_1").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("-\n-");

  }

  @Test
  void parses_escape_sequence_2(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_2").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with\nescape sequence");

  }

  @Test
  void parses_single_escaped(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("single_escaped").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("single quoted 'string'");

  }

  @Test
  void parses_single_multi_line(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("single_multi_line").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("single quoted\n" +
        "multi\n" +
        "line\n" +
        "string");

  }

  @Test
  void parses_double_multi_line(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("double_multi_line").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("double quoted\n" +
        "multi\n" +
        "line\n" +
        "string");

  }

  @Test
  void parses_here_doc(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("here_doc").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("Here ~~~ String");

  }

  @Test
  void parses_with_hash(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("with_hash").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with # hash");

  }

  @Test
  void parses_with_escaped_interpolation(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("with_escaped_interpolation").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with #{hash}");

  }

  @Test
  void parses_with_interpolation(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("with_interpolation").getValueExpression();
    assertThat(expNode).isInstanceOf(StringConcatNode.class);
    StringConcatNode string_inter = (StringConcatNode) expNode;
    assertThat(string_inter.getLeftExpression()).isInstanceOf(StringNode.class);
    StringNode string_inter_left = (StringNode) string_inter.getLeftExpression();
    assertThat(string_inter_left.getStringVal()).isEqualTo("string with ");

    assertThat(string_inter.getRightExpression()).isInstanceOf(ReferenceNode.class);
    ReferenceNode string_inter_right = (ReferenceNode) string_inter.getRightExpression();
    assertThat(string_inter_right.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> string_inter_right_elements = string_inter_right.getElements();
    assertThat(string_inter_right_elements).hasSize(1);
    assertThat(string_inter_right_elements).containsExactly("hash");

  }

}