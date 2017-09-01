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

package com.twineworks.tweakflow.examples;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.values.ValueInspector;

public class SimpleEvaluation {

  public static void main(String[] args) {

    String[] expressions = {
        "1+2",
        "'Hello' .. ' '.. 'World!'",
        "let {a: 1; b: 2; c: 3} a+b+c",
        "(x) -> x*x",
        "((x) -> x*x)(3)",
        "for i <- [1, 2, 3], i*i"
    };

    for (String expression : expressions) {
      System.out.println(expression+"\n=\n"+ ValueInspector.inspect(TweakFlow.evaluate(expression))+"\n");
    }
  }

}
