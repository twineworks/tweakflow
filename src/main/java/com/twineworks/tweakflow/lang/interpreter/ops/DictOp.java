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

package com.twineworks.tweakflow.lang.interpreter.ops;

import com.twineworks.tweakflow.lang.interpreter.Evaluator;
import com.twineworks.tweakflow.lang.ast.expressions.DictEntryNode;
import com.twineworks.tweakflow.lang.ast.expressions.DictNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;

import java.util.List;

final public class DictOp implements ExpressionOp {

  private final DictNode node;

  public DictOp(DictNode node) {
    this.node = node;
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    List<DictEntryNode> entries = node.getEntries();

    DictValue map = new DictValue();
    for (DictEntryNode entryNode : entries) {
      ExpressionNode keyExp = entryNode.getKey();
      Value key = keyExp.getOp().eval(stack, context);
      if (key == Values.NIL) throw new LangException(LangError.NIL_ERROR, "dict key cannot be nil", stack, keyExp.getSourceInfo());

      ExpressionNode valueExp = entryNode.getValue();
      Value value = valueExp.getOp().eval(stack, context);

      map = map.put(key.string(), value);
    }
    return Values.make(map);

  }

  @Override
  public boolean isConstant() {

    for (DictEntryNode entryNode : node.getEntries()) {
      if (!entryNode.getKey().getOp().isConstant()) return false;
      if (!entryNode.getValue().getOp().isConstant()) return false;
    }

    return true;
  }

  @Override
  public ExpressionOp specialize() {


    // all dict keys constant?
    boolean allDictKeysConstant = true;
    for (DictEntryNode dictEntryNode : node.getEntries()) {
      ExpressionOp keyOp = dictEntryNode.getKey().getOp();
      if (!keyOp.isConstant()){
        allDictKeysConstant = false;
        break;
      }
      else{
        try {
          Evaluator.evaluateInEmptyScope(dictEntryNode.getKey());
        } catch (LangException ignored){
          allDictKeysConstant = false;
          break;
        }
      }
    }

    if (allDictKeysConstant){
      return new ConstantKeysDictOp(node);
    }

    return new DictOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new DictOp(node);
  }


}
