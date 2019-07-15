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

package com.twineworks.tweakflow.spec;

import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.spec.runner.SpecRunner;
import com.twineworks.tweakflow.spec.runner.SpecRunnerOptions;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.ArrayList;
import java.util.List;

public class Spec {

  private static ArgumentParser createMainArgumentParser(){

    ArgumentParser parser = ArgumentParsers.newFor("spec").build();

    parser.addArgument("-I", "--load_path")
        .required(false)
        .type(String.class)
        .setDefault(new ArrayList<String>())
        .action(Arguments.append());

    parser.addArgument("-R", "--resource_load_path")
        .required(false)
        .type(String.class)
        .setDefault(new ArrayList<String>())
        .action(Arguments.append());

    parser.addArgument("module")
        .type(String.class)
        .nargs("+");

    return parser;

  }

  public static void main(String[] args){

    ArgumentParser parser = createMainArgumentParser();

    SpecRunnerOptions specRunnerOptions = new SpecRunnerOptions();

    try {

      Namespace res = parser.parseArgs(args);

      List loadPathArgs = (List) res.getAttrs().get("load_path");
      List resourceLoadPathArgs = (List) res.getAttrs().get("resource_load_path");

      for (Object loadPathArg : loadPathArgs) {
        specRunnerOptions.loadPathOptions.loadPath.add(loadPathArg.toString());
      }

      for (Object resourceLoadPathArg : resourceLoadPathArgs) {
        specRunnerOptions.loadPathOptions.resourceLoadPath.add(resourceLoadPathArg.toString());
      }

      specRunnerOptions.modules.addAll(res.getList("module"));

    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
    }

    try {

      SpecRunner specRunner = new SpecRunner(specRunnerOptions);
      specRunner.run();
      if (specRunner.hasErrors()){
        System.exit(1);
      }
      else {
        System.exit(0);
      }

    }
    catch(LangException e){
      System.err.println(e.getDigestMessage());
      System.exit(1);
    }

  }

}
