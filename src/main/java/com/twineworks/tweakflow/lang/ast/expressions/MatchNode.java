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

import com.twineworks.tweakflow.lang.interpreter.ops.ConstantOp;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLineNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLines;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchNode extends AExpressionNode implements ExpressionNode {

  private ExpressionNode subject;
  private MatchLines matchLines = new MatchLines();

  @Override
  public List<? extends Node> getChildren() {
    return Arrays.asList(subject, matchLines);
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

    // Check if all branches are uniform, and return the uniform type if they are.
    // Return ANY otherwise.
    ArrayList<MatchLineNode> elements = matchLines.getElements();
    if (elements.isEmpty()) return Types.ANY;
    Type initialType = elements.get(0).getExpression().getValueType();

    for (int i = 1; i < elements.size(); i++) {
      MatchLineNode lineNode = elements.get(i);
      Type lineType = lineNode.getExpression().getValueType();
      if (lineNode != initialType) return Types.ANY;
    }

    return initialType;

  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.MATCH;
  }

  public ExpressionNode getSubject() {
    return subject;
  }

  public MatchNode setSubject(ExpressionNode subject) {
    this.subject = subject;
    return this;
  }

  public MatchLines getMatchLines() {
    return matchLines;
  }
}
