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

import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.ast.expressions.MinusNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

final public class MinusOp implements ExpressionOp {

  private final MinusNode node;
  private final ExpressionOp leftOp;
  private final ExpressionOp rightOp;

  public MinusOp(MinusNode node) {
    this.node = node;
    leftOp = node.getLeftExpression().getOp();
    rightOp = node.getRightExpression().getOp();
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    Value left = node.getLeftExpression().getOp().eval(stack, context);
    Value right = node.getRightExpression().getOp().eval(stack, context);

    ensureValidTypes(left, right);

    if (left == Values.NIL) return Values.NIL;
    if (right == Values.NIL) return Values.NIL;

    Type leftType = left.type();
    Type rightType = right.type();

    if (leftType == Types.LONG){
      if (rightType == Types.LONG){
        return Values.make(left.longNum() - right.longNum());
      }
      if (rightType == Types.DOUBLE){
        return Values.make(left.longNum() - right.doubleNum());
      }
    }
    if (leftType == Types.DOUBLE){
      if (rightType == Types.LONG){
        return Values.make(left.doubleNum() - right.longNum());
      }
      if (rightType == Types.DOUBLE){
        return Values.make(left.doubleNum() - right.doubleNum());
      }

    }
    throw new LangException(LangError.CAST_ERROR, "cannot subtract types: "+leftType.name()+" and "+rightType.name(), stack, node.getSourceInfo());

  }

  private void ensureValidTypes(Value left, Value right){
    Type leftType = left.type();
    Type rightType = right.type();

    if ((left == Values.NIL || leftType == Types.DOUBLE || leftType == Types.LONG) &&
        (right == Values.NIL || rightType == Types.DOUBLE || rightType == Types.LONG)){
      return;
    }

    throw new LangException(LangError.CAST_ERROR, "cannot subtract types "+left.type().name()+" and " + right.type().name());

  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {

    Type leftType = node.getLeftExpression().getValueType();
    Type rightType = node.getRightExpression().getValueType();

    try {
      if (leftType == rightType){
        if (leftType == Types.DOUBLE){
          return new MinusOpDD(node);
        }
        if (leftType == Types.LONG){
          if (rightOp.isConstant()){
            Value right = Interpreter.evaluateInEmptyScope(node.getRightExpression());
            if (!right.isNil()){
              // x + non-nil const
              return new MinusOpLCL(node);
            }
          }
          return new MinusOpLL(node);
        }
      }
    } catch (LangException ignored){}


    return new MinusOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new MinusOp(node);
  }

}
