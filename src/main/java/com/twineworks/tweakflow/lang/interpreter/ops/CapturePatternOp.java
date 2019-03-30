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

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.ast.structure.match.CapturePatternNode;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpace;

final public class CapturePatternOp implements PatternOp {

  private final CapturePatternNode node;
  private final Symbol symbol;
  private final ConstShapeMap.Accessor<Cell> accessor;
  private final boolean capturing;

  public boolean isCapturing(){
    return capturing;
  }

  public CapturePatternOp(CapturePatternNode node) {
    this.node = node;
    this.capturing = node.getSymbolName() != null;
    if (capturing){
      symbol = node.getSymbol();
      accessor = ConstShapeMap.accessor(symbol.getName());
    }
    else{
      symbol = null;
      accessor = null;
    }
  }

  @Override
  public boolean matches(Value subject, Stack stack, EvaluationContext context) {
    return true;
  }

  @Override
  public void bind(Value subject, MemorySpace frame) {
    if (capturing){
      Cell cell = new Cell().setLeafSymbol(symbol).setValue(subject).setEnclosingSpace(frame);
      frame.getCells().seta(accessor, cell);
    }
  }

}
