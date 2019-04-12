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

package com.twineworks.tweakflow.lang.values;

import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.util.LangUtil;

public class ValueInspector {

  public static String inspect(Value v){
    StringBuilder out = new StringBuilder();
    inspect(out, v, "", "",   "  ", false);
    return out.toString();
  }

  public static String inspect(Value v, boolean expandFunctions){
    StringBuilder out = new StringBuilder();
    inspect(out, v, "", "",   "  ", expandFunctions);
    return out.toString();
  }

  public static void inspect(StringBuilder out, Value v, String leadingIndent, String inheritedIndent, String indentationUnit, boolean expandFunctions){

    if (v == Values.NIL){
      out.append(leadingIndent).append("nil");
      return;
    }

    if (v == Values.TRUE){
      out.append(leadingIndent).append("true");
      return;
    }

    if (v == Values.FALSE){
      out.append(leadingIndent).append("false");
      return;
    }

    if (v.type() == Types.STRING){
      out.append(leadingIndent)
          .append('"')
          .append(LangUtil.escapeString(v.string()))
          .append('"');
      return;
    }

    if (v.type() == Types.LONG){
      out.append(leadingIndent).append(v.longNum().toString());
      return;
    }

    if (v.type() == Types.DOUBLE){
      out.append(leadingIndent).append(v.doubleNum().toString());
      return;
    }

    if (v.type() == Types.DATETIME){
      out.append(leadingIndent).append(v.dateTime().toString());
      return;
    }

    if (v.type() == Types.LIST){
      out.append(leadingIndent).append("[");
      ListValue list = v.list();
      for (int i = 0, listSize = list.size(); i < listSize; i++) {
        Value value = list.get(i);
        inspect(out, value, "", inheritedIndent, indentationUnit, expandFunctions);
        if (i < (listSize -1)) out.append(", ");
      }

      out.append("]");
      return;
    }

    if (v.type() == Types.DICT){
      out.append(leadingIndent).append("{");
      if (!v.dict().isEmpty()) out.append("\n");
      int size = v.dict().size();
      int i=1;
      DictValue dict = v.dict();
      for (String key : dict.keys()) {
        Value value = dict.get(key);
        out.append(inheritedIndent).append(indentationUnit).append(LangUtil.getKeyLiteral(key));
        out.append(" ");
        inspect(out, value, "", inheritedIndent+indentationUnit, indentationUnit, expandFunctions);
        if (i < size){
          out.append(",");
        }
        out.append("\n");
        i+=1;
      }
      out.append(inheritedIndent).append("}");
      return;
    }

    if (v.type() == Types.FUNCTION){

      if (expandFunctions){

        if (v.function() instanceof StandardFunctionValue){
          StandardFunctionValue f = (StandardFunctionValue) v.function();
          if (f.getSourceInfo() != null && f.getSourceInfo().getSourceCode() != null){
            out.append(leadingIndent).append("\n");
            String[] lines = f.getSourceInfo().getSourceCode().split("\r?\n");
            for (String line : lines) {
              out.append(inheritedIndent).append(indentationUnit).append(line).append("\n");
            }

          }
          else{
            out.append(leadingIndent).append("function");
          }
        }
        else{
          out.append(leadingIndent).append("native function");
        }

      }
      else{
        out.append(leadingIndent).append("function");
      }

      return;
    }

    out.append(leadingIndent);
    out.append(v.value().toString());

  }

}
