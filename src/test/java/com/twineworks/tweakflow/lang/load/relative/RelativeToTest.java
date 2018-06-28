/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Twineworks GmbH
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

package com.twineworks.tweakflow.lang.load.relative;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.interpreter.DefaultDebugHandler;
import com.twineworks.tweakflow.lang.load.loadpath.FilesystemLocation;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class RelativeToTest {

  private static String assetDir = "src/test/resources/fixtures/tweakflow/loading/relative";


  public static Runtime compile(Map<String, String> modules){

    // create a memory location with all modules
    MemoryLocation.Builder memLocationBuilder = new MemoryLocation.Builder()
        .allowNativeFunctions(false);

    for (String name : modules.keySet()) {
      memLocationBuilder.add(name, modules.get(name));
    }

    MemoryLocation memoryLocation = memLocationBuilder.build();

    // the file system location to resolve against
    FilesystemLocation fsLocation = new FilesystemLocation.Builder(Paths.get(assetDir)).build();

    // place standard library and user code on load path
    LoadPath loadPath = new LoadPath.Builder()
        .addStdLocation()
        .add(memoryLocation)
        .add(fsLocation)
        .withRelativeResolver(new RelativeTo("./base_asset.ext", fsLocation))
        .build();

    // compile the modules
    return TweakFlow.compile(loadPath, new ArrayList<>(modules.keySet()), new DefaultDebugHandler());

  }

  @Test
  public void resolves_relative_import_against_fixed_location() {

    // make an in-memory module that imports an on-disk module through relative notation
    HashMap<String, String> modules = new HashMap<>();
    modules.put("test", "import util from './module.tf';");
    Runtime runtime = compile(modules);
    Runtime.Var var = (Runtime.Var) runtime.getModules().get("test")
        .getChildren().get("util")
        .getChildren().get("foo");
    var.evaluate();
    assertThat(var.getValue().string()).isEqualTo("data");
  }

}