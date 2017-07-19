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

package com.twineworks.tweakflow.interpreter.runtime;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.Loader;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;
import com.twineworks.tweakflow.lang.load.user.UserObjectFactory;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.interpreter.*;
import com.twineworks.tweakflow.interpreter.Stack;
import com.twineworks.tweakflow.interpreter.memory.Cell;
import com.twineworks.tweakflow.interpreter.memory.LocalMemorySpace;
import com.twineworks.tweakflow.interpreter.memory.MemorySpace;

import java.util.*;

public class TweakFlowRuntime {

  public static class VarHandle {

    private final Cell cell;
    private final List<Cell> dependants;

    public VarHandle(Cell cell, List<Cell> dependants) {
      this.cell = cell;
      this.dependants = dependants;
    }

    public Value getValue(){
      return cell.getValue();
    }

    List<Cell> getDependants(){
      return dependants;
    }

  }

  private final RuntimeSet runtimeSet;
  private final EvaluationContext context;

  public TweakFlowRuntime(RuntimeSet runtimeSet) {
    this.runtimeSet = runtimeSet;
    context = new EvaluationContext(new UserObjectFactory(), new DefaultDebugHandler());
  }

  public TweakFlowRuntime(RuntimeSet runtimeSet, DebugHandler debugHandler) {
    this.runtimeSet = runtimeSet;
    context = new EvaluationContext(new UserObjectFactory(), debugHandler);
  }

  private String modulePath(String path){
    Loader loader = runtimeSet.getAnalysisSet().getLoader();
    LoadPath loadPath = loader.getLoadPath();
    LoadPathLocation loadPathLocation = loadPath.pathLocationFor(path);
    if (loadPathLocation == null) return null;
    return loadPathLocation.resolve(path);
  }

  public boolean varExists(String path, String lib, String var){
    return false;
  }

  public boolean libraryExists(String path, String lib){
    return false;
  }

  public boolean moduleExists(String path){
    return false;
  }

  public VarHandle createVarHandle(String path, String lib, String var){

    String resolvedPath = modulePath(path);

    if (resolvedPath == null){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "module path "+path+" not found");
    }

    LocalMemorySpace unitSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace();
    Cell moduleCell = unitSpace.getCells().gets(resolvedPath);

    if (moduleCell == null){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "module "+path+" resolving to "+resolvedPath+" is not loaded");
    }

    Cell libCell = moduleCell.getCells().gets(lib);

    if (libCell == null){
      throw new LangException(LangError.UNRESOLVED_REFERENCE, "module "+path+" resolving to "+resolvedPath+" does not contain library "+lib);
    }

    Cell varCell = libCell.getCells().gets(var);

    if (varCell == null){
      throw new LangException(LangError.UNRESOLVED_REFERENCE, "module "+path+" resolving to "+resolvedPath+" does not contain var "+var+" in library "+lib);
    }

    IdentityHashMap<Symbol, LinkedHashSet<Symbol>> dependants = runtimeSet.getAnalysisSet().getDependants();

    Symbol symbol = varCell.getSymbol();

    ArrayList<Cell> dependantCells = new ArrayList<>();
    LinkedHashSet<Symbol> dependantSymbols = dependants.get(symbol);

    if (dependantSymbols != null){
      // collect dirties for re-evaluation
      for (Symbol dep : dependantSymbols) {
        String modulePath = dep.getNode().getSourceInfo().getParseUnit().getPath();
        MemorySpace moduleSpace = unitSpace.getCells().gets(modulePath);
        String libraryName = ((Symbol)dep.getScope()).getNode().getSymbolName();
        String varName = dep.getName();
        Cell depCell = moduleSpace.getCells().gets(libraryName).getCells().gets(varName);
        dependantCells.add(depCell);
      }
    }

    return new VarHandle(varCell, dependantCells);

  }

  public void updateVar(VarHandle varHandle, Value value){

    Cell cell = varHandle.cell;
    cell.setValue(value);

    for (Cell dependant : varHandle.dependants) {
      dependant.setDirty(true);
    }

    for (Cell dependant : varHandle.dependants) {
      Stack stack = new Stack();
      stack.push(new StackEntry(dependant.getSymbol().getNode(), dependant, Collections.emptyMap()));
      Evaluator.evaluateCell(dependant, stack, context);
    }

  }

  public EvaluatorUserCallContext createCallContext(VarHandle varHandle){
    Cell cell = varHandle.cell;
    Stack stack = new Stack();
    stack.push(new StackEntry(cell.getSymbol().getNode(), cell, Collections.emptyMap()));
    return new EvaluatorUserCallContext(stack, context);
  }

}
