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

package com.twineworks.tweakflow.lang.runtime;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.analysis.AnalysisResult;
import com.twineworks.tweakflow.lang.analysis.AnalysisSet;
import com.twineworks.tweakflow.lang.ast.MetaDataNode;
import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.NilNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.*;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.LocalMemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.Spaces;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.values.*;

import java.util.*;
import java.util.stream.Collectors;

public class Runtime {

  public interface Node {
    Map<String, Runtime.Node> getChildren();
    List<String> getNames();
    void evaluate();
  }

  public interface Name extends Node { }
  public interface Unit extends Node { }

  public interface DocMeta {
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

    public boolean hasVar(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        return false;
      }

      return entityCell.isVar();

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

    public Runtime getRuntime(){
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

    public boolean hasLibrary(String name){
      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        return false;
      }

      if (entityCell.isLibrary()){
        return true;
      }

      return false;
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

  public static class Library implements Name, DocMeta, ValueProvider {

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

    public boolean hasVar(String name){

      Cell entityCell = cell.getCells().gets(name);

      if (entityCell == null){
        return false;
      }

      return entityCell.isVar();

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

    @Override
    public Value getValue() {
      // returns a map of all contained variables
      TransientDictValue d = new TransientDictValue();
      for (String name : getNames()) {
        Value v = getVar(name).getValue();
        if (v == null){
          v = Values.NIL;
        }
        d.put(name, v);
      }
      return Values.make(d.persistent());
    }
  }

  public static class Var implements Name, DocMeta, ValueProvider {

    private final Cell cell;
    private final Runtime runtime;
    private final List<Cell> dependants;
    private final boolean isProvided;
    private final VarDefNode varDefNode;
    private final Type declaredType;
    private final String name;

    private Var(Runtime runtime, Cell cell) {

      this.cell = cell;
      this.runtime = runtime;
      this.varDefNode = (VarDefNode) cell.getSymbol().getNode();
      this.isProvided = varDefNode.isDeclaredProvided();
      this.declaredType = varDefNode.getDeclaredType();
      this.name = varDefNode.getSymbolName();

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

    public boolean dependsOn(Runtime.Var other){
      LinkedHashSet<Symbol> dependencies = runtime.getRuntimeSet().getAnalysisSet().getDependencies().get(cell.getSymbol());
      if (dependencies == null) return false;
      return dependencies.contains(other.cell.getSymbol());
    }

    public boolean isReferencedBy(Runtime.Var other){
      return dependants.contains(other.cell);
    }

    public Type getDeclaredType() {
      return declaredType;
    }

    public boolean isProvided() {
      return isProvided;
    }

    public boolean isReferenced() {return !dependants.isEmpty();}

    public void update(Value value){
      runtime.updateVar(this, value);
    }

    public void set(Value value){
      runtime.setVar(this, value);
    }

    @Override
    public Value getValue(){
      return cell.getValue();
    }

    public boolean isDirty(){
      return cell.isDirty();
    }

    public String getName() {
      return name;
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
      if (cell.isDirty()){
        Stack stack = new Stack();
        stack.push(new StackEntry(cell.getSymbol().getNode(), cell.getEnclosingSpace(), Collections.emptyMap()));
        Interpreter.evaluateCell(cell, stack, runtime.getEvaluationContext());
      }
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

  public static class UpdateBatch {

    private final Var[] vars;
    private final Cell[] dependants;
    private final EvaluationContext context;
    private final Stack stack = new Stack();

    UpdateBatch (Var[] vars, Cell[] dependants, EvaluationContext context){
      this.vars = vars;
      this.dependants = dependants;
      this.context = context;
    }

    public void update(ValueProvider[] valueProviders){

      for (int i = 0; i < valueProviders.length; i++) {
        Var var = vars[i];
        Value value = valueProviders[i].getValue();
        var.cell.setValue(value.castTo(var.getDeclaredType()));
      }

      for (Cell dependant : dependants) {
        dependant.setDirty(true);
      }

      for (Cell dependant : dependants) {
        if (dependant.isDirty()){
          stack.push(new StackEntry(dependant.getSymbol().getNode(), dependant, Collections.emptyMap()));
          Interpreter.evaluateCell(dependant, stack, context);
          stack.pop();
        }
      }
    }

    public void set(ValueProvider[] valueProviders){

      for (int i = 0; i < valueProviders.length; i++) {
        Var var = vars[i];
        Value value = valueProviders[i].getValue();
        var.cell.setValue(value.castTo(var.getDeclaredType()));
      }

      for (Cell dependant : dependants) {
        dependant.setDirty(true);
      }

    }
  }

  public static class ChangeSensitiveUpdateBatch {

    private final Var[] vars;
    private final HashSet<Cell> dependants;
    private final EvaluationContext context;
    private final Stack stack = new Stack();

    ChangeSensitiveUpdateBatch(Var[] vars, EvaluationContext context){
      this.vars = vars;
      this.dependants = new HashSet<>();
      this.context = context;
    }

    public void update(ValueProvider[] valueProviders){

      dependants.clear();
      for (int i = 0; i < valueProviders.length; i++) {
        Var var = vars[i];
        Value value = valueProviders[i].getValue().castTo(var.getDeclaredType());
        Value existing = var.getValue();
        if (!existing.equals(value)){
          var.cell.setValue(value);
          dependants.addAll(var.dependants);
        }
      }

      for (Cell dependant : dependants) {
        dependant.setDirty(true);
      }

      for (Cell dependant : dependants) {
        if (dependant.isDirty()){
          stack.push(new StackEntry(dependant.getSymbol().getNode(), dependant, Collections.emptyMap()));
          Interpreter.evaluateCell(dependant, stack, context);
          stack.pop();
        }
      }
      dependants.clear();
    }

    public void set(ValueProvider[] valueProviders){

      dependants.clear();
      for (int i = 0; i < valueProviders.length; i++) {
        Var var = vars[i];
        Value value = valueProviders[i].getValue().castTo(var.getDeclaredType());
        Value existing = var.getValue();
        if (!existing.equals(value)){
          var.cell.setValue(value);
          dependants.addAll(var.dependants);
        }
      }

      for (Cell dependant : dependants) {
        dependant.setDirty(true);
      }
      dependants.clear();

    }
  }

  private final RuntimeSet runtimeSet;
  private final EvaluationContext context;

  public Runtime(RuntimeSet runtimeSet) {
    this(runtimeSet, new SimpleDebugHandler());
  }

  public Runtime(RuntimeSet runtimeSet, DebugHandler debugHandler) {
    this.runtimeSet = runtimeSet;
    context = new EvaluationContext(debugHandler);
  }

  public Runtime copy(){
    RuntimeSet rs = runtimeSet.copy();
    return new Runtime(rs, getDebugHandler());
  }

  public Runtime copy(DebugHandler debugHandler){
    RuntimeSet rs = runtimeSet.copy();
    return new Runtime(rs, debugHandler);
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

  public void setVars(Runtime.Var[] vars, Value[] values){

    Objects.requireNonNull(vars, "vars cannot be null");
    Objects.requireNonNull(values, "values cannot be null");

    if (vars.length != values.length){
      throw new IllegalArgumentException("vars and values must have same length");
    }

    for (int i = 0; i < vars.length; i++) {
      Var var = vars[i];
      Objects.requireNonNull(var, "index: "+i+" var cannot be null");
      Value value = values[i];
      Objects.requireNonNull(value, "index: "+i+" value cannot be null, use Values.NIL instead");

      if (!var.isProvided()) throw new UnsupportedOperationException("index: "+i+" only vars declared as provided can change.");

      Value existing = var.cell.getValue();
      if (value.equals(existing)) continue;
      try {
        var.cell.setValue(value.castTo(var.getDeclaredType()));
      }
      catch (LangException e){
        e.setSourceInfo(var.varDefNode.getSourceInfo());
        throw e;
      }

    }

  }

  public void updateVars(Runtime.Var[] vars, Value[] values){

    Objects.requireNonNull(vars, "vars cannot be null");
    Objects.requireNonNull(values, "values cannot be null");

    if (vars.length != values.length){
      throw new IllegalArgumentException("vars and values must have same length");
    }

    HashSet<Cell> dependants = new HashSet<>();
    for (int i = 0; i < vars.length; i++) {
      Var var = vars[i];
      Objects.requireNonNull(var, "index: "+i+" var cannot be null");
      Value value = values[i];
      Objects.requireNonNull(value, "index: "+i+" value cannot be null, use Values.NIL instead");

      if (!var.isProvided()) throw new UnsupportedOperationException("index: "+i+" only vars declared as provided can change.");

      // if there is no change in value, there's no need to re-evaluate anything
      Value existing = var.cell.getValue();
      if (value.equals(existing)) continue;
      var.cell.setValue(value.castTo(var.getDeclaredType()));
      dependants.addAll(var.dependants);
    }

    for (Cell dependant : dependants) {
      dependant.setDirty(true);
    }

    for (Cell dependant : dependants) {
      Stack stack = new Stack();
      stack.push(new StackEntry(dependant.getSymbol().getNode(), dependant, Collections.emptyMap()));
      Interpreter.evaluateCell(dependant, stack, getEvaluationContext());
    }

  }

  public void updateVars(Object ... varsAndValuesInPairs){

    Objects.requireNonNull(varsAndValuesInPairs, "vars cannot be null");

    if (varsAndValuesInPairs.length % 2 != 0){
      throw new IllegalArgumentException("vars and values must come in pairs");
    }

    HashSet<Cell> dependants = new HashSet<>();
    for (int i = 0; i < varsAndValuesInPairs.length; i+=2) {
      Var var = (Var) varsAndValuesInPairs[i];
      Objects.requireNonNull(var, "index: "+i+" var cannot be null");
      Value value = (Value) varsAndValuesInPairs[i+1];
      Objects.requireNonNull(value, "index: "+(i+1)+" value cannot be null, use Values.NIL instead");

      if (!var.isProvided()) throw new UnsupportedOperationException("index: "+i+" only vars declared as provided can change.");

      // if there is no change in value, there's no need to re-evaluate anything
      Value existing = var.cell.getValue();
      if (value.equals(existing)) continue;
      var.cell.setValue(value.castTo(var.getDeclaredType()));
      dependants.addAll(var.dependants);
    }

    for (Cell dependant : dependants) {
      dependant.setDirty(true);
    }

    for (Cell dependant : dependants) {
      Stack stack = new Stack();
      stack.push(new StackEntry(dependant.getSymbol().getNode(), dependant, Collections.emptyMap()));
      Interpreter.evaluateCell(dependant, stack, getEvaluationContext());
    }

  }

  public void updateVars(List<Runtime.Var> vars, List<Value> values){

    Objects.requireNonNull(vars, "vars cannot be null");
    Objects.requireNonNull(values, "values cannot be null");

    if (vars.size() != values.size()){
      throw new IllegalArgumentException("vars and values must have same size");
    }

    HashSet<Cell> dependants = new HashSet<>();
    for (int i = 0; i < vars.size(); i++) {
      Var var = vars.get(i);
      Objects.requireNonNull(var, "index: "+i+" var cannot be null");
      Value value = values.get(i);
      Objects.requireNonNull(value, "index: "+i+" value cannot be null, use Values.NIL instead");

      if (!var.isProvided()) throw new UnsupportedOperationException("index: "+i+" only vars declared as provided can change.");

      // if there is no change in value, there's no need to re-evaluate anything
      Value existing = var.cell.getValue();
      if (value.equals(existing)) continue;
      var.cell.setValue(value.castTo(var.getDeclaredType()));
      dependants.addAll(var.dependants);
    }

    for (Cell dependant : dependants) {
      dependant.setDirty(true);
    }

    for (Cell dependant : dependants) {
      Stack stack = new Stack();
      stack.push(new StackEntry(dependant.getSymbol().getNode(), dependant, Collections.emptyMap()));
      Interpreter.evaluateCell(dependant, stack, getEvaluationContext());
    }

  }

  private void updateVar(Runtime.Var var, Value value){

    Objects.requireNonNull(value, "Value cannot be null, use Values.NIL instead");
    if (!var.isProvided()) throw new UnsupportedOperationException("Only vars declared as provided can change.");

    // if there is no change in value, there's no need to re-evaluate anything
    Value existing = var.cell.getValue();
    if (value.equals(existing)) return;

    var.cell.setValue(value.castTo(var.getDeclaredType()));

    for (Cell dependant : var.dependants) {
      dependant.setDirty(true);
    }

    for (Cell dependant : var.dependants) {
      Stack stack = new Stack();
      stack.push(new StackEntry(dependant.getSymbol().getNode(), dependant, Collections.emptyMap()));
      Interpreter.evaluateCell(dependant, stack, getEvaluationContext());
    }

  }

  private void setVar(Runtime.Var var, Value value){

    Objects.requireNonNull(value, "Value cannot be null, use Values.NIL instead");
    if (!var.isProvided()) throw new UnsupportedOperationException("Only vars declared as provided can change.");

    var.cell.setValue(value.castTo(var.getDeclaredType()));

  }

  public CallContext createCallContext(){
    ParseUnit parseUnit = new MemoryLocation.Builder().add("<none>", "").build().getParseUnit("<none>");
    ExpressionNode stubNode = new NilNode().setSourceInfo(new SourceInfo(parseUnit, 0, 0, 0, 0));

    Stack stack = new Stack();
    stack.push(new StackEntry(stubNode, new Cell().setValue(Values.NIL), Collections.emptyMap()));
    return new CallContext(stack, getEvaluationContext());
  }

  public UpdateBatch createUpdateBatch(Runtime.Var[] vars){
    HashSet<Cell> dependants = new HashSet<>();
    for (int i = 0; i < vars.length; i++) {
      Var var = vars[i];
      if (!var.isProvided()) throw new UnsupportedOperationException("index: "+i+" only vars declared as provided can change.");
      dependants.addAll(var.dependants);
    }
    return new UpdateBatch(vars, dependants.toArray(new Cell[0]), getEvaluationContext());
  }

  public ChangeSensitiveUpdateBatch createChangeSensitiveUpdateBatch(Runtime.Var[] vars){

    for (int i = 0; i < vars.length; i++) {
      Var var = vars[i];
      if (!var.isProvided()) throw new UnsupportedOperationException("index: "+i+" only vars declared as provided can change.");
    }
    return new ChangeSensitiveUpdateBatch(vars, getEvaluationContext());
  }

}
