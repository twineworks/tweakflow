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

import com.twineworks.tweakflow.lang.interpreter.ops.ConstantOp;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReferenceNode extends AExpressionNode implements ExpressionNode {

  public enum Anchor {
    LOCAL, LIBRARY, FLOW, MODULE, GLOBAL
  }

  private Anchor anchor = Anchor.LOCAL;
  private List<String> elements = new ArrayList<>();
  private Symbol referencedSymbol;
  private boolean isClosure = false;
  private boolean isSimpleLocal = false;
  private boolean isSimpleParent = false;
  private String simpleName;

  public Symbol getReferencedSymbol() {
    return referencedSymbol;
  }

  public ReferenceNode setReferencedSymbol(Symbol referencedSymbol) {
    this.referencedSymbol = referencedSymbol;
    isSimpleLocal = (referencedSymbol != null) && referencedSymbol.getScope() == scope && elements.size() == 1;
    isSimpleParent = (referencedSymbol != null) && referencedSymbol.getScope() == scope.getEnclosingScope() && elements.size() == 1;
    if (isSimpleLocal || isSimpleParent){
      simpleName = elements.get(0);
    }

    return this;
  }

  public boolean isSimpleLocal(){
    return isSimpleLocal;
  }

  public boolean isSimpleParent() {
    return isSimpleParent;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public boolean isClosure() {
    return isClosure;
  }

  public ReferenceNode setClosure(boolean isClosure) {
    this.isClosure = isClosure;
    return this;
  }

  public boolean isResolved(){
    return referencedSymbol != null;
  }

  public Anchor getAnchor() {
    return anchor;
  }

  public ReferenceNode setAnchor(Anchor anchor) {
    this.anchor = anchor;
    return this;
  }

  public List<String> getElements() {
    return elements;
  }

  public ReferenceNode setElements(List<String> elements) {
    this.elements = elements;
    return this;
  }

  @Override
  public List<? extends Node> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public ExpressionNode accept(Visitor visitor) {
    return visitor.visit(this);
  }

  @Override
  public ReferenceNode copy() {
    ReferenceNode copy = new ReferenceNode();
    copy.sourceInfo = sourceInfo;
    copy.anchor = anchor;
    copy.elements.addAll(elements);
    copy.simpleName = simpleName;
    copy.isClosure = isClosure;
    copy.isSimpleLocal = isSimpleLocal;
    copy.isSimpleParent = isSimpleParent;
    return copy;
  }

  @Override
  public Type getValueType() {

    if (expressionOp instanceof ConstantOp){
      return expressionOp.eval(null, null).type();
    }

    if (isResolved()){
      return referencedSymbol.getVarType();
    }
    else{
      return Types.ANY;
    }

  }

  @Override
  public ExpressionType getExpressionType() {
    return ExpressionType.REFERENCE;
  }


}
