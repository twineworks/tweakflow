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

package com.twineworks.tweakflow.lang.runtime;

import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.util.LangUtil;

import java.util.Map;

public class RuntimeInspector {

  public static String inspect(Runtime.Node node){
    StringBuilder out = new StringBuilder();
    inspect(out, node, "", "", "  ", false);
    return out.toString();
  }

  public static String inspect(Runtime.Node node, boolean expandFunctions){
    StringBuilder out = new StringBuilder();
    inspect(out, node, "", "", "  ", expandFunctions);
    return out.toString();
  }

  public static void inspect(StringBuilder out, Runtime.Node node, String leadingIndent, String inheritedIndent, String indentationUnit, boolean expandFunctions){

    if (node == null){
      out.append(leadingIndent).append("null");
      return;
    }

    boolean inspectChildren = false;

    if (node instanceof Runtime.Globals){
      out.append("# globals").append("\n");
      inspectChildren = true;
    }
    else if (node instanceof Runtime.Units){
      out.append("# units").append("\n");
      inspectChildren = true;
    }
    else if (node instanceof Runtime.Exports){
      out.append("# exports").append("\n");
      inspectChildren = true;
    }
    else if (node instanceof Runtime.Module){
      out.append("# module ").append("\n");
      inspectChildren = true;
    }
    else if (node instanceof Runtime.ModuleExports){
      out.append("# imported module ").append("\n");
      inspectChildren = true;
    }
    else if (node instanceof Runtime.InteractiveUnit){
      out.append("# interactive unit").append("\n");
      inspectChildren = true;
    }
    else if (node instanceof Runtime.InteractiveSection){
      out.append("# interactive section").append("\n");
      inspectChildren = true;
    }
    else if (node instanceof Runtime.Library){
      out.append("# library").append("\n");
      inspectChildren = true;
    }
    else if (node instanceof Runtime.Var){
      ValueInspector.inspect(out, ((Runtime.Var) node).getValue(), "", inheritedIndent, indentationUnit, expandFunctions);
      out.append("\n");
      inspectChildren = false;
    }
    else {
      out.append("unknown node type: ").append(node.toString());
      inspectChildren = false;
    }

    if (inspectChildren){
      inspectChildren(out, node, leadingIndent, inheritedIndent, indentationUnit, expandFunctions);
    }

  }

  private static void inspectChildren(StringBuilder out, Runtime.Node node, String leadingIndent, String inheritedIndent, String indentationUnit, boolean expandFunctions){

    Map<String, Runtime.Node> children = node.getChildren();
    String childIndent = inheritedIndent+indentationUnit;
    for (String key : children.keySet()) {
      out.append(childIndent).append(LangUtil.escapeIdentifier(key)).append(": ");
      inspect(out, children.get(key), childIndent, childIndent, indentationUnit, expandFunctions);
    }

  }

}
