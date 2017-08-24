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

import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.Parser;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.runtime.RuntimeInspector;
import com.twineworks.tweakflow.repl.ReplState;
import com.twineworks.tweakflow.repl.console.TextTerminal;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class InspectCommand implements Command {

  public static void supplyParser(Subparsers subparsers) {
    Subparser subparser = subparsers.addParser("\\inspect", false)
        .aliases("\\i")
        .defaultHelp(false)
        .help("inspect memory space")
        .setDefault("command", new InspectCommand());

    subparser.addArgument("-f", "--function-definitions")
        .help("print function definitions")
        .setDefault(false)
        .action(Arguments.storeConst())
        .setConst(true)
        .required(false);

    subparser.addArgument("space")
        .setDefault("")
        .help("reference memory space to inspect\n" +
            "  - omit to inspect interactive space\n"+
            "  - pass :: to inspect module space\n" +
            "  - pass $ to inspect global space\n" +
            "  - pass / to inspect unit space\n" +
            "  - pass * to inspect export space\n" +
            "  - pass a reference to inspect memory contents")
        .nargs("?")
        .type(String.class);
  }

  @Override
  public ReplState perform(Namespace args, String input, TextTerminal terminal, ReplState state) {

    String spaceRef = args.getString("space");
    boolean expandFunctions = args.getBoolean("function_definitions");

    if (spaceRef.isEmpty()){
      terminal.print(RuntimeInspector.inspect(state.getInteractiveSection(), expandFunctions));
    }
    else if (spaceRef.equals("$")){
      terminal.print(RuntimeInspector.inspect(state.getGlobals(), expandFunctions));
    }
    else if (spaceRef.equals("::")){
      terminal.print(RuntimeInspector.inspect(state.getMainModule(), expandFunctions));
    }
    else if (spaceRef.equals("/")){
      terminal.print(RuntimeInspector.inspect(state.getUnits(), expandFunctions));
    }
    else if (spaceRef.equals("*")){
      terminal.print(RuntimeInspector.inspect(state.getExports(), expandFunctions));
    }
    else {

      ParseUnit parseUnit  = new MemoryLocation.Builder()
          .add("<prompt>", spaceRef).build()
          .getParseUnit("<prompt>");

      ParseResult parseResult = new Parser(parseUnit).parseInteractiveInput();

      if (parseResult.isError()){
        terminal.println(parseResult.getException().getDigestMessage());
      }
      // input was a reference?
      else if (parseResult.getNode() instanceof ReferenceNode){
        ReferenceNode node = (ReferenceNode) parseResult.getNode();
        try {
          terminal.print(RuntimeInspector.inspect(state.getInteractiveSection().resolve(node), expandFunctions));
        } catch (LangException e){
          terminal.println(e.getDigestMessage());
        }
      }
      else {
        terminal.println("not a reference: "+spaceRef);
      }

    }

    return state;

  }
}
