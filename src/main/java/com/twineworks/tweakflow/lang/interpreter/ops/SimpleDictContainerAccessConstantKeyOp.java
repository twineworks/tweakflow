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

import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.ast.expressions.ContainerAccessNode;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;

final public class SimpleDictContainerAccessConstantKeyOp implements ExpressionOp {

  private final ExpressionOp containerOp;
  private final String key;
  private final ContainerAccessNode node;

  public SimpleDictContainerAccessConstantKeyOp(ContainerAccessNode node) {
    this.node = node;
    containerOp = node.getContainerExpression().getOp();
    this.key = Interpreter.evaluateInEmptyScope(node.getKeysExpression()).list().get(0).castTo(Types.STRING).string();
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {
    DictValue xs = containerOp.eval(stack, context).dict();
    if (xs == null) return Values.NIL;
    return xs.get(key);
  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new SimpleDictContainerAccessConstantKeyOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new SimpleDictContainerAccessConstantKeyOp(node);
  }


}
