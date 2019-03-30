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
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Arity1CallSite;
import com.twineworks.tweakflow.lang.values.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModuleEvaluation {

  private static Runtime compile(Map<String, String> modules){

    // create a memory location with all modules
    MemoryLocation.Builder memLocationBuilder = new MemoryLocation.Builder()
        .allowNativeFunctions(false);

    for (String name : modules.keySet()) {
      memLocationBuilder.add(name, modules.get(name));
    }

    MemoryLocation memoryLocation = memLocationBuilder.build();

    // place standard library and user code on load path
    LoadPath loadPath = new LoadPath.Builder()
        .addStdLocation()
        .add(memoryLocation)
        .build();

    // compile the modules
    return TweakFlow.compile(loadPath, new ArrayList<>(modules.keySet()));

  }

  public static void main(String[] args) {

    // a utility module
    String acmeModule = "import regex, strings from 'std'\n" +
        "export library product_codes {\n" +
        "  normalize: (string pn) ->\n" +
        "  ->> (pn)\n" +
        "      # remove whitespace\n" +
        "      (x) -> regex.replacing('\\s', \"\")(x),\n" +
        "      # remove any dashes\n" +
        "      (x) -> strings.replace(x, \"-\", \"\"),\n" +
        "      # split to a list of blocks of up to 4 chars\n" +
        "      (x) -> regex.splitting('(?<=\\G.{4})')(x),\n" +
        "      # place dashes between blocks converting to single string\n" +
        "      (xs) -> strings.join(xs, \"-\"),\n" +
        "      # upper case all characters\n" +
        "      strings.upper_case" +
        "}";

    // the main module
    // imports from the utility module
    // its main.main variable is a function that converts product codes into a standard format
    String mainModule = "import product_codes from 'acme'\n" +
        "library main {\n" +
        "  main: (string x) -> product_codes.normalize(x)\n" +
        "}";

    // compile the modules
    HashMap<String, String> modules = new HashMap<>();
    modules.put("acme", acmeModule);
    modules.put("main", mainModule);

    Runtime runtime = compile(modules);

    // get a handle to main.main
    Runtime.Module module = runtime.getModules().get(runtime.unitKey("main"));
    Runtime.Var main = module.getLibrary("main").getVar("main");

    // evaluate, so the function becomes available
    main.evaluate();

    // get a callsite to the function
    Arity1CallSite f = main.arity1CallSite();

    // and keep calling it to normalize product codes
    // UUIDs stand in for simple to generate 'product codes'

    for (int i=0;i<100;i++) {
      String code = UUID.randomUUID().toString().substring(0, 12);
      System.out.println(code +" -> "+f.call(Values.make(code)));
    }

  }

}
