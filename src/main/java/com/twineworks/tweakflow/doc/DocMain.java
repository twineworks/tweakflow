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

package com.twineworks.tweakflow.doc;

import com.twineworks.tweakflow.interpreter.DefaultDebugHandler;
import com.twineworks.tweakflow.interpreter.runtime.TweakFlowRuntime;
import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.Loader;
import com.twineworks.tweakflow.lang.load.loadpath.FilesystemLocation;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
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

public class DocMain {

  private static ArgumentParser createMainArgumentParser() {

    ArgumentParser parser = ArgumentParsers.newArgumentParser("tfdoc");

    parser.addArgument("-I", "--load_path")
        .required(false)
        .type(String.class)
        .setDefault(new ArrayList())
        .action(Arguments.append());

    parser.addArgument("-t", "--transformer")
        .required(false)
        .type(String.class)
        .setDefault(new ArrayList())
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


      // get the modules and process them in order
      Loader loader = new Loader(loadPath);
      List<Arity1CallSite> transformers = createTransformers(loader, (List) res.getAttrs().get("transformer"));

      List modules = (List) res.getAttrs().get("module");

      for (Object moduleObj : modules) {
        StringBuilder out = new StringBuilder();
        String module = (String) moduleObj;
        AnalysisUnit analysisUnit = loader.load(module);
        Value value = Doc.makeMetaValue(analysisUnit.getUnit());

        if (transformers.isEmpty()) {
          out.append("file: ").append(analysisUnit.getUnit().getSourceInfo().getParseUnit().getPath());
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
          // replace filename extension, if any, with given one
//          if (fileName.contains(".")){
//            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
//          }
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
      e.printDigest();
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

  }

  private static List<Arity1CallSite> createTransformers(Loader loader, List transformers) {

    List<Arity1CallSite> ret = new ArrayList<>();
    for (Object transformer : transformers) {
      String path = transformer.toString();
      TweakFlowRuntime runtime = TweakFlow.evaluate(loader, path, new DefaultDebugHandler());
      TweakFlowRuntime.VarHandle f = runtime.createVarHandle(path, "transform", "transform");
      ret.add(runtime.createCallContext(f).createArity1CallSite(f.getValue()));
    }

    return ret;

  }

}
