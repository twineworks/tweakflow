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

package com.twineworks.tweakflow.lang.scope;

import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;

import java.util.*;

public class Scopes {

  static public Map<String, Symbol> getVisibleSymbols(Scope scope){

    HashMap<String, Symbol> result = new HashMap<>();

    // go up the scope hierarchy and collect symbols in a list
    // map the contents of that list by name in reverse order
    // so symbols far up in the hierarchy are written first
    // and are overwritten (shadowed) by same-name equivalents
    // closer to the starting scope. Global scope is not considered.

    ArrayList<Symbol> reachableSymbols = new ArrayList<>();
    Scope currentScope = scope;

    while (!(currentScope instanceof GlobalScope)){
      reachableSymbols.addAll(currentScope.getSymbols().values());
      currentScope = currentScope.getEnclosingScope();
    }

    for (int i = reachableSymbols.size() - 1; i >= 0; i--) {
      Symbol symbol = reachableSymbols.get(i);
      result.put(symbol.getName(), symbol);
    }

    return result;

  }

  static public Symbol resolve(ReferenceNode node){
    Scope scope = node.getScope();
    List<String> elements = node.getElements();

    try {
      switch(node.getAnchor()){
        case LOCAL:
          return resolveInLocal(elements, scope);
        case GLOBAL:
          return resolveInGlobal(elements, scope);
        case MODULE:
          return resolveInModule(elements, scope);
        case LIBRARY:
          return resolveInLibrary(elements, scope);
        default:
          throw new AssertionError("Invalid reference node: unknown or missing anchor "+node.getAnchor());
      }
    } catch (LangException e){
      if (e.getSourceInfo() == null){
        e.setSourceInfo(node.getSourceInfo());
      }
      throw e;
    }

  }

  private static Scope findGlobalScope(Scope s){

    Objects.requireNonNull(s);

    Scope currentScope = s;
    while(!(currentScope instanceof GlobalScope)){
      currentScope = currentScope.getEnclosingScope();
      // this should never happen, as all code has an enclosing global scope
      if (currentScope == null){
        throw new AssertionError("Could not find global scope");
      }
    }
    return currentScope;
  }

  private static boolean isModuleScope(Scope s){
    if (s == null) return false;
    if (!(s instanceof Symbol)) return false;
    Symbol sym = (Symbol) s;
    return (sym.getTarget() == SymbolTarget.MODULE);
  }

  public static boolean isLibraryScope(Scope s){
    if (s == null) return false;
    if (!(s instanceof Symbol)) return false;
    Symbol sym = (Symbol) s;
    return (sym.getTarget() == SymbolTarget.LIBRARY);
  }

  static public Scope findModuleScope(Scope s){

    Objects.requireNonNull(s);

    // find module scope up the hierarchy
    Scope currentScope = s;
    while(!isModuleScope(currentScope)){

      currentScope = currentScope.getEnclosingScope();
      // illegal reference, there is no module to anchor from
      // this should never happen, as all code has an enclosing module
      if (currentScope == null){
        throw new AssertionError("Cannot find enclosing module");
      }
    }

    return currentScope;
  }

  private static Scope findLibraryScope(Scope s){
    Objects.requireNonNull(s);

    // find symbol scope up the hierarchy
    Scope currentScope = s;
    while(!isLibraryScope(currentScope)){

      currentScope = currentScope.getEnclosingScope();
      // illegal reference, there is no library to anchor from
      if (currentScope == null){
        throw new LangException(LangError.UNRESOLVED_REFERENCE, "cannot find enclosing library");
      }
    }

    return currentScope;
  }

//  static public Scope findFlowScope(Scope s){
//    Objects.requireNonNull(s);
//
//    // find symbol scope up the hierarchy
//    Scope currentScope = s;
//    while(!isFlowScope(currentScope)){
//
//      currentScope = currentScope.getEnclosingScope();
//      // illegal reference, there is no flow to anchor from
//      if (currentScope == null){
//        throw new LangException(LangError.UNRESOLVED_REFERENCE, "cannot find enclosing flow");
//      }
//    }
//
//    return currentScope;
//  }


  static private Symbol resolveInGlobal(List<String> names, Scope scope) {

    Objects.requireNonNull(names);
    Objects.requireNonNull(scope);

    if (names.isEmpty()){
      throw new IllegalArgumentException("Names to resolve cannot be empty");
    }

    return Scopes.resolveMembers(names, findGlobalScope(scope));

  }

  static private Symbol resolveInModule(List<String> names, Scope scope) {

    Objects.requireNonNull(names);
    Objects.requireNonNull(scope);

    if (names.isEmpty()){
      throw new IllegalArgumentException("Names to resolve cannot be empty");
    }

    return Scopes.resolveMembers(names, findModuleScope(scope));

  }

  static private Symbol resolveInLibrary(List<String> names, Scope scope){

    Objects.requireNonNull(names);
    Objects.requireNonNull(scope);

    if (names.isEmpty()){
      throw new IllegalArgumentException("Names to resolve cannot be empty");
    }

    // resolve members starting from that scope
    return Scopes.resolveMembers(names, findLibraryScope(scope));

  }


//  static private Symbol resolveInFlow(List<String> names, Scope scope){
//
//    Objects.requireNonNull(names);
//    Objects.requireNonNull(scope);
//
//    if (names.isEmpty()){
//      throw new IllegalArgumentException("Names to resolve cannot be empty");
//    }
//
//    // resolve members starting from that scope
//    return Scopes.resolveMembers(names, findFlowScope(scope));
//
//  }


  static private Symbol resolveInLocal(List<String> names, Scope scope){

    Objects.requireNonNull(names);
    Objects.requireNonNull(scope);

    if (names.isEmpty()){
      throw new IllegalArgumentException("Names to resolve cannot be empty");
    }

    String initial = names.get(0);
    Symbol symbol = Scopes.resolveInHierarchy(initial, scope);

    // if that was a simple reference: done
    if (names.size() == 1){
      return symbol;
    }

    if (symbol.isScoped()){
      // if the reference has multiple elements, continue resolving
      // the following parts as members
      return Scopes.resolveMembers(names.subList(1, names.size()), symbol);
    }

    throw new LangException(LangError.UNRESOLVED_REFERENCE);

  }

  static private Symbol resolveInHierarchy(String name, Scope scope){

    Scope currentScope = scope;
    while(currentScope.getScopeType() != ScopeType.GLOBAL){ /* global scope is not searched */

      Map<String, Symbol> symbols = currentScope.getSymbols();

      if (symbols.containsKey(name)){
        return symbols.get(name);
      }

//      Symbol inheritedSymbol = findInInherited(name, scope);
//      if (inheritedSymbol != null) return inheritedSymbol;
      currentScope = currentScope.getEnclosingScope();
    }
    // nothing found, throw
    throw new LangException(LangError.UNRESOLVED_REFERENCE);
  }

//  private static Symbol findInInherited(String name, Scope scope) {
//    Scope inheritedScope = scope.getInheritedScope();
//    while (inheritedScope != null){
//      Map<String, Symbol> symbols = inheritedScope.getSymbols();
//      if (symbols.containsKey(name)){
//        return symbols.get(name);
//      }
//      inheritedScope = inheritedScope.getInheritedScope();
//    }
//    return null;
//
//  }

  static private Symbol resolveMembers(List<String> names, Scope scope){

    if (names.isEmpty()){
      throw new IllegalArgumentException("Names to resolve cannot be empty");
    }

    Scope currentScope = scope;

    // loop over names
    for (int i = 0; i < names.size(); i++) {

      // last item may be a simple symbol, all in-between must be scoped to
      // accommodate member resolution
      boolean last = i == names.size()-1;
      String currentName = names.get(i);

      Map<String, Symbol> symbols = currentScope.getSymbols();

      if (symbols.containsKey(currentName)){
        // found symbol, if last just return it, else dig down
        if (last) {
          return symbols.get(currentName);
        }
        else{
          // dig down the symbol if more names need resolving
          Symbol symbol = symbols.get(currentName);
          if (symbol.isScoped()){
            currentScope = symbol;
          }
          else{
            throw new LangException(LangError.UNRESOLVED_REFERENCE);
          }
        }
      }
      else {
        throw new LangException(LangError.UNRESOLVED_REFERENCE);
//        Symbol inheritedSymbol = findInInherited(currentName, scope);
//        if (inheritedSymbol != null){
//          if (last){
//            return inheritedSymbol;
//          }
//          else{
//            // dig down
//            if (inheritedSymbol.isScoped()){
//              currentScope = inheritedSymbol;
//            }
//            else{
//              throw new LangException(LangError.UNRESOLVED_REFERENCE);
//            }
//          }
//        }
//        // not found as inherited either
//        else{
//          throw new LangException(LangError.UNRESOLVED_REFERENCE);
//        }
      }
    }

    throw new AssertionError("Should never be here");
  }


}
