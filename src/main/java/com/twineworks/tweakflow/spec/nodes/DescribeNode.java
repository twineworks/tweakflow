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

package com.twineworks.tweakflow.spec.nodes;

import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.spec.runner.SpecContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DescribeNode implements SpecNode, TaggableSpecNode {

  private boolean selected = true;
  private String name = "unknown";
  private ArrayList<SpecNode> nodes;
  private ArrayList<BeforeNode> beforeNodes;
  private ArrayList<AfterNode> afterNodes;
  private SubjectSpecNode subjectNode;
  private NodeLocation at = new NodeLocation();
  private DescribeNode parent;
  private String fullName;

  private long startedMillis;
  private long endedMillis;

  private boolean success = true;
  private boolean didRun = false;
  private String errorMessage;
  private Throwable cause;
  private Value source;

  private Set<String> tags;
  private Set<String> fullTags;

  public ArrayList<SpecNode> getNodes() {
    return nodes;
  }

  public DescribeNode setNodes(ArrayList<SpecNode> nodes) {
    this.nodes = nodes;

    for (SpecNode node : this.nodes) {
      if (node instanceof ItNode) {
        ((ItNode) node).setParent(this);
      } else if (node instanceof DescribeNode) {
        ((DescribeNode) node).setParent(this);
      }
    }

    return this;
  }

  public String getFullName() {
    if (fullName == null) {
      if (parent == null) return name;
      fullName = parent.getFullName() + " " + name;
    }
    return fullName;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public NodeLocation at() {
    return at;
  }

  public DescribeNode getParent() {
    return parent;
  }

  public DescribeNode setParent(DescribeNode parent) {
    this.parent = parent;
    return this;
  }

  public DescribeNode setAt(NodeLocation at) {
    this.at = at;
    return this;
  }

  public String getName() {
    return name;
  }

  public DescribeNode setName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public SpecNodeType getType() {
    return SpecNodeType.DESCRIBE;
  }

  @Override
  public void run(SpecContext context) {
    startedMillis = System.currentTimeMillis();
    context.onEnterDescribe(this);

    if (success) {

      didRun = true;

      if (beforeNodes != null) {
        for (BeforeNode beforeNode : beforeNodes) {
          context.run(beforeNode);
          if (!beforeNode.isSuccess()) {
            BeforeNode failedBeforeNode = beforeNode;
            fail("failed to run before hook: " + failedBeforeNode.getErrorMessage(), failedBeforeNode.getCause());
            break;
          }
        }
      }

      if (subjectNode != null) {
        context.run(subjectNode);
        if (success) {
          if (!subjectNode.isSuccess()) {
            SubjectSpecNode failedSubjectNode = subjectNode;
            fail("failed to evaluate subject: " + failedSubjectNode.getErrorMessage(), failedSubjectNode.getCause());
          }
        }
      }

      for (SpecNode node : nodes) {
        context.run(node);
        if (!node.isSuccess()){
          success = false;
        }
      }

      // after nodes are run, if before nodes started to run
      // failures go up the stack and end the process
      if (afterNodes != null) {
        for (AfterNode afterNode : afterNodes) {
          context.run(afterNode);
        }
      }

    }
    endedMillis = System.currentTimeMillis();
    context.onLeaveDescribe(this);
  }

  @Override
  public Value getSource() {
    return source;
  }

  public DescribeNode setSource(Value source) {
    this.source = source;
    return this;
  }

  @Override
  public void fail(String errorMessage, Throwable cause) {

    // fail all child nodes
    success = false;
    this.errorMessage = errorMessage;
    this.cause = cause;

    for (SpecNode node : nodes) {
      node.fail(errorMessage, cause);
    }

  }

  @Override
  public boolean didRun() {
    return didRun;
  }

  @Override
  public boolean isSuccess() {
    return success;
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public Throwable getCause() {
    return cause;
  }

  public void setBeforeNodes(ArrayList<BeforeNode> beforeNodes) {
    for (BeforeNode beforeNode : beforeNodes) {
      beforeNode.setParent(this);
    }
    this.beforeNodes = beforeNodes;
  }

  public void setAfterNodes(ArrayList<AfterNode> afterNodes) {
    this.afterNodes = afterNodes;
  }

  public void setSubjectNode(SubjectSpecNode subjectNode) {
    if (subjectNode != null){
      subjectNode.setParent(this);
    }
    this.subjectNode = subjectNode;
  }

  @Override
  public long getDurationMillis() {
    return endedMillis - startedMillis;
  }

  public DescribeNode setTags(Set<String> tags) {
    this.tags = tags;
    return this;
  }

  @Override
  public Set<String> getTags() {

    if (fullTags == null) {
      if (parent == null) return tags;
      fullTags = new HashSet<>(parent.getTags());
      fullTags.addAll(tags);
    }
    return fullTags;
  }

  @Override
  public Set<String> getOwnTags() {
    return tags;
  }
}
