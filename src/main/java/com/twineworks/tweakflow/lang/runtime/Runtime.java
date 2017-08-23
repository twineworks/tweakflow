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

package com.twineworks.tweakflow.lang.runtime;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.*;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.LocalMemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpace;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.values.Arity1CallSite;
import com.twineworks.tweakflow.lang.values.Arity2CallSite;
import com.twineworks.tweakflow.lang.values.Arity3CallSite;
import com.twineworks.tweakflow.lang.values.Value;

import java.util.*;

public class Runtime {

  public static interface Name { }

  public static class Module {

    private final Cell cell;
    private final Runtime runtime;

    private Module(Runtime runtime, Cell cell) {
      this.cell = cell;
      this.runtime = runtime;
    }

    private Runtime getRuntime(){
      return runtime;
    }

    public Name getName(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, "module does not contain name: "+name);
      }

      if (entityCell.isLibrary()){
        return new Library(runtime, entityCell);
      }
      else if (entityCell.isModule()){
        return new ModuleExports(runtime, entityCell);
      }
      else if (entityCell.isVar()){
        return new Var(runtime, entityCell);
      }

      throw new AssertionError("unexpected cell content");

    }

    public Library getLibrary(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, "module does not contain name: "+name);
      }

      if (entityCell.isLibrary()){
        return new Library(runtime, entityCell);
      }

      throw new LangException(LangError.UNRESOLVED_REFERENCE, "module contains name: "+name+" but it is not a library");

    }

    public void evaluate(){
      Evaluator.evaluateCell(cell, new Stack(), runtime.getEvaluationContext());
    }

  }

  public static class ModuleExports implements Name {

    private final Cell cell;
    private final Runtime runtime;

    private ModuleExports(Runtime runtime, Cell cell) {
      this.cell = cell;
      this.runtime = runtime;
    }

    private Runtime getRuntime(){
      return runtime;
    }

    public Name getName(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, "module exports do not contain name: "+name);
      }

      if (entityCell.isLibrary()){
        return new Library(runtime, entityCell);
      }
      else if (entityCell.isModule()){
        return new ModuleExports(runtime, entityCell);
      }
      else if (entityCell.isVar()){
        return new Var(runtime, entityCell);
      }

      throw new AssertionError("unexpected cell content");

    }

    public Name getLibrary(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, "module exports do not contain name: "+name);
      }

      if (entityCell.isLibrary()){
        return new Library(runtime, entityCell);
      }

      throw new LangException(LangError.UNRESOLVED_REFERENCE, "module exports contain name: "+name+" but it is not a library");

    }

    public void evaluate(){
      Evaluator.evaluateCell(cell, new Stack(), runtime.getEvaluationContext());
    }

  }

  public static class Library implements Name {

    private final Cell cell;
    private final Runtime runtime;

    private Library(Runtime runtime, Cell cell) {
      this.cell = cell;
      this.runtime = runtime;
    }

    public Var getVar(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, "library does not contain var: "+name);
      }

      if (entityCell.isVar()){
        return new Var(runtime, entityCell);
      }

      throw new AssertionError("unexpected cell content");

    }

    public Runtime getRuntime() {
      return runtime;
    }

    public void evaluate(){
      Evaluator.evaluateCell(cell, new Stack(), runtime.getEvaluationContext());
    }
  }

  public static class Var implements Name {

    private final Cell cell;
    private final Runtime runtime;
    private final List<Cell> dependants;

    private Var(Runtime runtime, Cell cell) {

      this.cell = cell;
      this.runtime = runtime;

      RuntimeSet runtimeSet = runtime.getRuntimeSet();
      LocalMemorySpace unitSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace();

      IdentityHashMap<Symbol, LinkedHashSet<Symbol>> allDependants = runtimeSet.getAnalysisSet().getDependants();

      Symbol symbol = cell.getSymbol();

      this.dependants = new ArrayList<>();
      LinkedHashSet<Symbol> dependantSymbols = allDependants.get(symbol);

      if (dependantSymbols != null){
        // collect dirties for re-evaluation
        for (Symbol dep : dependantSymbols) {
          String modulePath = dep.getNode().getSourceInfo().getParseUnit().getPath();
          MemorySpace moduleSpace = unitSpace.getCells().gets(modulePath);
          String libraryName = ((Symbol)dep.getScope()).getNode().getSymbolName();
          String varName = dep.getName();
          Cell depCell = moduleSpace.getCells().gets(libraryName).getCells().gets(varName);
          dependants.add(depCell);
        }
      }

    }

    public Value getValue(){
      return cell.getValue();
    }

    public Runtime getRuntime() {
      return runtime;
    }

    private List<Cell> getDependants(){
      return dependants;
    }

    public Value call(Value ... args) {
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell, Collections.emptyMap()));
      return new EvaluatorUserCallContext(stack, runtime.getEvaluationContext()).call(getValue(), args);
    }

    public Arity1CallSite arity1CallSite() {
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell, Collections.emptyMap()));
      return new EvaluatorUserCallContext(stack, runtime.getEvaluationContext()).createArity1CallSite(getValue());
    }

    public Arity2CallSite arity2CallSite() {
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell, Collections.emptyMap()));
      return new EvaluatorUserCallContext(stack, runtime.getEvaluationContext()).createArity2CallSite(getValue());
    }

    public Arity3CallSite arity3CallSite() {
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell, Collections.emptyMap()));
      return new EvaluatorUserCallContext(stack, runtime.getEvaluationContext()).createArity3CallSite(getValue());
    }

    public void evaluate(){
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell.getEnclosingSpace(), Collections.emptyMap()));
      Evaluator.evaluateCell(cell, stack, runtime.getEvaluationContext());
    }

  }

  private final RuntimeSet runtimeSet;
  private final EvaluationContext context;

  public Runtime(RuntimeSet runtimeSet) {
    this(runtimeSet, new DefaultDebugHandler());
  }

  public Runtime(RuntimeSet runtimeSet, DebugHandler debugHandler) {
    this.runtimeSet = runtimeSet;
    context = new EvaluationContext(debugHandler);
  }

  public void evaluate(){
    Evaluator.evaluateSpace(runtimeSet.getGlobalMemorySpace().getUnitSpace(), context);
  }

  public String moduleKey(String path){
    LoadPath loadPath = runtimeSet.getAnalysisSet().getLoadPath();
    LoadPathLocation loadPathLocation = loadPath.pathLocationFor(path);
    if (loadPathLocation == null){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "module path "+path+" not found");
    }
    return loadPathLocation.resolve(path);
  }

  public Map<String, Module> getModules(){

    HashMap<String, Module> result = new HashMap<>();
    LocalMemorySpace unitSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace();
    ConstShapeMap<Cell> cells = unitSpace.getCells();
    for (ShapeKey key : cells.keySet()) {
      Cell c = cells.get(key);
      if (c.isModule()){
        result.put(key.sym, new Module(this, c));
      }
    }

    return result;

  }

  private RuntimeSet getRuntimeSet() {
    return runtimeSet;
  }

  private EvaluationContext getEvaluationContext() {
    return context;
  }

}
