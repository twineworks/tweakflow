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

package com.twineworks.tweakflow.lang.load.loadpath;

import com.twineworks.tweakflow.interpreter.EvaluatorUserCallContext;
import com.twineworks.tweakflow.interpreter.runtime.TweakFlowRuntime;
import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Fail.fail;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class MemoryLocationTest {

  private static String assetDir = "src/test/resources/fixtures/tweakflow/loading/on_path";

  private String readFile(String onPathFile) throws Exception {
    return new String(Files.readAllBytes(Paths.get(assetDir+"/"+onPathFile)), StandardCharsets.UTF_8);
  }

  @Test
  public void allowing_native_evaluates_native_function() throws Exception {
    LoadPath loadPath = new LoadPath.Builder()
        .add(new MemoryLocation.Builder()
            .allowNativeFunctions(true)
            .add("native.tf", readFile("native.tf"))
            .build())
        .build();

    TweakFlowRuntime runtime = TweakFlow.evaluate(loadPath, "native.tf");
    TweakFlowRuntime.VarHandle varHandle = runtime.createVarHandle("native.tf", "native", "yes");
    EvaluatorUserCallContext callContext = runtime.createCallContext(varHandle);
    assertThat(callContext.call(varHandle.getValue())).isSameAs(Values.TRUE);
  }

  @Test
  public void disallowing_native_throws_evaluating_native_function() throws Exception {
    LoadPath loadPath = new LoadPath.Builder()
        .add(new MemoryLocation.Builder()
            .allowNativeFunctions(false)
            .add("native.tf", readFile("native.tf"))
            .build())
        .build();

    try {
      TweakFlow.evaluate(loadPath, "native.tf");
    } catch(LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.NATIVE_CODE_RESTRICTED);
      return;
    }

    fail("should have thrown");
  }

}