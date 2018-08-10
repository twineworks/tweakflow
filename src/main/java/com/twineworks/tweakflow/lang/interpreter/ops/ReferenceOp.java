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

package com.twineworks.tweakflow.lang.interpreter.ops;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.Spaces;
import com.twineworks.tweakflow.lang.values.Value;

import java.util.List;

import static com.twineworks.tweakflow.lang.interpreter.Interpreter.evaluateCell;

final public class ReferenceOp implements ExpressionOp {
  private final ReferenceNode node;
  private final ReferenceNode.Anchor anchor;
  private final ConstShapeMap.Accessor[] names;
  private MemorySpace lastSpace;
  private Cell lastCell;

  public ReferenceOp(ReferenceNode node) {
    this.node = node;
    this.anchor = node.getAnchor();
    this.names = new ConstShapeMap.Accessor[node.getElements().size()];
    List<String> elements = node.getElements();

    for (int i = 0; i < elements.size(); i++) {
      String s = elements.get(i);
      names[i] = ConstShapeMap.accessor(s);
    }

  }

  public ReferenceNode getNode() {
    return node;
  }

  public ReferenceNode.Anchor getAnchor() {
    return anchor;
  }

  public ConstShapeMap.Accessor[] getNames() {
    return names;
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    MemorySpace space = stack.peek().getSpace();

    Cell cell = lastCell;
    if (lastSpace != space){
      cell = Spaces.resolve(this, space);
      lastCell = cell;
      lastSpace = space;
    }
//    Cell cell = Spaces.resolve(this, space);
//    Cell cell = resolvedCells.get(space);

//    if (cell == null){
//      cell = Spaces.resolve(this, space);
//      resolvedCells.put(space, cell);
//    }

    if (cell.isDirty()) evaluateCell(cell, stack, context);

    return cell.getValue();
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new ReferenceOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new ReferenceOp(node);
  }


}
