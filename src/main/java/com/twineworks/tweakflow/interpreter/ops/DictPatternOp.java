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

import com.twineworks.tweakflow.lang.ast.structure.match.DictPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchPatternNode;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.interpreter.EvaluationContext;
import com.twineworks.tweakflow.interpreter.Stack;
import com.twineworks.tweakflow.interpreter.memory.MemorySpace;

import java.util.LinkedHashMap;

final public class DictPatternOp implements PatternOp {

  private final DictPatternNode node;
  private final PatternOp[] patternOps;
  private final String[] keys;
  private final CapturePatternOp captureOp;

  public DictPatternOp(DictPatternNode node) {
    this.node = node;
    LinkedHashMap<String, MatchPatternNode> elements = node.getElements();
    int size = elements.size();

    patternOps = new PatternOp[size];
    keys = new String[size];

    int i = 0;
    for (String key : elements.keySet()) {
      keys[i] = key;
      MatchPatternNode matchPatternNode = elements.get(key);
      patternOps[i] = matchPatternNode.getPatternOp();
      i++;
    }

    if (node.getCapture() != null) {
      captureOp = node.getCapture().getPatternOp();
    }
    else{
      captureOp = null;
    }

  }

  @Override
  public boolean matches(Value subject, Stack stack, EvaluationContext context) {
    if (!subject.isDict()) return false;
    DictValue dict = subject.dict();

    // requires map to match keys exactly
    if (dict.size() != keys.length){
      return false;
    }

    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      PatternOp pattern = patternOps[i];
      if (!dict.containsKey(key)) return false; // matched key must be present
      if (!pattern.matches(dict.get(key), stack, context)) return false;
    }

    return true;
  }

  @Override
  public void bind(Value subject, MemorySpace space) {
    DictValue dict = subject.dict();
    for (int i = 0; i < keys.length; i++) {
      String key = keys[i];
      PatternOp pattern = patternOps[i];
      pattern.bind(dict.get(key), space);
    }

    if (captureOp != null){
      captureOp.bind(subject, space);
    }
  }

}
