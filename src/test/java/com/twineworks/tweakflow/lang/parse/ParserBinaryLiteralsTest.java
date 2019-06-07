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

import com.twineworks.tweakflow.lang.ast.expressions.BinaryNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserBinaryLiteralsTest {

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
  void parses_0b() {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/binaries.tf");

    ExpressionNode expNode = varDefMap.get("bin_empty").getValueExpression();
    assertThat(expNode).isInstanceOf(BinaryNode.class);
    BinaryNode node = (BinaryNode) expNode;
    assertThat(node.getBytes()).isEqualTo(new byte[0]);

  }

  @Test
  void parses_0b00() {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/binaries.tf");

    ExpressionNode expNode = varDefMap.get("bin_00").getValueExpression();
    assertThat(expNode).isInstanceOf(BinaryNode.class);
    BinaryNode node = (BinaryNode) expNode;
    assertThat(node.getBytes()).isEqualTo(new byte[] {0});

  }

  @Test
  void parses_0b01() {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/binaries.tf");

    ExpressionNode expNode = varDefMap.get("bin_01").getValueExpression();
    assertThat(expNode).isInstanceOf(BinaryNode.class);
    BinaryNode node = (BinaryNode) expNode;
    assertThat(node.getBytes()).isEqualTo(new byte[] {1});

  }

  @Test
  void parses_0b0001() {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/binaries.tf");

    ExpressionNode expNode = varDefMap.get("bin_0001").getValueExpression();
    assertThat(expNode).isInstanceOf(BinaryNode.class);
    BinaryNode node = (BinaryNode) expNode;
    assertThat(node.getBytes()).isEqualTo(new byte[] {0, 1});

  }

  @Test
  void parses_0b0100() {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/binaries.tf");

    ExpressionNode expNode = varDefMap.get("bin_0100").getValueExpression();
    assertThat(expNode).isInstanceOf(BinaryNode.class);
    BinaryNode node = (BinaryNode) expNode;
    assertThat(node.getBytes()).isEqualTo(new byte[] {1, 0});

  }

  @Test
  void parses_0baAbB() {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/binaries.tf");

    ExpressionNode expNode = varDefMap.get("bin_aAbB").getValueExpression();
    assertThat(expNode).isInstanceOf(BinaryNode.class);
    BinaryNode node = (BinaryNode) expNode;
    assertThat(node.getBytes()).isEqualTo(new byte[] {(byte) 0xaA, (byte) 0xbB});

  }

  @Test
  void parses_0bfAcE() {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/binaries.tf");

    ExpressionNode expNode = varDefMap.get("bin_fAcE").getValueExpression();
    assertThat(expNode).isInstanceOf(BinaryNode.class);
    BinaryNode node = (BinaryNode) expNode;
    assertThat(node.getBytes()).isEqualTo(new byte[] {(byte) 0xfA, (byte) 0xcE});

  }

  @Test
  void parses_0b0123456789abcdef() {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/binaries.tf");

    ExpressionNode expNode = varDefMap.get("bin_all").getValueExpression();
    assertThat(expNode).isInstanceOf(BinaryNode.class);
    BinaryNode node = (BinaryNode) expNode;
    assertThat(node.getBytes()).isEqualTo(new byte[] {0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef});

  }

}