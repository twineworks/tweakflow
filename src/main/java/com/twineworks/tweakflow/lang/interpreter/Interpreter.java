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

package com.twineworks.tweakflow.lang.interpreter;

import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpaceBuilder;
import com.twineworks.tweakflow.lang.analysis.AnalysisSet;
import com.twineworks.tweakflow.lang.errors.LangException;

public class Interpreter {

  private final RuntimeSet runtimeSet;
  private final DebugHandler debugHandler;

  public Interpreter(AnalysisSet analysisSet) {
    debugHandler = new DefaultDebugHandler();
    runtimeSet = new RuntimeSet(analysisSet);
  }

  public Interpreter(AnalysisSet analysisSet, DebugHandler debugHandler) {
    runtimeSet = new RuntimeSet(analysisSet);
    this.debugHandler = debugHandler;
  }

  public EvaluationResult evaluate(){
    EvaluationContext context = new EvaluationContext(debugHandler);
    try {
      MemorySpaceBuilder.buildRuntimeSpace(runtimeSet);
      Evaluator.evaluateSpace(runtimeSet.getGlobalMemorySpace().getUnitSpace(), context);
      return EvaluationResult.ok(runtimeSet);
    }
    catch (Throwable e){
      return EvaluationResult.error(LangException.wrap(e));
    }
  }

}
