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

  public static ArrayList<Value> evalValueNodes(Runtime runtime, ArrayList<String> modules){

    Map<String, Runtime.Module> runtimeModules = runtime.getModules();

    ArrayList<Value> valueNodes = new ArrayList<>();

    for (String moduleName : modules) {
      Runtime.Module module = runtimeModules.get(runtime.unitKey(moduleName));
      Runtime.Library mainLib = module.getLibrary("spec");
      if (mainLib == null) {
        continue;
      }

      Runtime.Var mainVar = mainLib.getVar("spec");
      if (mainVar == null) {
        continue;
      }

      mainVar.evaluate();
      valueNodes.add(mainVar.getValue());

    }
    return valueNodes;
  }

  public static ArrayList<SpecNode> parseNodes(ArrayList<Value> nodes, HashMap<String, SpecEffect> effects, Runtime runtime) {

    ArrayList<SpecNode> ret = new ArrayList<>();
    for (Value node : nodes) {
      ret.add(parseNode(node, effects, runtime));
    }
    return ret;
  }

  private static SpecNode parseNode(Value node, HashMap<String, SpecEffect> effects, Runtime runtime) {
    return SpecNodes.fromValue(node, effects, runtime);
  }
}
