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

package com.twineworks.tweakflow.lang.analysis.references;

import com.twineworks.tweakflow.lang.analysis.visitors.AExpressionDescendingVisitor;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class ClosureAnalysisVisitor extends AExpressionDescendingVisitor implements Visitor {

  private IdentityHashMap<FunctionNode, Set<ReferenceNode>> functionDependencies = new IdentityHashMap<>();
  private ArrayDeque<Set<ReferenceNode>> depStack = new ArrayDeque<>();

  public IdentityHashMap<FunctionNode, Set<ReferenceNode>> getFunctionDependencies() {
    return functionDependencies;
  }

  @Override
  public ModuleNode visit(ModuleNode node) {
    node.getComponents().forEach(this::visit);
    return node;
  }

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
  public LibraryNode visit(LibraryNode node) {
    visit(node.getVars());
    return node;
  }

  private boolean symbolIsLocalTo(Symbol symbol, Scope scope){

    Scope symbolScope = symbol.getScope();
    if (symbolScope == scope) return true;

    // run up the parent list to check if any of those is the one
    Scope currentScope = symbolScope;
    while(currentScope != null){
      if (currentScope == scope){
        return true;
      }
      else {
        currentScope = currentScope.getEnclosingScope();
      }
    }

    return false;
  }

  @Override
  public FunctionNode visit(FunctionNode node) {

    visit(node.getParameters());

    if (node.getExpression() != null){
      HashSet<ReferenceNode> refs = new HashSet<>();

      depStack.push(refs);
      visit(node.getExpression());

      HashSet<ReferenceNode> nonLocalRefs = new HashSet<>();
      Scope bodyScope = node.getExpression().getScope();
      // must filter to keep only non-local nodes
      for (ReferenceNode ref : refs) {
        Symbol referencedSymbol = ref.getReferencedSymbol();
        if (!symbolIsLocalTo(referencedSymbol, bodyScope)){
          nonLocalRefs.add(ref);
          ref.setClosure(true);
        }
      }

      depStack.pop();
      node.setClosedOverReferences(nonLocalRefs);

      // any parent function inherits this functions dependency references
      Set<ReferenceNode> parentDependencies = depStack.peek();
      if (parentDependencies != null){
        parentDependencies.addAll(nonLocalRefs);
      }
    }

    return node;
  }

  @Override
  public ReferenceNode visit(ReferenceNode node) {
    Set<ReferenceNode> currentDependencies = depStack.peek();
    if (currentDependencies != null){
      currentDependencies.add(node);
    }
    return node;
  }

}
