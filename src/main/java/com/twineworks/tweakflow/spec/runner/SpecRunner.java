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
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.spec.nodes.SpecNode;
import com.twineworks.tweakflow.spec.nodes.SuiteNode;
import com.twineworks.tweakflow.spec.runner.helpers.Filter;
import com.twineworks.tweakflow.spec.runner.helpers.GlobFileFinder;
import com.twineworks.tweakflow.spec.runner.helpers.LoadPathHelper;
import com.twineworks.tweakflow.spec.runner.helpers.NodeHelper;

import java.util.ArrayList;

public class SpecRunner {

  private final SpecRunnerOptions options;
  private SpecContext specContext;

  public SpecRunner(SpecRunnerOptions options) {
    this.options = options;
  }

  public void run() {

    LoadPath loadPath = new LoadPathHelper(options.loadPathOptions).build();
    ArrayList<String> modules = GlobFileFinder.findModules(options.modules);
    Runtime runtime = TweakFlow.compile(loadPath, modules, new SimpleDebugHandler());

    // evaluate
    ArrayList<Value> valueNodes = NodeHelper.evalValueNodes(runtime, modules);

    // parse into nodes, evaluating pre-effects
    ArrayList<SpecNode> nodes = NodeHelper.parseNodes(valueNodes, options.effects, runtime);

    // TODO: handle errors during before hooks [abort - sensible error message]
    // TODO: handle errors during after hooks  [abort - sensible error message]
    // TODO: handle errors during effects (pre) [abort - sensible error message]
    // TODO: handle errors during effects (during) [abort - sensible error message]
    // TODO: handle tags inclusion, anything tagged is excluded by default
    // TODO: measure duration of everything

    // filtering
    if (options.filters.size() > 0) {
      new Filter(options.filters).filter(nodes);
    }

    SuiteNode suite = new SuiteNode().setNodes(nodes);

    // execution
    specContext = new SpecContext(runtime, options.reporters);
    specContext.run(suite);
  }

  public boolean hasErrors(){
    return specContext.hasErrors();
  }

}
