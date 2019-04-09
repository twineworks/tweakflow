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
import com.twineworks.tweakflow.lang.ast.expressions.LongNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserLongLiteralsTest {

  private HashMap<String, Map<String, VarDefNode>> moduleCache = new HashMap<>();

  private synchronized Map<String, VarDefNode> getVars(String ofModule) {
    if (!moduleCache.containsKey(ofModule)) {
      Parser p = new Parser(
          new ResourceParseUnit(new ResourceLocation.Builder().build(), ofModule)
      );
      ParseResult result = p.parseUnit();

      if (result.isError()) {
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
  void parses_dec_0() {

//      dec_0: 0;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("dec_0").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(0L);

  }

  @Test
  void parses_dec_1() {

//      dec_1: 1;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("dec_1").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(1L);

  }

  @Test
  void parses_dec_neg_1() {

//      dec_neg_1: -1;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("dec_neg_1").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(-1L);

  }

  @Test
  void parses_dec_pos_1() {

//      dec_pos_1: +1;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("dec_pos_1").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(1L);

  }

  @Test
  void parses_dec_max() {

//      dec_max: 9223372036854775807;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("dec_max").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(9223372036854775807L);

  }

  @Test
  void parses_dec_min() {

//      dec_min: -9223372036854775808;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("dec_min").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(-9223372036854775808L);

  }

  @Test
  void parses_hex_0() {

//      hex_0: 0x00;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("hex_0").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(0L);

  }

  @Test
  void parses_hex_1() {

//      hex_0: 0x00;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("hex_1").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(1L);

  }

  @Test
  void parses_hex_neg_1() {

//      hex_neg_1: 0xFFFFFFFFFFFFFFFF;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/longs.tf");

    ExpressionNode expNode = varDefMap.get("hex_neg_1").getValueExpression();
    assertThat(expNode).isInstanceOf(LongNode.class);
    LongNode node = (LongNode) expNode;
    assertThat(node.getLongNum()).isEqualTo(-1L);

  }

}