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

package com.twineworks.tweakflow.lang.ast.expressions;

import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;

import java.util.Arrays;
import java.util.List;

public class DefaultNode extends AExpressionNode implements ExpressionNode {

  private ExpressionNode expression;
  private ExpressionNode defaultExpression;

  public ExpressionNode getExpression() {
    return expression;
  }

  public DefaultNode setExpression(ExpressionNode expression) {
    this.expression = expression;
    return this;
  }

  public ExpressionNode getDefaultExpression() {
    return defaultExpression;
  }

  public DefaultNode setDefaultExpression(ExpressionNode defaultExpression) {
    this.defaultExpression = defaultExpression;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    return Arrays.asList(expression, defaultExpression);
  }

  @Override
  public ExpressionNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public Type getValueType() {
    if (expression.getValueType() == defaultExpression.getValueType()) return expression.getValueType();
    return Types.ANY;
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.DEFAULT;
  }




}
