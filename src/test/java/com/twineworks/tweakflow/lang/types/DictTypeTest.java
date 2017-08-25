/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Twineworks GmbH
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
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DictTypeTest {

  @Test
  public void casts_from_dict() throws Exception {
    Value map = Values.makeDict("a", 1L, "b", 2L);
    assertThat(Types.DICT.canAttemptCastFrom(Types.DICT)).isTrue();
    assertThat(Types.DICT.castFrom(map)).isSameAs(map);
  }

  @Test
  public void casts_from_any() throws Exception {
    assertThat(Types.DICT.canAttemptCastFrom(Types.ANY)).isTrue();
  }

  @Test
  public void casts_from_list() throws Exception {
    Value list = Values.makeList("a", 1L, "b", 2L);
    assertThat(Types.DICT.canAttemptCastFrom(Types.LIST)).isTrue();
    assertThat(Types.DICT.castFrom(list)).isEqualTo(Values.makeDict("a", 1L, "b", 2L));
  }

  @Test
  public void casts_from_void() throws Exception {
    assertThat(Types.DICT.canAttemptCastFrom(Types.VOID)).isTrue();
    assertThat(Types.DICT.castFrom(Values.NIL)).isSameAs(Values.NIL);
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_boolean() throws Exception {
    assertThat(Types.DICT.canAttemptCastFrom(Types.BOOLEAN)).isFalse();
    Types.DICT.castFrom(Values.TRUE);
  }

  @Test
  public void casts_from_datetime() throws Exception {
    assertThat(Types.DICT.canAttemptCastFrom(Types.DATETIME)).isTrue();
    assertThat(Types.DICT.castFrom(Values.EPOCH)).isEqualTo(Values.makeDict(
        "year", 1970,
        "month", 1,
        "day_of_month", 1,
        "hour", 0,
        "minute", 0,
        "second", 0,
        "nano_of_second", 0,
        "day_of_year", 1,
        "day_of_week", 4,
        "week_of_year", 1,
        "offset_seconds", 0,
        "zone", "UTC"
    ));
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_string() throws Exception {
    assertThat(Types.DICT.canAttemptCastFrom(Types.STRING)).isFalse();
    Types.DICT.castFrom(Values.make("hello"));
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_double() throws Exception {
    assertThat(Types.DICT.canAttemptCastFrom(Types.DOUBLE)).isFalse();
    Types.DICT.castFrom(Values.make(1.0d));
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_long() throws Exception {
    assertThat(Types.DICT.canAttemptCastFrom(Types.LONG)).isFalse();
    Types.DICT.castFrom(Values.make(1L));
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_function() throws Exception {
    assertThat(Types.DICT.canAttemptCastFrom(Types.FUNCTION)).isFalse();
    Types.DICT.castFrom(TestHelper.makeConstantFunctionStub(Values.make(1L)));
  }

}