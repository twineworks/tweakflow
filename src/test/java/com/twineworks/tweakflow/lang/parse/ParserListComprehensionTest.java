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

import com.twineworks.tweakflow.lang.ast.ForHeadElementNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.structure.ForHead;
import com.twineworks.tweakflow.lang.ast.structure.GeneratorNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserListComprehensionTest {

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
  public void parses_list_comprehension() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/list_comprehension.tf");

//   gen: for x <- [1, 2, 3],                    # list comprehension generator
//           x**2;

    ExpressionNode forExp = varDefMap.get("gen").getValueExpression();
    assertThat(forExp).isInstanceOf(ForNode.class);
    ForNode forNode = (ForNode) forExp;

    ForHead head = forNode.getHead();
    assertThat(head.getElements()).hasSize(1);

    ForHeadElementNode forHeadElementNode = head.getElements().get(0);
    assertThat(forHeadElementNode).isInstanceOf(GeneratorNode.class);
    GeneratorNode gen = (GeneratorNode) forHeadElementNode;
    assertThat(gen.getSymbolName()).isEqualTo("x");
    assertThat(gen.getValueExpression()).isInstanceOf(ListNode.class);
    ListNode genList = (ListNode) gen.getValueExpression();
    assertThat(genList.getElements()).hasSize(3);

    assertThat(genList.getElements().get(0)).isInstanceOf(LongNode.class);
    assertThat(genList.getElements().get(1)).isInstanceOf(LongNode.class);
    assertThat(genList.getElements().get(2)).isInstanceOf(LongNode.class);
    assertThat(((LongNode)genList.getElements().get(0)).getLongNum()).isEqualTo(1L);
    assertThat(((LongNode)genList.getElements().get(1)).getLongNum()).isEqualTo(2L);
    assertThat(((LongNode)genList.getElements().get(2)).getLongNum()).isEqualTo(3L);

    assertThat(forNode.getExpression()).isInstanceOf(PowNode.class);
    PowNode pow = (PowNode) forNode.getExpression();
    assertThat(pow.getLeftExpression()).isInstanceOf(ReferenceNode.class);
    ReferenceNode powX = (ReferenceNode) pow.getLeftExpression();
    assertThat(powX.getAnchor()).isEqualTo(ReferenceNode.Anchor.LOCAL);
    assertThat(powX.getElements()).hasSize(1);
    assertThat(powX.getElements()).containsExactly("x");

    assertThat(pow.getRightExpression()).isInstanceOf(LongNode.class);
    LongNode powP = (LongNode) pow.getRightExpression();
    assertThat(powP.getLongNum()).isEqualTo(2L);

  }

}