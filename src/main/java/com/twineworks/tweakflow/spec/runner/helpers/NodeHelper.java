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

package com.twineworks.tweakflow.spec.runner.helpers;

import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.spec.effects.SpecEffect;
import com.twineworks.tweakflow.spec.nodes.SpecNode;
import com.twineworks.tweakflow.spec.nodes.SpecNodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NodeHelper {

  public static HashMap<String, Value> evalValueNodes(Runtime runtime, ArrayList<String> modules){

    Map<String, Runtime.Module> runtimeModules = runtime.getModules();

    HashMap<String, Value> valueNodes = new HashMap<>();

    for (String moduleName : modules) {
      Runtime.Module module = runtimeModules.get(runtime.unitKey(moduleName));

      if (!module.hasLibrary("spec")) {
        continue;
      }

      Runtime.Library mainLib = module.getLibrary("spec");

      if (!mainLib.hasVar("spec")){
        continue;
      }

      Runtime.Var mainVar = mainLib.getVar("spec");

      mainVar.evaluate();
      valueNodes.put(moduleName, mainVar.getValue());

    }
    return valueNodes;
  }

  public static HashMap<String, SpecNode> parseNodes(HashMap<String, Value> nodes, HashMap<String, SpecEffect> effects, Runtime runtime) {

    HashMap<String, SpecNode> ret = new HashMap<>();
    for (String key : nodes.keySet()) {
      ret.put(key, parseNode(nodes.get(key), effects, runtime));
    }
    return ret;
  }

  private static SpecNode parseNode(Value node, HashMap<String, SpecEffect> effects, Runtime runtime) {
    return SpecNodes.fromValue(node, effects, runtime);
  }
}
