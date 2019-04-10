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

package com.twineworks.tweakflow.doc;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.Loader;
import com.twineworks.tweakflow.lang.load.loadpath.FilesystemLocation;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.Arity1CallSite;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocMain {

  private static ArgumentParser createMainArgumentParser() {

    ArgumentParser parser = ArgumentParsers.newFor("doc").build();

    parser.addArgument("-I", "--load_path")
        .required(false)
        .type(String.class)
        .setDefault(new ArrayList<String>())
        .action(Arguments.append());

    parser.addArgument("-t", "--transformer")
        .required(false)
        .type(String.class)
        .setDefault(new ArrayList<String>())
        .action(Arguments.append());

    parser.addArgument("-o", "--output")
        .required(false)
        .type(String.class);

    parser.addArgument("-oe", "--output-extension")
        .required(false)
        .setDefault("txt")
        .type(String.class);

    parser.addArgument("module")
        .nargs("+")
        .type(String.class);

    return parser;

  }

  public static void main(String[] args) {

    ArgumentParser parser = createMainArgumentParser();
    LoadPath loadPath;

    try {

      // load path
      Namespace res = parser.parseArgs(args);
      List loadPathArgs = (List) res.getAttrs().get("load_path");

      if (loadPathArgs.size() == 0) {
        // default load path
        loadPath = new LoadPath.Builder()
            .addStdLocation()
            .addCurrentWorkingDirectory()
            .build();
      } else {
        LoadPath.Builder loadPathBuilder = new LoadPath.Builder();
        // custom load path
        for (Object loadPathArg : loadPathArgs) {
          FilesystemLocation location = new FilesystemLocation.Builder(Paths.get(loadPathArg.toString()))
              .allowNativeFunctions(true)
              .confineToPath(true)
              .build();
          loadPathBuilder.add(location);
        }
        loadPath = loadPathBuilder.build();
      }

      // output path
      String output = (String) res.getAttrs().getOrDefault("output", null);
      String outputExtension = (String) res.getAttrs().get("output_extension");
      Path outputPath = null;

      if (output != null) {
        outputPath = Paths.get(output);
      }

      // create transformers

      List<String> transformerList  = new ArrayList<>();
      for (Object t : (List) res.getAttrs().get("transformer")) {
        transformerList.add(t.toString());
      }

      List<Arity1CallSite> transformers = createTransformers(loadPath, transformerList);

      List modules = (List) res.getAttrs().get("module");

      // get the modules and process them in order
      for (Object moduleObj : modules) {
        StringBuilder out = new StringBuilder();
        String module = (String) moduleObj;
        AnalysisUnit analysisUnit = Loader.load(loadPath, module);
        Value value = Doc.makeMetaValue(analysisUnit.getUnit());

        if (transformers.isEmpty()) {
          out.append(ValueInspector.inspect(value));
        } else {
          for (Arity1CallSite transformer : transformers) {
            value = transformer.call(value);
          }
          out.append(value.castTo(Types.STRING).string());
        }

        String moduleOut = out.toString();
        // output module
        if (outputPath == null) {
          System.out.println(moduleOut);
        } else {
          Path outPath = outputPath.resolve(module);
          String fileName = outPath.getFileName().toString();
          fileName += "." + outputExtension;
          outPath = outputPath.resolve(module).resolveSibling(fileName);
          Files.createDirectories(outPath.getParent());
          Files.write(outPath, moduleOut.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        }

      }

    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
    } catch (LangException e) {
      e.printDigestMessage();
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

  }

  private static List<Arity1CallSite> createTransformers(LoadPath loadPath, List<String> transformers) {

    List<Arity1CallSite> ret = new ArrayList<>();

    Runtime runtime = TweakFlow.compile(loadPath, transformers);
    Map<String, Runtime.Module> modules = runtime.getModules();

    for (String transformer : transformers) {
      String key = runtime.unitKey(transformer);
      Runtime.Module module = modules.get(key);
      Runtime.Library transformLib = module.getLibrary("transform");
      Runtime.Var transformVar = transformLib.getVar("transform");
      transformVar.evaluate();
      ret.add(transformVar.arity1CallSite());
    }

    return ret;

  }

}
