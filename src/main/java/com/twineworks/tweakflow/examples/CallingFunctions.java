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

package com.twineworks.tweakflow.examples;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.interpreter.CallContext;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Arity1CallSite;
import com.twineworks.tweakflow.lang.values.DateTimeValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.time.ZonedDateTime;

public class CallingFunctions {

  private static Runtime.Module compileModule(String module){

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

  public static void main(String[] args) {

    // a module where time_format.format is a formatting function
    String module = "import time from 'std';\n" +
        "library time_format {\n" +
        "  format: time.formatter(\"cccc, d MMMM uuuu HH:mm:ss 'in' VV\");\n" +
        "}";

    // compile the module
    Runtime.Module m = compileModule(module);
    // get a handle on time_format.format which evaluated to a function
    Runtime.Var format = m.getLibrary("time_format").getVar("format");

    // evaluate the module so vars get evaluated
    m.evaluate();

    // calling a function: variant 1, use var call
    // get now() as per local timezone
    Value now = Values.make(new DateTimeValue(ZonedDateTime.now()));

    // print the result of calling format with now as argument
    System.out.println("var call: "+format.call(now).string());

    // calling a function: variant 2, use var call site
    // call format in a loop using a call site
    Arity1CallSite callSite = format.arity1CallSite();

    for(int i=0;i<3;i++){
      System.out.println("var callsite: "+callSite.call(now).string());
    }

    // calling a function: variant 3, use runtime call context
    CallContext callContext = m.getRuntime().createCallContext();
    System.out.println("runtime call context: "+ callContext.call(format.getValue(), now).string());


  }

}
