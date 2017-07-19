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
import com.twineworks.tweakflow.lang.ast.structure.match.CapturePatternNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.scope.SymbolTarget;

import java.util.*;

public class DependencyVerificationVisitor extends AExpressionDescendingVisitor implements Visitor {

  private IdentityHashMap<Symbol, LinkedHashSet<Symbol>> directDependencies = new IdentityHashMap<>();
  private IdentityHashMap<Symbol, LinkedHashSet<Symbol>> varDependencies = new IdentityHashMap<>();

  private ArrayDeque<LinkedHashSet<Symbol>> depStack = new ArrayDeque<>();
  private ArrayDeque<LinkedHashSet<Symbol>> topLevelStack = new ArrayDeque<>();

  public IdentityHashMap<Symbol, LinkedHashSet<Symbol>> getDirectDependencies() {
    return directDependencies;
  }

  public IdentityHashMap<Symbol, LinkedHashSet<Symbol>> getVarDependencies() {
    return varDependencies;
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
  public ModuleNode visit(ModuleNode node) {
    node.getComponents().forEach(this::visit);
    return node;
  }

  @Override
  public LibraryNode visit(LibraryNode node) {
    visit(node.getVars());
    return node;
  }

  @Override
  public VarDefNode visit(VarDefNode node) {

    // if this var is defined as a local var inside another variable,
    // the child is a dependency of the parent
    LinkedHashSet<Symbol> parentDependencies = depStack.peek();
    if (parentDependencies != null){
      parentDependencies.add(node.getSymbol());
    }

    boolean libraryVar = node.getSymbol().isLibraryVar();

    LinkedHashSet<Symbol> myDependencies = new LinkedHashSet<>();
    LinkedHashSet<Symbol> myVarDependencies = libraryVar ? new LinkedHashSet<>() : null;
    depStack.push(myDependencies);

    if (libraryVar){
      topLevelStack.push(myVarDependencies);
    }

    visit(node.getValueExpression());
    depStack.pop();
    directDependencies.put(node.getSymbol(), myDependencies);

    if (libraryVar){
      topLevelStack.pop();
      varDependencies.put(node.getSymbol(), myVarDependencies);
    }

    return node;
  }

  @Override
  public GeneratorNode visit(GeneratorNode node) {

    // if this var is defined as a local var inside another variable,
    // the child is a dependency of the parent
    LinkedHashSet<Symbol> parentDependencies = depStack.peek();
    if (parentDependencies != null){
      parentDependencies.add(node.getSymbol());
    }

    LinkedHashSet<Symbol> myDependencies = new LinkedHashSet<>();
    depStack.push(myDependencies);
    visit(node.getValueExpression());
    depStack.pop();
    directDependencies.put(node.getSymbol(), myDependencies);

    return node;
  }

  @Override
  public CapturePatternNode visit(CapturePatternNode node) {

    // ignore, if this does not bind anything
    if (node.getSymbolName() == null) return node;

    // if this var is defined as a local var inside another variable,
    // the child is a dependency of the parent
    LinkedHashSet<Symbol> parentDependencies = depStack.peek();
    if (parentDependencies != null){
      parentDependencies.add(node.getSymbol());
    }

    LinkedHashSet<Symbol> myDependencies = new LinkedHashSet<>();
    directDependencies.put(node.getSymbol(), myDependencies);

    return node;
  }

  @Override
  public CallNode visit(CallNode node) {

     visit(node.getExpression());

    // if the node is a reference, it must point to a value
    if (node.getExpression() instanceof ReferenceNode){
      ReferenceNode ref = (ReferenceNode) node.getExpression();

      if (ref.getReferencedSymbol().getTarget() != SymbolTarget.VAR){
        throw new LangException(
            LangError.INVALID_REFERENCE_TARGET,
            "Cannot call "+ref.getReferencedSymbol().getTarget().name()+". Not a value.",
            node.getSourceInfo()
        );
      }

    }
    visit(node.getArguments());
    return node;
  }

  @Override
  public ReferenceNode visit(ReferenceNode node) {
    Set<Symbol> currentDependencies = depStack.peek();

    if (currentDependencies != null){
      Symbol referencedSymbol = node.getReferencedSymbol();
      // expressions can only depend on expressions
      if (referencedSymbol.getTarget() != SymbolTarget.VAR){
        throw new LangException(LangError.INVALID_REFERENCE_TARGET, "Cannot reference "+referencedSymbol.getTarget().name()+". Not a value.", node.getSourceInfo());
      }
      // functions can call themselves recursively and are not part of cyclic dependency detection
      if (referencedSymbol.getTargetNode() instanceof NamedValueNode){
        NamedValueNode ref = (NamedValueNode) referencedSymbol.getTargetNode();
        if (ref.getValueExpression().getExpressionType() != ExpressionType.FUNCTION){
          currentDependencies.add(referencedSymbol);
        }
      }

    }

    LinkedHashSet<Symbol> currentVarDependencies = topLevelStack.peek();
    if (currentVarDependencies != null){
      Symbol referencedSymbol = node.getReferencedSymbol();
      if (referencedSymbol.isLibraryVar()){
        currentVarDependencies.add(referencedSymbol);
      }
    }
    return node;
  }

}
