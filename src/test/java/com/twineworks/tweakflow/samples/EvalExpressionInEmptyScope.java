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

package com.twineworks.tweakflow.samples;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class EvalExpressionInEmptyScope {

  @Test
  public void evaluates_expression() throws Exception {
    assertThat(TweakFlow.evaluate("1+2")).isEqualTo(Values.make(3L));
  }

  @Test
  public void evaluates_complex_expression() throws Exception {

    String code = "let {" +
        "  a: 'hello'" +
        "  b: 'world'" +
        "} a .. ' ' .. b";

    assertThat(TweakFlow.evaluate(code)).isEqualTo(Values.make("hello world"));

  }

  @Test(expected = LangException.class)
  public void cannot_evaluate_invalid_expression() throws Exception {
    TweakFlow.evaluate("mary had a little lamb");
  }

  @Test(expected = LangException.class)
  public void cannot_use_standard_library_in_standalone_evaluation() throws Exception {
    String code = "strings.length('foo')";
    TweakFlow.evaluate(code);
  }

}
