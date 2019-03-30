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

package com.twineworks.tweakflow.embedding;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.util.LangUtil;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.fail;

public class EvalExpressionInEmptyScopeTest {

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

  @Test
  public void evaluates_embedded_expression() throws Exception {

    String exp = "if (first_name && last_name) then \n" +
        "'Dear ' .. first_name .. ' '.. last_name\n" +
        "else\n" +
        "'Dear customer'";
    // make sure exp parses as an expression
    assertThat(TweakFlow.parse(exp).isSuccess()).isTrue();

    String firstName = "Mary";
    String lastName = "Poppins";

    String code = "let {" +
        "  first_name: \""+ LangUtil.escapeString(firstName)+"\";" +
        "  last_name: \""+ LangUtil.escapeString(lastName)+"\";" +
        "} "+exp;

    assertThat(TweakFlow.evaluate(code)).isEqualTo(Values.make("Dear Mary Poppins"));

  }

  @Test
  public void catches_runtime_error() throws Exception {

    try {
      TweakFlow.evaluate("1 // 0"); // integer division by 0 not possible
    } catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.DIVISION_BY_ZERO);
      assertThat(e.getSourceInfo().getFullLocation()).isEqualTo("eval:1:1");
      assertThat(e.getSourceInfo().getSourceCode()).isEqualTo("1 // 0");
      return;
    }

    fail("expected to throw/catch and return");

  }

  @Test
  public void catches_manual_throw() throws Exception {

    try {
      TweakFlow.evaluate("throw 'catch me if you can!'");
    } catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.CUSTOM_ERROR);
      assertThat(e.getSourceInfo().getFullLocation()).isEqualTo("eval:1:1");
      assertThat(e.getSourceInfo().getSourceCode()).isEqualTo("throw 'catch me if you can!'");
      assertThat(e.toErrorValue()).isEqualTo(Values.make("catch me if you can!"));
      return;
    }

    fail("expected to throw/catch and return");

  }

  @Test
  public void cannot_evaluate_invalid_expression() throws Exception {

    try {
      TweakFlow.evaluate("mary had a little lamb");
      //                            ^ 'mary' might be a reference, but another reference 'had' cannot follow
    } catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.PARSE_ERROR);
      assertThat(e.getSourceInfo().getFullLocation()).isEqualTo("eval:1:6");
      return;
    }

    fail("expected to throw/catch and return");

  }

  @Test
  public void cannot_use_standard_library_in_empty_scope() throws Exception {
    String code = "strings.length('foo')";
    //             ^ strings is not defined, standard library is not imported, scope is empty
    try {
      TweakFlow.evaluate(code);
    } catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);
      assertThat(e.getSourceInfo().getFullLocation()).isEqualTo("eval:1:1");
      assertThat(e.getSourceInfo().getSourceCode()).isEqualTo("strings.length");
      return;
    }

    fail("expected to throw/catch and return");
  }

}
