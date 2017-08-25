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
import com.twineworks.tweakflow.lang.interpreter.DebugHandler;
import com.twineworks.tweakflow.lang.analysis.AnalysisResult;
import com.twineworks.tweakflow.lang.analysis.AnalysisSet;
import com.twineworks.tweakflow.lang.ast.MetaDataNode;
import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.*;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.LocalMemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.Spaces;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.values.Arity1CallSite;
import com.twineworks.tweakflow.lang.values.Arity2CallSite;
import com.twineworks.tweakflow.lang.values.Arity3CallSite;
import com.twineworks.tweakflow.lang.values.Value;

import java.util.*;
import java.util.stream.Collectors;

public class Runtime {

  public static interface Node {
    Map<String, Runtime.Node> getChildren();
    List<String> getNames();
    void evaluate();
  }
  public static interface Name extends Node { }
  public static interface Unit extends Node { }

  public static interface DocMeta {
    Value getMeta();
    Value getDoc();
  }

  public static class Globals implements Node {

    private final MemorySpace space;
    private final Runtime runtime;

    private Globals(Runtime runtime, MemorySpace space) {
      this.space = space;
      this.runtime = runtime;
    }

    @Override
    public Map<String, Node> getChildren() {
      return runtime.childrenOf(space);
    }

    public List<String> getNames(){
      return runtime.namesOf(space);
    }

    @Override
    public void evaluate() {
      Interpreter.evaluateSpace(space, runtime.getEvaluationContext());
    }

  }

  public static class Units implements Node {

    private final MemorySpace space;
    private final Runtime runtime;

    private Units(Runtime runtime, MemorySpace space) {
      this.space = space;
      this.runtime = runtime;
    }

    @Override
    public Map<String, Node> getChildren() {
      return runtime.childrenOf(space);
    }

    public List<String> getNames(){
      return runtime.namesOf(space);
    }

    @Override
    public void evaluate() {
      Interpreter.evaluateSpace(space, runtime.getEvaluationContext());
    }

  }

  public static class Exports implements Node {

    private final MemorySpace space;
    private final Runtime runtime;

    private Exports(Runtime runtime, MemorySpace space) {
      this.space = space;
      this.runtime = runtime;
    }

    @Override
    public Map<String, Node> getChildren() {
      return runtime.childrenOf(space);
    }

    public List<String> getNames(){
      return runtime.namesOf(space);
    }

    @Override
    public void evaluate() {
      Interpreter.evaluateSpace(space, runtime.getEvaluationContext());
    }
  }

  public static class InteractiveUnit implements Unit {
    private final Cell cell;
    private final Runtime runtime;

    private InteractiveUnit(Runtime runtime, Cell cell) {
      this.cell = cell;
      this.runtime = runtime;
    }

    private Runtime getRuntime(){
      return runtime;
    }

    public List<String> getNames(){
      return cell.getCells().keySet().stream()
          .map((x) -> x.sym)
          .collect(Collectors.toList());
    }

    public InteractiveSection getSection(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, "interactive unit does not contain name: "+name);
      }

      if (entityCell.isInteractiveSection()){
        return new InteractiveSection(runtime, entityCell);
      }

      throw new LangException(LangError.UNRESOLVED_REFERENCE, "interactive unit contains "+name+" but it is not a section");

    }

    public void evaluate(){
      Interpreter.evaluateCell(cell, new Stack(), runtime.getEvaluationContext());
    }

    @Override
    public Map<String, Node> getChildren() {
      return runtime.childrenOf(cell);
    }
  }

  public static class InteractiveSection implements Name {

    private final Cell cell;
    private final Runtime runtime;

    private InteractiveSection(Runtime runtime, Cell cell) {
      this.cell = cell;
      this.runtime = runtime;
    }

    @Override
    public List<String> getNames(){
      return runtime.namesOf(cell);
    }

    public Var getVar(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, "interactive section does not contain var: "+name);
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
      Interpreter.evaluateCell(cell, new Stack(), runtime.getEvaluationContext());
    }

    public Name resolve(ReferenceNode node){

      Cell resolved = Spaces.resolve(node, cell);

      if (resolved.isLibrary()){
        return new Library(runtime, resolved);
      }
      else if (resolved.isModule()){
        return new ModuleExports(runtime, resolved);
      }
      else if (resolved.isVar()){
        return new Var(runtime, resolved);
      }

      throw new AssertionError("unexpected cell content");
    }

    @Override
    public Map<String, Node> getChildren() {
      return runtime.childrenOf(cell);
    }
  }

  public static class Module implements Unit, DocMeta {

    private final Cell cell;
    private final Runtime runtime;

    private Module(Runtime runtime, Cell cell) {
      this.cell = cell;
      this.runtime = runtime;
    }

    private Runtime getRuntime(){
      return runtime;
    }

    @Override
    public List<String> getNames(){
      return runtime.namesOf(cell);
    }

    public Name resolve(ReferenceNode node){

      Cell resolved = Spaces.resolve(node, cell);

      if (resolved.isLibrary()){
        return new Library(runtime, resolved);
      }
      else if (resolved.isModule()){
        return new ModuleExports(runtime, resolved);
      }
      else if (resolved.isVar()){
        return new Var(runtime, resolved);
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
      Interpreter.evaluateCell(cell, new Stack(), runtime.getEvaluationContext());
    }

    @Override
    public Value getMeta() {
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      return Interpreter.evaluateMetaExpression((MetaDataNode) targetNode);
    }

    @Override
    public Value getDoc() {
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      return Interpreter.evaluateDocExpression((MetaDataNode) targetNode);
    }

    @Override
    public Map<String, Node> getChildren() {
      return runtime.childrenOf(cell);
    }
  }

  public static class ModuleExports implements Name, DocMeta {

    private final Cell cell;
    private final Runtime runtime;

    private ModuleExports(Runtime runtime, Cell cell) {
      this.cell = cell;
      this.runtime = runtime;
    }

    private Runtime getRuntime(){
      return runtime;
    }

    @Override
    public List<String> getNames(){
      return runtime.namesOf(cell);
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
      Interpreter.evaluateCell(cell, new Stack(), runtime.getEvaluationContext());
    }

    @Override
    public Value getMeta() {
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      return Interpreter.evaluateMetaExpression((MetaDataNode) targetNode);
    }

    @Override
    public Value getDoc() {
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      return Interpreter.evaluateDocExpression((MetaDataNode) targetNode);
    }

    @Override
    public Map<String, Node> getChildren() {
      return runtime.childrenOf(cell);
    }

  }

  public static class Library implements Name, DocMeta {

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


    @Override
    public List<String> getNames(){
      return runtime.namesOf(cell);
    }

    public void evaluate(){
      Interpreter.evaluateCell(cell, new Stack(), runtime.getEvaluationContext());
    }

    @Override
    public Value getMeta() {
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      return Interpreter.evaluateMetaExpression((MetaDataNode) targetNode);
    }

    @Override
    public Value getDoc() {
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      return Interpreter.evaluateDocExpression((MetaDataNode) targetNode);
    }

    @Override
    public Map<String, Node> getChildren() {
      return runtime.childrenOf(cell);
    }
  }

  public static class Var implements Name, DocMeta {

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
      return new CallContext(stack, runtime.getEvaluationContext()).call(getValue(), args);
    }

    public Arity1CallSite arity1CallSite() {
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell, Collections.emptyMap()));
      return new CallContext(stack, runtime.getEvaluationContext()).createArity1CallSite(getValue());
    }

    public Arity2CallSite arity2CallSite() {
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell, Collections.emptyMap()));
      return new CallContext(stack, runtime.getEvaluationContext()).createArity2CallSite(getValue());
    }

    public Arity3CallSite arity3CallSite() {
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell, Collections.emptyMap()));
      return new CallContext(stack, runtime.getEvaluationContext()).createArity3CallSite(getValue());
    }

    public void evaluate(){
      Stack stack = new Stack();
      stack.push(new StackEntry(cell.getSymbol().getNode(), cell.getEnclosingSpace(), Collections.emptyMap()));
      Interpreter.evaluateCell(cell, stack, runtime.getEvaluationContext());
    }

    @Override
    public Value getMeta() {
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      return Interpreter.evaluateMetaExpression((MetaDataNode) targetNode);
    }

    @Override
    public Value getDoc() {
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      return Interpreter.evaluateDocExpression((MetaDataNode) targetNode);
    }

    @Override
    public Map<String, Node> getChildren() {
      return Collections.emptyMap();
    }


    @Override
    public List<String> getNames(){
      return Collections.emptyList();
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
    Interpreter.evaluateSpace(runtimeSet.getGlobalMemorySpace().getUnitSpace(), context);
  }

  public String unitKey(String path){
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

  public Runtime.Units getUnits(){
    LocalMemorySpace unitSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace();
    return new Runtime.Units(this, unitSpace);
  }

  public Runtime.Globals getGlobals() {
    MemorySpace space = runtimeSet.getGlobalMemorySpace();
    return new Runtime.Globals(this, space);
  }

  public Runtime.Exports getExports() {
    MemorySpace space = runtimeSet.getGlobalMemorySpace().getExportSpace();
    return new Runtime.Exports(this, space);
  }

  private Node nodeFor(Cell cell) {

    if (cell == null){
      throw new NullPointerException("cell cannot be null");
    }
    if (cell.isLibrary()){
      return new Library(this, cell);
    }
    else if (cell.isModule()){
      return new ModuleExports(this, cell);
    }
    else if (cell.isVar()){
      return new Var(this, cell);
    }
    else if (cell.isInteractiveUnit()){
      return new InteractiveUnit(this, cell);
    }
    else if (cell.isInteractiveSection()){
      return new InteractiveSection(this, cell);
    }

    throw new AssertionError("unexpected cell content");

  }

  private Map<String, Runtime.Node> childrenOf(MemorySpace space){
    HashMap<String, Runtime.Node> children = new HashMap<>();
    ConstShapeMap<Cell> cells = space.getCells();
    for (ShapeKey key : cells.keySet()) {
      children.put(key.sym, nodeFor(cells.get(key)));
    }

    return children;
  }

  private List<String> namesOf(MemorySpace space){
    return space.getCells().keySet().stream()
        .map((x) -> x.sym)
        .collect(Collectors.toList());
  }

  /**
   *
   * @return foo
   */
  public RuntimeSet getRuntimeSet() {
    return runtimeSet;
  }

  public AnalysisSet getAnalysisSet(){
    return runtimeSet.getAnalysisSet();
  }

  public AnalysisResult getAnalysisResult(){
    return runtimeSet.getAnalysisResult();
  }

  public DebugHandler getDebugHandler(){
    return context.getDebugHandler();
  }

  private EvaluationContext getEvaluationContext() {
    return context;
  }

}
