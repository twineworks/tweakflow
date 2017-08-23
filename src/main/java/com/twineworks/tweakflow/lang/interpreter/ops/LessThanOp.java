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

import com.twineworks.tweakflow.lang.ast.expressions.LessThanNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;

final public class LessThanOp implements ExpressionOp {

  private final LessThanNode node;

  public LessThanOp(LessThanNode node) {
    this.node = node;
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    Value left = node.getLeftExpression().getOp().eval(stack, context);
    Value right = node.getRightExpression().getOp().eval(stack, context);

    ensureValidTypes(left, right);

    if (left == Values.NIL) return Values.FALSE;
    if (right == Values.NIL) return Values.FALSE;

    Type leftType = left.type();
    Type rightType = right.type();

    if (leftType == Types.LONG){
      if (rightType == Types.LONG){
        return (left.longNum() < right.longNum()) ? Values.TRUE : Values.FALSE;
      }
      if (rightType == Types.DOUBLE){
        return (left.longNum() < right.doubleNum()) ? Values.TRUE : Values.FALSE;
      }
    }
    if (leftType == Types.DOUBLE){
      if (rightType == Types.LONG){
        return (left.doubleNum() < right.longNum()) ? Values.TRUE : Values.FALSE;
      }
      if (rightType == Types.DOUBLE){
        return (left.doubleNum() < right.doubleNum()) ? Values.TRUE : Values.FALSE;
      }

    }
    throw new LangException(LangError.CAST_ERROR, "Cannot compare types: "+leftType.name()+" and "+rightType.name(), stack, node.getSourceInfo());

  }

  private void ensureValidTypes(Value left, Value right){
    Type leftType = left.type();
    Type rightType = right.type();

    if ((left == Values.NIL || leftType == Types.DOUBLE || leftType == Types.LONG) &&
        (right == Values.NIL || rightType == Types.DOUBLE || rightType == Types.LONG)){
      return;
    }

    throw new LangException(LangError.CAST_ERROR, "cannot compare types "+left.type().name()+" and " + right.type().name());

  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {

    Type leftType = node.getLeftExpression().getValueType();
    Type rightType = node.getRightExpression().getValueType();

    if (leftType == rightType){
      if (leftType == Types.DOUBLE){
        return new LessThanOpDD(node);
      }
    }

    return new LessThanOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new LessThanOp(node);
  }


}
