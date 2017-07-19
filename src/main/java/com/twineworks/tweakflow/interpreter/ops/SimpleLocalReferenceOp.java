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

import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.interpreter.EvaluationContext;
import com.twineworks.tweakflow.interpreter.Stack;
import com.twineworks.tweakflow.interpreter.memory.Cell;

import static com.twineworks.collections.shapemap.ConstShapeMap.Accessor;
import static com.twineworks.collections.shapemap.ConstShapeMap.accessor;
import static com.twineworks.tweakflow.interpreter.Evaluator.evaluateCell;

final public class SimpleLocalReferenceOp implements ExpressionOp {

  private final Accessor<Cell> name;

  public SimpleLocalReferenceOp(String name) {
    this.name = accessor(name);
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {
    Cell cell = name.get(stack.peek().getSpace().getCells());
    if (cell.isDirty()) evaluateCell(cell, stack, context);
    return cell.getValue();
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return this;
  }

  @Override
  public ExpressionOp refresh() {
    return this;
  }


}
