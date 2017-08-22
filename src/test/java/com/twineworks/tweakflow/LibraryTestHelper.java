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

package com.twineworks.tweakflow;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.analysis.Analysis;
import com.twineworks.tweakflow.lang.analysis.AnalysisResult;
import com.twineworks.tweakflow.lang.load.Loader;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.interpreter.EvaluationResult;
import com.twineworks.tweakflow.interpreter.Interpreter;
import com.twineworks.tweakflow.interpreter.memory.Cell;
import com.twineworks.tweakflow.interpreter.memory.MemorySpace;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class LibraryTestHelper {

  public static void assertSpecModule(String path){

    LoadPath loadPath = new LoadPath();
    loadPath.getLocations().add(new ResourceLocation.Builder().path(Paths.get("com/twineworks/tweakflow/std")).build());
    loadPath.getLocations().add(new ResourceLocation.Builder().build());

    Loader loader = new Loader(loadPath);

    List<String> paths = Collections.singletonList(path);
    AnalysisResult analysisResult = Analysis.analyze(paths, loader);

    if (analysisResult.isError()){
      analysisResult.getException().printDetails();
    }

    assertThat(analysisResult.isSuccess()).isTrue();

    Interpreter interpreter = new Interpreter(analysisResult.getAnalysisSet());
    EvaluationResult evaluate = interpreter.evaluate();

    if (evaluate.isError()){
      evaluate.getException().printDetails();
    }

    assertThat(evaluate.isSuccess()).isTrue();

    // find all libraries that have names ending in _spec, and ensure all their cells are boolean true
    MemorySpace moduleSpace = evaluate.getRuntimeSet().getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    ConstShapeMap<Cell> libCells = moduleSpace.getCells();
    for (ShapeKey s : libCells.keySet()) {
      if (!s.sym.endsWith("_spec")) continue;
      Cell lib = libCells.get(s);
      ConstShapeMap<Cell> vars = lib.getCells();
      for (ShapeKey varName : vars.keySet()) {
        Value value = vars.get(varName).getValue();
        if (value != Values.TRUE){
          fail(s+"."+varName+" is:\n"+ ValueInspector.inspect(value)+"\nexpected:\ntrue");
        }
      }

    }

  }


}
