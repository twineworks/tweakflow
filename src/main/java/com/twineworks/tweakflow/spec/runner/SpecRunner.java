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

package com.twineworks.tweakflow.spec.runner;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.SimpleDebugHandler;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.spec.nodes.DescribeNode;
import com.twineworks.tweakflow.spec.nodes.FileNode;
import com.twineworks.tweakflow.spec.nodes.SpecNode;
import com.twineworks.tweakflow.spec.nodes.SuiteNode;
import com.twineworks.tweakflow.spec.reporter.SpecReporter;
import com.twineworks.tweakflow.spec.reporter.helpers.SpecReporterDelegate;
import com.twineworks.tweakflow.spec.runner.helpers.Filter;
import com.twineworks.tweakflow.spec.runner.helpers.LoadPathHelper;
import com.twineworks.tweakflow.spec.runner.helpers.NodeHelper;
import com.twineworks.tweakflow.spec.runner.helpers.SpecFileFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SpecRunner {

  private final SpecRunnerOptions options;
  private ArrayList<String> modules;
  private SuiteNode suite;
  private Filter filter;

  public SpecRunner(SpecRunnerOptions options) {
    this.options = options;
    suite = new SuiteNode();
    filter = new Filter(options.filters, options.tags, options.runNotTagged);
  }

  public void run() {

    SpecReporter reporter = new SpecReporterDelegate(options.reporters);
    LoadPath loadPath = new LoadPathHelper(options.loadPathOptions).build();

    suite.open();

    reporter.onEnterSuite(suite);

    modules = SpecFileFinder.findModules(options.modules);
    reporter.onFoundSpecModules(this);

    modules.sort(Comparator.naturalOrder());

    for (String module : modules) {

      // compile
      ArrayList<String> toCompile = new ArrayList<>();
      toCompile.add(module);

      Runtime runtime;
      try {
        runtime = TweakFlow.compile(loadPath, toCompile, new SimpleDebugHandler());
        reporter.onModuleCompiled(module, runtime);
      } catch (Throwable e){

        FileNode fileNode = new FileNode()
            .setName(module);
        fileNode.fail(e.getMessage(), e);

        reporter.onModuleFailedToCompile(fileNode, e);
        suite.fail(e.getMessage(), e);
        continue;
      }

      // evaluate
      Value specNode = NodeHelper.evalSpecNode(runtime, module);
      if (specNode == null) {
        // no spec.spec found in module
        continue;
      }

      // parse into spec node, evaluating pre-effects
      SpecNode node = NodeHelper.parseNode(specNode, options.effects, runtime);

      // filtering
      filter.filter(Collections.singletonList(node));

      // if node is selected run it

      if (!(node instanceof DescribeNode)) {
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "file " + module + ": spec must begin with describe(...)");
      }
      DescribeNode dNode = (DescribeNode) node;
      if (dNode.isSelected()) {

        FileNode fileNode = new FileNode()
            .setName(module)
            .setNodes(Collections.singletonList(dNode))
            .setAnalysisResult(runtime.getAnalysisResult());


        suite.runNode(runtime, fileNode, reporter);

      }

    }

    suite.finish();
    reporter.onLeaveSuite(suite);

  }


  public boolean hasErrors() {
    return suite.hasErrors();
  }

  public SpecRunnerOptions getOptions() {
    return options;
  }

  public ArrayList<String> getModules() {
    return modules;
  }


}
