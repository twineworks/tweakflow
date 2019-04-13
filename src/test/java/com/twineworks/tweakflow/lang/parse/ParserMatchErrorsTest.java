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

public class ParserMatchErrorsTest {

  private ParseResult parseFailing(String fixturePath){
    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), fixturePath)
    );
    ParseResult result = p.parseUnit();
    assertThat(result.isSuccess()).isFalse();
    return result;
  }

  @Test
  public void fails_on_missing_line_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_line_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("7:5");
  }

  @Test
  public void fails_on_missing_mid_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_mid_pattern_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:10");
  }

  @Test
  public void fails_on_extra_final_mid_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_mid_pattern_extra_final_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:19");
  }

  @Test
  public void fails_on_extra_initial_mid_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_mid_pattern_extra_initial_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:8");
  }

  @Test
  public void fails_on_extra_middle_mid_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_mid_pattern_extra_middle_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:10");
  }


  @Test
  public void fails_on_missing_init_last_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_init_last_pattern_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:17");
  }

  @Test
  public void fails_on_extra_final_init_last_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_init_last_pattern_extra_final_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:19");
  }

  @Test
  public void fails_on_extra_initial_init_last_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_init_last_pattern_extra_initial_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:8");
  }

  @Test
  public void fails_on_extra_middle_init_last_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_init_last_pattern_extra_middle_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:17");
  }

  @Test
  public void fails_on_missing_head_tail_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_head_tail_pattern_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:10");
  }

  @Test
  public void fails_on_extra_final_head_tail_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_head_tail_pattern_extra_final_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:22");
  }

  @Test
  public void fails_on_extra_initial_head_tail_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_head_tail_pattern_extra_initial_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:8");
  }

  @Test
  public void fails_on_extra_middle_head_tail_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_head_tail_pattern_extra_middle_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:10");
  }

  @Test
  public void fails_on_missing_guard_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_guard_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:7");
  }

  @Test
  public void fails_on_missing_list_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_list_pattern_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:10");
  }

  @Test
  public void fails_on_list_pattern_wrong_side_colon_key() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_list_pattern_wrong_side_colon_key.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected 'name:'");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:12");
  }

  @Test
  public void fails_on_extra_final_list_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_list_pattern_extra_final_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:12");
  }

  @Test
  public void fails_on_extra_initial_list_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_list_pattern_extra_initial_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:8");
  }

  @Test
  public void fails_on_extra_middle_list_pattern_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_list_pattern_extra_middle_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:10");
  }

  @Test
  public void fails_on_list_multi_splat() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_list_pattern_multi_splat.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected splat capture");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:20");
  }

  @Test
  public void fails_on_open_dict_multi_splat() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_multi_splat.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.ALREADY_DEFINED);
    assertThat(e.getMessage()).contains("splat capture already defined");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:31");
  }

  @Test
  public void fails_on_dict_missing_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_dict_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:19");
  }

  @Test
  public void fails_on_dict_extra_initial_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_dict_extra_initial_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:6");
  }

  @Test
  public void fails_on_dict_extra_mid_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_dict_extra_mid_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:19");
  }

  @Test
  public void fails_on_dict_extra_final_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_dict_extra_final_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:38");
  }

  @Test
  public void fails_on_dict_unbalanced_pairs() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_dict_unbalanced_pairs.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("value for key ':name' expected");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:22");
  }

  @Test
  public void fails_on_dict_non_string_key() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_dict_non_string_key.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting key");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:20");
  }

  @Test
  public void fails_on_dict_wrong_side_colon_key() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_dict_wrong_side_colon_key.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected 'name:'");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:22");
  }

  @Test
  public void fails_on_open_dict_non_string_key() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_non_string_key.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting key");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:30");
  }

  @Test
  public void fails_on_open_dict_unbalanced_pairs() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_unbalanced_pairs.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("value for key ':name' expected");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:29");
  }

  @Test
  public void fails_on_open_dict_wrong_side_colon_key() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_wrong_side_colon_key.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected 'profession:'");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:30");
  }

  @Test
  public void fails_on_open_dict_late_non_string_key() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_late_non_string_key.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting key");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:40");
  }

  @Test
  public void fails_on_open_dict_missing_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_missing_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:19");
  }

  @Test
  public void fails_on_open_dict_missing_early_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_missing_early_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:15");
  }

  @Test
  public void fails_on_open_dict_missing_late_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_missing_late_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:28");
  }

  @Test
  public void fails_on_open_dict_multi_splat_missing_late_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_multi_splat_missing_late_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("expecting ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:57");
  }

  @Test
  public void fails_on_open_dict_extra_initial_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_extra_initial_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:6");
  }

  @Test
  public void fails_on_open_dict_extra_early_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_extra_early_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:15");
  }

  @Test
  public void fails_on_open_dict_extra_mid_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_extra_mid_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:29");
  }

  @Test
  public void fails_on_open_dict_extra_final_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_extra_final_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:48");
  }

  @Test
  public void fails_on_open_dict_extra_late_separator() throws Exception {
    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/match_open_dict_extra_late_separator.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getMessage()).contains("unexpected ','");
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("5:59");
  }

}