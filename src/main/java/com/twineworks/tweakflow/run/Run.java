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

package com.twineworks.tweakflow.run;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.DefaultDebugHandler;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.load.loadpath.FilesystemLocation;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.Parser;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.lang.values.Values;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Run {

  public static class StringArg {
    public final String str;

    public StringArg(String str) {
      this.str = str;
    }
    public static StringArg valueOf(String str){
      return new StringArg(str);
    }
  }

  public static class EvalArg {
    public final String str;

    public EvalArg(String str) {
      this.str = str;
    }

    public static EvalArg valueOf(String str){
      return new EvalArg(str);
    }

  }

  private static ArgumentParser createMainArgumentParser(){

    ArgumentParser parser = ArgumentParsers.newArgumentParser("tf");

    parser.addArgument("-I", "--load_path")
        .required(false)
        .type(String.class)
        .setDefault(new ArrayList())
        .action(Arguments.append());

    parser.addArgument("-m", "--main")
        .setDefault("main.main")
        .type(String.class);

    parser.addArgument("-a", "--arg")
        .required(false)
        .dest("arg")
        .type(StringArg.class)
        .action(Arguments.append());

    parser.addArgument("-ea", "--eval_arg")
        .required(false)
        .dest("arg")
        .type(EvalArg.class)
        .action(Arguments.append());

    parser.addArgument("module")
        .type(String.class)
        .nargs("+");

    return parser;

  }

  private static String[] parseMain(String main){

    // get main
    ParseUnit parseUnit = new MemoryLocation.Builder().add("<main>", main).build().getParseUnit("<main>");
    ParseResult parseResult = new Parser(parseUnit).parseReference();

    if (parseResult.isError()){
      System.err.println("syntax error! invalid main "+main);
      System.exit(1);
    }

    ReferenceNode ref = (ReferenceNode) parseResult.getNode();
    if (ref.getElements().size() != 2 || ref.getAnchor() != ReferenceNode.Anchor.LOCAL){
      System.err.println("syntax error! main must be of the form: lib.var "+main);
      System.exit(1);
    }

    return new String[]{ref.getElements().get(0), ref.getElements().get(1)};

  }

  public static ExpressionNode parseExp(String exp){
    ParseUnit parseUnit = new MemoryLocation.Builder().add("<exp>", exp).build().getParseUnit("<exp>");
    ParseResult parseResult = new Parser(parseUnit).parseExpression();

    if (parseResult.isError()){
      System.err.println("syntax error! invalid argument "+exp);
      System.exit(1);
    }

    return (ExpressionNode) parseResult.getNode();

  }

  public static Value evalExp(ExpressionNode node){
    return Interpreter.evaluateInEmptyScope(node);
  }

  public static void main(String[] args){

    ArgumentParser parser = createMainArgumentParser();
    LoadPath.Builder loadPathBuilder = new LoadPath.Builder().addStdLocation();
    List<String> modules = Collections.singletonList("std");

    LoadPath loadPath = null;

    String main = "main.main";
    String lib = "main";
    String var = "main";

    Value[] callArgValues = null;

    try {

      // load path
      Namespace res = parser.parseArgs(args);
      List loadPathArgs = (List) res.getAttrs().get("load_path");
      modules = res.getList("module");

      if (loadPathArgs.size() == 0){
        // default load path
        loadPathBuilder.addCurrentWorkingDirectory();
      }
      else{
        // custom load path
        for (Object loadPathArg : loadPathArgs) {
          FilesystemLocation location = new FilesystemLocation.Builder(Paths.get(loadPathArg.toString()))
              .allowNativeFunctions(true)
              .confineToPath(true)
              .build();
          loadPathBuilder.add(location);
        }
      }

      loadPath = loadPathBuilder.build();

      // main
      main = res.getString("main");
      String[] mainParts = parseMain(main);
      lib = mainParts[0];
      var = mainParts[1];

      // args
      List callArgs = (List) res.getAttrs().get("arg");
      if (callArgs == null){
        callArgs = Collections.emptyList();
      }
      callArgValues = new Value[callArgs.size()];

      try {
        for (int i = 0; i < callArgs.size(); i++) {
          Object callArg = callArgs.get(i);
          if (callArg instanceof StringArg){
            StringArg arg = (StringArg) callArg;
            Value callValue = Values.make(arg.str);
            callArgValues[i] = callValue;
          }
          else if (callArg instanceof EvalArg){
            EvalArg arg = (EvalArg) callArg;
            Value callValue = TweakFlow.evaluate(arg.str);
            callArgValues[i] = callValue;
          }
        }

      } catch (LangException e){
        System.err.println(e.getDigestMessage());
        System.exit(1);
      }

    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
    }

    try {
      Runtime runtime = TweakFlow.compile(loadPath, modules, new DefaultDebugHandler());
      runtime.evaluate();
      Map<String, Runtime.Module> runtimeModules = runtime.getModules();
      Runtime.Module module = runtimeModules.get(runtime.unitKey(modules.get(0)));
      Runtime.Var mainVar = module.getLibrary(lib).getVar(var);
      Value result = mainVar.call(callArgValues);

      System.out.println(ValueInspector.inspect(result));
    }
    catch(LangException e){
      System.err.println(e.getDigestMessage());
      System.exit(1);
    }

  }

}
