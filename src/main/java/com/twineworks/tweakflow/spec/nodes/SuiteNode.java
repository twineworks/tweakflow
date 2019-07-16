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

import com.twineworks.tweakflow.spec.runner.SpecContext;

import java.util.ArrayList;

public class SuiteNode implements SpecNode {

  private String name = "suite";
  private ArrayList<SpecNode> nodes;
  private long startedMillis;
  private long endedMillis;

  private boolean success;


  public SuiteNode setName(String name){
    this.name = name;
    return this;
  }

  public SuiteNode setNodes(ArrayList<SpecNode> nodes) {
    this.nodes = nodes;
    return this;
  }

  @Override
  public SpecNodeType getType() {
    return SpecNodeType.SUITE;
  }

  public String getName() {
    return name;
  }

  @Override
  public void run(SpecContext context) {
    startedMillis = System.currentTimeMillis();
    context.onEnterSuite(this);
    for (SpecNode node : nodes) {
      context.run(node);
      if (!node.isSuccess()){
        success = false;
      }
    }
    endedMillis = System.currentTimeMillis();
    context.onLeaveSuite(this);
  }

  @Override
  public void fail(String errorMessage, Throwable cause) {
    success = false;
  }

  @Override
  public boolean didRun() {
    return true;
  }

  @Override
  public boolean isSuccess() {
    return success;
  }

  @Override
  public String getErrorMessage() {
    return "";
  }

  @Override
  public Throwable getCause() {
    return null;
  }

  public long getDurationMillis(){
    return endedMillis-startedMillis;
  }

}
