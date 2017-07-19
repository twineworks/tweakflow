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

package com.twineworks.tweakflow.lang.ast.expressions;

import com.twineworks.tweakflow.interpreter.ops.ConstantOp;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.structure.VarDecNode;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;

import java.util.ArrayList;
import java.util.List;

public class TryCatchNode extends AExpressionNode implements ExpressionNode {

  private ExpressionNode tryExpression;
  private VarDecNode caughtException;
  private VarDecNode caughtTrace;
  private ExpressionNode catchExpression;

  public ExpressionNode getCatchExpression() {
    return catchExpression;
  }

  public TryCatchNode setCatchExpression(ExpressionNode catchExpression) {
    this.catchExpression = catchExpression;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    List<Node> ret = new ArrayList<>();
    ret.add(tryExpression);
    if (caughtException != null) ret.add(caughtException);
    if (caughtTrace != null) ret.add(caughtTrace);
    ret.add(catchExpression);
    return ret;
  }

  @Override
  public ExpressionNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public Type getValueType() {

    if (expressionOp instanceof ConstantOp){
      return expressionOp.eval(null, null).type();
    }

    Type tryType = tryExpression.getValueType();
    Type catchType = catchExpression.getValueType();

    if (tryType == catchType){
      return tryType;
    }
    else{
      return Types.ANY;
    }

  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.TRY_CATCH;
  }

  public ExpressionNode getTryExpression() {
    return tryExpression;
  }

  public TryCatchNode setTryExpression(ExpressionNode tryExpression) {
    this.tryExpression = tryExpression;
    return this;
  }

  public VarDecNode getCaughtException() {
    return caughtException;
  }

  public TryCatchNode setCaughtException(VarDecNode caughtException) {
    this.caughtException = caughtException;
    return this;
  }

  public VarDecNode getCaughtTrace() {
    return caughtTrace;
  }

  public TryCatchNode setCaughtTrace(VarDecNode caughtTrace) {
    this.caughtTrace = caughtTrace;
    return this;
  }

}
