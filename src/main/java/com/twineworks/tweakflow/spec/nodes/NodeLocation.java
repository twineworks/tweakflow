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

public class NodeLocation {
  public final String file;
  public final int line;
  public final int charInLine;

  public static NodeLocation at(String at){
    String file = "<none>";
    int line = 0;
    int charInLine = 0;

    if (at == null) return new NodeLocation(file, line, charInLine);

    String[] split = at.split(":");

    if (split.length >= 1){
      file = split[0];
    }

    if (split.length >= 2){
      line = Integer.parseInt(split[1], 10);
    }

    if (split.length >= 3){
      charInLine = Integer.parseInt(split[2], 10);
    }

    return new NodeLocation(file, line, charInLine);
  }

  public NodeLocation(){
    this("none", 0, 0);
  }

  public NodeLocation(String file, int line, int charInLine) {
    this.file = file;
    this.line = line;
    this.charInLine = charInLine;
  }
}
