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

package com.twineworks.tweakflow.interpreter;

import com.twineworks.tweakflow.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.load.user.UserObjectFactory;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class EvaluationContext {

  private final UserObjectFactory userObjectFactory;
  private final DebugHandler debugHandler;

  private final Map<Cell, List<RecursiveDeferredClosure>> recursiveDeferredClosures;

  public EvaluationContext(UserObjectFactory userObjectFactory) {
    this.userObjectFactory = userObjectFactory;
    this.debugHandler = new DefaultDebugHandler();
    this.recursiveDeferredClosures = new IdentityHashMap<>();
  }

  public EvaluationContext(UserObjectFactory userObjectFactory, DebugHandler debugHandler) {
    this.userObjectFactory = userObjectFactory;
    this.debugHandler = debugHandler;
    this.recursiveDeferredClosures = new IdentityHashMap<>();
  }

  public Map<Cell, List<RecursiveDeferredClosure>> getRecursiveDeferredClosures() {
    return recursiveDeferredClosures;
  }

  public UserObjectFactory getUserObjectFactory() {
    return userObjectFactory;
  }

  public DebugHandler getDebugHandler() {
    return debugHandler;
  }
}
