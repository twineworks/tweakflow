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

import com.twineworks.tweakflow.lang.ast.expressions.PowNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

final public class PowOp implements ExpressionOp {

  private final PowNode node;
  private final ExpressionOp leftOp;
  private final ExpressionOp rightOp;

  public PowOp(PowNode node) {
    this.node = node;
    leftOp = node.getLeftExpression().getOp();
    rightOp = node.getRightExpression().getOp();
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    Value base = leftOp.eval(stack, context);
    Value exponent = rightOp.eval(stack, context);

    ensureValidTypes(base, exponent, stack);

    if (base == Values.NIL || exponent == Values.NIL) return Values.NIL;

    Type baseType = base.type();
    Type exponentType = exponent.type();

    if (baseType == Types.LONG && exponentType == Types.LONG){
      double pow = java.lang.Math.pow(base.longNum(), exponent.longNum());
      return Values.make(pow);
    }

    if (baseType == Types.DOUBLE && exponentType == Types.DOUBLE){
      return Values.make(java.lang.Math.pow(base.doubleNum(), exponent.doubleNum()));
    }

    if (baseType == Types.DOUBLE && exponentType == Types.DECIMAL){
      return Values.make(java.lang.Math.pow(base.doubleNum(), exponent.decimal().doubleValue()));
    }

    if (baseType == Types.LONG && exponentType == Types.DOUBLE){
      return Values.make(java.lang.Math.pow(base.longNum(), exponent.doubleNum()));
    }

    if (baseType == Types.LONG && exponentType == Types.DECIMAL){
      return Values.make(java.lang.Math.pow(base.longNum(), exponent.decimal().doubleValue()));
    }

    if (baseType == Types.DOUBLE && exponentType == Types.LONG){
      return Values.make(java.lang.Math.pow(base.doubleNum(), exponent.longNum()));
    }

    if (baseType == Types.DECIMAL && exponentType == Types.LONG){
      long exp = exponent.longNum();
      // documented range of BigDecimal.pow
      if (exp >= 0 && exp <= 999999999){
        return Values.make(base.decimal().pow((int)exp));
      }
      else{
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "exponent too large, must be within range of 0 to 999999999, cannot lift base of type "+base.type().name()+" to exponent of "+exp, stack, node.getSourceInfo());
      }
    }

    if (baseType == Types.DECIMAL && exponentType == Types.DOUBLE){
      return Values.make(java.lang.Math.pow(base.decimal().doubleValue(), exponent.doubleNum()));
    }

    if (baseType == Types.DECIMAL && exponentType == Types.DECIMAL){
      return Values.make(java.lang.Math.pow(base.decimal().doubleValue(), exponent.decimal().doubleValue()));
    }


    throw new LangException(LangError.CAST_ERROR, "cannot lift base of type "+base.type().name()+" to exponent of type " + exponent.type().name(), stack, node.getSourceInfo());

  }

  private void ensureValidTypes(Value left, Value right, Stack stack){
    Type leftType = left.type();
    Type rightType = right.type();

    if ((left == Values.NIL || leftType == Types.DOUBLE || leftType == Types.LONG || leftType == Types.DECIMAL) &&
        (right == Values.NIL || rightType == Types.DOUBLE || rightType == Types.LONG || rightType == Types.DECIMAL)){
      return;
    }

    throw new LangException(LangError.CAST_ERROR, "cannot lift base of type "+left.type().name()+" to exponent of type " + right.type().name(), stack, node.getSourceInfo());

  }


  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new PowOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new PowOp(node);
  }


}
