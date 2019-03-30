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

package com.twineworks.tweakflow.lang.interpreter.memory;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.ScopeType;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.scope.SymbolTarget;
import com.twineworks.tweakflow.lang.interpreter.ops.ReferenceOp;

import java.util.List;
import java.util.Objects;

public class Spaces {

  static public Cell resolve(ReferenceNode node, MemorySpace space){

    // short circuit simple local reference
//    String simpleLocalName = node.getSimpleName();
//    if (simpleLocalName != null){
//      return space.getCells().gets(simpleLocalName);
//    }

    List<String> elements = node.getElements();

    try {
      Cell cell;
      switch(node.getAnchor()){
        case LOCAL:
          cell = resolveInLocal(elements, space);
          break;
        case GLOBAL:
          cell = resolveInGlobal(elements, space);
          break;
        case MODULE:
          cell = resolveInModule(elements, space);
          break;
        case LIBRARY:
          cell = resolveInLibrary(elements, space);
          break;
        default:
          throw new AssertionError("Invalid reference node: unknown or missing anchor "+node.getAnchor());
      }
      return cell;
    } catch (LangException e){
      if (e.getSourceInfo() != null){
        e.setSourceInfo(node.getSourceInfo());
        e.putIfAbsent("reference", node.getSourceInfo().getSourceCode());
      }

      throw e;
    }

  }

  static public Cell resolve(ReferenceOp op, MemorySpace space){

//    IdentityHashMap<Object, Cell> cache = space.cache();
//    Cell cell = cache.get(op);
//    if (cell != null) return cell;
    Cell cell;
    ConstShapeMap.Accessor[] names = op.getNames();
    try {

      switch(op.getAnchor()){
        case LOCAL:
          cell = resolveInLocal(names, space);
          break;
        case GLOBAL:
          cell = resolveInGlobal(names, space);
          break;
        case MODULE:
          cell = resolveInModule(names, space);
          break;
        case LIBRARY:
          cell = resolveInLibrary(names, space);
          break;
        default:
          throw new AssertionError("Invalid reference: unknown or missing anchor "+op.getAnchor());
      }
//      cache.put(op, cell);
      return cell;
    } catch (LangException e){
      if (e.getSourceInfo() != null){
        e.setSourceInfo(op.getNode().getSourceInfo());
        e.putIfAbsent("reference", op.getNode().getSourceInfo().getSourceCode());
      }

      throw e;
    }

  }

  private static MemorySpace findGlobalSpace(MemorySpace s){

    Objects.requireNonNull(s);

    MemorySpace currentSpace = s;
    while(!(currentSpace instanceof GlobalMemorySpace)){
      currentSpace = currentSpace.getEnclosingSpace();
    }
    return currentSpace;
  }

  private static boolean isModuleSpace(MemorySpace s){
    if (s == null) return false;
    Scope scope = s.getScope();
    return scope.getScopeType() == ScopeType.SYMBOL && ((Symbol) scope).getTarget() == SymbolTarget.MODULE;
  }

  private static boolean isLibrarySpace(MemorySpace s){
    if (s == null) return false;
    Scope scope = s.getScope();
    return scope.getScopeType() == ScopeType.SYMBOL && ((Symbol) scope).getTarget() == SymbolTarget.LIBRARY;
  }

  private static MemorySpace findModuleSpace(MemorySpace s){

    Objects.requireNonNull(s);

    // find module scope up the hierarchy
    MemorySpace currentSpace = s;
    while(!isModuleSpace(currentSpace)){
      currentSpace = currentSpace.getEnclosingSpace();
    }

    return currentSpace;
  }

  private static MemorySpace findLibrarySpace(MemorySpace s){
    Objects.requireNonNull(s);

    // find symbol scope up the hierarchy
    MemorySpace currentSpace = s;
    while(!isLibrarySpace(currentSpace)){
      currentSpace = currentSpace.getEnclosingSpace();
    }

    return currentSpace;
  }

  static private Cell resolveInGlobal(List<String> names, MemorySpace space) {

    return Spaces.resolveMembers(names, findGlobalSpace(space));

  }

  static private Cell resolveInGlobal(ConstShapeMap.Accessor[] names, MemorySpace space) {

    return Spaces.resolveMembers(names, 0, findGlobalSpace(space));

  }

  static private Cell resolveInModule(List<String> names, MemorySpace space) {

    return Spaces.resolveMembers(names, findModuleSpace(space));

  }

  static private Cell resolveInModule(ConstShapeMap.Accessor[] names, MemorySpace space) {

    return Spaces.resolveMembers(names, 0, findModuleSpace(space));

  }

  static private Cell resolveInLibrary(List<String> names, MemorySpace space){

    // resolve members starting from that scope
    return Spaces.resolveMembers(names, findLibrarySpace(space));

  }

  static private Cell resolveInLibrary(ConstShapeMap.Accessor[] names, MemorySpace space){

    // resolve members starting from that scope
    return Spaces.resolveMembers(names, 0, findLibrarySpace(space));

  }

  static private Cell resolveInLocal(List<String> names, MemorySpace space){

    String initial = names.get(0);
    Cell cell = Spaces.resolveInHierarchy(initial, space);

    // if that was a simple reference: done
    if (names.size() == 1){
      return cell;
    }

    return Spaces.resolveMembers(names.subList(1, names.size()), cell);

  }

  static private Cell resolveInLocal(ConstShapeMap.Accessor[] names, MemorySpace space){

    ConstShapeMap.Accessor initial = names[0];
    Cell cell = Spaces.resolveInHierarchy(initial, space);

    // if that was a simple reference: done
    if (names.length == 1){
      return cell;
    }

    return Spaces.resolveMembers(names, 1, cell);

  }

  static private Cell resolveInHierarchy(String name, MemorySpace space){

    MemorySpace currentSpace = space;
    while(currentSpace.getScope().getScopeType() != ScopeType.GLOBAL){ /* global scope is not searched */

      ConstShapeMap<Cell> cells = currentSpace.getCells();

      Cell ret = cells.gets(name);
      if (ret != null){
        return ret;
      }

      currentSpace = currentSpace.getEnclosingSpace();
    }
    // nothing found, throw
    throw new LangException(LangError.UNRESOLVED_REFERENCE);
  }

  @SuppressWarnings("unchecked")
  static private Cell resolveInHierarchy(ConstShapeMap.Accessor name, MemorySpace space){

    // short circuit simple find
    ConstShapeMap<Cell> cells = space.getCells();
    Cell ret = cells.geta(name);
    if (ret != null) return ret;

    // keep looking
    MemorySpace currentSpace = space.getEnclosingSpace();
    while(currentSpace.getMemorySpaceType() != MemorySpaceType.GLOBAL){ /* global scope is not searched */

      cells = currentSpace.getCells();

      ret = cells.geta(name);
      if (ret != null){
        return ret;
      }

      currentSpace = currentSpace.getEnclosingSpace();
    }
    // nothing found, throw
    throw new LangException(LangError.UNRESOLVED_REFERENCE);
  }


  static private Cell resolveMembers(List<String> names, MemorySpace space){

    if (names.isEmpty()){
      throw new IllegalArgumentException("Names to resolve cannot be empty");
    }

    MemorySpace memorySpace = space;

    // loop over names
    for (int i = 0; i < names.size(); i++) {

      // last item may be a simple symbol, all in-between must be scoped to
      // accommodate member resolution
      boolean last = i == names.size()-1;
      String currentName = names.get(i);

      ConstShapeMap<Cell> cells = memorySpace.getCells();

      Cell found = cells.gets(currentName);
      if (found != null){
        // found symbol, if last just return it, else dig down
        if (last) {
          return found;
        }
        else{
          // dig down the symbol if more names need resolving
          memorySpace = found;
        }
      }
      else {
        throw new LangException(LangError.UNRESOLVED_REFERENCE);
      }
    }

    throw new AssertionError("Should never be here");
  }

  @SuppressWarnings("unchecked")
  static private Cell resolveMembers(ConstShapeMap.Accessor[] names, int idx, MemorySpace space){

    MemorySpace memorySpace = space;

    // loop over names
    int lastIndex = names.length-1;
    for (int i = idx; i < names.length; i++) {

      ConstShapeMap.Accessor currentName = names[i];

      ConstShapeMap<Cell> cells = memorySpace.getCells();

      Cell found = cells.geta(currentName);
      if (found != null){
        // found symbol, if last just return it, else dig down
        boolean last = i == lastIndex;
        if (last) {
          return found;
        }
        else{
          // dig down the symbol if more names need resolving
          memorySpace = found;
        }
      }
      else {
        throw new LangException(LangError.UNRESOLVED_REFERENCE);
      }
    }

    throw new AssertionError("Should never be here");
  }




}
