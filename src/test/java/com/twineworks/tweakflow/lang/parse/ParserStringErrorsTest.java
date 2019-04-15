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
import org.assertj.core.api.StrictAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserStringErrorsTest {

  private ParseResult parseFailing(String fixturePath){
    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), fixturePath)
    );
    ParseResult result = p.parseUnit();
    assertThat(result.isSuccess()).isFalse();
    return result;
  }

  @Test
  void fails_on_single_unterminated(){

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/string_single_unterminated.tf");
    LangException e = r.getException();
    StrictAssertions.assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    StrictAssertions.assertThat(e.getMessage()).contains("unterminated or incorrectly quoted string");
    StrictAssertions.assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("2:6");

  }

  @Test
  void fails_on_double_unterminated(){

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/string_double_unterminated.tf");
    LangException e = r.getException();
    StrictAssertions.assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    StrictAssertions.assertThat(e.getMessage()).contains("unterminated or incorrectly quoted string");
    StrictAssertions.assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("3:2");

  }

  @Test
  void fails_on_double_unterminated_interpolation(){

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/string_double_unterminated_interpolation.tf");
    LangException e = r.getException();
    StrictAssertions.assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    StrictAssertions.assertThat(e.getMessage()).contains("unterminated or incorrectly quoted string");
    StrictAssertions.assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("3:22");

  }

  @Test
  void fails_on_double_invalid_interpolation(){

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/string_double_invalid_interpolation.tf");
    LangException e = r.getException();
    StrictAssertions.assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    StrictAssertions.assertThat(e.getMessage()).contains("unterminated or incorrectly quoted string");
    StrictAssertions.assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("2:21");

  }

  @Test
  void fails_on_double_nested_invalid_interpolation(){

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/string_double_nested_invalid_interpolation.tf");
    LangException e = r.getException();
    StrictAssertions.assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    StrictAssertions.assertThat(e.getMessage()).contains("unterminated or incorrectly quoted string");
    StrictAssertions.assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("2:27");

  }

  @Test
  void fails_on_double_bad_escape(){

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/string_double_bad_escape.tf");
    LangException e = r.getException();
    StrictAssertions.assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    StrictAssertions.assertThat(e.getMessage()).contains("invalid escape sequence");
    StrictAssertions.assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("2:16");

  }

  @Test
  void fails_on_here_unterminated(){

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/string_here_unterminated.tf");
    LangException e = r.getException();
    StrictAssertions.assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    StrictAssertions.assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:1");

  }

}