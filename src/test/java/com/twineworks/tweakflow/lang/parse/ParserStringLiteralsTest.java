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

import com.twineworks.tweakflow.lang.ast.NodeStructureAssert;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.StringNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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
  void parses_key(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("key").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("key");

  }

  @Test
  void parses_quoted_key(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("quoted_key").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("quoted key");

  }

  @Test
  void parses_digit_key(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("digit_key").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("123");

  }

  @Test
  void parses_dash_key(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("dash_key").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("content-type");

  }

  @Test
  void parses_plus_key(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("plus_key").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("+and+");

  }

  @Test
  void parses_slash_key(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("slash_key").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("/slash/");

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
  void parses_escape_sequence_newline(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_newline").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("-\n-");

  }

  @Test
  void parses_escape_sequence_backslash(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_backslash").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with \\ backslash");

  }

  @Test
  void parses_escape_sequence_mixed(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_mixed").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("\\\t\r\n⊇\"\uD834\uDD1E");

  }

  @Test
  void parses_escape_sequence_double_quote(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_double_quote").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with \" double quote");

  }

  @Test
  void parses_escape_sequence_tab(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_tab").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with \t tab");

  }

  @Test
  void parses_escape_sequence_cr(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_cr").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with \r carriage return");

  }

  @Test
  void parses_escape_sequence_bmp(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_bmp").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with ⊇ superset");

  }

  @Test
  void parses_escape_sequence_unicode(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("escape_sequence_unicode").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with \uD834\uDD1E clef");

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
  void parses_with_hash_at_end(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode expNode = varDefMap.get("with_hash_at_end").getValueExpression();
    assertThat(expNode).isInstanceOf(StringNode.class);
    StringNode node = (StringNode) expNode;
    assertThat(node.getStringVal()).isEqualTo("string with #");

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

//    with_interpolation: "string with #{hash}";
    ExpressionNode expNode = varDefMap.get("with_interpolation").getValueExpression();
//    with_interpolation_expected: "string with "..(hash as string);
    ExpressionNode expNodeExpected = varDefMap.get("with_interpolation_expected").getValueExpression();

    NodeStructureAssert.assertThat(expNode).hasSameStructureAs(expNodeExpected);

  }

  @Test
  void parses_with_serial_interpolation(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

//    with_serial_interpolation: "### #{name}{##{id}}";
    ExpressionNode serialInterpolation = varDefMap.get("with_serial_interpolation").getValueExpression();

    //    with_serial_interpolation_expected: "### "..(name as string).."{#"..(id as string).."}";
    ExpressionNode serialInterpolationExpected = varDefMap.get("with_serial_interpolation_expected").getValueExpression();
    NodeStructureAssert.assertThat(serialInterpolation).hasSameStructureAs(serialInterpolationExpected);
  }

  @Test
  void parses_with_nested_interpolation(){

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/strings.tf");

    ExpressionNode inter = varDefMap.get("with_nested_interpolation").getValueExpression();
    ExpressionNode inter_expected = varDefMap.get("with_nested_interpolation_expected").getValueExpression();
    NodeStructureAssert.assertThat(inter).hasSameStructureAs(inter_expected);
  }


}