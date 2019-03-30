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

package com.twineworks.tweakflow.lang.analysis.references;

import com.twineworks.tweakflow.lang.analysis.AnalysisSet;
import com.twineworks.tweakflow.lang.analysis.AnalysisStage;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.scope.SymbolType;
import com.twineworks.tweakflow.util.TopoSort;

import java.util.*;
import java.util.stream.Collectors;

public class DependencyVerification {

  private static IdentityHashMap<Symbol, LinkedHashSet<Symbol>> invertDependencies(IdentityHashMap<Symbol, LinkedHashSet<Symbol>> dependencies){
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> dependants = new IdentityHashMap<>();

    for (Symbol symbol : dependencies.keySet()) {
      LinkedHashSet<Symbol> deps = dependencies.get(symbol);
      for (Symbol dep : deps) {
        if (!dependants.containsKey(dep)){
          dependants.put(dep, new LinkedHashSet<>());
        }
        dependants.get(dep).add(symbol);
      }
    }

    return dependants;
  }

  private static boolean isTopLevel(Symbol s){
    return s.getScope().isLibrary() || s.getSymbolType() == SymbolType.ALIAS || s.getSymbolType() == SymbolType.NAME_IMPORT || s.getSymbolType() == SymbolType.EXPORT;
  }

  private static Symbol findTopLevelVar(Symbol symbol){
    Symbol s = symbol;

    while (!isTopLevel(s)){
      Scope scope = s.getScope();
      while (!(scope instanceof Symbol)){
        scope = scope.getEnclosingScope();
        if (scope == null){
          throw new AssertionError("should never be here");
        }
      }
      s = (Symbol) scope;
    }

    return s;
  }

  private static LinkedHashSet<Symbol> traceSymbolsToTopLevelVars(LinkedHashSet<Symbol> symbols){
    LinkedHashSet<Symbol> ret = new LinkedHashSet<>();
    for (Symbol symbol : symbols) {
      ret.add(findTopLevelVar(symbol));
    }

    return ret;
  }

  private static IdentityHashMap<Symbol, LinkedHashSet<Symbol>> topLevelDependees(IdentityHashMap<Symbol, LinkedHashSet<Symbol>> varDependencies){

    // retain only library vars
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> ret = new IdentityHashMap<>();
    for (Symbol symbol : varDependencies.keySet()) {
      if (symbol.getScope().isLibrary()){
        LinkedHashSet<Symbol> deps = varDependencies.get(symbol);
        ret.put(symbol, traceSymbolsToTopLevelVars(deps));
      }
    }

    return ret;
  }

  @SuppressWarnings("unchecked")
  public static void verify(AnalysisSet analysisSet){

    DependencyVerificationVisitor analysis = new DependencyVerificationVisitor();

    for (AnalysisUnit unit : analysisSet.getUnits().values()) {
      if (unit.getStage().getProgress() >= AnalysisStage.DEPENDENCIES_VERIFIED.getProgress()){
        continue;
      }
      analysis.visit(unit.getUnit());
    }

    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> dependencies = analysis.getDirectDependencies();
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> varDependencies = analysis.getVarDependencies();
    analysisSet.setDependencies(dependencies);

    // find global evaluation order
    List<Symbol> globalOrder;

    try {
      globalOrder = TopoSort.calcTopoOrder(dependencies);
    }
    catch (TopoSort.CyclicDependencyException e){
      Symbol s = (Symbol) e.getCyclicItem();

      throw new LangException(LangError.CYCLIC_REFERENCE, s.getNode().getSourceInfo())
          .put("cycle", cycleText(e.getCycle()));
    }

    // form transitive closure of top level dependencies
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> transitiveVarDependencies = transitiveClosure(varDependencies);
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> dependants = invertDependencies(transitiveVarDependencies);

    analysisSet.setDependants(dependants);

    // determine evaluation order per scope
    IdentityHashMap<Scope, List<Symbol>> localOrders = new IdentityHashMap<>();
    for (Symbol symbol : globalOrder) {
      Scope scope = symbol.getScope();

      if (!localOrders.containsKey(scope)){
        localOrders.put(scope, new ArrayList<>());
      }

      List<Symbol> localOrderSymbols = localOrders.get(scope);
      localOrderSymbols.add(symbol);
    }

    // and set them on all scopes
    for (Map.Entry<Scope, List<Symbol>> entry : localOrders.entrySet()) {
      entry.getKey().setDependencyOrderedSymbols(entry.getValue());
    }

    for (AnalysisUnit unit : analysisSet.getUnits().values()) {
      unit.setStage(AnalysisStage.DEPENDENCIES_VERIFIED);
    }

  }

  @SuppressWarnings("unchecked")
  public static void verify(ExpressionNode node){

    DependencyVerificationVisitor analysis = new DependencyVerificationVisitor();
    analysis.visit(node);

    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> dependencies = analysis.getDirectDependencies();
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> varDependencies = analysis.getVarDependencies();

    // find global evaluation order
    List<Symbol> globalOrder;

    try {
      globalOrder = TopoSort.calcTopoOrder(dependencies);
    }
    catch (TopoSort.CyclicDependencyException e){
      Symbol s = (Symbol) e.getCyclicItem();

      throw new LangException(LangError.CYCLIC_REFERENCE, s.getNode().getSourceInfo())
          .put("cycle", cycleText(e.getCycle()));
    }

    // form transitive closure of top level dependencies
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> transitiveVarDependencies = transitiveClosure(varDependencies);
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> dependants = invertDependencies(transitiveVarDependencies);

    // determine evaluation order per scope
    IdentityHashMap<Scope, List<Symbol>> localOrders = new IdentityHashMap<>();
    for (Symbol symbol : globalOrder) {
      Scope scope = symbol.getScope();

      if (!localOrders.containsKey(scope)){
        localOrders.put(scope, new ArrayList<>());
      }

      List<Symbol> localOrderSymbols = localOrders.get(scope);
      localOrderSymbols.add(symbol);
    }

    // and set them on all scopes
    for (Map.Entry<Scope, List<Symbol>> entry : localOrders.entrySet()) {
      entry.getKey().setDependencyOrderedSymbols(entry.getValue());
    }

  }

  private static void transitiveDependencies(Symbol x, IdentityHashMap<Symbol, LinkedHashSet<Symbol>> deps, LinkedHashSet<Symbol> collect){

    LinkedHashSet<Symbol> directDeps = deps.get(x);
    for (Symbol dep : directDeps) {
      if (!collect.contains(dep)){
        collect.add(dep);
        transitiveDependencies(dep, deps, collect);
      }
    }

  }

  private static IdentityHashMap<Symbol, LinkedHashSet<Symbol>> transitiveClosure(IdentityHashMap<Symbol, LinkedHashSet<Symbol>> topLevelDeps) {
    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> transitive = new IdentityHashMap<>();
    for (Symbol symbol : topLevelDeps.keySet()) {
      LinkedHashSet<Symbol> transitiveDeps = new LinkedHashSet<>();
      transitiveDependencies(symbol, topLevelDeps, transitiveDeps);
      transitive.put(symbol, transitiveDeps);
    }

    return transitive;
  }

  private static String cycleText(List<Symbol> cycle){
    return cycle.stream()
        .map((x) -> x.getName() + "@" + x.getNode().getSourceInfo().toString())
        .collect(Collectors.joining(" -> "));
  }

}
