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

import com.twineworks.tweakflow.lang.ast.expressions.DateTimeNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserDatetimeLiteralsTest {

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
  void utc_imp_s() {

//    utc_imp_s:    2017-03-17T16:04:02;                 # implied UTC, second precision
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/datetimes.tf");

    ExpressionNode expNode = varDefMap.get("utc_imp_s").getValueExpression();
    assertThat(expNode).isInstanceOf(DateTimeNode.class);
    DateTimeNode node = (DateTimeNode) expNode;
    assertThat(node.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 0, ZoneOffset.UTC)
    );
  }

  @Test
  void utc_imp_ns() {

//    uts_imp_ns:   2017-03-17T16:04:02.123456789;       # implied UTC, nano-second precision
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/datetimes.tf");

    ExpressionNode expNode = varDefMap.get("utc_imp_ns").getValueExpression();
    assertThat(expNode).isInstanceOf(DateTimeNode.class);
    DateTimeNode node = (DateTimeNode) expNode;
    assertThat(node.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123456789, ZoneOffset.UTC)
    );
  }

  @Test
  void local() {

//    local:        2017-03-17T16:04:02+01:00@`Europe/Berlin`; # local date in Berlin, second precision
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/datetimes.tf");

    ExpressionNode expNode = varDefMap.get("local").getValueExpression();
    assertThat(expNode).isInstanceOf(DateTimeNode.class);
    DateTimeNode node = (DateTimeNode) expNode;
    assertThat(node.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 0, ZoneId.of("Europe/Berlin"))
    );
  }

  @Test
  void local_ms() {

//    local_ms:     2017-03-17T16:04:02.123+01:00@`Europe/Berlin`; # local date in Berlin, milli-second precision
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/datetimes.tf");

    ExpressionNode expNode = varDefMap.get("local_ms").getValueExpression();
    assertThat(expNode).isInstanceOf(DateTimeNode.class);
    DateTimeNode node = (DateTimeNode) expNode;
    assertThat(node.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123_000_000, ZoneId.of("Europe/Berlin"))
    );
  }

  @Test
  void utc_s() {

//    utc_s:        2017-03-17T16:04:02Z;                 # UTC time, second precision
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/datetimes.tf");

    ExpressionNode expNode = varDefMap.get("utc_s").getValueExpression();
    assertThat(expNode).isInstanceOf(DateTimeNode.class);
    DateTimeNode node = (DateTimeNode) expNode;
    assertThat(node.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 0, ZoneOffset.UTC)
    );
  }

  @Test
  void utc_plus_2() {

//    utc_plus_2:   2017-03-17T16:04:02+02:00;            # UTC+2 time, implied time zone
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/datetimes.tf");

    ExpressionNode expNode = varDefMap.get("utc_plus_2").getValueExpression();
    assertThat(expNode).isInstanceOf(DateTimeNode.class);
    DateTimeNode node = (DateTimeNode) expNode;
    assertThat(node.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 0, ZoneOffset.ofHours(2))
    );
  }

  @Test
  void utc_midnight() {

//    utc_midnight: 2017-03-17T;                          # implied UTC, implied time midnight
    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/literals/datetimes.tf");

    ExpressionNode expNode = varDefMap.get("utc_midnight").getValueExpression();
    assertThat(expNode).isInstanceOf(DateTimeNode.class);
    DateTimeNode node = (DateTimeNode) expNode;
    assertThat(node.getDateTime().getZoned()).isEqualTo(
        ZonedDateTime.of(2017, 3, 17, 0, 0, 0, 0, ZoneOffset.UTC)
    );
  }

}