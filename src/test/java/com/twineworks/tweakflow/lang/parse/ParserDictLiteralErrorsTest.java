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

public class ParserDictLiteralErrorsTest {

  private ParseResult parseFailing(String fixturePath){
    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), fixturePath)
    );
    ParseResult result = p.parseUnit();
    assertThat(result.isSuccess()).isFalse();
    return result;
  }

  @Test
  public void fails_on_missing_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("3:12");
  }

  @Test
  public void fails_on_missing_early_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_missing_early_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("4:12");
  }

  @Test
  public void fails_on_missing_late_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_missing_late_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("4:30");
  }

  @Test
  public void fails_on_missing_3_splat_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_missing_3_splat_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("4:42");
  }

  @Test
  public void fails_on_extra_initial_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_extra_initial_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("3:7");
  }

  @Test
  public void fails_on_extra_mid_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_extra_mid_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("3:12");
  }

  @Test
  public void fails_on_extra_early_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_extra_early_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("4:12");
  }

  @Test
  public void fails_on_extra_late_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_extra_late_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("4:30");
  }

  @Test
  public void fails_on_extra_3_splat_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_extra_3_splat_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("4:36");
  }

  @Test
  public void fails_on_wrong_side_colon_key() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_wrong_side_colon_key.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected 'b:'");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("3:13");
  }

  @Test
  public void fails_on_unbalanced_pairs() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/dict_unbalanced_pairs.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("value for key ':b' expected");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("3:13");
  }

}