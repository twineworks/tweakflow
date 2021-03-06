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

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserModuleErrorsTest {

  private ParseResult parseFailing(String fixturePath){
    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), fixturePath)
    );
    ParseResult result = p.parseUnit();
    assertThat(result.isSuccess()).isFalse();
    return result;
  }

  @Test
  public void fails_on_missing_eos() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/module_missing_eos.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ';'");
  }

  @Test
  public void fails_on_global_missing_eos() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/module_global_missing_eos.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ';'");
  }

  @Test
  public void fails_on_import_missing_eos() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/module_import_missing_eos.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ';'");
  }

  @Test
  public void fails_on_export_missing_eos() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/module_export_missing_eos.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ';'");
  }

  @Test
  public void fails_on_alias_missing_eos() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/module_alias_missing_eos.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ';'");
  }

  @Test
  public void fails_on_invalid_start() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/module_invalid_start.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("'this'");
  }

  @Test
  public void fails_on_invalid_middle() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/module_invalid_middle.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("extra content");
  }

  @Test
  public void fails_on_invalid_end() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/module_invalid_end.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("extra content");
  }

}