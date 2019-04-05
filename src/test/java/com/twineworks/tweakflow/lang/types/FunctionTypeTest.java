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

public class FunctionTypeTest {

  @Test
  public void casts_from_function() throws Exception {
    assertThat(Types.FUNCTION.canAttemptCastFrom(Types.FUNCTION)).isTrue();
    Value x = TestHelper.makeConstantFunctionStub(Values.TRUE);
    assertThat(Types.FUNCTION.castFrom(x)).isSameAs(x);
  }

  @Test
  public void casts_from_any() throws Exception {
    assertThat(Types.FUNCTION.canAttemptCastFrom(Types.ANY)).isTrue();
  }

  @Test
  public void cannot_cast_from_boolean() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.FUNCTION.canAttemptCastFrom(Types.BOOLEAN)).isFalse();
      Types.FUNCTION.castFrom(Values.TRUE);
    });
  }

  @Test
  public void cannot_cast_from_double() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.FUNCTION.canAttemptCastFrom(Types.DOUBLE)).isFalse();
      Types.FUNCTION.castFrom(Values.make(0.0d));
    });
  }

  @Test
  public void cannot_cast_from_list() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.FUNCTION.canAttemptCastFrom(Types.LIST)).isFalse();
      Types.FUNCTION.castFrom(Values.makeList());
    });

  }

  @Test
  public void cannot_cast_from_long() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.FUNCTION.canAttemptCastFrom(Types.LONG)).isFalse();
      Types.FUNCTION.castFrom(Values.make(1L));
    });
  }

  @Test
  public void cannot_cast_from_datetime() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.FUNCTION.canAttemptCastFrom(Types.DATETIME)).isFalse();
      Types.FUNCTION.castFrom(Values.EPOCH);
    });
  }

  @Test
  public void cannot_cast_from_dict() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.FUNCTION.canAttemptCastFrom(Types.DICT)).isFalse();
      Types.FUNCTION.castFrom(Values.makeDict());
    });
  }

  @Test
  public void cannot_cast_from_string() throws Exception {
    Assertions.assertThrows(LangException.class, () -> {
      assertThat(Types.FUNCTION.canAttemptCastFrom(Types.STRING)).isFalse();
      Types.FUNCTION.castFrom(Values.make(""));
    });
  }

  @Test
  public void casts_from_void() throws Exception {
    assertThat(Types.FUNCTION.canAttemptCastFrom(Types.VOID)).isTrue();
    assertThat(Types.FUNCTION.castFrom(Values.NIL)).isSameAs(Values.NIL);
  }


}