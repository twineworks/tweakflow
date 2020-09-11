/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Twineworks GmbH
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

package com.twineworks.tweakflow.lang.analysis;

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;

public class AnalysisInspector {

  public static void getSymbolsInScopeAt(AnalysisSet analysisSet, String atModule, int atLine, int atChar){

    AnalysisUnit unit = analysisSet.getUnits().get(atModule);

  }

  private static Node childNodeAt(Node parent, int atLine, int atChar){
    Node bestChild = null;
    for(Node child : parent.getChildren()){
      SourceInfo childSrc = child.getSourceInfo();
      if (childSrc != null && childSrc.encloses(atLine, atChar)){
        bestChild = child;
      }
    }
    if (bestChild != null && bestChild != parent) return childNodeAt(bestChild, atLine, atChar);
    return parent;
  }

  public static Node getNodeAt(AnalysisSet analysisSet, String atModule, int atLine, int atChar){

    AnalysisUnit unit = analysisSet.getUnits().get(atModule);
    if (unit == null) return null;

    Node node = unit.getUnit();
    if (node == null) return null;

    return childNodeAt(node, atLine, atChar);

  }

  public static Scope getScopeAt(AnalysisSet analysisSet, String atModule, int atLine, int atChar){
    Node node = getNodeAt(analysisSet, atModule, atLine, atChar);
    if (node == null) return null;
    return node.getScope();
  }

}
