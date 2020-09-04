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

import com.twineworks.tweakflow.lang.errors.LangException;

import java.util.ArrayList;
import java.util.List;

public class AnalysisResult {

  private final LangException exception;
  private final AnalysisSet analysisSet;
  private final long analysisDurationMillis;
  private final ArrayList<LangException> recoveryErrors;
  private final boolean isRecovery;


  static public AnalysisResult ok(AnalysisSet analysisSet, long analysisDurationMillis){
    return new AnalysisResult(false, null, null, analysisSet, analysisDurationMillis);
  }

  static public AnalysisResult error(LangException exception, long analysisDurationMillis){
    return new AnalysisResult(false, null, exception, null, analysisDurationMillis);
  }

  static public AnalysisResult recovery(ArrayList<LangException> recoveryErrors, AnalysisSet analysisSet, long analysisDurationMillis){
    return new AnalysisResult(true, recoveryErrors, null, analysisSet, analysisDurationMillis);
  }

  private AnalysisResult(boolean isRecovery, List<LangException> recoveryErrors, LangException exception, AnalysisSet analysisSet, long analysisDurationMillis){
    this.isRecovery = isRecovery;
    this.recoveryErrors = (recoveryErrors != null) ? new ArrayList<>(recoveryErrors) : null;
    this.analysisSet = analysisSet;
    this.exception = exception;
    this.analysisDurationMillis = analysisDurationMillis;
  }

  public boolean isSuccess() {
    if (isRecovery){
      return analysisSet != null;
    }
    return exception == null && analysisSet != null;
  }

  public boolean isError(){
    return !isSuccess();
  }

  public LangException getException() {
    if (isRecovery){
      if (recoveryErrors != null && recoveryErrors.size() > 0){
        return recoveryErrors.get(0);
      }
      return null;
    }
    else{
      return exception;
    }
  }

  public ArrayList<LangException> getRecoveryErrors() {
    return recoveryErrors;
  }

  public boolean hasRecoveryErrors() {
    return recoveryErrors != null && recoveryErrors.size() > 0;
  }

  public boolean isRecovery() {
    return isRecovery;
  }

  public AnalysisSet getAnalysisSet() {
    return analysisSet;
  }

  public long getAnalysisDurationMillis() {
    return analysisDurationMillis;
  }

}
