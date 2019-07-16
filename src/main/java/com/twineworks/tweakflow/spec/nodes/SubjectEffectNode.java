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

public class SubjectEffectNode implements SpecNode {

  private String name = "subject_effect";
  private EffectNode effectNode;

  public SubjectEffectNode setEffect(EffectNode effectNode){
    this.effectNode = effectNode;
    return this;
  }

  @Override
  public SpecNodeType getType() {
    return SpecNodeType.SUBJECT_EFFECT;
  }

  public String getName() {
    return name;
  }

  @Override
  public void run(SpecContext context) {
    Value newSubject = effectNode.execute(context);
    context.setSubject(newSubject);
  }

  @Override
  public void fail(String errorMessage, Throwable cause) {

  }

  @Override
  public boolean didRun() {
    return false;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

  @Override
  public String getErrorMessage() {
    return null;
  }

  @Override
  public Throwable getCause() {
    return null;
  }

}
