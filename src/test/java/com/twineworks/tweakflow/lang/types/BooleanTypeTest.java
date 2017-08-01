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

import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BooleanTypeTest {

  @Test
  public void casts_from_boolean() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.BOOLEAN)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.TRUE)).isSameAs(Values.TRUE);
    assertThat(Types.BOOLEAN.castFrom(Values.FALSE)).isSameAs(Values.FALSE);
  }

  @Test
  public void casts_from_any() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.ANY)).isTrue();
  }

  @Test
  public void casts_from_void() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.VOID)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.NIL)).isSameAs(Values.NIL);
  }

  @Test
  public void casts_from_datetime() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.DATETIME)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.EPOCH)).isSameAs(Values.TRUE);
  }

  @Test
  public void casts_from_function() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.FUNCTION)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.makeConstantFunctionStub(Values.make(1L)))).isSameAs(Values.TRUE);
  }

  @Test
  public void casts_from_list() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.LIST)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.makeList())).isSameAs(Values.FALSE);
    assertThat(Types.BOOLEAN.castFrom(Values.makeList(1L))).isSameAs(Values.TRUE);
  }

  @Test
  public void casts_from_map() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.DICT)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.makeDict())).isSameAs(Values.FALSE);
    assertThat(Types.BOOLEAN.castFrom(Values.makeDict("key", 1L))).isSameAs(Values.TRUE);
  }

  @Test
  public void casts_from_long() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.LONG)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.make(0L))).isSameAs(Values.FALSE);
    assertThat(Types.BOOLEAN.castFrom(Values.make(1L))).isSameAs(Values.TRUE);
  }

  @Test
  public void casts_from_double() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.DOUBLE)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.make(0.0d))).isSameAs(Values.FALSE);
    assertThat(Types.BOOLEAN.castFrom(Values.make(-0.0d))).isSameAs(Values.FALSE);
    assertThat(Types.BOOLEAN.castFrom(Values.make(Double.NaN))).isSameAs(Values.FALSE);
    assertThat(Types.BOOLEAN.castFrom(Values.make(1.0d))).isSameAs(Values.TRUE);
    assertThat(Types.BOOLEAN.castFrom(Values.make(Double.POSITIVE_INFINITY))).isSameAs(Values.TRUE);
    assertThat(Types.BOOLEAN.castFrom(Values.make(Double.NEGATIVE_INFINITY))).isSameAs(Values.TRUE);
  }

  @Test
  public void casts_from_string() throws Exception {
    assertThat(Types.BOOLEAN.canAttemptCastFrom(Types.STRING)).isTrue();
    assertThat(Types.BOOLEAN.castFrom(Values.make(""))).isSameAs(Values.FALSE);
    assertThat(Types.BOOLEAN.castFrom(Values.make("a"))).isSameAs(Values.TRUE);
  }

}