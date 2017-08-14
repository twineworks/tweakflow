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

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.ast.ForHeadElementNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.ForNode;
import com.twineworks.tweakflow.lang.ast.structure.ForHead;
import com.twineworks.tweakflow.lang.ast.structure.GeneratorNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.interpreter.EvaluationContext;
import com.twineworks.tweakflow.interpreter.Stack;
import com.twineworks.tweakflow.interpreter.StackEntry;
import com.twineworks.tweakflow.interpreter.memory.Cell;
import com.twineworks.tweakflow.interpreter.memory.LocalMemorySpace;
import com.twineworks.tweakflow.interpreter.memory.MemorySpace;
import com.twineworks.tweakflow.interpreter.memory.MemorySpaceType;

import java.util.HashSet;
import java.util.Set;

final public class ForOp implements ExpressionOp {

  private final ForNode node;
  private final ForHead head;
  private final ExpressionOp expressionOp;
  private final ForHeadElementNode[] elements;
  private final ConstShapeMap.Accessor[] accessors;
  private final ConstShapeMap<Cell> templateShapeMap;

  public ForOp(ForNode node) {
    this.node = node;
    this.head = node.getHead();

    this.elements = head.getElements().toArray(new ForHeadElementNode[head.getElements().size()]);

    this.expressionOp = node.getExpression().getOp();

    // build frame infrastructure
    Set<ShapeKey> keySet = new HashSet<>();
    accessors = new ConstShapeMap.Accessor[head.getElements().size()];

    for (int i = 0; i < elements.length; i++) {

      ForHeadElementNode element = elements[i];

      if (element instanceof VarDefNode) {
        // locals
        VarDefNode def = (VarDefNode) element;
        keySet.add(ShapeKey.get(def.getSymbolName()));
        accessors[i] = ConstShapeMap.accessor(def.getSymbolName());
      } else if (element instanceof GeneratorNode) {
        // generators
        GeneratorNode gen = (GeneratorNode) element;
        keySet.add(ShapeKey.get(gen.getSymbolName()));
        accessors[i] = ConstShapeMap.accessor(gen.getSymbolName());
      }
    }

    templateShapeMap = new ConstShapeMap<>(keySet);

  }

  private ListValue processGenerator(GeneratorNode gen, int i, Cell[] cells, ListValue list, Stack stack, EvaluationContext context){

    Value iteration = gen.getValueExpression().getOp().eval(stack, context).castTo(Types.LIST);
    if (iteration == Values.NIL) return null;

    for (Value it : iteration.list()) {
      cells[i].setValue(it);
      list = processElement(i+1, cells, list, stack, context);
      if (list == null) return null;
    }

    return list;
  }

  private ListValue processLocal(VarDefNode def, int i, Cell[] cells, ListValue list, Stack stack, EvaluationContext context){
    Value value = def.getValueExpression().getOp().eval(stack, context);
    cells[i].setValue(value);
    return processElement(i+1, cells, list, stack, context);
  }

  private ListValue processPredicate(ExpressionOp op, int i, Cell[] cells, ListValue list, Stack stack, EvaluationContext context){
    Value value = op.eval(stack, context).castTo(Types.BOOLEAN);
    if (value == Values.TRUE) return processElement(i+1, cells, list, stack, context);
    return list;
  }

  private ListValue processElement(int i, Cell[] cells, ListValue list, Stack stack, EvaluationContext context){

    if (i < elements.length){
      ForHeadElementNode element = elements[i];
      if (element instanceof GeneratorNode){
        GeneratorNode gen = (GeneratorNode) element;
        return processGenerator(gen, i, cells, list, stack, context);
      }
      else if (element instanceof VarDefNode){
        VarDefNode def = (VarDefNode) element;
        return processLocal(def, i, cells, list, stack, context);
      }
      else {
        ExpressionNode p = (ExpressionNode) element;
        return processPredicate(p.getOp(), i, cells, list, stack, context);
      }
    }
    else{
      return list.append(expressionOp.eval(stack, context));
    }

  }

  private LocalMemorySpace makeFrame(MemorySpace parentSpace){

    ConstShapeMap<Cell> cellMap = new ConstShapeMap<>(templateShapeMap);

    return new LocalMemorySpace(
        parentSpace,
        node.getScope(),
        MemorySpaceType.LOCAL,
        cellMap
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    StackEntry entrance = stack.peek();

    LocalMemorySpace frame = makeFrame(entrance.getSpace());
    ConstShapeMap<Cell> cellMap = frame.getCells();
    stack.push(new StackEntry(node, frame, entrance.getClosures()));

    Cell[] cells = new Cell[elements.length];
    for (int i = 0; i < elements.length; i++) {

      ForHeadElementNode element = elements[i];

      if (element instanceof VarDefNode) {
        // locals
        VarDefNode def = (VarDefNode) element;
        Cell cell = new Cell().setLeafSymbol(def.getSymbol());
        cellMap.seta(accessors[i], cell);
        cells[i] = cell;
      } else if (element instanceof GeneratorNode) {
        // generators
        GeneratorNode gen = (GeneratorNode) element;
        Cell cell = new Cell().setLeafSymbol(gen.getSymbol());
        cellMap.seta(accessors[i], cell);
        cells[i] = cell;
      }
    }

    ListValue list = Values.EMPTY_LIST.list();

    list = processElement(0, cells, list, stack, context);

    stack.pop();

    if (list == null) return Values.NIL;

    return Values.make(list);
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new ForOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new ForOp(node);
  }


}
