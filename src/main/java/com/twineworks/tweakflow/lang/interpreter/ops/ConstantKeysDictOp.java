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

package com.twineworks.tweakflow.lang.interpreter.ops;

import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.ast.expressions.DictEntryNode;
import com.twineworks.tweakflow.lang.ast.expressions.DictNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

final public class ConstantKeysDictOp implements ExpressionOp {

  private final DictNode node;
  private final ExpressionOp[] valueOps;
  private final ExpressionOp[] nonConstValueOps;
  private final Map.Entry<String, Value>[] entries;
  private final Map.Entry<String, Value>[] nonConstEntries;
  private final int nonConstSize;
  private final int size;
  private final int constSize;
  private final DictValue constEntries;

  @SuppressWarnings("unchecked")
  public ConstantKeysDictOp(DictNode dictNode) {
    this.node = dictNode;
    size = dictNode.getEntries().size();
    valueOps = new ExpressionOp[size];
    entries = new Map.Entry[size];
    int nonConst = 0;

    int i=0;
    ArrayList<Map.Entry<String, Value>> nonConstEntriesList = new ArrayList<>(size);
    ArrayList<ExpressionOp> nonConstOpsList = new ArrayList<>(size);
    DictValue c = new DictValue();

    for (DictEntryNode entryNode : dictNode.getEntries()) {
      ExpressionNode keyExp = entryNode.getKey();
      ExpressionOp keyOp = keyExp.getOp();

      String key = Interpreter.evaluateInEmptyScope(keyOp).string();
      if (key == null) throw new LangException(LangError.NIL_ERROR, "dict key cannot be nil", keyExp.getSourceInfo());
      entries[i] = new AbstractMap.SimpleEntry<String, Value>(key, Values.NIL);
      valueOps[i] = entryNode.getValue().getOp();

      ExpressionOp valueOp = valueOps[i];
      Value v = null;

      if (valueOp.isConstant()){
        try {
          v = Interpreter.evaluateInEmptyScope(valueOp);
        }
        catch (LangException ignored){}
      }

      if (v != null){
        entries[i].setValue(v);
        c = c.put(key, v);
      }
      else{
        nonConstEntriesList.add(entries[i]);
        nonConstOpsList.add(valueOps[i]);
        nonConst++;
      }
      i+=1;
    }
    nonConstSize = nonConst;
    nonConstEntries = nonConstEntriesList.toArray(new Map.Entry[0]);
    nonConstValueOps = nonConstOpsList.toArray(new ExpressionOp[0]);
    constEntries = c;
    constSize = constEntries.size();
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    for (int i = 0; i < nonConstSize; i++) {
      nonConstEntries[i].setValue(nonConstValueOps[i].eval(stack, context));
    }

    Value ret;
    if (constSize == 0){
      ret = Values.make(new DictValue(nonConstEntries));
    }
    else{
      ret = Values.make(constEntries.putAll(new DictValue(nonConstEntries)));
    }

    // aggressively clear non-constants from memory
    for (int i = 0; i < nonConstSize; i++) {
      nonConstEntries[i].setValue(null);
    }

    return ret;

  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new ConstantKeysDictOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new ConstantKeysDictOp(node);
  }



}
