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

import com.twineworks.tweakflow.lang.ast.expressions.DoubleNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import com.twineworks.tweakflow.lang.types.Types;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserDoubleLiteralsTest {

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
  void parses_zero() {

//      zero: 0.0;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("zero").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(0.0);
  }

  @Test
  void parses_neg_zero() {

//      neg_zero: -0.0;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("neg_zero").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(-0.0);
  }

  @Test
  void parses_pos_zero() {

//      pos_zero: +0.0;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("pos_zero").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(0.0);
  }

  @Test
  void parses_sci() {

//      sci: 2e-1;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("sci").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(2e-1);
  }

  @Test
  void parses_neg_sci() {

//      neg_sci: -2e-1;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("neg_sci").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(-2e-1);
  }

  @Test
  void parses_pos_sci() {

//      pos_sci: +2e-1;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("pos_sci").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(2e-1);
  }

  @Test
  void parses_pi_variants() {

//    pi1: 3.1315;
//    pi2: 0.31315e1;
//    pi3: .31315e1;
//    pi4: 31315e-4;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode pi1 = varDefMap.get("pi1").getValueExpression();
    ExpressionNode pi2 = varDefMap.get("pi2").getValueExpression();
    ExpressionNode pi3 = varDefMap.get("pi3").getValueExpression();
    ExpressionNode pi4 = varDefMap.get("pi4").getValueExpression();

    assertThat(pi1).hasSameStructureAs(pi2);
    assertThat(pi1).hasSameStructureAs(pi3);
    assertThat(pi1).hasSameStructureAs(pi4);

    assertThat(pi1.getValueType()).isSameAs(Types.DOUBLE);
    assertThat(pi1).isInstanceOf(DoubleNode.class);
    assertThat(((DoubleNode) pi1).getDoubleNum()).isEqualTo(3.1315);

  }

  @Test
  void parses_neg_pi_variants() {

//    neg_pi1: -3.1315;
//    neg_pi2: -0.31315e1;
//    neg_pi3: -.31315e1;
//    neg_pi4: -31315e-4;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode neg_pi1 = varDefMap.get("neg_pi1").getValueExpression();
    ExpressionNode neg_pi2 = varDefMap.get("neg_pi2").getValueExpression();
    ExpressionNode neg_pi3 = varDefMap.get("neg_pi3").getValueExpression();
    ExpressionNode neg_pi4 = varDefMap.get("neg_pi4").getValueExpression();

    assertThat(neg_pi1).hasSameStructureAs(neg_pi2);
    assertThat(neg_pi1).hasSameStructureAs(neg_pi3);
    assertThat(neg_pi1).hasSameStructureAs(neg_pi4);

    assertThat(neg_pi1.getValueType()).isSameAs(Types.DOUBLE);
    assertThat(neg_pi1).isInstanceOf(DoubleNode.class);
    assertThat(((DoubleNode) neg_pi1).getDoubleNum()).isEqualTo(-3.1315);

  }

  @Test
  void parses_pos_pi_variants() {

//    pos_pi1: +3.1315;
//    pos_pi2: +0.31315e1;
//    pos_pi3: +.31315e1;
//    pos_pi4: +31315e-4;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode pos_pi1 = varDefMap.get("pos_pi1").getValueExpression();
    ExpressionNode pos_pi2 = varDefMap.get("pos_pi2").getValueExpression();
    ExpressionNode pos_pi3 = varDefMap.get("pos_pi3").getValueExpression();
    ExpressionNode pos_pi4 = varDefMap.get("pos_pi4").getValueExpression();

    assertThat(pos_pi1).hasSameStructureAs(pos_pi2);
    assertThat(pos_pi1).hasSameStructureAs(pos_pi3);
    assertThat(pos_pi1).hasSameStructureAs(pos_pi4);

    assertThat(pos_pi1.getValueType()).isSameAs(Types.DOUBLE);
    assertThat(pos_pi1).isInstanceOf(DoubleNode.class);
    assertThat(((DoubleNode) pos_pi1).getDoubleNum()).isEqualTo(3.1315);

  }

  @Test
  void parses_inf() {

//    inf: Infinity;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("inf").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(Double.POSITIVE_INFINITY);
  }

  @Test
  void parses_neg_inf() {

//    neg_inf: -Infinity;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("neg_inf").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(Double.NEGATIVE_INFINITY);
  }

  @Test
  void parses_pos_inf() {

//    pos_inf: +Infinity;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("pos_inf").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(Double.POSITIVE_INFINITY);
  }

  @Test
  void parses_NaN() {

//    nan: NaN;
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/doubles.tf");

    ExpressionNode expNode = varDefMap.get("nan").getValueExpression();
    assertThat(expNode).isInstanceOf(DoubleNode.class);
    DoubleNode node = (DoubleNode) expNode;
    assertThat(node.getDoubleNum()).isEqualTo(Double.NaN);
  }

}