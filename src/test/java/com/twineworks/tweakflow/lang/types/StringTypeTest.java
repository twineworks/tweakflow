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
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class StringTypeTest {

  @Test
  public void casts_from_string() throws Exception {
    assertThat(Types.STRING.canAttemptCastFrom(Types.STRING)).isTrue();
    Value x = Values.make("x");
    assertThat(Types.STRING.castFrom(x)).isSameAs(x);
  }

  @Test
  public void casts_from_any() throws Exception {
    assertThat(Types.STRING.canAttemptCastFrom(Types.ANY)).isTrue();
  }

  @Test
  public void casts_from_boolean() throws Exception {
    assertThat(Types.STRING.canAttemptCastFrom(Types.BOOLEAN)).isTrue();
    assertThat(Types.STRING.castFrom(Values.TRUE)).isEqualTo(Values.make("true"));
    assertThat(Types.STRING.castFrom(Values.FALSE)).isEqualTo(Values.make("false"));
  }

  @Test
  public void casts_from_long() throws Exception {
    assertThat(Types.STRING.canAttemptCastFrom(Types.LONG)).isTrue();
    assertThat(Types.STRING.castFrom(Values.make(1L))).isEqualTo(Values.make("1"));
    assertThat(Types.STRING.castFrom(Values.make(0L))).isEqualTo(Values.make("0"));
  }

  @Test
  public void casts_from_double() throws Exception {
    assertThat(Types.STRING.canAttemptCastFrom(Types.DOUBLE)).isTrue();
    assertThat(Types.STRING.castFrom(Values.make(1.0d))).isEqualTo(Values.make("1.0"));
    assertThat(Types.STRING.castFrom(Values.make(0.0d))).isEqualTo(Values.make("0.0"));
  }

  @Test
  public void casts_from_decimal() throws Exception {
    assertThat(Types.STRING.canAttemptCastFrom(Types.DECIMAL)).isTrue();
    assertThat(Types.STRING.castFrom(Values.DECIMAL_ONE)).isEqualTo(Values.make("1"));
    assertThat(Types.STRING.castFrom(Values.DECIMAL_ZERO)).isEqualTo(Values.make("0"));
    assertThat(Types.STRING.castFrom(Values.make(new BigDecimal("1234.5678")))).isEqualTo(Values.make("1234.5678"));
  }

  @Test
  public void cannot_cast_from_function() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.STRING.canAttemptCastFrom(Types.FUNCTION)).isFalse();
      Types.STRING.castFrom(TestHelper.makeConstantFunctionStub(Values.TRUE));
    });
  }

  @Test
  public void cannot_cast_from_list() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.STRING.canAttemptCastFrom(Types.LIST)).isFalse();
      Types.STRING.castFrom(Values.makeList());
    });
  }

  @Test
  public void cannot_cast_from_dict() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.STRING.canAttemptCastFrom(Types.DICT)).isFalse();
      Types.STRING.castFrom(Values.makeDict());
    });
  }

  @Test
  public void casts_from_datetime() throws Exception {
    assertThat(Types.STRING.canAttemptCastFrom(Types.DATETIME)).isTrue();
    assertThat(Types.STRING.castFrom(Values.EPOCH)).isEqualTo(Values.make("1970-01-01T00:00:00Z@UTC"));
  }

  @Test
  public void casts_from_void() throws Exception {
    assertThat(Types.STRING.canAttemptCastFrom(Types.VOID)).isTrue();
    assertThat(Types.STRING.castFrom(Values.NIL)).isSameAs(Values.NIL);
  }

}