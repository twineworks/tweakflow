/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Twineworks GmbH
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

package com.twineworks.tweakflow.lang.values;

import com.twineworks.tweakflow.lang.TweakFlow;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ValueInspectorTest {

  String stripWS(String x){
    return x.replaceAll("\\s", "");
  }

  @Test
  void inspects_dicts() {
    Value v = Values.makeDict("a", 1, "b", 2);
    String s = ValueInspector.inspect(v);
    assertThat(stripWS(s)).isEqualTo(stripWS("{:a 1, :b 2}"));
  }

  @Test
  void inspects_empty_dicts() {
    Value v = Values.EMPTY_DICT;
    String s = ValueInspector.inspect(v);
    assertThat(s).isEqualTo("{}");
  }

  @Test
  void inspects_lists() {
    Value v = Values.makeList(1, 2, 3, 4);
    String s = ValueInspector.inspect(v);
    assertThat(s).isEqualTo("[1, 2, 3, 4]");
  }

  @Test
  void inspects_empty_lists() {
    Value v = Values.EMPTY_LIST;
    String s = ValueInspector.inspect(v);
    assertThat(s).isEqualTo("[]");
  }

  @Test
  void inspects_list_of_dicts () {
    Value v = Values.makeList(Values.makeDict("a", 1, "b", 2), Values.makeDict("a", 1, "b", 2));
    String s = ValueInspector.inspect(v);
    assertThat(stripWS(s)).isEqualTo(stripWS("[{:a 1, :b 2},{:a 1, :b 2}]"));
  }

  @Test
  void inspects_mixed_lists() {
    Value v = Values.makeList(
        Values.makeDict("a", 1, "b", 2),
        1L,
        2L,
        Values.makeList(1, 2, 3),
        Values.makeDict("a", 1, "b", 2),
        Values.makeDict("a", 1, "b", Values.makeDict("a", 1, "b", 2)),
        3L
    );
    String s = ValueInspector.inspect(v);
    assertThat(stripWS(s)).isEqualTo(stripWS("[{:a 1, :b 2}, 1, 2, [1, 2, 3], {:a 1, :b 2}, {:a 1, :b {:a 1, :b 2}}, 3]"));
  }

}