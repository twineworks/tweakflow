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
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.util.ArrayList;

final public class StringMultiConcatOp implements ExpressionOp {

  final ArrayList<ExpressionOp> ops;

  public StringMultiConcatOp(ArrayList<ExpressionOp> ops) {
    this.ops = new ArrayList<>(ops);
  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    StringBuilder sb = new StringBuilder();
    for (ExpressionOp op : ops) {
      String s = op.eval(stack, context).string();
      if (s == null) s = "nil";
      sb.append(s);
    }

    return Values.make(sb.toString());
  }

  @Override
  public boolean isConstant() {
    for (ExpressionOp op : ops) {
      if (!op.isConstant()) return false;
    }
    return true;
  }

  @Override
  public ExpressionOp specialize() {

    ArrayList<ExpressionOp> specializedOps = new ArrayList<>();

    boolean canSpecialize = false;

    for (ExpressionOp op : ops) {
      op = op.specialize();
      specializedOps.add(op);
      if (op instanceof StringMultiConcatOp || op instanceof StringConcatOp){
        canSpecialize = true;
      }
    }

    if (!canSpecialize){
      return new StringMultiConcatOp(specializedOps);
    }

    ArrayList<ExpressionOp> compactedOps = new ArrayList<>();

    for (int i = 0; i < specializedOps.size(); i++) {
      ExpressionOp op = specializedOps.get(i);
      if (op instanceof StringConcatOp) {
        StringConcatOp sop = (StringConcatOp) op;
        compactedOps.add(sop.node.getLeftExpression().getOp());
        compactedOps.add(sop.node.getRightExpression().getOp());
      }
      else if (op instanceof StringMultiConcatOp){
        StringMultiConcatOp sop = (StringMultiConcatOp) op;
        compactedOps.addAll(sop.ops);
      }
      else {
        compactedOps.add(op);
      }
    }

    return new StringMultiConcatOp(compactedOps);

  }

  @Override
  public ExpressionOp refresh() {
    return new StringMultiConcatOp(ops);
  }

}
