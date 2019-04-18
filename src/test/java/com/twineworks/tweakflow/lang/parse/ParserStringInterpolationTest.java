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
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
    ExpressionNode string_ref_inter = varDefMap.get("string_ref_inter").getValueExpression();
//  string_ref_inter_expected:  "string "..(e0 as string);
    ExpressionNode string_ref_inter_expected = varDefMap.get("string_ref_inter_expected").getValueExpression();
    NodeStructureAssert.assertThat(string_ref_inter).hasSameStructureAs(string_ref_inter_expected);

  }

  @Test
  public void parses_string_expression_interpolation() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/string_interpolation.tf");

//    string_sum_inter:     "string #{1+2}";
    ExpressionNode string_sum_inter = varDefMap.get("string_sum_inter").getValueExpression();

//    string_sum_inter_expected:  "string "..((1+2) as string);
    ExpressionNode string_sum_inter_expected  = varDefMap.get("string_sum_inter_expected").getValueExpression();
    NodeStructureAssert.assertThat(string_sum_inter).hasSameStructureAs(string_sum_inter_expected);

  }

}