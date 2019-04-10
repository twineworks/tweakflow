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
import com.twineworks.tweakflow.lang.interpreter.ops.ExpressionOp;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.types.Type;

import java.util.ArrayList;
import java.util.List;

public class DebugNode implements ExpressionNode {

  private ArrayList<ExpressionNode> expressions;
  private SourceInfo sourceInfo;
  private Scope scope;
  private ExpressionOp op;

  public DebugNode setExpressions(ArrayList<ExpressionNode> expressions) {
    this.expressions = expressions;
    return this;
  }

  public ArrayList<ExpressionNode> getExpressions() {
    return expressions;
  }

  @Override
  public SourceInfo getSourceInfo() {
    return sourceInfo;
  }

  @Override
  public DebugNode setSourceInfo(SourceInfo sourceInfo) {
    this.sourceInfo = sourceInfo;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    return expressions;
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public DebugNode setScope(Scope scope) {
    this.scope = scope;
    return this;
  }

  @Override
  public ExpressionNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public Type getValueType() {
    return expressions.get(expressions.size()-1).getValueType();
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.DEBUG;
  }

  @Override
  public DebugNode setOp(ExpressionOp op) {
    this.op = op;
    return this;
  }

  @Override
  public ExpressionOp getOp() {
    return op;
  }


}
