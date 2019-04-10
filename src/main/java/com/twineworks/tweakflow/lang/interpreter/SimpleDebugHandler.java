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

package com.twineworks.tweakflow.lang.interpreter;

import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.ValueInspector;

import java.io.PrintStream;

public class SimpleDebugHandler implements DebugHandler {

  private final PrintStream out;
  private final boolean quoteStrings;

  public SimpleDebugHandler() {
    this(System.out, false);
  }

  public SimpleDebugHandler(boolean quoteStrings) {
    this(System.out, quoteStrings);
  }

  public SimpleDebugHandler(PrintStream out, boolean quoteStrings) {
    this.out = out;
    this.quoteStrings = quoteStrings;
  }

  @Override
  public void debug(Value... vs) {
    if (vs == null) return;
    for (int i = 0; i < vs.length; i++) {
      Value v = vs[i];
      if (i > 0) out.print(" ");
      out.print(v.isString() && !quoteStrings ? v.string() : ValueInspector.inspect(v));
      if (i == vs.length-1){
        out.println();
      }
    }
  }
}
