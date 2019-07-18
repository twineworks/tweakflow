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
import java.util.HashMap;

public class SpecRunner {

  private final SpecRunnerOptions options;
  private SpecContext specContext;
  private ArrayList<String> modules;
  private Runtime runtime;

  public SpecRunner(SpecRunnerOptions options) {
    this.options = options;
  }

  public void run() {

    SpecReporter reporter = new SpecReporterDelegate(options.reporters);
    LoadPath loadPath = new LoadPathHelper(options.loadPathOptions).build();

    modules = SpecFileFinder.findModules(options.modules);
    reporter.onFoundSpecModules(this);

    runtime = TweakFlow.compile(loadPath, modules, new SimpleDebugHandler());
    reporter.onCompiledSpecModules(this);

    // evaluate
    HashMap<String, Value> valueNodes = NodeHelper.evalValueNodes(runtime, modules);

    // parse into nodes, evaluating pre-effects
    HashMap<String, SpecNode> nodes = NodeHelper.parseNodes(valueNodes, options.effects, runtime);

    // filtering
    new Filter(options.filters, options.tags, options.runNotTagged)
        .filter(nodes.values());

    // for every selected describe node, wrap it in a File Node and add to suite
    ArrayList<FileNode> fileNodes = new ArrayList<>();
    for (String key : nodes.keySet()) {
      SpecNode specNode = nodes.get(key);
      if (!(specNode instanceof DescribeNode)){
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "file "+key+": spec must begin with describe(...)");
      }
      DescribeNode dNode = (DescribeNode) specNode;
      if (dNode.isSelected()){
        fileNodes.add(
            new FileNode()
                .setName(key)
                .setNodes(Collections.singletonList(dNode)));
      }
    }

    fileNodes.sort(Comparator.comparing(FileNode::getName));
    SuiteNode suite = new SuiteNode().setNodes(fileNodes);

    // execution
    specContext = new SpecContext(runtime, reporter);
    specContext.run(suite);
  }

  public boolean hasErrors(){
    return specContext.hasErrors();
  }

  public SpecRunnerOptions getOptions() {
    return options;
  }

  public ArrayList<String> getModules() {
    return modules;
  }

  public Runtime getRuntime() {
    return runtime;
  }

}
