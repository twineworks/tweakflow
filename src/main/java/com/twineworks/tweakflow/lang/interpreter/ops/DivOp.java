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

import com.twineworks.tweakflow.lang.ast.expressions.DivNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;

import java.math.BigDecimal;
import java.math.RoundingMode;

final public class DivOp implements ExpressionOp {

  private final DivNode node;
  private final ExpressionOp leftOp;
  private final ExpressionOp rightOp;
  private final static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  private final static int DEFAULT_SCALE = 20;

  public DivOp(DivNode node) {
    this.node = node;
    leftOp = node.getLeftExpression().getOp();
    rightOp = node.getRightExpression().getOp();
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    Value left = leftOp.eval(stack, context);
    Value right = rightOp.eval(stack, context);

    ensureValidTypes(left, right, stack);

    if (left == Values.NIL) return Values.NIL;
    if (right == Values.NIL) return Values.NIL;

    Type leftType = left.type();
    Type rightType = right.type();

    // normal division promotes longs to doubles or decimals
    if (leftType == Types.LONG){
      if (rightType == Types.LONG){
        return Values.make(left.longNum().doubleValue() / right.longNum().doubleValue());
      }
      if (rightType == Types.DOUBLE){
        return Values.make(left.longNum().doubleValue() / right.doubleNum());
      }
      if (rightType == Types.DECIMAL){
        BigDecimal divisor = right.decimal();
        if (divisor.compareTo(BigDecimal.ZERO) == 0){
          throw new LangException(LangError.DIVISION_BY_ZERO, "division by zero", stack, node.getSourceInfo());
        }
        BigDecimal result = BigDecimal.valueOf(left.longNum()).divide(divisor, DEFAULT_SCALE, ROUNDING_MODE).stripTrailingZeros();
        if(result.scale() < 0){
          result = result.setScale(0);
        }
        return Values.make(result);
      }
    }
    else if (leftType == Types.DOUBLE){
      if (rightType == Types.LONG){
        return Values.make(left.doubleNum() / right.longNum().doubleValue());
      }
      if (rightType == Types.DOUBLE){
        return Values.make(left.doubleNum() / right.doubleNum());
      }
      if (rightType == Types.DECIMAL){
        double d = left.doubleNum();
        if (Double.isFinite(d)){
          BigDecimal divisor = right.decimal();
          if (divisor.compareTo(BigDecimal.ZERO) == 0) throw new LangException(LangError.DIVISION_BY_ZERO, "division by zero", stack, node.getSourceInfo());
          BigDecimal result = BigDecimal.valueOf(left.doubleNum()).divide(divisor, DEFAULT_SCALE, ROUNDING_MODE).stripTrailingZeros();
          if(result.scale() < 0){
            result = result.setScale(0);
          }
          return Values.make(result);
        }
        else{
          // NaN / some_d -> NaN
          if (Double.isNaN(d)) return left;
          // Infinity / pos_d -> Infinity
          // Infinity / neg_d -> -Infinity
          if (right.decimal().compareTo(BigDecimal.ZERO) >= 0){
            return left;
          }
          else {
            return Values.make(-d);
          }
        }
      }
    }
    else if (leftType == Types.DECIMAL){

      if (rightType == Types.LONG){
        BigDecimal divisor = BigDecimal.valueOf(right.longNum());
        if (divisor.compareTo(BigDecimal.ZERO) == 0) throw new LangException(LangError.DIVISION_BY_ZERO, "division by zero", stack, node.getSourceInfo());
        BigDecimal dividend = left.decimal();
        BigDecimal result = dividend.divide(divisor, DEFAULT_SCALE, ROUNDING_MODE).stripTrailingZeros();
        if(result.scale() < dividend.scale()){
          result = result.setScale(dividend.scale(), RoundingMode.UNNECESSARY);
        }
        return Values.make(result);
      }
      if (rightType == Types.DOUBLE){
        double d = right.doubleNum();
        if (Double.isFinite(d)){
          BigDecimal divisor = BigDecimal.valueOf(right.doubleNum());
          if (divisor.compareTo(BigDecimal.ZERO) == 0) throw new LangException(LangError.DIVISION_BY_ZERO, "division by zero", stack, node.getSourceInfo());
          BigDecimal dividend = left.decimal();
          BigDecimal result = dividend.divide(divisor, DEFAULT_SCALE, ROUNDING_MODE).stripTrailingZeros();
          if(result.scale() < dividend.scale()){
            result = result.setScale(dividend.scale(), RoundingMode.UNNECESSARY);
          }
          return Values.make(result);
        }
        else{
          // some_d / NaN -> NaN
          if (Double.isNaN(d)) return right;
          // some_d / +-Infinity -> 0
          return Values.DECIMAL_ZERO;
        }
      }
      if (rightType == Types.DECIMAL){
        BigDecimal divisor = right.decimal();
        if (divisor.compareTo(BigDecimal.ZERO) == 0) throw new LangException(LangError.DIVISION_BY_ZERO, "division by zero", stack, node.getSourceInfo());
        BigDecimal dividend = left.decimal();
        int scale = Math.max(DEFAULT_SCALE, dividend.scale());
        BigDecimal result = dividend.divide(right.decimal(), scale, ROUNDING_MODE).stripTrailingZeros();
        if(result.scale() < dividend.scale()){
          result = result.setScale(dividend.scale(), RoundingMode.UNNECESSARY);
        }
        return Values.make(result);
      }
    }
    throw new LangException(LangError.CAST_ERROR, "cannot divide types: "+leftType.name()+" and "+rightType.name(), stack, node.getSourceInfo());

  }

  private void ensureValidTypes(Value left, Value right, Stack stack){
    Type leftType = left.type();
    Type rightType = right.type();

    if ((left == Values.NIL || leftType == Types.DOUBLE || leftType == Types.LONG || leftType == Types.DECIMAL) &&
        (right == Values.NIL || rightType == Types.DOUBLE || rightType == Types.LONG || rightType == Types.DECIMAL)){
      return;
    }

    throw new LangException(LangError.CAST_ERROR, "cannot divide types "+left.type().name()+" and " + right.type().name(), stack, node.getSourceInfo());

  }

  @Override
  public boolean isConstant() {
    return leftOp.isConstant() && rightOp.isConstant();
  }

  @Override
  public ExpressionOp specialize() {
    return new DivOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new DivOp(node);
  }


}
