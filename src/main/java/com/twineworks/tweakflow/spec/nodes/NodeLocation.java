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

import java.util.ArrayList;
import java.util.Collections;

public class NodeLocation {
  public final String file;
  public final int line;
  public final int charInLine;

  public NodeLocation() {
    this("none", 0, 0);
  }

  public NodeLocation(String file, int line, int charInLine) {
    this.file = file;
    this.line = line;
    this.charInLine = charInLine;
  }

  public static NodeLocation at(String at) {
    String file = "<none>";
    int line = 0;
    int charInLine = 0;

    if (at == null) return new NodeLocation(file, line, charInLine);

    // split off path:line:char_in_line such that the path may contain the : character
    int splitIdx;
    ArrayList<String> parts = new ArrayList<>();

    while (parts.size() < 3){
      if ((splitIdx = at.lastIndexOf(":")) > -1){
        String part = at.substring(splitIdx+1);
        at = at.substring(0, splitIdx);
        parts.add(part);
      }
      else{
        parts.add(at);
        break;
      }
    }
    Collections.reverse(parts);
    if (parts.size() > 0) file = parts.get(0);
    if (parts.size() > 1) line = Integer.parseInt(parts.get(1), 10);
    if (parts.size() > 2) charInLine = Integer.parseInt(parts.get(2), 10);

    return new NodeLocation(file, line, charInLine);
  }
}
