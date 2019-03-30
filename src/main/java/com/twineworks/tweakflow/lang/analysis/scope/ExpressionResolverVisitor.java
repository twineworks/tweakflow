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

package com.twineworks.tweakflow.lang.analysis.scope;

import com.twineworks.tweakflow.lang.analysis.visitors.AExpressionDescendingVisitor;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.structure.InteractiveNode;
import com.twineworks.tweakflow.lang.ast.structure.InteractiveSectionNode;
import com.twineworks.tweakflow.lang.ast.structure.LibraryNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Scopes;

public class ExpressionResolverVisitor extends AExpressionDescendingVisitor implements Visitor {

  @Override
  public InteractiveNode visit(InteractiveNode node) {
    node.getSections().forEach(this::visit);
    return node;
  }

  @Override
  public InteractiveSectionNode visit(InteractiveSectionNode node) {
    visit(node.getVars());
    return node;
  }

  @Override
  public ModuleNode visit(ModuleNode node) {
    node.getImports().forEach(this::visit);
    node.getComponents().forEach(this::visit);
    return node;
  }

  @Override
  public LibraryNode visit(LibraryNode node) {
    visit(node.getVars());
    return node;
  }

  @Override
  public ReferenceNode visit(ReferenceNode node) {
    node.setReferencedSymbol(Scopes.resolve(node));

    // vars in ordered scopes must be referenced after they are defined

    Scope scope = node.getReferencedSymbol().getScope();
    if (scope.isOrdered()){
      SourceInfo varSrc = node.getReferencedSymbol().getNode().getSourceInfo();
      SourceInfo refSrc = node.getSourceInfo();
      if (refSrc.precedes(varSrc)){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, node.getSourceInfo());
      }
    }
    return node;
  }


}
