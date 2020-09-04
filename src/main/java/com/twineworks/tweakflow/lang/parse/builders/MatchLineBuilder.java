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

package com.twineworks.tweakflow.lang.parse.builders;

import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseVisitor;
import com.twineworks.tweakflow.lang.ast.structure.match.DefaultPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLineNode;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.util.List;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class MatchLineBuilder extends TweakFlowParserBaseVisitor<MatchLineNode>{

  private final ParseUnit parseUnit;
  private final boolean recovery;
  private final List<LangException> recoveryErrors;

  public MatchLineBuilder(ParseUnit parseUnit, boolean recovery, List<LangException> recoveryErrors) {
    this.parseUnit = parseUnit;
    this.recovery = recovery;
    this.recoveryErrors = recoveryErrors;
  }

  @Override
  public MatchLineNode visitPatternLine(TweakFlowParser.PatternLineContext ctx) {
    MatchLineNode matchLineNode = new MatchLineNode();
    matchLineNode.setSourceInfo(srcOf(parseUnit, ctx));
    matchLineNode.setExpression(new ExpressionBuilder(parseUnit, recovery, recoveryErrors).visit(ctx.expression()));
    if (ctx.matchGuard() != null){
      matchLineNode.setGuard(new ExpressionBuilder(parseUnit, recovery, recoveryErrors).visit(ctx.matchGuard()));
    }
    matchLineNode.setPattern(new MatchPatternBuilder(parseUnit, recovery, recoveryErrors).visit(ctx.matchPattern()));
    return matchLineNode;
  }

  @Override
  public MatchLineNode visitDefaultLine(TweakFlowParser.DefaultLineContext ctx) {
    MatchLineNode matchLineNode = new MatchLineNode();
    matchLineNode.setSourceInfo(srcOf(parseUnit, ctx));
    matchLineNode.setExpression(new ExpressionBuilder(parseUnit, recovery, recoveryErrors).visit(ctx.expression()));

    DefaultPatternNode patternNode = new DefaultPatternNode();
    patternNode.setSourceInfo(srcOf(parseUnit, ctx));

    matchLineNode.setPattern(patternNode);
    return matchLineNode;
  }
}
