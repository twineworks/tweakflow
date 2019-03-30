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
import com.twineworks.tweakflow.lang.ast.args.Parameters;
import com.twineworks.tweakflow.lang.ast.meta.ViaNode;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.FunctionSignature;
import com.twineworks.tweakflow.lang.values.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionNode extends AExpressionNode implements ExpressionNode {

  private Parameters parameters = new Parameters();
  private ExpressionNode expression;
  private Set<ReferenceNode> closedOverReferences = new HashSet<>();
  private ViaNode via;
  private FunctionSignature signature;
  private Type declaredReturnType;
  private Value functionValue;

  public Value getFunctionValue() {
    return functionValue;
  }

  public FunctionNode setFunctionValue(Value functionValue) {
    this.functionValue = functionValue;
    return this;
  }

  public ViaNode getVia() {
    return via;
  }

  public FunctionNode setVia(ViaNode via) {
    this.via = via;
    return this;
  }

  public FunctionSignature getSignature() {
    return signature;
  }

  public FunctionNode setSignature(FunctionSignature signature) {
    this.signature = signature;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    List<Node> ret = new ArrayList<>();
    ret.add(parameters);
    if (via != null) ret.add(via);
    if (expression != null) ret.add(expression);
    return ret;
  }

  @Override
  public ExpressionNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public Type getValueType() {
    return Types.FUNCTION;
  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.FUNCTION;
  }

  public ExpressionNode getExpression() {
    return expression;
  }

  public FunctionNode setExpression(ExpressionNode expression) {
    this.expression = expression;
    return this;
  }

  public Parameters getParameters() {
    return parameters;
  }

  public FunctionNode setParameters(Parameters parameters) {
    this.parameters = parameters;
    return this;
  }

  public Type getDeclaredReturnType() {
    return declaredReturnType;
  }

  public FunctionNode setDeclaredReturnType(Type declaredReturnType) {
    this.declaredReturnType = declaredReturnType;
    return this;
  }

  public Set<ReferenceNode> getClosedOverReferences() {
    return closedOverReferences;
  }

  public FunctionNode setClosedOverReferences(Set<ReferenceNode> closedOverReferences) {
    this.closedOverReferences = closedOverReferences;
    return this;
  }


}
