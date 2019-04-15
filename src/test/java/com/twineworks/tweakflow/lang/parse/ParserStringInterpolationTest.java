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

import com.twineworks.tweakflow.lang.ast.expressions.*;
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

public class ParserStringInterpolationTest {

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
  public void parses_string_reference_interpolation() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/string_interpolation.tf");

//  string_ref_inter: "string ${e0}"         # string reference interpolation
    ExpressionNode string_inter_node = varDefMap.get("string_ref_inter").getValueExpression();
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

  }

  @Test
  public void parses_string_expression_interpolation() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/string_interpolation.tf");

//    string_sum_inter:     "string #{1+2}";
    ExpressionNode string_inter_node = varDefMap.get("string_sum_inter").getValueExpression();
    assertThat(string_inter_node).isInstanceOf(StringConcatNode.class);
    StringConcatNode string_inter = (StringConcatNode) string_inter_node;
    assertThat(string_inter.getLeftExpression()).isInstanceOf(StringNode.class);
    StringNode string_inter_left = (StringNode) string_inter.getLeftExpression();
    assertThat(string_inter_left.getStringVal()).isEqualTo("string ");

    assertThat(string_inter.getRightExpression()).isInstanceOf(PlusNode.class);
    PlusNode exp = (PlusNode) string_inter.getRightExpression();
    assertThat(exp.getLeftExpression()).isInstanceOf(LongNode.class);
    assertThat(((LongNode)exp.getLeftExpression()).getLongNum()).isEqualTo(1L);
    assertThat(exp.getRightExpression()).isInstanceOf(LongNode.class);
    assertThat(((LongNode)exp.getRightExpression()).getLongNum()).isEqualTo(2L);

  }

}