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

import com.twineworks.tweakflow.spec.nodes.DescribeNode;
import com.twineworks.tweakflow.spec.nodes.ItNode;
import com.twineworks.tweakflow.spec.nodes.SpecNode;

import java.util.ArrayList;

public class Filter {

  private final ArrayList<String> filters;

  public Filter(ArrayList<String> filters) {
    this.filters = filters;
  }

  private boolean filtersMatch(String name) {
    for (String filter : filters) {
      if (!name.contains(filter)) return false;
    }

    return true;
  }

  private void select(DescribeNode node, boolean flag) {

    node.setSelected(flag);
    // if the node is selected, all its parents, and all children are recursively selected
    if (flag){
      // set all parents
      DescribeNode parent = node.getParent();
      do {
        parent.setSelected(true);
      }
      while ((parent = parent.getParent()) != null);

      // set all children
      selectRec(node, true);
    }

  }

  private void select(ItNode node, boolean flag) {
    node.setSelected(flag);

    // if the node is selected, all its parents are selected
    if (flag) {
      DescribeNode parent = node.getParent();
      do {
        parent.setSelected(true);
      }
      while ((parent = parent.getParent()) != null);
    }
  }

  private void selectRec(DescribeNode node, boolean flag){
    node.setSelected(flag);
    for (SpecNode child : node.getNodes()) {
      if (child instanceof DescribeNode){
        selectRec((DescribeNode) child, flag);
      }
      else if (child instanceof ItNode){
        ItNode itNode = (ItNode) child;
        itNode.setSelected(flag);
      }
    }
  }

  public void filter(ArrayList<SpecNode> nodes) {
    for (SpecNode node : nodes) {
      if (node instanceof DescribeNode) {
        DescribeNode dNode = (DescribeNode) node;
        String fullName = dNode.getFullName();
        boolean match = filtersMatch(fullName);
        select(dNode, match);
        if (!match) {
          // children may still match
          filter(dNode.getNodes());
        }
      } else if (node instanceof ItNode) {
        ItNode itNode = (ItNode) node;
        String fullName = itNode.getFullName();
        boolean match = filtersMatch(fullName);
        select(itNode, match);
      }
    }

  }

}
