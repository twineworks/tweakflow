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
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class EvalProvidedVars {

  private Runtime.Module compileModule(String module){

    // place standard library and user code module on load path
    LoadPath loadPath = new LoadPath.Builder()
        .addStdLocation()
        .add(new MemoryLocation.Builder()
            .allowNativeFunctions(false)
            .add("<userModule>", module)
            .build())
        .build();

    // compile the module
    Runtime runtime = TweakFlow.compile(loadPath, "<userModule>");
    // get user module from runtime
    return runtime
        .getModules().get(runtime.unitKey("<userModule>"));
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
  public void updates_a_variable() throws Exception {

    String module = "library lib {" +
        "provided long a; " +
        "f: (x) -> x+a" +
        "}";
    Runtime.Module m = compileModule(module);

    // initially
    // a: 0
    Runtime.Var a = m.getLibrary("lib").getVar("a");
    a.update(Values.make(0L));

    Runtime.Var f = m.getLibrary("lib").getVar("f");
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

}
