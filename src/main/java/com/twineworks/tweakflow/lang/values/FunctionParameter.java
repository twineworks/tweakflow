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

package com.twineworks.tweakflow.lang.values;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.types.Type;

public class FunctionParameter {

  private final int index;
  private final String name;
  private final ConstShapeMap.Accessor shapeAccessor;
  private final Type declaredType;
  private final Value defaultValue;

  public FunctionParameter(int index, String name, Type declaredType, Value defaultValue) {
    this.index = index;
    this.name = name;
    this.declaredType = declaredType;
    this.defaultValue = defaultValue;
    this.shapeAccessor = ConstShapeMap.accessor(name);
  }

  public int getIndex() {
    return index;
  }

  public String getName() {
    return name;
  }

  public Type getDeclaredType() {
    return declaredType;
  }

  public Value getDefaultValue() {
    return defaultValue;
  }

  public ConstShapeMap.Accessor getShapeAccessor() {
    return shapeAccessor;
  }
}
