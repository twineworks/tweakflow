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
import com.twineworks.tweakflow.lang.ast.structure.match.DefaultPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.ExpressionPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLineNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserMatchTest {

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
  public void parses_match() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/match.tf");

    /*
      match_42: match 42                          # match wit basic patterns
        10 ->      "ten",
        20 ->      "twenty",
        42 ->      "the answer to everything",
        default -> "unknown";
     */

    ExpressionNode match_42_node = varDefMap.get("match_42").getValueExpression();
    assertThat(match_42_node).isInstanceOf(MatchNode.class);

    MatchNode match_42 = (MatchNode) match_42_node;
    assertThat(match_42.getSubject() instanceof LongNode);
    assertThat(((LongNode) match_42.getSubject()).getLongNum()).isEqualTo(42L);
    List<MatchLineNode> match_42_lines = match_42.getMatchLines().getElements();

    assertThat(match_42_lines).hasSize(4);

    MatchLineNode match_42_line_0 = match_42_lines.get(0);
    assertThat(match_42_line_0.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode match_42_line_0_pattern = (ExpressionPatternNode) match_42_line_0.getPattern();
    assertThat(match_42_line_0_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode) match_42_line_0_pattern.getExpression()).getLongNum()).isEqualTo(10L);
    assertThat(match_42_line_0.getExpression() instanceof StringNode);
    assertThat(((StringNode) match_42_line_0.getExpression()).getStringVal()).isEqualTo("ten");

    MatchLineNode match_42_line_1 = match_42_lines.get(1);
    assertThat(match_42_line_1.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode match_42_line_1_pattern = (ExpressionPatternNode) match_42_line_1.getPattern();
    assertThat(match_42_line_1_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode) match_42_line_1_pattern.getExpression()).getLongNum()).isEqualTo(20L);
    assertThat(match_42_line_1.getExpression() instanceof StringNode);
    assertThat(((StringNode) match_42_line_1.getExpression()).getStringVal()).isEqualTo("twenty");

    MatchLineNode match_42_line_2 = match_42_lines.get(2);
    assertThat(match_42_line_2.getPattern() instanceof ExpressionPatternNode);
    ExpressionPatternNode match_42_line_2_pattern = (ExpressionPatternNode) match_42_line_2.getPattern();
    assertThat(match_42_line_2_pattern.getExpression() instanceof LongNode);
    assertThat(((LongNode)match_42_line_2_pattern.getExpression()).getLongNum()).isEqualTo(42L);
    assertThat(match_42_line_2.getExpression() instanceof StringNode);
    assertThat(((StringNode) match_42_line_2.getExpression()).getStringVal()).isEqualTo("the answer to everything");

    MatchLineNode match_42_line_3 = match_42_lines.get(3);
    assertThat(match_42_line_3.getPattern() instanceof ExpressionPatternNode);
    DefaultPatternNode match_42_line_3_pattern = (DefaultPatternNode) match_42_line_3.getPattern();
    assertThat(match_42_line_3.getExpression() instanceof StringNode);
    assertThat(((StringNode) match_42_line_3.getExpression()).getStringVal()).isEqualTo("unknown");

  }

}