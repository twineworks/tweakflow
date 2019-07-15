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
import com.twineworks.tweakflow.lang.interpreter.SimpleDebugHandler;
import com.twineworks.tweakflow.lang.load.loadpath.FilesystemLocation;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.spec.nodes.DescribeNode;
import com.twineworks.tweakflow.spec.nodes.ItNode;
import com.twineworks.tweakflow.spec.nodes.SpecNode;
import com.twineworks.tweakflow.spec.nodes.SpecNodes;
import com.twineworks.tweakflow.spec.reporter.SpecReporter;


import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class SpecRunner implements SpecReporter {

  LoadPath loadPath;
  private Runtime runtime;

  private final SpecRunnerOptions options;
  private int errors = 0;

  public SpecRunner(SpecRunnerOptions options) {
    this.options = options;
    LoadPath.Builder builder = new LoadPath.Builder().addStdLocation();

    // add resource locations to loadpath
    for (String loc : options.loadPathOptions.resourceLoadPath) {
      ResourceLocation location = new ResourceLocation.Builder()
          .path(Paths.get(loc))
          .allowCaching(true)
          .allowNativeFunctions(true)
          .build();

      builder.add(location);
    }

    // add regular locations to loadpath
    if (options.loadPathOptions.loadPath.size() == 0){
      // default load path
      builder.addCurrentWorkingDirectory();
    }
    else{
      // custom load path
      for (String loc : options.loadPathOptions.loadPath) {
        FilesystemLocation location = new FilesystemLocation.Builder(Paths.get(loc))
            .allowCaching(true)
            .allowNativeFunctions(true)
            .confineToPath(false)
            .build();
        builder.add(location);
      }
    }

    loadPath = builder.build();
  }

  public void run() {

    ArrayList<String> modules = options.modules;
    // TODO: find glob patterns

    runtime = TweakFlow.compile(loadPath, modules, new SimpleDebugHandler());

//    System.out.println("okay, compiled runtime");

    Map<String, Runtime.Module> runtimeModules = runtime.getModules();
    ArrayList<Value> rawNodes = new ArrayList<>();

    for (String moduleName : modules) {
      Runtime.Module module = runtimeModules.get(runtime.unitKey(moduleName));
      Runtime.Library mainLib = module.getLibrary("spec");
      if (mainLib == null) {
        System.out.println("no spec lib in "+moduleName+" ... skipping");
        continue;
      }

      Runtime.Var mainVar = mainLib.getVar("spec");
      if (mainVar == null){
        System.out.println("no spec var in spec lib in "+moduleName+" ... skipping");
        continue;
      }

      mainVar.evaluate();
      rawNodes.add(mainVar.getValue());

    }

    // parsing
    ArrayList<SpecNode> nodes = parseNodes(rawNodes);
//    System.out.println("pre-processed all nodes");

    // TODO: effects registry
    // TODO: nodes on active path selection
    // TODO: before and after hook execution

    // TODO: pre-processing: effect-resolution
    // TODO: construction of full names

    // TODO: selection of nodes to run via regex or substring match

    // TODO: subject tracking

    // TODO: info-sharing failed assertions (trace, expected vs. found)
    // TODO: reporter(s) for output and file creation

    // TODO: measure duration of everything

    // execution
    SpecContext specContext = new SpecContext(runtime, this);
    onEnterSuite();
    for (SpecNode node : nodes) {
      specContext.run(node);
    }
    onLeaveSuite();
  }

  public boolean hasErrors(){
    return errors > 0;
  }

  private ArrayList<SpecNode> parseNodes(ArrayList<Value> nodes){

    ArrayList<SpecNode> ret = new ArrayList<>();
    for (Value node : nodes) {
      ret.add(parseNode(node));
    }
    return ret;
  }

  private SpecNode parseNode(Value node){
    return SpecNodes.fromValue(node, options.effects, runtime);
  }

  @Override
  public void onEnterSuite() {
    for (SpecReporter reporter : options.reporters) {
      reporter.onEnterSuite();
    }
  }

  @Override
  public void onEnterDescribe(DescribeNode node) {
    for (SpecReporter reporter : options.reporters) {
      reporter.onEnterDescribe(node);
    }

  }

  @Override
  public void onLeaveDescribe(DescribeNode node) {
    for (SpecReporter reporter : options.reporters) {
      reporter.onLeaveDescribe(node);
    }

  }

  @Override
  public void onEnterIt(ItNode node) {
    for (SpecReporter reporter : options.reporters) {
      reporter.onEnterIt(node);
    }

  }

  @Override
  public void onLeaveIt(ItNode node) {
    for (SpecReporter reporter : options.reporters) {
      reporter.onLeaveIt(node);
      if (!node.isPending() && !node.isSuccess()){
        errors++;
      }
    }

  }

  @Override
  public void onLeaveSuite() {
    for (SpecReporter reporter : options.reporters) {
      reporter.onLeaveSuite();
    }

  }
}
