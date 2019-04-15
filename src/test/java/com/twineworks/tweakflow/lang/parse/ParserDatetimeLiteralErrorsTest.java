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

public class ParserDatetimeLiteralErrorsTest {

  private ParseResult parseFailing(String fixturePath){
    Parser p = new Parser(
        new ResourceParseUnit(new ResourceLocation.Builder().build(), fixturePath)
    );
    ParseResult result = p.parseUnit();
    assertThat(result.isSuccess()).isFalse();
    return result;
  }

  @Test
  public void fails_on_month_out_of_range() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_month_out_of_range.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("Invalid value for Month");
  }

  @Test
  public void fails_on_month_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_month_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("month must consist of one or two digits");
  }

  @Test
  public void fails_on_year_too_short() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_year_too_short.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("invalid year");
  }

  @Test
  public void fails_on_year_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_year_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("invalid year");
  }

  @Test
  public void fails_on_day_of_month_out_of_range() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_day_of_month_out_of_range.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("Invalid value for Day");
  }

  @Test
  public void fails_on_day_of_month_out_of_range_on_month() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_day_of_month_out_of_range_on_month.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("Invalid date");
  }

  @Test
  public void fails_on_day_of_month_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_day_of_month_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("day of month must consist of one or two digits");
  }

  @Test
  public void fails_on_hour_out_of_range() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_hour_out_of_range.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("Invalid value for Hour");
  }

  @Test
  public void fails_on_hour_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_hour_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("hour must consist of one or two digits");
  }

  @Test
  public void fails_on_minute_out_of_range() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_minute_out_of_range.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("Invalid value for Minute");
  }

  @Test
  public void fails_on_minute_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_minute_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("minute must consist of one or two digits");
  }

  @Test
  public void fails_on_second_out_of_range() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_second_out_of_range.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("Invalid value for Second");
  }

  @Test
  public void fails_on_second_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_second_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("second must consist of one or two digits");
  }

  @Test
  public void fails_on_fraction_of_second_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_fraction_of_second_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("invalid fraction of second");
  }

  @Test
  public void fails_on_offset_hour_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_offset_hour_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("offset hour must consist of one or two digits");
  }

  @Test
  public void fails_on_offset_minute_too_long() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_offset_minute_too_long.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("offset minute must consist of one or two digits");
  }

  @Test
  public void fails_on_offset_invalid_for_tz() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_offset_invalid_for_tz.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("ZoneOffset '+03:00' is not valid");
  }

  @Test
  public void fails_on_tz_unknown() throws Exception {

    ParseResult r = parseFailing("fixtures/tweakflow/analysis/parsing/errors/datetime_tz_unknown.tf");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.INVALID_DATETIME);
    assertThat(e.getMessage()).contains("Unknown time");
  }

}