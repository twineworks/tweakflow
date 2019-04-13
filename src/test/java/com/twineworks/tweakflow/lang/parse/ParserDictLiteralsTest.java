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

import com.twineworks.tweakflow.lang.ast.expressions.DictEntryNode;
import com.twineworks.tweakflow.lang.ast.expressions.DictNode;
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

public class ParserDictLiteralsTest {

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
  void parses_empty() {

//      empty: {};
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/dicts.tf");

    ExpressionNode expNode = varDefMap.get("empty").getValueExpression();
    assertThat(expNode).isInstanceOf(DictNode.class);
    DictNode node = (DictNode) expNode;
    assertThat(node.getEntries()).hasSize(0);

  }

  @Test
  void parses_basic() {

//      basic: {:key "value"};
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/dicts.tf");

    ExpressionNode expNode = varDefMap.get("basic").getValueExpression();
    assertThat(expNode).isInstanceOf(DictNode.class);
    DictNode node = (DictNode) expNode;
    assertThat(node.getEntries()).hasSize(1);

    DictEntryNode e0 = node.getEntries().get(0);
    assertThat(e0.getKey()).isInstanceOf(StringNode.class);
    StringNode e0k = (StringNode) e0.getKey();
    assertThat(e0k.getStringVal()).isEqualTo("key");

    assertThat(e0.getValue()).isInstanceOf(StringNode.class);
    StringNode e0v = (StringNode) e0.getValue();
    assertThat(e0v.getStringVal()).isEqualTo("value");

  }

  @Test
  void parses_escaped_key() {

//      escaped_key: {:`escaped key` "value"};
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/dicts.tf");

    ExpressionNode expNode = varDefMap.get("escaped_key").getValueExpression();
    assertThat(expNode).isInstanceOf(DictNode.class);
    DictNode node = (DictNode) expNode;
    assertThat(node.getEntries()).hasSize(1);

    DictEntryNode e0 = node.getEntries().get(0);
    assertThat(e0.getKey()).isInstanceOf(StringNode.class);
    StringNode e0k = (StringNode) e0.getKey();
    assertThat(e0k.getStringVal()).isEqualTo("escaped key");

    assertThat(e0.getValue()).isInstanceOf(StringNode.class);
    StringNode e0v = (StringNode) e0.getValue();
    assertThat(e0v.getStringVal()).isEqualTo("value");

  }

  @Test
  void parses_simple() {

//      simple: {:key1 "value1", :key2 "value2"};
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/dicts.tf");

    ExpressionNode expNode = varDefMap.get("simple").getValueExpression();
    assertThat(expNode).isInstanceOf(DictNode.class);
    DictNode node = (DictNode) expNode;
    assertThat(node.getEntries()).hasSize(2);

    DictEntryNode e0 = node.getEntries().get(0);
    assertThat(e0.getKey()).isInstanceOf(StringNode.class);
    StringNode e0k = (StringNode) e0.getKey();
    assertThat(e0k.getStringVal()).isEqualTo("key1");

    assertThat(e0.getValue()).isInstanceOf(StringNode.class);
    StringNode e0v = (StringNode) e0.getValue();
    assertThat(e0v.getStringVal()).isEqualTo("value1");

    DictEntryNode e1 = node.getEntries().get(1);
    assertThat(e1.getKey()).isInstanceOf(StringNode.class);
    StringNode e1k = (StringNode) e1.getKey();
    assertThat(e1k.getStringVal()).isEqualTo("key2");

    assertThat(e1.getValue()).isInstanceOf(StringNode.class);
    StringNode e1v = (StringNode) e1.getValue();
    assertThat(e1v.getStringVal()).isEqualTo("value2");

  }

  @Test
  void parses_extra_final_separator() {

//      extra_final_separator: {:key1 "value1", :key2 "value2", };
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/dicts.tf");

    ExpressionNode expNode = varDefMap.get("extra_final_separator").getValueExpression();
    assertThat(expNode).isInstanceOf(DictNode.class);
    DictNode node = (DictNode) expNode;
    assertThat(node.getEntries()).hasSize(2);

    DictEntryNode e0 = node.getEntries().get(0);
    assertThat(e0.getKey()).isInstanceOf(StringNode.class);
    StringNode e0k = (StringNode) e0.getKey();
    assertThat(e0k.getStringVal()).isEqualTo("key1");

    assertThat(e0.getValue()).isInstanceOf(StringNode.class);
    StringNode e0v = (StringNode) e0.getValue();
    assertThat(e0v.getStringVal()).isEqualTo("value1");

    DictEntryNode e1 = node.getEntries().get(1);
    assertThat(e1.getKey()).isInstanceOf(StringNode.class);
    StringNode e1k = (StringNode) e1.getKey();
    assertThat(e1k.getStringVal()).isEqualTo("key2");

    assertThat(e1.getValue()).isInstanceOf(StringNode.class);
    StringNode e1v = (StringNode) e1.getValue();
    assertThat(e1v.getStringVal()).isEqualTo("value2");

  }

  @Test
  void parses_nested() {

//      nested: {"k" "v", "sub" {:key "value"}};
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/dicts.tf");

    ExpressionNode expNode = varDefMap.get("nested").getValueExpression();
    assertThat(expNode).isInstanceOf(DictNode.class);
    DictNode node = (DictNode) expNode;
    assertThat(node.getEntries()).hasSize(2);

    DictEntryNode e0 = node.getEntries().get(0);
    assertThat(e0.getKey()).isInstanceOf(StringNode.class);
    StringNode e0k = (StringNode) e0.getKey();
    assertThat(e0k.getStringVal()).isEqualTo("k");

    assertThat(e0.getValue()).isInstanceOf(StringNode.class);
    StringNode e0v = (StringNode) e0.getValue();
    assertThat(e0v.getStringVal()).isEqualTo("v");

    DictEntryNode e1 = node.getEntries().get(1);
    assertThat(e1.getKey()).isInstanceOf(StringNode.class);
    StringNode e1k = (StringNode) e1.getKey();
    assertThat(e1k.getStringVal()).isEqualTo("sub");

    assertThat(e1.getValue()).isInstanceOf(DictNode.class);
    DictNode e1v = (DictNode) e1.getValue();
    assertThat(e1v.getEntries()).hasSize(1);

    DictEntryNode e1_0 = e1v.getEntries().get(0);
    assertThat(e1_0.getKey()).isInstanceOf(StringNode.class);
    StringNode e1_0k = (StringNode) e1_0.getKey();
    assertThat(e1_0k.getStringVal()).isEqualTo("key");
    assertThat(e1_0.getValue()).isInstanceOf(StringNode.class);
    StringNode e1_0v = (StringNode) e1_0.getValue();
    assertThat(e1_0v.getStringVal()).isEqualTo("value");

  }

}