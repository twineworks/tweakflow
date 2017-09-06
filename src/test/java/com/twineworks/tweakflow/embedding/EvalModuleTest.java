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
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.fail;

public class EvalModuleTest {

  private Runtime.Module compileModule(String module){

    // place standard library and user code module on load path
    LoadPath loadPath = new LoadPath.Builder()
        .addStdLocation()
        .add(new MemoryLocation.Builder()
            .allowNativeFunctions(false)
            .add("userModule", module)
            .build())
        .build();

    // compile the module
    Runtime runtime = TweakFlow.compile(loadPath, "userModule");
    // get user module from runtime
    return runtime
        .getModules().get(runtime.unitKey("userModule"));
  }

  @Test
  public void evaluates_module_successfully() throws Exception {

    String module = "library lib {f: (x) -> x+1}";
    Runtime.Module m = compileModule(module);
    m.evaluate();
    Runtime.Var f = m.getLibrary("lib").getVar("f");
    assertThat(f.getValue().isFunction()).isTrue();

  }

  @Test
  public void evaluates_module_with_parse_error() throws Exception {

    String module = "library {f: (x) -> x+1}";
    //                       ^ Library name missing. Error.
    try {
      Runtime.Module m = compileModule(module);
    } catch (LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.PARSE_ERROR);
      SourceInfo sourceInfo = e.getSourceInfo();
      assertThat(sourceInfo.getFullLocation()).isEqualTo("userModule:1:11");
      return;
    }

    fail("Expected to catch and return. Should not be here.");

  }

  @Test
  public void evaluates_module_with_compilation_error() throws Exception {

    String exp = "library lib {f: foo}";
    //                            ^ Unresolved reference to variable foo. Error.
    try {
      Runtime.Module m = compileModule(exp);
    } catch (LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.UNRESOLVED_REFERENCE);
      SourceInfo sourceInfo = e.getSourceInfo();
      assertThat(sourceInfo.getFullLocation()).isEqualTo("userModule:1:17");
      // the bad reference in question
      assertThat(sourceInfo.getSourceCode()).isEqualTo("foo");
      return;
    }

    fail("Expected to catch and return. Should not be here.");

  }

  @Test
  public void evaluates_module_with_evaluation_error() throws Exception {

    String exp = "library lib {f: 1 // 0}";
    //                            ^ can't do integer division by 0. Error.
    try {
      Runtime.Module x = compileModule(exp);
      x.evaluate();
    } catch (LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.DIVISION_BY_ZERO);
      SourceInfo sourceInfo = e.getSourceInfo();
      assertThat(sourceInfo.getFullLocation()).isEqualTo("userModule:1:17");
      // the throwing expression
      assertThat(sourceInfo.getSourceCode()).isEqualTo("1 // 0");
      return;
    }

    fail("Expected to catch and return. Should not be here.");

  }

  @Test
  public void evaluates_module_with_manual_throw() throws Exception {

    String exp = "library lib {f: throw {:bad 'error'}}";
    //            ^ manual throw
    try {
      Runtime.Module x = compileModule(exp);
      x.evaluate();
    } catch (LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.CUSTOM_ERROR);
      SourceInfo sourceInfo = e.getSourceInfo();
      assertThat(sourceInfo.getFullLocation()).isEqualTo("userModule:1:17");
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
