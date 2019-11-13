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

package com.twineworks.tweakflow.lang.runtime;

import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.util.VarTable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.StrictAssertions.assertThat;

class RuntimeTest {

  @Test
  void can_create_independent_copy() {

    VarTable table = new VarTable.Builder()
        .setPrologue("library row { \n" +
            "  provided a;\n" +
            "}\n")
        .setVarLibraryName("lib")
        .addVar("p", "row.a*row.a")
        .build();

    Runtime orig = table.compile();
    Runtime copy = orig.copy();

    Runtime.Var origVarA = orig.getModules()
        .get(table.getModulePath())
        .getLibrary("row")
        .getVar("a");

    Runtime.Var copyVarA = copy.getModules()
        .get(table.getModulePath())
        .getLibrary("row")
        .getVar("a");

    Runtime.Var origVarP = orig.getModules()
        .get(table.getModulePath())
        .getLibrary(table.getVarLibraryName())
        .getVar("p");


    Runtime.Var copyVarP = copy.getModules()
        .get(table.getModulePath())
        .getLibrary(table.getVarLibraryName())
        .getVar("p");

    // nothing evaluated yet
    assertThat(origVarP.getValue()).isSameAs(null);
    assertThat(copyVarP.getValue()).isSameAs(null);

    // pass in variables in original runtime
    orig.updateVars(origVarA, Values.make(12));

    // original runtime has a value, copy has not evaluated yet
    assertThat(origVarP.getValue()).isSameAs(Values.make(144));
    assertThat(copyVarP.getValue()).isSameAs(null);

    // pass in variables in copy runtime
    copy.updateVars(copyVarA, Values.make(7));

    // original runtime maintains old value, copy gets a new value
    assertThat(origVarP.getValue()).isSameAs(Values.make(144));
    assertThat(copyVarP.getValue()).isSameAs(Values.make(49));


  }

  @Test
  void can_access_copies_concurrently() throws Throwable {

    VarTable table = new VarTable.Builder()
        .setPrologue("import math from 'std';\n\n" +
            "library row { \n" +
            "  provided a;\n" +
            "}\n")
        .setVarLibraryName("lib")
        .addVar("p", "row.a*row.a")
        .build();

//    long startCompile = System.currentTimeMillis();
    Runtime src = table.compile();
//    System.out.println("time to compile: "+(System.currentTimeMillis()-startCompile)+"ms");

    // amount of threads, each evaluating against a a loop of provided values
    int threadCount = 50;

    ArrayList<Thread> threads = new ArrayList<>();

    for(int t=0;t<threadCount;t++){

      // create and start thread
      int seed = t;

      Thread thread = new Thread(() -> {

//        long startClone = System.currentTimeMillis();
        Runtime runtime = src.copy();
//        System.out.println("time to clone: "+(System.currentTimeMillis()-startClone)+"ms");

        Runtime.Var a = runtime.getModules()
            .get(table.getModulePath())
            .getLibrary("row")
            .getVar("a");

        Runtime.Var p = runtime.getModules()
            .get(table.getModulePath())
            .getLibrary(table.getVarLibraryName())
            .getVar("p");

        for (int i=0;i<10_000;i++){
          int n = seed+i;
          runtime.updateVars(a, Values.make(n));
          assertThat(p.getValue()).isEqualTo(Values.make(n*n));
        }
      });

      thread.setUncaughtExceptionHandler((x, e) -> e.printStackTrace());

      threads.add(thread);

      // start immediately
      thread.start();

    }

    // wait for all threads
    for (Thread thread : threads) {
      thread.join();
    }

  }
}