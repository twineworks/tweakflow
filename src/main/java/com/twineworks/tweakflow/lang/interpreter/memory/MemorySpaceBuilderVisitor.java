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

package com.twineworks.tweakflow.lang.interpreter.memory;

import com.twineworks.tweakflow.lang.analysis.visitors.AVisitor;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.ComponentNode;
import com.twineworks.tweakflow.lang.ast.UnitNode;
import com.twineworks.tweakflow.lang.ast.structure.*;

import java.util.ArrayDeque;


public class MemorySpaceBuilderVisitor extends AVisitor implements Visitor {

  private final ArrayDeque<MemorySpace> spaces = new ArrayDeque<>();
  private final GlobalMemorySpace globalMemorySpace;

  MemorySpaceBuilderVisitor(GlobalMemorySpace globalMemorySpace) {
    this.globalMemorySpace = globalMemorySpace;
    spaces.push(globalMemorySpace);
  }

  void buildUnitSpaces(UnitNode node){

    Cell unitCell;
    Cell exportCell;

    if (node instanceof ModuleNode){
      ModuleNode moduleNode = (ModuleNode) node;
      unitCell = buildModuleUnitSpace(moduleNode);
      exportCell = buildModuleExportSpace(moduleNode, unitCell);

      if (moduleNode.isGlobal()){
        globalMemorySpace.getCells().puts(moduleNode.getGlobalName(), exportCell);
      }

    }
    else if (node instanceof InteractiveNode){
      unitCell = buildInteractiveUnitSpace((InteractiveNode) node);
      exportCell = buildInteractiveExportSpace((InteractiveNode) node);
    }
    else {
      throw new AssertionError("unknown unit node: "+node.getClass().getName());
    }

    // place unit and exports into corresponding spaces
    String unitKey = node.getSourceInfo().getParseUnit().getPath();
    globalMemorySpace.getUnitSpace().getCells().puts(unitKey, unitCell);
    globalMemorySpace.getExportSpace().getCells().puts(unitKey, exportCell);

  }

  private Cell buildModuleUnitSpace(ModuleNode node){

    Cell moduleSpace = new Cell()
        .setEnclosingSpace(globalMemorySpace)
        .setSymbol(node.getSymbol())
        .setScope(node.getUnitScope());

    spaces.push(moduleSpace);
    visit(node);
    spaces.pop();
    return moduleSpace;
  }

  private Cell buildModuleExportSpace(ModuleNode node, MemorySpace unitSpace) {

    // add all components to exports
    Cell exports = new Cell()
        .setEnclosingSpace(globalMemorySpace)
        .setSymbol(node.getExportSymbol(), true)
        .setScope(node.getPublicScope());

    // put exported local components into export space
    // export nodes are added by the linker, who must traverse the
    // export reference, potentially to other modules
    for (ComponentNode componentNode : node.getComponents()) {
      if (componentNode instanceof LibraryNode) {
        LibraryNode libraryNode = (LibraryNode) componentNode;
        if (libraryNode.isExport()) {
          exports.getCells().puts(libraryNode.getSymbolName(), unitSpace.getCells().gets(libraryNode.getSymbolName()));
        }
      }
    }

    return exports;

  }

  private Cell buildInteractiveExportSpace(InteractiveNode node) {

    // interactive space has empty exports
    return new Cell()
        .setEnclosingSpace(globalMemorySpace)
        .setSymbol(node.getExportSymbol())
        .setScope(node.getPublicScope());

  }

  private Cell buildInteractiveUnitSpace(InteractiveNode node) {

    Cell interactiveSpace = new Cell()
        .setEnclosingSpace(globalMemorySpace)
        .setSymbol(node.getSymbol())
        .setScope(node.getUnitScope());

    spaces.push(interactiveSpace);
    visit(node);
    spaces.pop();
    return interactiveSpace;
  }

  @Override
  public ModuleNode visit(ModuleNode node) {
    node.getComponents().forEach(this::visit);
    return node;
  }

  @Override
  public InteractiveNode visit(InteractiveNode node) {
    node.getSections().forEach(this::visit);
    return node;
  }

  @Override
  public InteractiveSectionNode visit(InteractiveSectionNode node) {

    MemorySpace interactiveSpace = spaces.peek();
    Cell sectionSpace = new Cell()
        .setEnclosingSpace(interactiveSpace)
        .setSymbol(node.getSymbol())
        .setScope(node.getVars().getScope());

    interactiveSpace.getCells().puts(node.getSymbolName(), sectionSpace);
    spaces.push(sectionSpace);
    visit(node.getVars());
    spaces.pop();
    return node;
  }

  @Override
  public LibraryNode visit(LibraryNode node) {

    MemorySpace moduleSpace = spaces.peek();

    Cell librarySpace = new Cell()
        .setEnclosingSpace(moduleSpace)
        .setSymbol(node.getSymbol())
        .setScope(node.getVars().getScope());

    moduleSpace.getCells().puts(node.getSymbolName(), librarySpace);

    spaces.push(librarySpace);
    visit(node.getVars());
    spaces.pop();
    return node;
  }

  @Override
  public VarDefs visit(VarDefs node) {
    node.getMap().values().forEach(this::visit);
    return node;
  }

  @Override
  public VarDefNode visit(VarDefNode node) {
    MemorySpace space = spaces.peek();
    Cell varCell = new Cell()
        .setEnclosingSpace(space)
        .setScope(space.getScope())
        .setSymbol(node.getSymbol());

    space.getCells().puts(node.getSymbolName(), varCell);
    return node;
  }
}
