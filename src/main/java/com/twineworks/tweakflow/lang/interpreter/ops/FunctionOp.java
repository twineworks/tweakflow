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

import com.twineworks.tweakflow.lang.ast.expressions.FunctionNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.values.*;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.RecursiveDeferredClosure;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.StackEntry;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.Spaces;

import java.util.*;

import static com.twineworks.tweakflow.lang.interpreter.Interpreter.evaluateCell;

final public class FunctionOp implements ExpressionOp {

  private final FunctionNode node;

  public FunctionOp(FunctionNode node) {
    this.node = node;
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    // function nodes evaluate only to establish their closures
    // any function that does not close over non-constants becomes a constant itself
    // during constant folding, and is not evaluated by the interpreter

    FunctionSignature functionSignature = node.getSignature();
    Set<ReferenceNode> closedOverReferences = node.getClosedOverReferences();
    IdentityHashMap<ReferenceNode, ValueProvider> closures = new IdentityHashMap<>();
    Value value = Values.make(new StandardFunctionValue(node, functionSignature, closures));

    // find closed over values
    StackEntry currentStack = stack.peek();
    Map<ReferenceNode, ValueProvider> stackClosures = currentStack.getClosures();

    for (ReferenceNode closure : closedOverReferences) {

      // if this value has been closed over by a parent, it's on the stack in closure space
      if (stackClosures.containsKey(closure)){
        closures.put(closure, stackClosures.get(closure));
      }
      // not closed over by a parent, find cell in memory space, and capture the value
      else{
        Cell cell = Spaces.resolve(closure, currentStack.getSpace());

        // closed over value has not been evaluated yet
        if (cell.getValue() == null){
          closures.put(closure, cell);
          // is currently evaluating
          if (cell.isEvaluating()){
            // recursive closure call case, replace cell reference with cell value when cell evaluates
            RecursiveDeferredClosure deferredClosure = new RecursiveDeferredClosure(closures, closure);
            Map<Cell, List<RecursiveDeferredClosure>> deferredClosures = context.getRecursiveDeferredClosures();
            if (!deferredClosures.containsKey(cell)){
              deferredClosures.put(cell, new ArrayList<>());
            }
            deferredClosures.get(cell).add(deferredClosure);
          }
          // can be evaluated
          else{
            evaluateCell(cell, stack, context);
            closures.put(closure, cell.getValue());
          }
        }
        // closed over value is already present
        else{
          closures.put(closure, cell.getValue());
        }
      }
    }

    return value;

  }

  @Override
  public boolean isConstant() {
    // if all closures are constant, the function is constant
    Set<ReferenceNode> closedOverReferences = node.getClosedOverReferences();
    for (ReferenceNode closedOverReference : closedOverReferences) {
      if (!closedOverReference.getOp().isConstant()){
        return false;
      }
    }

    return true;
  }

  @Override
  public ExpressionOp specialize() {
    return new FunctionOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new FunctionOp(node);
  }

  public Value evalWithClosures(IdentityHashMap<ReferenceNode, ValueProvider> closures) {
    FunctionSignature functionSignature = node.getSignature();
    return Values.make(new StandardFunctionValue(node, functionSignature, closures));
  }
}
