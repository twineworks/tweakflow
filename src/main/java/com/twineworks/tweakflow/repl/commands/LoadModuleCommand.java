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

import com.twineworks.tweakflow.repl.ReplState;
import com.twineworks.tweakflow.repl.console.TextTerminal;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.util.List;

public class LoadModuleCommand implements Command {

  public static void supplyParser(Subparsers subparsers) {
    Subparser subparser = subparsers.addParser("\\load", false);
    subparser.aliases("\\l");
    subparser.defaultHelp(false);
    subparser.help("load a set of modules, removes interactive variables");
    subparser.setDefault("command", new LoadModuleCommand());

    subparser.addArgument("module")
        .help("modules to load, first given module's scope is entered, relative paths are searched in load_path")
        .nargs("+")
        .type(String.class);

  }

  @Override
  public ReplState perform(Namespace args, String input, TextTerminal terminal, ReplState state) {

    ReplState loadState = state.copy();
    List<String> paths = args.getList("module");

    loadState.getVarDefs().clear();
    loadState.setModulePaths(paths);
    loadState.evaluate();
    if (loadState.getEvaluationResult().isSuccess()){
      return loadState;
    }
    else{
      terminal.println("ERROR: "+loadState.getEvaluationResult().getException().getDigestMessage());
      return state;
    }

  }
}
