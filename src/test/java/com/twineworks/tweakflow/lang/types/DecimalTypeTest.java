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

public class DecimalTypeTest {

  @Test
  public void casts_from_decimal() throws Exception {
    assertThat(Types.DECIMAL.canAttemptCastFrom(Types.DECIMAL)).isTrue();
    Value x = Values.make(BigDecimal.valueOf(1.0));
    assertThat(Types.DECIMAL.castFrom(x)).isSameAs(x);
  }

  @Test
  public void casts_from_double() throws Exception {
    assertThat(Types.DECIMAL.canAttemptCastFrom(Types.DECIMAL)).isTrue();
    Value x = Values.make(1.0d);
    assertThat(Types.DECIMAL.castFrom(x)).isEqualTo(Values.DECIMAL_ONE);
    // casting non-finite values results in 0
    assertThat(Types.DECIMAL.castFrom(Values.NAN)).isEqualTo(Values.DECIMAL_ZERO);
    assertThat(Types.DECIMAL.castFrom(Values.INFINITY)).isEqualTo(Values.DECIMAL_ZERO);
    assertThat(Types.DECIMAL.castFrom(Values.NEG_INFINITY)).isEqualTo(Values.DECIMAL_ZERO);
  }
  
  @Test
  public void casts_from_any() throws Exception {
    assertThat(Types.DECIMAL.canAttemptCastFrom(Types.ANY)).isTrue();
  }

  @Test
  public void casts_from_boolean() throws Exception {
    assertThat(Types.DECIMAL.canAttemptCastFrom(Types.BOOLEAN)).isTrue();
    assertThat(Types.DECIMAL.castFrom(Values.TRUE)).isEqualTo(Values.DECIMAL_ONE);
    assertThat(Types.DECIMAL.castFrom(Values.FALSE)).isEqualTo(Values.DECIMAL_ZERO);
  }

  @Test
  public void casts_from_long() throws Exception {
    assertThat(Types.DECIMAL.canAttemptCastFrom(Types.LONG)).isTrue();
    assertThat(Types.DECIMAL.castFrom(Values.make(1L))).isEqualTo(Values.DECIMAL_ONE);
    assertThat(Types.DECIMAL.castFrom(Values.make(0L))).isEqualTo(Values.DECIMAL_ZERO);
  }

  @Test
  public void casts_from_string() throws Exception {
    assertThat(Types.DECIMAL.canAttemptCastFrom(Types.STRING)).isTrue();
    assertThat(Types.DECIMAL.castFrom(Values.make("1.0"))).isEqualTo(Values.make(BigDecimal.valueOf(1.0d)));
    assertThat(Types.DECIMAL.castFrom(Values.make("0.0"))).isEqualTo(Values.make(BigDecimal.valueOf(0.0d)));
    assertThat(Types.DECIMAL.castFrom(Values.make("999"))).isEqualTo(Values.make(BigDecimal.valueOf(999d)));
    assertThat(Types.DECIMAL.castFrom(Values.make("-3.2"))).isEqualTo(Values.make(BigDecimal.valueOf(-3.2d)));
    assertThat(Types.DECIMAL.castFrom(Values.make("+0.5e-2"))).isEqualTo(Values.make(BigDecimal.valueOf(0.005d)));
    assertThat(Types.DECIMAL.castFrom(Values.make(".4"))).isEqualTo(Values.make(BigDecimal.valueOf(.4d)));
    assertThat(Types.DECIMAL.castFrom(Values.make(".4E3"))).isEqualTo(Values.make(BigDecimal.valueOf(400d)));
    assertThat(Types.DECIMAL.castFrom(Values.make("-2.4E1"))).isEqualTo(Values.make(BigDecimal.valueOf(-24d)));
    // beyond long/double precision
    assertThat(Types.DECIMAL.castFrom(Values.make("922337203685477580700.922337203685477580700"))).isEqualTo(Values.make(new BigDecimal("922337203685477580700.922337203685477580700")));
  }

  @Test
  public void cannot_cast_from_function() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DECIMAL.canAttemptCastFrom(Types.FUNCTION)).isFalse();
      Types.DECIMAL.castFrom(TestHelper.makeConstantFunctionStub(Values.TRUE));
    });
  }

  @Test
  public void cannot_cast_from_datetime() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DECIMAL.canAttemptCastFrom(Types.DATETIME)).isFalse();
      Types.DECIMAL.castFrom(Values.EPOCH);
    });
  }

  @Test
  public void cannot_cast_from_invalid_string() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      Types.DECIMAL.castFrom(Values.make("0.2kg"));
    });
  }

  @Test
  public void cannot_cast_from_list() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DECIMAL.canAttemptCastFrom(Types.LIST)).isFalse();
      Types.DECIMAL.castFrom(Values.makeList());
    });
  }

  @Test
  public void cannot_cast_from_dict() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.DECIMAL.canAttemptCastFrom(Types.DICT)).isFalse();
      Types.DECIMAL.castFrom(Values.makeDict());
    });
  }

  @Test
  public void casts_from_void() throws Exception {
    assertThat(Types.DECIMAL.canAttemptCastFrom(Types.VOID)).isTrue();
    assertThat(Types.DECIMAL.castFrom(Values.NIL)).isSameAs(Values.NIL);

  }

}