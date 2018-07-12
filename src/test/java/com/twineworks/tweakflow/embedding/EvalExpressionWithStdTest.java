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

package com.twineworks.tweakflow.embedding;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.Parser;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.fail;

public class EvalExpressionWithStdTest {

  // helper method to parse a string as an expression
  private ParseResult parseExpression(String code){
    // place user content into a memory parse unit
    ParseUnit unit = new MemoryLocation.Builder()
        .allowNativeFunctions(false)        // disallow native function definitions
        .add("exp", code)             // place user input at the key exp
        .build()                            // create memory code location
        .getParseUnit("exp");          // and get the parse unit with key exp

    // and parse it
    return new Parser(unit).parseExpression();
  }

  private Runtime.Var compileUserExpression(String exp){

    String moduleTemplate = "import core, data, strings from 'std'\n" +
        "library lib {\n" +
        "  x: /* exp */ ; # placeholder for user expression\n" +
        "}";

    // parse user input as expression

    ParseResult parseResult = parseExpression(exp);

    if (parseResult.isError()) throw parseResult.getException();

    // user input parses as an expression, can try evaluation
    String userModule = moduleTemplate.replace("/* exp */", exp);

    // place standard library and user code module on load path
    LoadPath loadPath = new LoadPath.Builder()
        .addStdLocation()
        .add(new MemoryLocation.Builder()
            .allowNativeFunctions(false)
            .add("userModule", userModule)
            .build())
        .build();

    // compile the module
    Runtime runtime = TweakFlow.compile(loadPath, "userModule");
    // get user variable from runtime
    return runtime
        .getModules().get(runtime.unitKey("userModule"))
        .getLibrary("lib")
        .getVar("x");
  }

  @Test
  public void evaluates_expression_successfully() throws Exception {

    String exp = "data.size(['a', 'b'])";
    Runtime.Var x = compileUserExpression(exp);
    x.evaluate();
    assertThat(x.getValue()).isEqualTo(Values.make(2L));

  }

  @Test
  public void evaluates_expression_with_parse_error() throws Exception {

    String exp = "{error}";
    //                  ^ up till here this could have been a dict definition. Error.
    try {
      Runtime.Var x = compileUserExpression(exp);
    } catch (LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
      SourceInfo sourceInfo = e.getSourceInfo();
      assertThat(sourceInfo.getFullLocation()).isEqualTo("exp:1:7");
      assertThat(sourceInfo.getSourceCodeLine()).isEqualTo("{error}");
      return;
    }

    fail("Expected to catch and return. Should not be here.");

  }

  @Test
  public void evaluates_expression_with_compilation_error() throws Exception {

    String exp = "foo + 1";
    //            ^ Unresolved reference to variable foo. Error.
    try {
      Runtime.Var x = compileUserExpression(exp);
    } catch (LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.UNRESOLVED_REFERENCE);
      SourceInfo sourceInfo = e.getSourceInfo();
      assertThat(sourceInfo.getFullLocation()).isEqualTo("userModule:3:6");
      // the bad reference in question
      assertThat(sourceInfo.getSourceCode()).isEqualTo("foo");
      return;
    }

    fail("Expected to catch and return. Should not be here.");

  }

  @Test
  public void evaluates_expression_with_evaluation_error() throws Exception {

    String exp = "1 // 0";
    //            ^ can't do integer division by 0. Error.
    try {
      Runtime.Var x = compileUserExpression(exp);
      x.evaluate();
    } catch (LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.DIVISION_BY_ZERO);
      SourceInfo sourceInfo = e.getSourceInfo();
      assertThat(sourceInfo.getFullLocation()).isEqualTo("userModule:3:6");
      // the throwing expression
      assertThat(sourceInfo.getSourceCode()).isEqualTo("1 // 0");
      return;
    }

    fail("Expected to catch and return. Should not be here.");

  }

  @Test
  public void evaluates_expression_with_manual_throw() throws Exception {

    String exp = "throw {:bad 'error'}";
    //            ^ manual throw
    try {
      Runtime.Var x = compileUserExpression(exp);
      x.evaluate();
    } catch (LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.CUSTOM_ERROR);
      SourceInfo sourceInfo = e.getSourceInfo();
      assertThat(sourceInfo.getFullLocation()).isEqualTo("userModule:3:6");
      // the throwing expression
      assertThat(sourceInfo.getSourceCode()).isEqualTo("throw {:bad 'error'}");
      // and the value thrown
      Value thrown = e.toErrorValue();
      assertThat(thrown).isEqualTo(Values.makeDict("bad", "error"));
      return;
    }

    fail("Expected to catch and return. Should not be here.");

  }

}
