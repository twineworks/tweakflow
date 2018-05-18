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
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.assertj.core.api.StrictAssertions.fail;

public class EvalProvidedVarsTest {

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
  public void updates_a_variable() throws Exception {

    String module = "library lib {" +
        "provided long a; " +
        "f: (x) -> x+a" +
        "}";
    Runtime.Module m = compileModule(module);
    Runtime.Var a = m.getLibrary("lib").getVar("a");
    Runtime.Var f = m.getLibrary("lib").getVar("f");

    // initially
    // a: 0
    a.update(Values.make(0L));
    m.evaluate();

    // f evaluates to
    // f: (x) -> 0+x
    Value r0 = f.call(Values.make(3L));
    assertThat(r0).isEqualTo(Values.make(3L));

    // a: 1
    // f: (x) -> 1+x
    a.update(Values.make(1L));
    Value r1 = f.call(Values.make(3L));
    assertThat(r1).isEqualTo(Values.make(4L));

    // a: 2
    // f: (x) -> 2+x
    a.update(Values.make(2L));
    Value r2 = f.call(Values.make(3L));
    assertThat(r2).isEqualTo(Values.make(5L));

  }

  @Test
  public void updates_multiple_variables_atomically() throws Exception {

    String module = "library lib {" +
        "provided long a; " +
        "provided long b; " +
        "long c: if a != b then throw 'expected a and b to be equal' else a*b" +
        "}";
    Runtime.Module m = compileModule(module);
    Runtime.Var a = m.getLibrary("lib").getVar("a");
    Runtime.Var b = m.getLibrary("lib").getVar("b");
    Runtime.Var c = m.getLibrary("lib").getVar("c");

    Runtime.Var[] providedVars = {a, b};
    Runtime runtime = m.getRuntime();

    // initially
    // a: 0, b: 0
    // use variant 1
    runtime.updateVars(providedVars, new Value[] {Values.make(0), Values.make(0)});
    assertThat(c.getValue()).isEqualTo(Values.make(0));

    // a: 1, b: 1
    // use variant 2
    runtime.updateVars(
        a, Values.make(1),
        b, Values.make(1));
    assertThat(c.getValue()).isEqualTo(Values.make(1L));

    // a: 2, b: 2
    // use variant 3
    runtime.updateVars(
        Arrays.asList(a, b),
        Arrays.asList(Values.make(2), Values.make(2)));
    assertThat(c.getValue()).isEqualTo(Values.make(4L));

    // a:3, b: 3
    // updating non-atomically throws during eval of c
    try {
      a.update(Values.make(3L));
      b.update(Values.make(3L));
    }
    catch (LangException e){
      Value errorValue = e.toErrorValue();
      assertThat(errorValue.string()).isEqualTo("expected a and b to be equal");
      return;
    }

    fail("expected a throw/catch and return");

  }

  @Test
  public void provides_a_variable() throws Exception {

    String module = "library lib {" +
        "provided long a; " +
        "f: (x) -> x+a" +
        "}";
    Runtime.Module m = compileModule(module);

    Runtime.Var a = m.getLibrary("lib").getVar("a");
    a.update(Values.make(7L));

    Runtime.Var f = m.getLibrary("lib").getVar("f");
    m.evaluate();

    assertThat(f.getValue().isFunction()).isTrue();
    Value result = f.call(Values.make(3L));

    assertThat(result).isEqualTo(Values.make(10L));

  }

  @Test
  public void recognises_provided_variables_as_referenced() throws Exception {

    String module = "library lib {" +
        "provided long a; " +
        "provided long b; " +
        "f: (x) -> x+a" +
        "}";
    Runtime.Module m = compileModule(module);

    // a is referenced in the definition of f
    Runtime.Var a = m.getLibrary("lib").getVar("a");
    assertThat(a.isReferenced()).isTrue();

    // b is not referenced anywhere
    Runtime.Var b = m.getLibrary("lib").getVar("b");
    assertThat(b.isReferenced()).isFalse();

  }


  @Test
  public void provided_var_is_nil_until_value_given() throws Exception {

    String module = "library lib {" +
        "provided long a; " +
        "}";
    Runtime.Module m = compileModule(module);
    m.evaluate();

    Runtime.Var a = m.getLibrary("lib").getVar("a");
    assertThat(a.getValue()).isSameAs(Values.NIL);

    a.update(Values.make(0L));
    assertThat(a.getValue()).isEqualTo(Values.make(0L));

  }

  @Test(expected = UnsupportedOperationException.class)
  public void cannot_update_regular_var() throws Exception {
    String module = "library lib {" +
        "long a: 0 " +
        "}";

    Runtime.Module m = compileModule(module);
    m.evaluate();

    Runtime.Var a = m.getLibrary("lib").getVar("a");
    assertThat(a.getValue()).isEqualTo(Values.make(0L));

    // this throws
    a.update(Values.make(1L));

  }

  @Test(expected = NullPointerException.class)
  public void cannot_update_var_to_null() throws Exception {
    String module = "library lib {" +
        "provided a;" +
        "}";

    Runtime.Module m = compileModule(module);
    m.evaluate();

    Runtime.Var a = m.getLibrary("lib").getVar("a");

    // this throws
    a.update(null);

  }
}
