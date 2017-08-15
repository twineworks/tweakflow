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

package com.twineworks.tweakflow.lang.analysis.constants;

import com.twineworks.tweakflow.lang.values.Value;

import java.util.Comparator;
import java.util.TreeSet;

public class ConstantPool {

  private final TreeSet<Value> set = new TreeSet<>(new Comparator<Value>() {
    @Override
    public int compare(Value a, Value b) {

      if (a == b) return 0;
      if (a.valueAndTypeEquals(b)) return 0;

      int ha = a.hashCode();
      int hb = b.hashCode();
      if (ha < hb) return -1;
      if (ha > hb) return 1;

      int iha = System.identityHashCode(a);
      int ihb = System.identityHashCode(b);

      if (iha < ihb) return -1;
      return 1;
    }
  });

  public Value get(Value v){
    if (!set.contains(v)){
      set.add(v);
    }
    return set.subSet(v, true, v, true).first();
  }
}
