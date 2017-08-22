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

import com.twineworks.tweakflow.interpreter.Evaluator;
import com.twineworks.tweakflow.interpreter.memory.Cell;
import com.twineworks.tweakflow.interpreter.memory.Spaces;
import com.twineworks.tweakflow.lang.ast.MetaDataNode;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.Parser;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.repl.ReplState;
import com.twineworks.tweakflow.repl.console.TextTerminal;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class DocCommand implements Command {

  public static void supplyParser(Subparsers subparsers) {
    Subparser subparser = subparsers.addParser("\\doc", false);
    subparser.aliases("\\d");
    subparser.help("print doc");
    subparser.defaultHelp(false);
    subparser.setDefault("command", new DocCommand());

    subparser.addArgument("reference")
        .setDefault("")
        .help("item to extract documentation from\n" +
            "  - omit for current module\n" +
            "  - pass a reference to extract doc from referenced item")
        .nargs("?")
        .type(String.class);

  }

  private void printDoc(Node node, TextTerminal terminal){

    Value doc = Values.NIL;

    if (node instanceof MetaDataNode){
      try {
        doc = Evaluator.evaluateDocExpression((MetaDataNode) node);
      }
      catch(LangException e){
        terminal.println(e.getDigestMessage());
        return;
      }
    }

    if (doc == Values.NIL){
      terminal.println("no documentation available");
    }
    else{
      if (doc.isString()){
        terminal.println(doc.string());
      }
      else{
        terminal.println(ValueInspector.inspect(doc));
      }
    }

  }

  @Override
  public ReplState perform(Namespace args, String input, TextTerminal terminal, ReplState state) {

    String spaceRef = args.getString("reference");

    if (spaceRef.isEmpty()){
      SymbolNode targetNode = state.getModuleSpace().getSymbol().getTargetNode();
      // this should always be a module node
      if (targetNode instanceof MetaDataNode){
        printDoc(targetNode, terminal);
      }
    }
    else {

      ParseUnit parseUnit = new MemoryLocation.Builder().add("<prompt>", spaceRef).build().getParseUnit("<prompt>");
      ParseResult parseResult = new Parser(parseUnit).parseInteractiveInput();

      if (parseResult.isError()){
        terminal.println(parseResult.getException().getDigestMessage());
      }
      // input was a reference?
      else if (parseResult.getNode() instanceof ReferenceNode){
        ReferenceNode node = (ReferenceNode) parseResult.getNode();
        try {
          Cell resolved = Spaces.resolve(node, state.getInteractiveSpace());
          printDoc(resolved.getSymbol().getTargetNode(), terminal);
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
