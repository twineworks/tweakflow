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

package com.twineworks.tweakflow.lang.analysis;

import com.twineworks.tweakflow.lang.analysis.constants.ConstantOpsFolding;
import com.twineworks.tweakflow.lang.analysis.ops.OpBuilder;
import com.twineworks.tweakflow.lang.analysis.ops.OpSpecialization;
import com.twineworks.tweakflow.lang.analysis.references.ClosureAnalysis;
import com.twineworks.tweakflow.lang.analysis.references.DependencyVerification;
import com.twineworks.tweakflow.lang.analysis.references.MetaDataAnalysis;
import com.twineworks.tweakflow.lang.analysis.scope.ExpressionResolver;
import com.twineworks.tweakflow.lang.analysis.scope.Linker;
import com.twineworks.tweakflow.lang.analysis.scope.ScopeBuilder;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.Loader;
import com.twineworks.tweakflow.lang.load.ParallelLoader;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Analysis {

  private static void buildScope(AnalysisSet analysisSet, boolean recovery) {
    ScopeBuilder.buildScope(analysisSet, recovery);
  }

  private static void resolveReferences(AnalysisSet analysisSet, boolean recovery){
    ExpressionResolver.resolve(analysisSet, recovery);
  }

  private static void link (AnalysisSet analysisSet){
    Linker.link(analysisSet);
  }

  private static void verifyDependencies (AnalysisSet analysisSet, boolean recovery){
    DependencyVerification.verify(analysisSet, recovery);
  }

  private static void analyzeClosures (AnalysisSet analysisSet){
    ClosureAnalysis.analyze(analysisSet);
  }

  private static void analyzeMetaData (AnalysisSet analysisSet, boolean recovery){
    MetaDataAnalysis.analyze(analysisSet, recovery);
  }

  private static void foldConstantOps(AnalysisSet analysisSet) {
    ConstantOpsFolding.analyze(analysisSet);
  }

  private static void buildOps(AnalysisSet analysisSet) {
    OpBuilder.analyze(analysisSet);
  }

  private static void specializeOps(AnalysisSet analysisSet) {
    OpSpecialization.analyze(analysisSet);
  }

  public static AnalysisResult analyze(List<String> paths, LoadPath loadPath){
    return analyze(paths, loadPath, false);
  }

  public static AnalysisResult analyze(List<String> paths, LoadPath loadPath, boolean multiThreaded){
    long start = System.currentTimeMillis();
    try {
      AnalysisSet analysisSet = new AnalysisSet(loadPath);
      if (multiThreaded){
        ParallelLoader pl = new ParallelLoader(loadPath, false);
        analysisSet.getUnits().putAll(pl.load(paths));
      }
      else {
        Loader.load(loadPath, paths, analysisSet.getUnits(), true, false, null);
      }
      return analyze(analysisSet, start);
    } catch (RuntimeException e){
      long end = System.currentTimeMillis();
      return AnalysisResult.error(LangException.wrap(e), end-start);
    }
  }

  public static AnalysisResult recoveryAnalysis(List<String> paths, LoadPath loadPath, boolean multiThreaded){
    long start = System.currentTimeMillis();
    try {
      List<LangException> recoveryErrors;
      AnalysisSet analysisSet = new AnalysisSet(loadPath);
      if (multiThreaded){
        ParallelLoader pl = new ParallelLoader(loadPath, true);
        analysisSet.getUnits().putAll(pl.load(paths));
        analysisSet.getRecoveryErrors().addAll(pl.getRecoveryErrors());
      }
      else {
        recoveryErrors = new ArrayList<>();
        Loader.load(loadPath, paths, analysisSet.getUnits(), true, true, recoveryErrors);
        analysisSet.getRecoveryErrors().addAll(recoveryErrors);
      }

      return recoveryAnalysis(analysisSet, start);
    } catch (RuntimeException e){
      long end = System.currentTimeMillis();
      return AnalysisResult.error(LangException.wrap(e), end-start);
    }
  }

  public static AnalysisResult analyze(AnalysisSet analysisSet, long startMillis){
    long start = startMillis;
    try {
      analyzeMetaData(analysisSet, false);
      buildScope(analysisSet, false);
      link(analysisSet);
      resolveReferences(analysisSet, false);
      analyzeClosures(analysisSet);
      verifyDependencies(analysisSet, false);
      buildOps(analysisSet);
      foldConstantOps(analysisSet);
      specializeOps(analysisSet);

      // mark module space compiled
      for (AnalysisUnit spaceUnit : analysisSet.getUnits().values()) {
        spaceUnit.setStage(AnalysisStage.COMPILED);
      }
      long end = System.currentTimeMillis();
      return AnalysisResult.ok(analysisSet, end-start);

    } catch (RuntimeException e){
      long end = System.currentTimeMillis();
      return AnalysisResult.error(LangException.wrap(e), end-start);
    }
  }

  public static AnalysisResult recoveryAnalysis(AnalysisSet analysisSet, long startMillis){
    long start = startMillis;
    try {
      analyzeMetaData(analysisSet, true);
      buildScope(analysisSet, true);
      link(analysisSet);
      resolveReferences(analysisSet, true);
      analyzeClosures(analysisSet);
      verifyDependencies(analysisSet, true);
//      buildOps(analysisSet);
//      foldConstantOps(analysisSet);
//      specializeOps(analysisSet);

      // mark module space compiled
//      for (AnalysisUnit spaceUnit : analysisSet.getUnits().values()) {
//        spaceUnit.setStage(AnalysisStage.COMPILED);
//      }
      long end = System.currentTimeMillis();
      return AnalysisResult.recovery(analysisSet.getRecoveryErrors(), analysisSet, end-start);

    } catch (RuntimeException e){
      long end = System.currentTimeMillis();
      return AnalysisResult.error(LangException.wrap(e), end-start);
    }
  }

}
