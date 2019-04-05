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

import static org.assertj.core.api.Assertions.assertThat;

public class DoubleTypeTest {

  @Test
  public void casts_from_double() throws Exception {
    assertThat(Types.DOUBLE.canAttemptCastFrom(Types.DOUBLE)).isTrue();
    Value x = Values.make(1.0d);
    assertThat(Types.DOUBLE.castFrom(x)).isSameAs(x);
  }

  @Test
  public void casts_from_any() throws Exception {
    assertThat(Types.DOUBLE.canAttemptCastFrom(Types.ANY)).isTrue();
  }

  @Test
  public void casts_from_boolean() throws Exception {
    assertThat(Types.DOUBLE.canAttemptCastFrom(Types.BOOLEAN)).isTrue();
    assertThat(Types.DOUBLE.castFrom(Values.TRUE)).isEqualTo(Values.make(1.0d));
    assertThat(Types.DOUBLE.castFrom(Values.FALSE)).isEqualTo(Values.make(0.0d));
  }

  @Test
  public void casts_from_long() throws Exception {
    assertThat(Types.DOUBLE.canAttemptCastFrom(Types.LONG)).isTrue();
    assertThat(Types.DOUBLE.castFrom(Values.make(1L))).isEqualTo(Values.make(1.0d));
    assertThat(Types.DOUBLE.castFrom(Values.make(0L))).isEqualTo(Values.make(0.0d));
  }

  @Test
  public void casts_from_string() throws Exception {
    assertThat(Types.DOUBLE.canAttemptCastFrom(Types.STRING)).isTrue();
    assertThat(Types.DOUBLE.castFrom(Values.make("1.0"))).isEqualTo(Values.make(1.0d));
    assertThat(Types.DOUBLE.castFrom(Values.make("0.0"))).isEqualTo(Values.make(0.0d));
    assertThat(Types.DOUBLE.castFrom(Values.make("999"))).isEqualTo(Values.make(999d));
    assertThat(Types.DOUBLE.castFrom(Values.make("-3.2"))).isEqualTo(Values.make(-3.2d));
    assertThat(Types.DOUBLE.castFrom(Values.make("+0.5e-2"))).isEqualTo(Values.make(0.005d));
    assertThat(Types.DOUBLE.castFrom(Values.make(".4"))).isEqualTo(Values.make(.4d));
    assertThat(Types.DOUBLE.castFrom(Values.make(".4E3"))).isEqualTo(Values.make(400d));
    assertThat(Types.DOUBLE.castFrom(Values.make("-2.4E1"))).isEqualTo(Values.make(-24d));
    // beyond long range
    assertThat(Types.DOUBLE.castFrom(Values.make("922337203685477580700"))).isEqualTo(Values.make(922337203685477580700d));
  }

  @Test
  public void cannot_cast_from_function() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DOUBLE.canAttemptCastFrom(Types.FUNCTION)).isFalse();
      Types.DOUBLE.castFrom(TestHelper.makeConstantFunctionStub(Values.TRUE));
    });
  }

  @Test
  public void cannot_cast_from_datetime() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DOUBLE.canAttemptCastFrom(Types.DATETIME)).isFalse();
      Types.DOUBLE.castFrom(Values.EPOCH);
    });
  }

  @Test
  public void cannot_cast_from_invalid_string() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      Types.DOUBLE.castFrom(Values.make("0.2kg"));
    });
  }

  @Test
  public void cannot_cast_from_list() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DOUBLE.canAttemptCastFrom(Types.LIST)).isFalse();
      Types.DOUBLE.castFrom(Values.makeList());
    });
  }

  @Test
  public void cannot_cast_from_dict() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DOUBLE.canAttemptCastFrom(Types.DICT)).isFalse();
      Types.DOUBLE.castFrom(Values.makeDict());
    });
  }

  @Test
  public void casts_from_void() throws Exception {
    assertThat(Types.DOUBLE.canAttemptCastFrom(Types.VOID)).isTrue();
    assertThat(Types.DOUBLE.castFrom(Values.NIL)).isSameAs(Values.NIL);

  }

}