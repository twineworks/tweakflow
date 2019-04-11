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

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.structure.EmptyNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.Parser;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.repl.ReplState;
import com.twineworks.tweakflow.repl.console.TextTerminal;

public class InteractiveInputCommand {

  public ReplState perform(TextTerminal terminal, ReplState state, boolean measure) {

    // try parsing line as interactive input
    String input = state.getInput();
    ParseUnit parseUnit = new MemoryLocation.Builder().add("<prompt>", input).build().getParseUnit("<prompt>");
    ParseResult parseResult = new Parser(parseUnit).parseInteractiveInput();

    if (parseResult.isError()){
      terminal.println(parseResult.getException().getDigestMessage());
      return state;
    }
    // input was an expression?
    else if (parseResult.getNode() instanceof ExpressionNode){
      return new ExpressionCommand().perform(input, terminal, state, measure);
    }
    // input was a var definition?
    else if (parseResult.getNode() instanceof VarDefNode){
      VarDefNode varDefNode = (VarDefNode) parseResult.getNode();
      String varName = varDefNode.getSymbolName();
      // if there was a trailing ; remove it
      input = varDefNode.getSourceInfo().getSourceCode();
      return new VarDefCommand().perform(varName, input, terminal, state, measure);
    }
    // nothing but a comment and whitespace entered?
    else if (parseResult.getNode() instanceof EmptyNode){
      return state;
    }
    else {
      Node node = parseResult.getNode();
      if (node != null){
        terminal.println("unexpected input result: "+parseResult.getNode().getClass().getName());
      }
      else{
        terminal.println("unexpected input result: null");
      }
      return state;
    }

  }

}
