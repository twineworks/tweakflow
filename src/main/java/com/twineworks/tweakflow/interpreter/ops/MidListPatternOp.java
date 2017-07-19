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

package com.twineworks.tweakflow.interpreter.ops;

import com.twineworks.tweakflow.lang.ast.structure.match.MatchPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MidListPatternNode;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.interpreter.EvaluationContext;
import com.twineworks.tweakflow.interpreter.Stack;
import com.twineworks.tweakflow.interpreter.memory.MemorySpace;

import java.util.ArrayList;

final public class MidListPatternOp implements PatternOp {

  private final MidListPatternNode node;
  private final PatternOp[] headOps;
  private final PatternOp[] lastOps;
  private final CapturePatternOp midOp;
  private final CapturePatternOp captureOp;

  public MidListPatternOp(MidListPatternNode node) {

    this.node = node;
    int headSize = node.getHeadElements().size();
    headOps = new PatternOp[headSize];
    int lastSize = node.getLastElements().size();
    lastOps = new PatternOp[lastSize];

    midOp = node.getMidCapture().getPatternOp();

    ArrayList<MatchPatternNode> headElements = node.getHeadElements();
    for (int i = 0; i < headElements.size(); i++) {
      MatchPatternNode matchPatternNode = headElements.get(i);
      headOps[i] = matchPatternNode.getPatternOp();
    }

    ArrayList<MatchPatternNode> lastElements = node.getLastElements();
    for (int i = 0; i < lastElements.size(); i++) {
      MatchPatternNode matchPatternNode = lastElements.get(i);
      lastOps[i] = matchPatternNode.getPatternOp();
    }

    if (node.getCapture() != null){
      captureOp = node.getCapture().getPatternOp();
    }
    else{
      captureOp = null;
    }

  }

  @Override
  public boolean matches(Value subject, Stack stack, EvaluationContext context) {

    if (!subject.isList()) return false;

    ListValue list = subject.list();
    int listSize = list.size();

    if (listSize < headOps.length+lastOps.length) return false;

    // match heads
    int h=0;

    for (Value element : list) {
      if (h == headOps.length) break;
      PatternOp op = headOps[h];
      h++;
      if (!op.matches(element, stack, context)) return false;
    }

    // match tails
    // match the last n items
    int j = 0;
    for (int i=listSize-lastOps.length; i < listSize; i++) {
      PatternOp op = lastOps[j];
      Value element = list.get(i);
      if (!op.matches(element, stack, context)) return false;
      j++;
    }

    return true;
  }

  @Override
  public void bind(Value subject, MemorySpace space) {

    ListValue list = subject.list();

    int h=0;
    for (Value element : list) {
      if (h == headOps.length) break;
      PatternOp op = headOps[h];
      op.bind(element, space);
      h++;
    }

    int listSize = list.size();
    int j = 0;
    for (int i=listSize-lastOps.length; i < listSize; i++) {
      PatternOp op = lastOps[j];
      Value element = list.get(i);
      op.bind(element, space);
      j++;
    }

    // bind a sublist to the middle
    if (midOp.isCapturing()){
      Value mid = Values.make(list.slice(headOps.length, listSize-lastOps.length));
      midOp.bind(mid, space);
    }

    if (captureOp != null){
      captureOp.bind(subject, space);
    }

  }

}
