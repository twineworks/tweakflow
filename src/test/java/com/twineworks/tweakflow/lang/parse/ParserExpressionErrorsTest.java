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

package com.twineworks.tweakflow.lang.parse;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserExpressionErrorsTest {

  private ParseResult parseFailing(String text){
    HashMap<String, String> content = new HashMap<>();
    content.put("<test>", text);
    MemoryLocation mem = new MemoryLocation(true, false, content);

    Parser p = new Parser(
        mem.getParseUnit("<test>")
    );
    ParseResult result = p.parseExpression();
    assertThat(result.isSuccess()).isFalse();
    return result;
  }

  @Test
  void fails_on_trailing_eos(){

    ParseResult r = parseFailing("1;");
    LangException e = r.getException();
    assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
    assertThat(e.getSourceInfo().getShortLocation()).isEqualTo("1:2");
    assertThat(e.getMessage()).matches(Pattern.compile(".*unexpected.*';'.*"));

  }


}