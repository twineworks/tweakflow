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
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserContainerAccessTest {

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
  public void parses_single_collection_access() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/container_access.tf");

//    ca_single:        c[1];                     # container access single

    ExpressionNode ca_node = varDefMap.get("ca_single").getValueExpression();
    assertThat(ca_node).isInstanceOf(ContainerAccessNode.class);
    ContainerAccessNode ca = (ContainerAccessNode) ca_node;

    ExpressionNode containerExpression = ca.getContainerExpression();
    assertThat(containerExpression).isInstanceOf(ReferenceNode.class);
    ReferenceNode container = (ReferenceNode) containerExpression;
    assertThat(container.getAnchor()).isEqualTo(ReferenceNode.Anchor.LOCAL);
    assertThat(container.getElements()).containsExactly("c");

    ExpressionNode keysExpression = ca.getKeysExpression();
    assertThat(keysExpression).isInstanceOf(ListNode.class);

    ListNode keys = (ListNode) keysExpression;
    assertThat(keys.getElements()).hasSize(1);

    ExpressionNode k0Exp = keys.getElements().get(0);
    assertThat(k0Exp).isInstanceOf(LongNode.class);
    LongNode k0 = (LongNode) k0Exp;
    assertThat(k0.getLongNum()).isEqualTo(1L);

  }

  @Test
  public void parses_multi_collection_access() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/container_access.tf");

//    ca_multi:         c[1, :a, 3];              # container access multi

    ExpressionNode ca_node = varDefMap.get("ca_multi").getValueExpression();
    assertThat(ca_node).isInstanceOf(ContainerAccessNode.class);
    ContainerAccessNode ca = (ContainerAccessNode) ca_node;

    ExpressionNode containerExpression = ca.getContainerExpression();
    assertThat(containerExpression).isInstanceOf(ReferenceNode.class);
    ReferenceNode container = (ReferenceNode) containerExpression;
    assertThat(container.getAnchor()).isEqualTo(ReferenceNode.Anchor.LOCAL);
    assertThat(container.getElements()).containsExactly("c");

    ExpressionNode keysExpression = ca.getKeysExpression();
    assertThat(keysExpression).isInstanceOf(ListNode.class);

    ListNode keys = (ListNode) keysExpression;
    assertThat(keys.getElements()).hasSize(3);

    ExpressionNode k0Exp = keys.getElements().get(0);
    assertThat(k0Exp).isInstanceOf(LongNode.class);
    LongNode k0 = (LongNode) k0Exp;
    assertThat(k0.getLongNum()).isEqualTo(1L);

    ExpressionNode k1Exp = keys.getElements().get(1);
    assertThat(k1Exp).isInstanceOf(StringNode.class);
    StringNode k1 = (StringNode) k1Exp;
    assertThat(k1.getStringVal()).isEqualTo("a");


    ExpressionNode k2Exp = keys.getElements().get(2);
    assertThat(k2Exp).isInstanceOf(LongNode.class);
    LongNode k2 = (LongNode) k2Exp;
    assertThat(k2.getLongNum()).isEqualTo(3L);

  }

}