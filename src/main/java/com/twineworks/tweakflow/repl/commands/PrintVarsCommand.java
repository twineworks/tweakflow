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

package com.twineworks.tweakflow.repl.commands;

import com.twineworks.tweakflow.repl.ReplState;
import com.twineworks.tweakflow.repl.console.TextTerminal;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.util.Map;

public class PrintVarsCommand implements Command {

  public static void supplyParser(Subparsers subparsers) {
    Subparser subparser = subparsers.addParser("\\vars", false);
    subparser.aliases("\\v");
    subparser.defaultHelp(false);
    subparser.help("print interactive variable definitions");
    subparser.setDefault("command", new PrintVarsCommand());
  }

  private String getTitleLine(ReplState state){

    Map<String, String> varDefs = state.getVarDefs();

    if (varDefs.isEmpty()){
      return "no interactive variables defined";
    }
    else {
      if (varDefs.size() == 1) {
        return "1 interactive variable defined";
      } else {
        return varDefs.size() + " interactive variables defined";
      }
    }

  }

  @Override
  public ReplState perform(Namespace args, String input, TextTerminal terminal, ReplState state) {

    Map<String, String> varDefs = state.getVarDefs();

    terminal.println(getTitleLine(state));

    for (String s : varDefs.keySet()) {
     terminal.println(varDefs.get(s));
    }

    return state;
  }
}
