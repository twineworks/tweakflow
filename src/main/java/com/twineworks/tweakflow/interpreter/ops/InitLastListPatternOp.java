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

import com.twineworks.tweakflow.lang.ast.structure.match.InitLastListPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchPatternNode;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.interpreter.EvaluationContext;
import com.twineworks.tweakflow.interpreter.Stack;
import com.twineworks.tweakflow.interpreter.memory.MemorySpace;

import java.util.ArrayList;

final public class InitLastListPatternOp implements PatternOp {

  private final InitLastListPatternNode node;
  private final PatternOp[] patternOps;
  private final CapturePatternOp initOp;
  private final CapturePatternOp captureOp;

  public InitLastListPatternOp(InitLastListPatternNode node) {

    this.node = node;
    int size = node.getElements().size();
    patternOps = new PatternOp[size];
    initOp = node.getInitCapture().getPatternOp();

    ArrayList<MatchPatternNode> elements = node.getElements();
    for (int i = 0; i < elements.size(); i++) {
      MatchPatternNode matchPatternNode = elements.get(i);
      patternOps[i] = matchPatternNode.getPatternOp();
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

    if (listSize < patternOps.length) return false;

    // match the last n items
    int j = 0;
    for (int i=listSize-patternOps.length; i < listSize; i++) {
      PatternOp op = patternOps[j];
      Value element = list.get(i);
      if (!op.matches(element, stack, context)) return false;
      j++;
    }
    return true;
  }

  @Override
  public void bind(Value subject, MemorySpace space) {

    ListValue list = subject.list();
    int listSize = list.size();
    int j = 0;
    for (int i=listSize-patternOps.length; i < listSize; i++) {
      PatternOp op = patternOps[j];
      Value element = list.get(i);
      op.bind(element, space);
      j++;
    }

    // bind a sublist to the init section
    if (initOp.isCapturing()){
      Value tail = Values.make(list.slice(0, listSize-patternOps.length));
      initOp.bind(tail, space);
    }

    if (captureOp != null){
      captureOp.bind(subject, space);
    }

  }

}
