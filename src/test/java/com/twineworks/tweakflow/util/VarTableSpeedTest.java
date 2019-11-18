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

package com.twineworks.tweakflow.util;

import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


class VarTableSpeedTest {

  private ConcurrentHashMap<String, ParseResult> parseCache = new ConcurrentHashMap<>();

  @Test
  void test_compilation_time() {

    // populate the cache, and warm-up the JVM jit
    for (int i = 0; i < 100; i++) {
      run();
    }

    // measure 100 compilations utilizing the parse cache
    final long startNanos = System.nanoTime();

    for (int i = 0; i < 100; i++) {
      run();
    }

    final long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

    // expected timing on desktop-range hardware is [300 - 800] ms
    // cloud CI workers clock in at [700-1000] ms
    // if caching does not work, the timing is in the range of [6000 - 14000] ms
    // System.err.println("time: "+durationMillis);
    assertThat(durationMillis).as("cached parse tree compilation time").isLessThan(3_000);

  }

  private void run() {
    final VarTable table = new VarTable.Builder()
        .setPrologue("import" +
            " data as _data " +
            ", " +
            " math as _math " +
            ", " +
            " strings as _strings " +
            " from 'std';\n" +
            " alias input_table.field as field;\n" +
            " library input_table {provided field;}")
        .addVar("_result", "field('x')")
        // caches parse trees from locations that have caching enabled
        // the location of 'std' has caching enabled, so importing std should be cheap
        // after it lands in the cache
        .setParseCache(parseCache)
        // allows caching the parse result of the module the var table itself generates
        // this feature is only relevant if the generated module is of significant size
        // and makes little difference for small modules
        .cacheModulePath(true)
        // the name of the module this table generates,
        // this name is used as the key in the parse cache, so if caching is enabled
        // only identical modules should share the same path name
        .setModulePath("module.tf")
        .build();

    final Runtime compiledRuntime = table.compile();
  }

}