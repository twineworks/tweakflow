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

package com.twineworks.tweakflow.lang.interpreter.memory;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.scope.Scope;

public class LocalMemorySpace implements MemorySpace {

  public static final MemorySpace EMPTY = new LocalMemorySpace(null, null, MemorySpaceType.LOCAL, new ConstShapeMap<>());
  private final ConstShapeMap<Cell> cells;
  private MemorySpace enclosingSpace;
  private Scope scope;
  private final MemorySpaceType memorySpaceType;

  public LocalMemorySpace(MemorySpace enclosingSpace, Scope scope, MemorySpaceType memorySpaceType, ConstShapeMap<Cell> cells) {
    this.enclosingSpace = enclosingSpace;
    this.scope = scope;
    this.memorySpaceType = memorySpaceType;
    this.cells = cells;
  }

  public LocalMemorySpace setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public ConstShapeMap<Cell> getCells() {
    return cells;
  }

  @Override
  public MemorySpace getEnclosingSpace() {
    return enclosingSpace;
  }

  @Override
  public LocalMemorySpace setEnclosingSpace(MemorySpace space) {
    this.enclosingSpace = space;
    return this;
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public MemorySpaceType getMemorySpaceType() {
    return memorySpaceType;
  }

}
