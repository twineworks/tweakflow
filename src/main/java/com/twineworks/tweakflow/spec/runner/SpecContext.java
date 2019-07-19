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

import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.spec.nodes.*;
import com.twineworks.tweakflow.spec.reporter.SpecReporter;

import java.util.ArrayDeque;
import java.util.Map;

public class SpecContext implements SpecReporter {

  private final ArrayDeque<Value> subjects = new ArrayDeque<>();
  private final Runtime runtime;
  private int errors = 0;
  private final SpecReporter reporter;

  public SpecContext(Runtime runtime, SpecReporter reporter) {
    this.runtime = runtime;
    this.reporter = reporter;
  }

  public void run(SpecNode node){
    if (node instanceof DescribeNode){
      DescribeNode dNode = (DescribeNode) node;
      if (dNode.isSelected()){
        node.run(this);
      }
    }
    else if (node instanceof ItNode){
      ItNode itNode = (ItNode) node;
      if (itNode.isSelected()){
        node.run(this);
      }
    }
    else {
      node.run(this);
    }
  }

  public Runtime getRuntime(){
    return runtime;
  }

  public SpecReporter getReporter() {
    return this;
  }

  public Value getSubject() {
    return subjects.peek();
  }

  public void setSubject(Value subject) {
    // replace current subject
    subjects.pop();
    subjects.push(subject);
  }

  //
  // reporter delegation
  //

  @Override
  public void onFoundSpecModules(SpecRunner specRunner) {
  }

  @Override
  public void onEnterSuite(SuiteNode node) {
  }

  @Override
  public void onEnterDescribe(DescribeNode node) {
    // inherit current subject
    subjects.push(subjects.peek());
    reporter.onEnterDescribe(node);

  }

  @Override
  public void onEnterBefore(BeforeNode node) {
    reporter.onEnterBefore(node);
  }

  @Override
  public void onLeaveBefore(BeforeNode node) {
    reporter.onLeaveBefore(node);
    if (node.didRun() && !node.isSuccess()) errors++;
  }

  @Override
  public void onEnterAfter(AfterNode node) {
    reporter.onEnterAfter(node);
  }

  @Override
  public void onLeaveAfter(AfterNode node) {
    reporter.onLeaveAfter(node);
  }

  @Override
  public void onEnterSubject(SpecNode node) {
    reporter.onEnterSubject(node);
  }

  @Override
  public void onLeaveSubject(SpecNode node) {
    reporter.onLeaveSubject(node);
    if (node.didRun() && !node.isSuccess()) errors++;
  }

  @Override
  public void onLeaveDescribe(DescribeNode node) {
    reporter.onLeaveDescribe(node);
    subjects.pop();
    if (node.didRun() && !node.isSuccess()) errors++;
  }

  @Override
  public void onEnterIt(ItNode node) {
    reporter.onEnterIt(node);
  }

  @Override
  public void onLeaveIt(ItNode node) {
    reporter.onLeaveIt(node);

    if (!node.isPending() && node.didRun() && !node.isSuccess()) {
      errors++;
    }

  }

  @Override
  public void onLeaveSuite(SuiteNode node) {
  }

  @Override
  public void onEnterFile(FileNode node) {
    subjects.push(Values.NIL);
    reporter.onEnterFile(node);
  }

  @Override
  public void onLeaveFile(FileNode node) {
    reporter.onLeaveFile(node);
    subjects.pop();
  }

  @Override
  public void setOptions(Map<String, String> options) {
    reporter.setOptions(options);
  }

  @Override
  public void onModuleCompiled(String module, Runtime runtime) {
  }

  @Override
  public void onModuleFailedToCompile(FileNode module, Throwable error) {

  }

  public boolean hasErrors() {
    return errors > 0;
  }
}
