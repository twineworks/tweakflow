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

public class ParserTryCatchThrowTest {

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
  public void parses_try_catch_e() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/try_catch_throw.tf");

    // try_catch_e: try 0 catch e false
    ExpressionNode try_catch_e = varDefMap.get("try_catch_e").getValueExpression();
    assertThat(try_catch_e).isInstanceOf(TryCatchNode.class);
    TryCatchNode try_catch_e_node = (TryCatchNode) try_catch_e;
    assertThat(try_catch_e_node.getCaughtException().getSymbolName()).isEqualTo("e");
    assertThat(try_catch_e_node.getCaughtTrace()).isNull();

    assertThat(try_catch_e_node.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode try_catch_e_try_exp = (LongNode) try_catch_e_node.getTryExpression();
    assertThat(try_catch_e_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(try_catch_e_node.getCatchExpression()).isInstanceOf(BooleanNode.class);
    BooleanNode try_catch_e_catch = (BooleanNode) try_catch_e_node.getCatchExpression();
    assertThat(try_catch_e_catch.getBoolVal()).isFalse();

  }

  @Test
  public void parses_try_catch() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/try_catch_throw.tf");

    // try_catch: try 0 catch false
    ExpressionNode try_catch = varDefMap.get("try_catch").getValueExpression();
    assertThat(try_catch).isInstanceOf(TryCatchNode.class);
    TryCatchNode try_catch_node = (TryCatchNode) try_catch;
    assertThat(try_catch_node.getCaughtException()).isNull();
    assertThat(try_catch_node.getCaughtTrace()).isNull();

    assertThat(try_catch_node.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode try_catch_try_exp = (LongNode) try_catch_node.getTryExpression();
    assertThat(try_catch_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(try_catch_node.getCatchExpression()).isInstanceOf(BooleanNode.class);
    BooleanNode try_catch_catch = (BooleanNode) try_catch_node.getCatchExpression();
    assertThat(try_catch_catch.getBoolVal()).isFalse();

  }

  @Test
  public void parses_try_catch_e_t() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/try_catch_throw.tf");

    // try_catch_e_t: try 0 catch e, trace nil # try evaluating 0, catch exception and trace and return nil

    ExpressionNode try_catch_e_t_node = varDefMap.get("try_catch_e_t").getValueExpression();
    assertThat(try_catch_e_t_node).isInstanceOf(TryCatchNode.class);
    TryCatchNode try_catch_e_t = (TryCatchNode) try_catch_e_t_node;
    assertThat(try_catch_e_t.getCaughtException().getSymbolName()).isEqualTo("e");
    assertThat(try_catch_e_t.getCaughtTrace().getSymbolName()).isEqualTo("trace");

    assertThat(try_catch_e_t.getTryExpression()).isInstanceOf(LongNode.class);
    LongNode try_catch_e_t_try_exp = (LongNode) try_catch_e_t.getTryExpression();
    assertThat(try_catch_e_t_try_exp.getLongNum()).isEqualTo(0L);

    assertThat(try_catch_e_t.getCatchExpression()).isInstanceOf(NilNode.class);

  }

  @Test
  public void parses_throw() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/try_catch_throw.tf");

    // throw_nil: throw nil
    ExpressionNode throw_nil = varDefMap.get("throw_nil").getValueExpression();
    assertThat(throw_nil).isInstanceOf(ThrowNode.class);
    ThrowNode throw_nil_node = (ThrowNode) throw_nil;
    assertThat(throw_nil_node.getExceptionExpression()).isInstanceOf(NilNode.class);

  }

}