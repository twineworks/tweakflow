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

import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.spec.effects.SpecEffect;
import com.twineworks.tweakflow.spec.runner.SpecContext;

public class EffectNode implements SpecNode {

  private String name = "effect";
  private SpecEffect effect;
  private Value effectNode;

  private String errorMessage;
  private Throwable cause;
  private boolean success = true;
  private boolean didRun = false;
  private Value source;

  private long startedMillis;
  private long endedMillis;

  public EffectNode setEffect(Value effectNode, SpecEffect effect) {
    this.effectNode = effectNode;
    this.effect = effect;
    return this;
  }

  @Override
  public SpecNodeType getType() {
    return SpecNodeType.EFFECT;
  }

  public String getName() {
    return name;
  }

  @Override
  public Value getSource() {
    return source;
  }

  public EffectNode setSource(Value source) {
    this.source = source;
    return this;
  }

  @Override
  public void run(SpecContext context) {
    throw new IllegalStateException("Effect nodes do not run directly as part of the test suite. Their execution is orchestrated through other nodes.");
  }

  @Override
  public void fail(String errorMessage, Throwable cause) {
    success = false;
    this.errorMessage = errorMessage;
    this.cause = cause;
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

  public Value execute(SpecContext context) {
    startedMillis = System.currentTimeMillis();
    try {
      didRun = true;
      return effect.execute(context.getRuntime(), effectNode, context.getSubject());
    } catch (Throwable e) {
      fail(e.getMessage(), e);
      throw e;
    }
    finally {
      endedMillis = System.currentTimeMillis();
    }

  }

  public Value execute(Runtime runtime) {
    startedMillis = System.currentTimeMillis();
    try {
      didRun = true;
      return effect.execute(runtime, effectNode, Values.NIL);
    } catch (Throwable e) {
      fail(e.getMessage(), e);
      throw e;
    }
    finally {
      endedMillis = System.currentTimeMillis();
    }

  }

  @Override
  public long getDurationMillis() {
    return endedMillis - startedMillis;
  }

}
