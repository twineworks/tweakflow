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

import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.scope.Symbol;
import com.twineworks.tweakflow.lang.values.ValueInspector;
import com.twineworks.tweakflow.util.LangUtil;

public class MemorySpaceInspector {

  public static String inspect(MemorySpace space){
    StringBuilder out = new StringBuilder();
    inspect(out, space, "", "", "  ", false);
    return out.toString();
  }

  public static String inspect(MemorySpace space, boolean expandFunctions){
    StringBuilder out = new StringBuilder();
    inspect(out, space, "", "", "  ", expandFunctions);
    return out.toString();
  }

  public static void inspect(StringBuilder out, MemorySpace space, String leadingIndent, String inheritedIndent, String indentationUnit, boolean expandFunctions){

    if (space == null){
      out.append(leadingIndent).append("null");
      return;
    }

    MemorySpaceType spaceType = space.getMemorySpaceType();
    boolean inspectChildren = false;

    switch (spaceType){

      case GLOBAL: {
        out.append("# globals").append("\n");
        inspectChildren = true;
        break;
      }

      case UNIT: {
        out.append("# units").append("\n");
        inspectChildren = true;
        break;
      }

      case UNIT_EXPORTS: {
        out.append("# unit exports").append("\n");
        inspectChildren = true;
        break;
      }

      case MODULE: {
        // components inside
        Cell cell = (Cell) space;
        out.append("# module ").append("\n");
        inspectChildren = true;
        break;
      }

      case INTERACTIVE: {
        // components inside
        out.append("# interactive").append("\n");
        inspectChildren = true;
        break;
      }

      case INTERACTIVE_SECTION: {
        // components inside
        out.append("# interactive section").append("\n");
        inspectChildren = true;
        break;
      }

      case LIBRARY: {
        String name = ((Symbol) space.getScope()).getName();
        out.append("# library \n");

        inspectChildren = true;

        break;
      }

      case VAR: {
        Cell cell = (Cell) space;
        ValueInspector.inspect(out, cell.getValue(), "", inheritedIndent, indentationUnit, expandFunctions);
        out.append("\n");
        inspectChildren = false;
        break;
      }

      default: {
        out.append("unknown memory space type: ").append(spaceType.name());
        inspectChildren = false;
      }
    }

    if (inspectChildren){
      inspectChildren(out, space, leadingIndent, inheritedIndent, indentationUnit, expandFunctions);
    }

  }

  private static void inspectChildren(StringBuilder out, MemorySpace space, String leadingIndent, String inheritedIndent, String indentationUnit, boolean expandFunctions){

    ConstShapeMap<Cell> cells = space.getCells();
    String childIndent = inheritedIndent+indentationUnit;
    for (ShapeKey key : cells.keySet()) {
      out.append(childIndent).append(LangUtil.escapeIdentifier(key.toString())).append(": ");
      inspect(out, cells.get(key), childIndent, childIndent, indentationUnit, expandFunctions);
    }

  }

}
