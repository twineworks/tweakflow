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

import com.twineworks.tweakflow.lang.ast.args.ParameterNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.meta.ViaNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import com.twineworks.tweakflow.lang.types.Types;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserFunctionLiteralsTest {

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
  void parses_simple() {

//    simple: () -> true; # constant function returning true
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/functions.tf");

    ExpressionNode expNode = varDefMap.get("simple").getValueExpression();
    assertThat(expNode).isInstanceOf(FunctionNode.class);

    FunctionNode node = (FunctionNode) expNode;
    assertThat(node.getDeclaredReturnType()).isEqualTo(Types.ANY);
    assertThat(node.getParameters().getMap()).isEmpty();
    assertThat(node.getExpression()).isInstanceOf(BooleanNode.class);

    BooleanNode body = (BooleanNode) node.getExpression();
    assertThat(body.getBoolVal()).isTrue();
  }

  @Test
  void parses_with_args() {

//    with_args: (double x = 0.0, double y = 0.0) -> list [x, y];
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/functions.tf");

    ExpressionNode expNode = varDefMap.get("with_args").getValueExpression();
    assertThat(expNode).isInstanceOf(FunctionNode.class);
    FunctionNode node = (FunctionNode) expNode;
    assertThat(node.getDeclaredReturnType()).isEqualTo(Types.LIST);

    assertThat(node.getExpression()).isInstanceOf(ListNode.class);
    ListNode body = (ListNode) node.getExpression();
    assertThat(body.getElements()).hasSize(2);
    assertThat(body.getElements().get(0)).isInstanceOf(ReferenceNode.class);
    ReferenceNode body_0 = (ReferenceNode) body.getElements().get(0);
    assertThat(body_0.getElements().get(0)).isEqualTo("x");
    ReferenceNode body_1 = (ReferenceNode) body.getElements().get(1);
    assertThat(body_1.getElements().get(0)).isEqualTo("y");

    assertThat(node.getParameters().getMap()).hasSize(2);

    Iterator<String> iterator = node.getParameters().getMap().keySet().iterator();
    String x = iterator.next();
    assertThat(x).isEqualTo("x");
    String y = iterator.next();
    assertThat(y).isEqualTo("y");

    ParameterNode param_x = node.getParameters().getMap().get(x);
    assertThat(param_x.getDeclaredType()).isEqualTo(Types.DOUBLE);
    assertThat(param_x.getIndex()).isEqualTo(0);
    assertThat(param_x.getSymbolName()).isEqualTo("x");
    assertThat(param_x.getDefaultValue()).isInstanceOf(DoubleNode.class);
    DoubleNode param_x_default = (DoubleNode) param_x.getDefaultValue();
    assertThat(param_x_default.getDoubleNum()).isEqualTo(0.0d);

    ParameterNode param_y = node.getParameters().getMap().get(y);
    assertThat(param_y.getDeclaredType()).isEqualTo(Types.DOUBLE);
    assertThat(param_y.getIndex()).isEqualTo(1);
    assertThat(param_y.getSymbolName()).isEqualTo("y");
    assertThat(param_y.getDefaultValue()).isInstanceOf(DoubleNode.class);
    DoubleNode param_y_default = (DoubleNode) param_y.getDefaultValue();
    assertThat(param_y_default.getDoubleNum()).isEqualTo(0.0d);
  }

  @Test
  void parses_native() {

//    native: (list xs) -> any via "native";
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/functions.tf");

    ExpressionNode expNode = varDefMap.get("native").getValueExpression();
    assertThat(expNode).isInstanceOf(FunctionNode.class);
    FunctionNode node = (FunctionNode) expNode;
    assertThat(node.getDeclaredReturnType()).isEqualTo(Types.ANY);
    assertThat(node.getParameters().getMap()).hasSize(1);

    ParameterNode param_xs = node.getParameters().getMap().get("xs");
    assertThat(param_xs.getSymbolName()).isEqualTo("xs");
    assertThat(param_xs.getDeclaredType()).isEqualTo(Types.LIST);

    ViaNode via = node.getVia();
    assertThat(via).isNotNull();

    assertThat(via.getExpression()).isInstanceOf(StringNode.class);
    StringNode via_str = (StringNode) via.getExpression();
    assertThat(via_str.getStringVal()).isEqualTo("native");
  }

}