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

package com.twineworks.tweakflow.repl.commands;

import com.twineworks.tweakflow.lang.interpreter.EvaluationResult;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.repl.ReplState;
import com.twineworks.tweakflow.repl.console.TextTerminal;

public class ExpressionCommand {

  public ReplState perform(String line, TextTerminal terminal, ReplState state, boolean measure) {

    ReplState expressionState = state.copy();
    expressionState.setPromptInput(line);

    long started = System.currentTimeMillis();
    expressionState.evaluate();
    long ended = System.currentTimeMillis();
    long totalDuration = ended-started;

    EvaluationResult evaluationResult = expressionState.getEvaluationResult();

    if (evaluationResult.isError()){
      terminal.println(evaluationResult.getException().getDigestMessage());
      if (measure){
        printDuration(terminal, expressionState.getLoadDurationMillis(), expressionState.getAnalysisDurationMillis(), totalDuration);
      }
      return state;
    }
    else{
      expressionState.setPromptInput(null);
      Cell interactiveSpace = expressionState.getInteractiveSpace();
      terminal.println(
          ValueInspector.inspect(interactiveSpace.getCells().gets(state.getPromptVarName()).getValue())
      );
      if (measure){
        printDuration(terminal, expressionState.getLoadDurationMillis(), expressionState.getAnalysisDurationMillis(), totalDuration);
      }
      return expressionState;
    }

  }



  private void printDuration(TextTerminal terminal, long loadDurationMillis, long analysisDurationMillis, long totalDurationMillis) {
    long evalDurationMillis = totalDurationMillis-(loadDurationMillis+analysisDurationMillis);
    //terminal.println(String.format("load: %.2fs analysis: %.2fs eval: %.2fs total: %.2fs",  loadDurationMillis/1000.0, analysisDurationMillis/1000.0, evalDurationMillis/1000.0, totalDurationMillis/1000.0));
    terminal.println(String.format("load: %dms analysis: %dms eval: %dms total: %dms",  loadDurationMillis, analysisDurationMillis, evalDurationMillis, totalDurationMillis));
  }

}
