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
import com.twineworks.tweakflow.lang.ast.expressions.ContainerAccessNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;

final public class ContainerAccessOp implements ExpressionOp {

  private final ExpressionOp containerOp;
  private final ExpressionOp keysOp;
  private final ContainerAccessNode node;

  public ContainerAccessOp(ContainerAccessNode node) {
    containerOp = node.getContainerExpression().getOp();
    keysOp = node.getKeysExpression().getOp();
    this.node = node;
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {
    Value xs = containerOp.eval(stack, context);
    Value ks = keysOp.eval(stack, context);

    if (xs == Values.NIL) return Values.NIL;
    ListValue ksList = ks.list();

    Value current = xs;

    for (Value index : ksList) {

      // map navigation
      if (current.type() == Types.DICT){
        DictValue map = current.dict();
        Value key = index.castTo(Types.STRING);
        if (key == Values.NIL){
          return Values.NIL; // nil key
        }

        String strKey = key.string();
        current = map.get(strKey);
        if (current == Values.NIL) return Values.NIL;

      }

      // list navigation
      else if(current.type() == Types.LIST){

        ListValue list = current.list();
        Value idx = index.castTo(Types.LONG);
        if (idx == Values.NIL){
          return Values.NIL; // nil key
        }

        long idxLong = idx.longNum();

        Value next = list.get(idxLong);

        if (next == Values.NIL){
          return Values.NIL;
        }
        else{
          current = next;
        }

      }
      else {
        throw new LangException(LangError.CAST_ERROR, "container access not defined for type "+current.type().name());
      }
    }

    return current;

  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {

    try {
      // dict access?
      if (node.getContainerExpression().getValueType() == Types.DICT){
        // through a constant path?
        if (node.getKeysExpression().getOp().isConstant()){
          ListValue keys = Evaluator.evaluateInEmptyScope(node.getKeysExpression()).list();
          // of a single key?
          if (keys.size() == 1){
            String key = keys.get(0).castTo(Types.STRING).string();
            if (key == null) {
              return new ConstantOp(Values.NIL);
            }
            return new SimpleDictContainerAccessConstantKeyOp(node);
          }
        }
      }

      // list access?
      if (node.getContainerExpression().getValueType() == Types.LIST){
        // through a constant path?
        if (node.getKeysExpression().getOp().isConstant()){
          ListValue keys = Evaluator.evaluateInEmptyScope(node.getKeysExpression()).list();
          // of a single index?
          if (keys.size() == 1){
            Long idx = keys.get(0).castTo(Types.LONG).longNum();
            if (idx == null){
              return new ConstantOp(Values.NIL);
            }
            return new SimpleListContainerAccessConstantKeyOp(node);
          }
        }
      }
    } catch (LangException ignored){}

    return new ContainerAccessOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new ContainerAccessOp(node);
  }


}
