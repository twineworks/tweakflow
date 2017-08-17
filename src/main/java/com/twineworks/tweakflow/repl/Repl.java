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

package com.twineworks.tweakflow.repl;

import com.twineworks.tweakflow.repl.commands.*;
import com.twineworks.tweakflow.repl.console.ConsoleTextTerminal;
import com.twineworks.tweakflow.repl.console.SystemTextTerminal;
import com.twineworks.tweakflow.repl.console.TextTerminal;
import com.twineworks.tweakflow.repl.util.Tokenizer;
import jline.console.ConsoleReader;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class Repl {

  private static ArgumentParser createMainArgumentParser(){

    ArgumentParser parser = ArgumentParsers.newArgumentParser("itf");

    parser.addArgument("-I", "--load_path")
        .required(false)
        .type(String.class)
        .setDefault(new ArrayList())
        .action(Arguments.append());

    parser.addArgument("module")
        .setDefault(Collections.singletonList(new ReplState().getMainModulePath()))
        .nargs("*")
        .type(String.class);

    return parser;

  }

  private static ArgumentParser createInputParser(){
    ArgumentParser parser = ArgumentParsers.newArgumentParser(">", false, "-");
    Subparsers subparsers = parser.addSubparsers();
    subparsers.title("interactive commands");
    subparsers.description("").metavar("COMMAND");

    QuitCommand.supplyParser(subparsers);
    HelpCommand.supplyParser(subparsers, parser);
    PrintVarsCommand.supplyParser(subparsers);
    EditToggleCommand.supplyParser(subparsers);
    InspectCommand.supplyParser(subparsers);
    PrintLoadPathCommand.supplyParser(subparsers);
    PrintWorkingDirectoryCommand.supplyParser(subparsers);
    LoadModuleCommand.supplyParser(subparsers);
    MetaCommand.supplyParser(subparsers);
    DocCommand.supplyParser(subparsers);
    TimeCommand.supplyParser(subparsers);

    parser.epilog("You can: \n" +
        "  - use one of the commands above\n" +
        "  - enter an expression to evaluate it\n" +
        "  - enter a variable definition of the form\n" +
        "    var_name: expression\n" +
        "    you can then reference the variable in other expressions");

    return parser;
  }

  public static void main(String[] args){

    ArgumentParser parser = createMainArgumentParser();

    ReplState state = new ReplState();

    try {

      Namespace res = parser.parseArgs(args);

      // put load path in state
      List loadPathArgs = (List) res.getAttrs().get("load_path");
      List<String> loadPath = state.getLoadPath();

      if (loadPathArgs.size() == 0){
        // default load path
        loadPath.add(".");
      }
      else{
        // custom load path
        for (Object loadPathArg : loadPathArgs) {
          loadPath.add(loadPathArg.toString());
        }
      }

      // put current module path in state
      List<String> modules = res.getList("module");
      state.setModulePaths(modules);


    } catch (ArgumentParserException e) {
      parser.handleError(e);
      return;
    }

    // enter read-evaluate-print-loop
    if(readEvaluatePrintLoop(state)){
      System.exit(0);
    }
    else{
      System.exit(1);
    }

  }

  private static TextTerminal createTextTerminal(){
    if (System.console() != null){
      try {
        ConsoleTextTerminal consoleTextTerminal = new ConsoleTextTerminal(new ConsoleReader());
        consoleTextTerminal.setUserInterruptHandler((x) -> System.exit(-1));
        return consoleTextTerminal;
      } catch (IOException e) {
        throw new RuntimeException("could not create text terminal");
      }
    }
    else {
      return new SystemTextTerminal();
    }
  }

  private static void printBanner(TextTerminal textTerminal) {
    textTerminal.println("tweakflow interactive shell    \\? for help, \\q to quit");
  }

  private static String prompt(ReplState state){
    String prompt = Paths.get(state.getMainModuleKey()).getFileName().toString();

    if (state.isMultiLine()){
      prompt += "*";
    }
    else {
      prompt += ">";
    }

    prompt += " ";
    return prompt;
  }

  private static boolean readEvaluatePrintLoop(ReplState initialState) {

    TextTerminal textTerminal = createTextTerminal();
    ArgumentParser inputParser = createInputParser();
    ReplState state = initialState;

    printBanner(textTerminal);
    state.evaluate();

    if (state.getEvaluationResult().isError()){
      textTerminal.println(state.getEvaluationResult().getException().getDigestMessage());
      return false;
    }

    while (!state.shouldQuit()) {

      boolean measureEval = false;
      // print prompt, and read input
      String input;

      try{
        input = textTerminal.read(prompt(state));
      } catch (NoSuchElementException e){
        // EOF
        break;
      }

      String trimInput = input.trim();

      // preserve empty lines in multi-line mode
      if (trimInput.isEmpty() && !state.isMultiLine()) {
        continue;
      }
      else if (trimInput.startsWith("\\")){
        // time command?
        if (trimInput.startsWith("\\t ")){
          measureEval = true;
          input = trimInput.substring(3);
        }
        else if (trimInput.startsWith("\\time ")){
          measureEval = true;
          input = trimInput.substring(6);
        }
        else{
          // parse input to find command
          Namespace parsedInput;
          try {
            String[] inputArgs = Tokenizer.parseCommandLine(trimInput);
            parsedInput = inputParser.parseArgs(inputArgs);
          } catch (ArgumentParserException e) {
            textTerminal.println(e.getParser().formatHelp());
            continue;
          } catch(RuntimeException e){
            textTerminal.println(e.getMessage());
            continue;
          }

          Command command = parsedInput.get("command");
          state = command.perform(parsedInput, input, textTerminal, state);
          continue;
        }

      }

      if (state.isMultiLine()){
        state.addInputLine(input);
      }
      else{
        state.setInput(input);
        state = new InteractiveInputCommand().perform(textTerminal, state, measureEval);
      }

    }

    return true;

  }

}
