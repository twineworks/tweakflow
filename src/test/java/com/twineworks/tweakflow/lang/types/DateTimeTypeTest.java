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

package com.twineworks.tweakflow.lang.types;

import com.twineworks.tweakflow.TestHelper;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeTypeTest {

  @Test
  public void casts_from_datetime() throws Exception {
    assertThat(Types.DATETIME.canAttemptCastFrom(Types.DATETIME)).isTrue();
    Value x = Values.EPOCH;
    assertThat(Types.DATETIME.castFrom(x)).isSameAs(x);
  }

  @Test
  public void cannot_cast_from_double() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DATETIME.canAttemptCastFrom(Types.DOUBLE)).isFalse();
      Types.DATETIME.castFrom(Values.make(1.0d));
    });
  }

  @Test
  public void casts_from_any() throws Exception {
    assertThat(Types.DATETIME.canAttemptCastFrom(Types.ANY)).isTrue();
  }

  @Test
  public void cannot_cast_from_boolean() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DATETIME.canAttemptCastFrom(Types.BOOLEAN)).isFalse();
      Types.DATETIME.castFrom(Values.TRUE);
    });
  }


  @Test
  public void cannot_cast_from_long() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DATETIME.canAttemptCastFrom(Types.LONG)).isFalse();
      Types.DATETIME.castFrom(Values.LONG_ZERO);
    });
  }

  @Test
  public void casts_from_string() throws Exception {
    assertThat(Types.DATETIME.canAttemptCastFrom(Types.STRING)).isTrue();

    // short format
    assertThat(Types.DATETIME.castFrom(Values.make("2020-05-04"))).isEqualTo(Values.make(LocalDateTime.of(2020, 5, 4, 0, 0 )));
    assertThat(Types.DATETIME.castFrom(Values.make("2020-05-04T"))).isEqualTo(Values.make(LocalDateTime.of(2020, 5, 4, 0, 0 )));

    // full format

    // with backticks
    assertThat(Types.DATETIME.castFrom(Values.make("2017-03-17T16:04:02.123456789+01:00@`Europe/Berlin`")))
        .isEqualTo(Values.make(ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123456789, ZoneId.of("Europe/Berlin"))));

    // without backticks
    assertThat(Types.DATETIME.castFrom(Values.make("2017-03-17T16:04:02.123456789+01:00@Europe/Berlin")))
        .isEqualTo(Values.make(ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123456789, ZoneId.of("Europe/Berlin"))));

    // no offset
    assertThat(Types.DATETIME.castFrom(Values.make("2017-03-17T16:04:02.123456789@Europe/Berlin")))
        .isEqualTo(Values.make(ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123456789, ZoneId.of("Europe/Berlin"))));

    // no offset and utc based zone with backticks
    assertThat(Types.DATETIME.castFrom(Values.make("2017-03-17T16:04:02.123456789@`UTC+04:00`")))
        .isEqualTo(Values.make(ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123456789, ZoneId.of("UTC+04:00"))));

    // with offset and utc based zone with backticks
    assertThat(Types.DATETIME.castFrom(Values.make("2017-03-17T16:04:02.123456789+04:00@`UTC+04:00`")))
        .isEqualTo(Values.make(ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123456789, ZoneId.of("UTC+04:00"))));

    // no offset and utc based zone without backticks
    assertThat(Types.DATETIME.castFrom(Values.make("2017-03-17T16:04:02.123456789@UTC+04:00")))
        .isEqualTo(Values.make(ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123456789, ZoneId.of("UTC+04:00"))));

    // with offset and utc based zone without backticks
    assertThat(Types.DATETIME.castFrom(Values.make("2017-03-17T16:04:02.123456789+04:00@UTC+04:00")))
        .isEqualTo(Values.make(ZonedDateTime.of(2017, 3, 17, 16, 4, 2, 123456789, ZoneId.of("UTC+04:00"))));

  }

  @Test
  public void cannot_cast_from_function() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DATETIME.canAttemptCastFrom(Types.FUNCTION)).isFalse();
      Types.DATETIME.castFrom(TestHelper.makeConstantFunctionStub(Values.TRUE));
    });
  }

  @Test
  public void cannot_cast_from_list() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DATETIME.canAttemptCastFrom(Types.LIST)).isFalse();
      Types.DATETIME.castFrom(Values.makeList());
    });
  }

  @Test
  public void cannot_cast_from_dict() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DATETIME.canAttemptCastFrom(Types.DICT)).isFalse();
      Types.DATETIME.castFrom(Values.makeDict());
    });
  }

  @Test
  public void casts_from_void() throws Exception {
    assertThat(Types.DATETIME.canAttemptCastFrom(Types.VOID)).isTrue();
    assertThat(Types.DATETIME.castFrom(Values.NIL)).isSameAs(Values.NIL);
  }
}