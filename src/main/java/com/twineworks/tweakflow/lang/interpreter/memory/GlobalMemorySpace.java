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
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.scope.GlobalScope;
import com.twineworks.tweakflow.lang.scope.Scope;

import java.util.Set;

public class GlobalMemorySpace implements MemorySpace {

  private final ConstShapeMap<Cell> cells;
  private final GlobalScope scope;

  private final LocalMemorySpace unitSpace;
  private final LocalMemorySpace exportSpace;

  public GlobalMemorySpace(GlobalScope scope) {
    this.scope = scope;

    cells = new ConstShapeMap<>(ShapeKey.getAll(scope.getSymbols().keySet()));

    Set<ShapeKey> unitKeys = ShapeKey.getAll(scope.getUnitScope().getSymbols().keySet());

    // Unit space holds internal spaces of modules: imports, aliases, libraries, etc.
    // Cells are keyed by unit path.
    unitSpace = new LocalMemorySpace(this, scope.getUnitScope(), MemorySpaceType.UNIT, new ConstShapeMap<>(unitKeys));

    // Export space holds external spaces of modules.
    // Cells are keyed by unit path.

    // Only the exported module components and explicit exports are present.
    // An exported memory space is not always a subset of the internal one.
    // A module may choose to export something under an alias.

    // example:
    // import conf_lib from "./my_conf.tf"
    // export conf_lib as conf

    // The export node establishes an external name "conf" that is not
    // present internally. Therefore the internal memory space of this module
    // contains the name "conf_lib", but not the name "conf"
    // The export space contains the name "conf" and is therefore not a subset
    // of the internal space.

    // This is the reason behind there being an extra external space per unit.

    exportSpace = new LocalMemorySpace(this, scope.getUnitScope(), MemorySpaceType.UNIT_EXPORTS, new ConstShapeMap<>(unitKeys));
  }

  public LocalMemorySpace getUnitSpace() {
    return unitSpace;
  }

  public LocalMemorySpace getExportSpace() {
    return exportSpace;
  }

  @Override
  public ConstShapeMap<Cell> getCells() {
    return cells;
  }

  @Override
  public MemorySpace getEnclosingSpace() {
    return null;
  }

  @Override
  public MemorySpace setEnclosingSpace(MemorySpace space) {
    if (space != null){
      throw new AssertionError("Cannot set enclosing space of global space");
    }
    return this;
  }

  @Override
  public Scope getScope() {
    return scope;
  }

  @Override
  public MemorySpaceType getMemorySpaceType() {
    return MemorySpaceType.GLOBAL;
  }

}
