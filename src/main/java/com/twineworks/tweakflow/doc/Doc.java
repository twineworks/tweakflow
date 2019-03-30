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

package com.twineworks.tweakflow.doc;

import com.twineworks.tweakflow.lang.ast.ComponentNode;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.structure.LibraryNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.TransientDictValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.nio.file.Paths;

class Doc {

  private static Value makeMetaValue(ModuleNode node){

    TransientDictValue moduleDict = new TransientDictValue();
    moduleDict.put("node", Values.make("module"));
    moduleDict.put("path", Values.make(node.getSourceInfo().getParseUnit().getPath()));
    moduleDict.put("file", Values.make(Paths.get(node.getSourceInfo().getParseUnit().getPath()).getFileName().toString()));
    moduleDict.put("doc", Interpreter.evaluateDocExpression(node));
    moduleDict.put("meta", Interpreter.evaluateMetaExpression(node));
    moduleDict.put("global", node.isGlobal() ? Values.TRUE : Values.FALSE);

    if (node.isGlobal()){
      moduleDict.put("global_name", Values.make(node.getGlobalName()));
    }

    ListValue components = new ListValue();
    for (ComponentNode componentNode : node.getComponents()) {
      components = components.append(makeMetaValue(componentNode));
    }
    moduleDict.put("components", Values.make(components));
    return Values.make(moduleDict.persistent());

  }

  private static Value makeMetaValue(LibraryNode node){

    TransientDictValue libDict = new TransientDictValue();
    libDict.put("node", Values.make("library"));
    libDict.put("doc", Interpreter.evaluateDocExpression(node));
    libDict.put("meta", Interpreter.evaluateMetaExpression(node));
    libDict.put("export", node.isExport() ? Values.TRUE : Values.FALSE);
    libDict.put("name", Values.make(node.getSymbolName()));

    ListValue vars = new ListValue();
    for (VarDefNode varNode : node.getVars().getMap().values()) {
      vars = vars.append(makeMetaValue(varNode));
    }
    libDict.put("vars", Values.make(vars));
    return Values.make(libDict.persistent());

  }

  private static Value makeMetaValue(VarDefNode node){

    TransientDictValue varDict = new TransientDictValue();
    varDict.put("node", Values.make("var"));
    varDict.put("type", Values.make(node.getDeclaredType().name()));
    varDict.put("doc", Interpreter.evaluateDocExpression(node));
    varDict.put("meta", Interpreter.evaluateMetaExpression(node));
    varDict.put("name", Values.make(node.getSymbolName()));
    varDict.put("expression", Values.make(node.getValueExpression().getSourceInfo().getSourceCode()));
    return Values.make(varDict.persistent());

  }

  static Value makeMetaValue(Node node){

    // module
    if (node instanceof ModuleNode){
      return makeMetaValue((ModuleNode) node);
    }
    else if (node instanceof LibraryNode){
      return makeMetaValue((LibraryNode) node);
    }
    else if (node instanceof VarDefNode){
      return makeMetaValue((VarDefNode) node);
    }
    else {
      return Values.NIL;
    }

  }

}
