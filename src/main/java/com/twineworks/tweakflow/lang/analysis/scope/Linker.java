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

package com.twineworks.tweakflow.lang.analysis.scope;

import com.twineworks.tweakflow.lang.analysis.AnalysisStage;
import com.twineworks.tweakflow.lang.ast.UnitNode;
import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportMemberNode;
import com.twineworks.tweakflow.lang.ast.imports.ModuleImportNode;
import com.twineworks.tweakflow.lang.ast.imports.NameImportNode;
import com.twineworks.tweakflow.lang.ast.structure.InteractiveNode;
import com.twineworks.tweakflow.lang.ast.structure.InteractiveSectionNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.analysis.AnalysisSet;
import com.twineworks.tweakflow.lang.scope.Scope;
import com.twineworks.tweakflow.lang.scope.Scopes;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.scope.SymbolTarget;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Map;

public class Linker {

  /*

    linker resolves names that are potentially defined in other modules

    alias $conf as conf

    import lib_a as a from "foo"    # connects name import lib_a with the exported name symbol from "foo"
    import * as m from "foo"        # connects module import m with the "foo" module (name) symbol

    alias m as q                    # connects module import q with the m name symbol

    alias $std.strings as s         # connects name import with with the std module symbol
    alias $std as stdlib            # connects name import with the std module symbol

    export $std.strings [as strings]

   */


  public static void link(AnalysisSet analysisSet){

    // chain keeps tracks of imports, aliases, exports etc. being followed:
    // if a symbol points back to itself directly or indirectly
    // a cyclic reference error is thrown
    ArrayDeque<Symbol> chain = new ArrayDeque<>();

    for (AnalysisUnit unit : analysisSet.getUnits().values()) {

      if (unit.getStage().getProgress() >= AnalysisStage.LINKED.getProgress()) continue;

      linkUnit(unit.getUnit(), chain);

      unit.setStage(AnalysisStage.LINKED);

    }

  }

  private static void linkUnit(UnitNode node, ArrayDeque<Symbol> chain){

    if (node instanceof ModuleNode){
      linkModule((ModuleNode) node, chain);
    }

    if (node instanceof InteractiveNode){
      linkInteractive((InteractiveNode) node, chain);
    }

  }

  private static void linkModule(ModuleNode module, ArrayDeque<Symbol> chain){

    // link imports
    for (ImportMemberNode importMemberNode: module.getImportsMap().values()) {
      Symbol symbol = importMemberNode.getSymbol();
      link(symbol, chain);
    }

    // link aliases
    for (AliasNode aliasNode : module.getAliases()) {
      link(aliasNode.getSymbol(), chain);
    }

    // link exports
    for (ExportNode exportNode : module.getExports()) {
      link(exportNode.getExportedSymbol(), chain);
    }

  }

  private static void linkInteractive(InteractiveNode interactiveNode, ArrayDeque<Symbol> chain){

    // example:
    //
    // interactive
    //   in_scope `fixtures/tweakflow/interactive/module_a.tf`
    //    x: a
    //   in_scope `fixtures/tweakflow/interactive/module_b.tf`
    //    y: b

    for (InteractiveSectionNode sectionNode : interactiveNode.getSections()) {
      // sections are in unit scope, so they have access to all loaded files
      // the reference has only one component: the path of the target module
      // target module scope is used to evaluate the section vars in

      // find the unit symbol of target module
      ReferenceNode inScopeRef = sectionNode.getInScopeRef();
      Symbol inScopeTarget = Scopes.resolve(inScopeRef); // throws if module is not found

      // make it the enclosing scope of the vars
      inScopeRef.setReferencedSymbol(inScopeTarget);
      Scope varScope = sectionNode.getVars().getScope();
      varScope.setEnclosingScope(inScopeTarget);
    }


  }

  private static void link(Symbol symbol, ArrayDeque<Symbol> chain){

    if (symbol.isLocal()){
      return;
    }

    if (symbol.isRefResolved()){
      return;
    }

    if (chain.contains(symbol)){
      throw new LangException(LangError.CYCLIC_REFERENCE, symbol.getNode().getSourceInfo())
          .put("chain", chain)
          .put("symbol", symbol)
          .put("node", symbol.getNode())
          .put("reference", symbol.getRefNode());
    }

    if (symbol.isNameImport()){
      linkNameImport(symbol, chain);
    }
    else if (symbol.isModuleImport()){
      linkModuleImport(symbol, chain);
    }
    else if (symbol.isAlias()){
      linkAlias(symbol, chain);
    }
    else if (symbol.isExport()){
      linkExport(symbol, chain);
    }
    else {
      throw new AssertionError("Unknown symbol type");
    }

  }

  private static void linkExport(Symbol symbol, ArrayDeque<Symbol> chain) {

    ReferenceNode source = symbol.getRefNode();

    // export m.foo.bar as new_name
    //        ^ root of source
    // root of the alias might be a local component, an alias, an import, or missing
    // it must resolve or fail

    ReferenceNode root = (ReferenceNode) new ReferenceNode()
        .setAnchor(source.getAnchor())
        .setElements(Collections.singletonList(source.getElements().get(0)))
        .setScope(source.getScope())
        .setSourceInfo(source.getSourceInfo());

    Symbol rootSymbol = Scopes.resolve(root); // throws if not resolved

    chain.push(symbol);
    link(rootSymbol, chain); // throws on circular dependency
    chain.pop();

    Symbol sourceSymbol = Scopes.resolve(source);
    symbol.setRef(sourceSymbol);
    symbol.setTarget(sourceSymbol.getTarget());

  }

  private static void linkModuleImport(Symbol symbol, ArrayDeque<Symbol> chain) {
    ModuleImportNode imp = (ModuleImportNode) symbol.getNode();
    ModuleNode moduleNode = (ModuleNode) imp.getImportedCompilationUnit().getUnit();
    symbol.setRef(moduleNode.getSymbol());
    symbol.setTarget(SymbolTarget.MODULE);
  }

  private static void linkAlias(Symbol symbol, ArrayDeque<Symbol> chain) {

    ReferenceNode source = symbol.getRefNode();

    // alias m.foo.bar as new_name
    //       ^ root of source
    // root of the alias might be a local component, another alias, an import, or missing
    // it must resolve or fail

    ReferenceNode root = (ReferenceNode) new ReferenceNode()
        .setAnchor(source.getAnchor())
        .setElements(Collections.singletonList(source.getElements().get(0)))
        .setScope(source.getScope())
        .setSourceInfo(source.getSourceInfo());

    Symbol rootSymbol = Scopes.resolve(root); // throws if not resolved

    chain.push(symbol);
    link(rootSymbol, chain); // throws on circular dependency
    chain.pop();

    Symbol sourceSymbol = Scopes.resolve(source);
    symbol.setRef(sourceSymbol);
    symbol.setTarget(sourceSymbol.getTarget());

  }

  private static void linkNameImport(Symbol symbol, ArrayDeque<Symbol> chain){

    // find the export in question in referenced module
    NameImportNode imp = (NameImportNode) symbol.getNode();
    ModuleNode moduleNode = (ModuleNode) imp.getImportedCompilationUnit().getUnit();
    Map<String, Symbol> exports = moduleNode.getUnitScope().getPublicScope().getSymbols();

    if (!exports.containsKey(imp.getExportName())){
      throw new LangException(LangError.CANNOT_FIND_EXPORT, imp.getSourceInfo());
    }
    Symbol exp = exports.get(imp.getExportName());

    if (exp.isLocal()){
      symbol.setRef(exp);
      symbol.setTarget(exp.getTarget());
    }
    // export nodes
    else {
      chain.push(symbol);
      link(exp, chain);
      chain.pop();
      symbol.setRef(exp);
      symbol.setTarget(exp.getTarget());
    }

  }

}
