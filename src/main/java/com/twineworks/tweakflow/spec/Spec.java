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
import com.twineworks.tweakflow.spec.effects.helpers.EffectFactory;
import com.twineworks.tweakflow.spec.reporter.helpers.ReporterFactory;
import com.twineworks.tweakflow.spec.runner.SpecRunner;
import com.twineworks.tweakflow.spec.runner.SpecRunnerOptions;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.ArrayList;
import java.util.HashMap;
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

    parser.addArgument("-r", "--reporter")
        .required(false)
        .dest("reporters")
        .setDefault(new ArrayList<String>())
        .type(String.class)
        .action(Arguments.append());

    parser.addArgument("-f", "--filter")
        .required(false)
        .dest("filters")
        .setDefault(new ArrayList<String>())
        .type(String.class)
        .action(Arguments.append());

    parser.addArgument("-ro", "--reporter_option")
        .required(false)
        .action(Arguments.append())
        .setDefault(new ArrayList<String>())
        .dest("reporter_option")
        .type(String.class)
        .nargs(2);

    parser.addArgument("-e", "--effect")
        .required(false)
        .dest("effects")
        .setDefault(new ArrayList<String>())
        .type(String.class)
        .action(Arguments.append());

    parser.addArgument("--color")
        .setDefault(Boolean.FALSE)
        .action(Arguments.storeTrue())
        .type(Boolean.class);

    parser.addArgument("--untagged")
        .setDefault(Boolean.FALSE)
        .action(Arguments.storeTrue())
        .type(Boolean.class);

    parser.addArgument("-t", "--tag")
        .required(false)
        .dest("tags")
        .setDefault(new ArrayList<String>())
        .type(String.class)
        .action(Arguments.append());

    parser.addArgument("module")
        .type(String.class)
        .nargs("+");

    return parser;

  }

  public static void main(String[] args){

    ArgumentParser parser = createMainArgumentParser();

    SpecRunnerOptions options = new SpecRunnerOptions();

    try {

      Namespace res = parser.parseArgs(args);

      // load path
      List<String> loadPathArgs = res.getList("load_path");
      options.loadPathOptions.loadPath.addAll(loadPathArgs);

      List<String> resourceLoadPathArgs = res.getList("resource_load_path");
      options.loadPathOptions.resourceLoadPath.addAll(resourceLoadPathArgs);

      // filters
      List<String> filters = res.getList("filters");
      options.filters.addAll(filters);

      // tags
      List<String> tags = res.getList("tags");
      options.tags.addAll(tags);

      if (tags.isEmpty()){
        options.runNotTagged = true;
      }
      else{
        options.runNotTagged = res.getBoolean("untagged");
      }

      // reporter options
      List<ArrayList<String>> reporterOptions = res.getList("reporter_option");

      HashMap<String, String> reporterOpts = new HashMap<>();
      for (ArrayList<String> option : reporterOptions) {
        String name = option.get(0);
        String value = option.get(1);
        reporterOpts.put(name, value);
      }

      // --color as shortcut for -reporter_option color true
      if (res.getBoolean("color")){
        reporterOpts.put("color", "true");
      }

      // color auto-detection
      String colorOption = reporterOpts.get("color");
      if (colorOption == null || colorOption.equalsIgnoreCase("auto")){
        reporterOpts.put("color", System.console() == null ? "false" : "true");
      }

      // reporters
      List<String> reporters = res.getList("reporters");
      for (String reporter : reporters) {
        options.reporters.add(ReporterFactory.makeReporter(reporter, reporterOpts));
      }

      // default reporter, if none given
      if (options.reporters.size() == 0){
        options.reporters.add(ReporterFactory.makeReporter("doc", reporterOpts));
      }

      // effects
      List<String> effects = res.getList("effects");
      for (String ef : effects) {
        options.effects.putAll(EffectFactory.makeEffects(ef).getEffects());
      }

      options.modules.addAll(res.getList("module"));

    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
    }

    try {

      SpecRunner specRunner = new SpecRunner(options);
      specRunner.run();
      if (specRunner.hasErrors()){
        System.exit(1);
      }

    }
    catch(LangException e){
      System.err.println(e.getDigestMessage());
      System.exit(1);
    }

  }

}
