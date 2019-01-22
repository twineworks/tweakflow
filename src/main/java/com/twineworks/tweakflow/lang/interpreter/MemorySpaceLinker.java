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

package com.twineworks.tweakflow.lang.interpreter;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.ast.UnitNode;
import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportMemberNode;
import com.twineworks.tweakflow.lang.ast.imports.ModuleImportNode;
import com.twineworks.tweakflow.lang.ast.imports.NameImportNode;
import com.twineworks.tweakflow.lang.ast.structure.InteractiveNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.memory.*;
import com.twineworks.tweakflow.lang.scope.ScopeType;
import com.twineworks.tweakflow.lang.scope.Scopes;
import com.twineworks.tweakflow.lang.scope.Symbol;

import java.util.Collections;
import java.util.Map;

public class MemorySpaceLinker {

  public static void link(RuntimeSet runtimeSet){

    GlobalMemorySpace globalMemorySpace = runtimeSet.getGlobalMemorySpace();

    Map<String, AnalysisUnit> units = runtimeSet.getAnalysisSet().getUnits();
    LocalMemorySpace unitSpace = globalMemorySpace.getUnitSpace();
    ConstShapeMap<Cell> unitSpaceCells = unitSpace.getCells();

    // link all modules

    for (String unit : units.keySet()) {

      UnitNode unitNode = units.get(unit).getUnit();
      if (unitNode instanceof ModuleNode){

        ModuleNode module = (ModuleNode) unitNode;
        linkModule(module, globalMemorySpace);

      }

    }

    // link all interactive sections
    for (String unit : units.keySet()) {

      UnitNode unitNode = units.get(unit).getUnit();
      if (unitNode instanceof InteractiveNode) {

        MemorySpace interactiveSpace = unitSpaceCells.gets(unit);
        ConstShapeMap<Cell> sectionCells = interactiveSpace.getCells();
        for (ShapeKey moduleRef : sectionCells.keySet()) {
          sectionCells.get(moduleRef).setEnclosingSpace(unitSpaceCells.gets(moduleRef.toString()));
        }
      }

    }

  }

  private static void linkModule(ModuleNode module, GlobalMemorySpace globalMemorySpace){

    // link imports
    for (ImportMemberNode imp: module.getImportsMap().values()) {
      Symbol symbol = imp.getSymbol();
      link(symbol, globalMemorySpace);
    }

    // link aliases
    for (AliasNode aliasNode : module.getAliases()) {
      link(aliasNode.getSymbol(), globalMemorySpace);
    }

    // link exports
    for (ExportNode exportNode : module.getExports()) {
      link(exportNode.getExportedSymbol(), globalMemorySpace);
    }

  }

  private static void link(Symbol symbol, GlobalMemorySpace globalMemorySpace){

    if (symbol.isLocal() || symbol.getScope().getScopeType() == ScopeType.GLOBAL) return;

    LocalMemorySpace unitSpace = globalMemorySpace.getUnitSpace();
    LocalMemorySpace exportSpace = globalMemorySpace.getExportSpace();

    String modulePath = symbol.getNode().getSourceInfo().getParseUnit().getPath();
    MemorySpace moduleSpace = unitSpace.getCells().gets(modulePath);
    MemorySpace moduleExportSpace = exportSpace.getCells().gets(modulePath);

    if (symbol.isNameImport()){
      linkNameImport(symbol, globalMemorySpace, moduleSpace);
    }
    else if (symbol.isModuleImport()){
      linkModuleImport(symbol, globalMemorySpace, moduleSpace);
    }
    else if (symbol.isAlias()){
      linkAlias(symbol, globalMemorySpace, moduleSpace);
    }
    else if (symbol.isExport()){
      linkExport(symbol, globalMemorySpace, moduleExportSpace);
    }
    else {
      throw new LangException(LangError.UNKNOWN_ERROR, "unknown link symbol type: "+symbol.getSymbolType().name());
    }

  }

  private static void linkNameImport(Symbol symbol, GlobalMemorySpace globalMemorySpace, MemorySpace moduleSpace) {

    NameImportNode imp = (NameImportNode) symbol.getNode();
    // already linked?
    if (moduleSpace.getCells().gets(imp.getImportName()) != null) return;

    AnalysisUnit importedUnit = imp.getImportedCompilationUnit();
    String path = importedUnit.getPath();
    ConstShapeMap<Cell> targetExportCells = globalMemorySpace.getExportSpace().getCells().gets(path).getCells();
    Cell cell = targetExportCells.gets(imp.getExportName());

    // the export is not visible yet, need to link it
    if (cell == null){
      ModuleNode importedModule = (ModuleNode) importedUnit.getUnit();
      Symbol exportedSymbol = importedModule.getUnitScope().getPublicScope().getSymbols().get(imp.getExportName());
      link(exportedSymbol, globalMemorySpace);
      // relink self after export is done
      link(symbol, globalMemorySpace);
      cell = targetExportCells.gets(imp.getExportName());

      if (cell == null){
        throw new AssertionError("could not link symbol: "+exportedSymbol);
      }

    }

    moduleSpace.getCells().puts(imp.getImportName(), cell);

  }

  private static void linkModuleImport(Symbol symbol, GlobalMemorySpace globalMemorySpace, MemorySpace moduleSpace){

    // already linked?
    if (moduleSpace.getCells().gets(symbol.getName()) != null) return;

    String path;
    if (symbol.getNode() instanceof ModuleImportNode){
      ModuleImportNode node = (ModuleImportNode) symbol.getNode();
      path = node.getImportedCompilationUnit().getPath();
    }
    // happens for global modules referenced directly
    else {
      ModuleNode node = (ModuleNode) symbol.getNode();
      path = node.getSourceInfo().getParseUnit().getPath();
    }
    moduleSpace.getCells().puts(symbol.getName(), globalMemorySpace.getExportSpace().getCells().gets(path));
  }


  private static void linkAlias(Symbol symbol, GlobalMemorySpace globalMemorySpace, MemorySpace moduleSpace){

    // already linked?
    if (moduleSpace.getCells().gets(symbol.getName()) != null) return;

    String path = symbol.getNode().getSourceInfo().getParseUnit().getPath(); // use symbol local space
    MemorySpace targetModule = globalMemorySpace.getUnitSpace().getCells().gets(path);

    // the alias might refer to an imported export that is not linked yet
    ReferenceNode source = symbol.getRefNode();

    for(int i=0;i<source.getElements().size();i++){
      ReferenceNode partialRef = (ReferenceNode) new ReferenceNode()
          .setAnchor(source.getAnchor())
          .setElements(source.getElements().subList(0, i+1))
          .setScope(source.getScope())
          .setSourceInfo(source.getSourceInfo());

      Symbol partial = Scopes.resolve(partialRef);
      link(partial, globalMemorySpace);
    }

    Cell cell = Spaces.resolve(symbol.getRefNode(), targetModule);
    moduleSpace.getCells().puts(symbol.getName(), cell);

  }

  private static void linkExport(Symbol symbol, GlobalMemorySpace globalMemorySpace, MemorySpace exportSpace){
    // already linked?
    if (exportSpace.getCells().gets(symbol.getName()) != null) return;

    String path = symbol.getNode().getSourceInfo().getParseUnit().getPath(); // use symbol local space
    MemorySpace targetModule = globalMemorySpace.getUnitSpace().getCells().gets(path);

    // might refer to an imported export that is not linked yet
    ReferenceNode source = symbol.getRefNode();
    ReferenceNode root = (ReferenceNode) new ReferenceNode()
        .setAnchor(source.getAnchor())
        .setElements(Collections.singletonList(source.getElements().get(0)))
        .setScope(source.getScope())
        .setSourceInfo(source.getSourceInfo());

    link(Scopes.resolve(root), globalMemorySpace);
    exportSpace.getCells().puts(symbol.getName(), Spaces.resolve(symbol.getRefNode(), targetModule));
  }


}
