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
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ListTypeTest {

  @Test
  public void casts_from_list() throws Exception {
    Value list = Values.makeList(1L, 2L);
    assertThat(Types.LIST.canAttemptCastFrom(Types.LIST)).isTrue();
    assertThat(Types.LIST.castFrom(list)).isSameAs(list);
  }

  @Test
  public void casts_from_dict() throws Exception {
    Value map = Values.makeDict("a", "v_a", "b", "v_b");
    assertThat(Types.LIST.canAttemptCastFrom(Types.DICT)).isTrue();
    assertThat(Types.LIST.castFrom(map)).isEqualTo(Values.makeList("a", "v_a", "b", "v_b"));
  }

  @Test
  public void casts_from_string() throws Exception {
    // Uses the beautiful Mathematical Capital Script C char:
    // http://unicode-table.com/en/1D49E/
    // This tests partitioning by code-point rather than by (potentially surrogate) char
    Value string = Values.make(new String(Character.toChars(0x1d49e)) + " - Hello");
    assertThat(Types.LIST.canAttemptCastFrom(Types.STRING)).isTrue();
    assertThat(Types.LIST.castFrom(string)).isEqualTo(Values.makeList(
        new String(Character.toChars(0x1d49e)), " ", "-", " ", "H", "e", "l", "l", "o"));
  }

  @Test
  public void casts_from_empty_string() throws Exception {
    Value string = Values.make("");
    assertThat(Types.LIST.castFrom(string)).isEqualTo(Values.EMPTY_LIST);
  }

  @Test
  public void casts_from_any() throws Exception {
    assertThat(Types.LIST.canAttemptCastFrom(Types.ANY)).isTrue();
  }

  @Test
  public void casts_from_void() throws Exception {
    assertThat(Types.LIST.canAttemptCastFrom(Types.VOID)).isTrue();
    assertThat(Types.LIST.castFrom(Values.NIL)).isSameAs(Values.NIL);
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_long() throws Exception {
    assertThat(Types.LIST.canAttemptCastFrom(Types.LONG)).isFalse();
    Types.LIST.castFrom(Values.make(1L));
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_double() throws Exception {
    assertThat(Types.LIST.canAttemptCastFrom(Types.DOUBLE)).isFalse();
    Types.LIST.castFrom(Values.make(1.0d));
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_boolean() throws Exception {
    assertThat(Types.LIST.canAttemptCastFrom(Types.BOOLEAN)).isFalse();
    Types.LIST.castFrom(Values.TRUE);
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_function() throws Exception {
    assertThat(Types.LIST.canAttemptCastFrom(Types.FUNCTION)).isFalse();
    Types.LIST.castFrom(TestHelper.makeConstantFunctionStub(Values.TRUE));
  }

  @Test(expected = LangException.class)
  public void cannot_cast_from_datetime() throws Exception {
    assertThat(Types.LIST.canAttemptCastFrom(Types.DATETIME)).isFalse();
    Types.LIST.castFrom(Values.EPOCH);
  }

}