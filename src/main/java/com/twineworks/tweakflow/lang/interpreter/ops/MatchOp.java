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

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.interpreter.EvaluationContext;
import com.twineworks.tweakflow.lang.interpreter.Stack;
import com.twineworks.tweakflow.lang.interpreter.StackEntry;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.LocalMemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpaceType;
import com.twineworks.tweakflow.lang.ast.expressions.MatchNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLineNode;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;

import java.util.List;
import java.util.Set;

final public class MatchOp implements ExpressionOp {

  private final MatchNode node;
  private final ExpressionOp subjectOp;
  private final PatternOp[] patternOps;
  private final ExpressionOp[] guardOps;
  private final ExpressionOp[] resultOps;
  private final ConstShapeMap[] templateFrames;
  private final List<MatchLineNode> matchLineNodes;


  public MatchOp(MatchNode node) {
    this.node = node;
    subjectOp = node.getSubject().getOp();
    matchLineNodes = node.getMatchLines().getElements();
    patternOps = new PatternOp[matchLineNodes.size()];
    guardOps = new ExpressionOp[matchLineNodes.size()];
    resultOps = new ExpressionOp[matchLineNodes.size()];
    templateFrames = new ConstShapeMap[matchLineNodes.size()];

    for (int i = 0; i < matchLineNodes.size(); i++) {
      MatchLineNode lineNode = matchLineNodes.get(i);
      patternOps[i] = lineNode.getPattern().getPatternOp();
      guardOps[i] = lineNode.getGuard() != null ? lineNode.getGuard().getOp() : null;
      resultOps[i] = lineNode.getExpression().getOp();

      Set<String> keySet = lineNode.getExpression().getScope().getSymbols().keySet();
      templateFrames[i] = new ConstShapeMap<>(ShapeKey.getAll(keySet));

    }

  }

  @Override
  public Value eval(Stack stack, EvaluationContext context) {

    StackEntry stackEntry = stack.peek();
    Value subject = subjectOp.eval(stack, context);

    for (int i=0; i<patternOps.length; i++){
      PatternOp patternOp = patternOps[i];
      ExpressionOp guardOp = guardOps[i];

      if (patternOp.matches(subject, stack, context)){

        ConstShapeMap<Cell> cells = new ConstShapeMap<>(templateFrames[i]);
        MemorySpace frame = new LocalMemorySpace(stackEntry.getSpace(), matchLineNodes.get(i).getExpression().getScope(), MemorySpaceType.LOCAL, cells);

        patternOp.bind(subject, frame);
        stack.push(new StackEntry(matchLineNodes.get(i).getPattern(), frame, stackEntry.getClosures()));

        if (guardOp == null || guardOp.eval(stack, context).castTo(Types.BOOLEAN) == Values.TRUE){
          // guard passes
          Value result = resultOps[i].eval(stack, context);
          stack.pop();
          return result;
        }
        stack.pop();
      }
    }

    return Values.NIL;

  }

  @Override
  public boolean isConstant() {
    return false;
  }

  @Override
  public ExpressionOp specialize() {
    return new MatchOp(node);
  }

  @Override
  public ExpressionOp refresh() {
    return new MatchOp(node);
  }


}
