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
import com.twineworks.tweakflow.lang.load.Loader;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Fail.fail;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class ResourceLocationTest {

  private static String assetDir = "fixtures/tweakflow/loading/on_path";

  @Test
  public void finds_on_path_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.pathExists("module.tf")).isTrue();
  }

  @Test
  public void finds_on_path_relative_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.pathExists("./module.tf")).isTrue();
  }

  @Test
  public void finds_on_path_default_extension_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.pathExists("module")).isTrue();
  }


  @Test
  public void finds_on_path_custom_extension_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder()
        .path(Paths.get(assetDir))
        .defaultExtension(".ext")
        .build();
    assertThat(loc.pathExists("custom")).isTrue();
  }

  @Test
  public void finds_on_path_back_relative_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.pathExists("../on_path/module.tf")).isTrue();
  }

  @Test
  public void refuses_off_path_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.pathExists("../off_path/module.tf")).isFalse();
  }

  @Test
  public void finds_unit_on_path_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.getParseUnit("module.tf")).isNotNull();
  }

  @Test
  public void finds_unit_on_path_relative_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.getParseUnit("./module.tf")).isNotNull();
  }

  @Test
  public void finds_unit_on_path_default_extension_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.getParseUnit("module")).isNotNull();
  }


  @Test
  public void finds_unit_on_path_custom_extension_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).defaultExtension(".ext").build();
    assertThat(loc.getParseUnit("custom")).isNotNull();
  }

  @Test
  public void finds_unit_on_path_back_relative_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    assertThat(loc.getParseUnit("../on_path/module.tf")).isNotNull();
  }

  @Test(expected = LangException.class)
  public void refuses_unit_off_path_file() throws Exception {
    ResourceLocation loc = new ResourceLocation.Builder().path(Paths.get(assetDir)).build();
    loc.getParseUnit("../off_path/module.tf");
  }

  @Test
  public void allowing_native_evaluates_native_function() throws Exception {
    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation(Paths.get(assetDir), true, ".tf"))
        .build();

    TweakFlowRuntime runtime = TweakFlow.evaluate(new Loader(loadPath), "native.tf");
    TweakFlowRuntime.VarHandle varHandle = runtime.createVarHandle("native.tf", "native", "yes");
    EvaluatorUserCallContext callContext = runtime.createCallContext(varHandle);
    assertThat(callContext.call(varHandle.getValue())).isSameAs(Values.TRUE);
  }

  @Test
  public void disallowing_native_throws_evaluating_native_function() throws Exception {
    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation(Paths.get(assetDir),false, ".tf"))
        .build();

    try {
      TweakFlow.evaluate(new Loader(loadPath), "native.tf");
    } catch(LangException e){
      assertThat(e.getCode()).isEqualTo(LangError.NATIVE_CODE_RESTRICTED);
      return;
    }

    fail("should have thrown");
  }

}