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

import com.twineworks.tweakflow.lang.ast.expressions.BooleanNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.IfNode;
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

public class ParserIfThenElseTest {

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
  public void parses_if_else() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/if_then_else.tf");

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

  }

  @Test
  public void parses_if_then_else() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/if_then_else.tf");

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

  }

}