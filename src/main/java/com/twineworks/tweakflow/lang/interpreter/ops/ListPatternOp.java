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

package com.twineworks.tweakflow.lang.interpreter.ops;

import com.twineworks.tweakflow.lang.ast.structure.match.ListPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchPatternNode;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpace;

import java.util.ArrayList;

final public class ListPatternOp implements PatternOp {

  private final ListPatternNode node;
  private final PatternOp[] patternOps;
  private final CapturePatternOp captureOp;

  public ListPatternOp(ListPatternNode node) {
    this.node = node;
    int size = node.getElements().size();
    patternOps = new PatternOp[size];

    ArrayList<MatchPatternNode> elements = node.getElements();
    for (int i = 0; i < elements.size(); i++) {
      MatchPatternNode matchPatternNode = elements.get(i);
      patternOps[i] = matchPatternNode.getPatternOp();
    }

    if (node.getCapture() != null){
      captureOp = node.getCapture().getPatternOp();
    }
    else {
      captureOp = null;
    }

  }

  @Override
  public boolean matches(Value subject, Stack stack, EvaluationContext context) {
    if (!subject.isList()) return false;
    ListValue list = subject.list();
    if (list.size() != patternOps.length) return false;
    int i=0;
    for (Value element : list) {
      PatternOp op = patternOps[i];
      i++;
      if (!op.matches(element, stack, context)) return false;
    }
    return true;
  }

  @Override
  public void bind(Value subject, MemorySpace space) {
    int i=0;
    for (Value element : subject.list()) {
      PatternOp op = patternOps[i];
      op.bind(element, space);
      i++;
    }
    if (captureOp != null){
      captureOp.bind(subject, space);
    }
  }

}
