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

package com.twineworks.tweakflow.lang.parse.builders;

import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseVisitor;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.structure.GeneratorNode;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.types.Type;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.*;

public class GeneratorBuilder extends TweakFlowParserBaseVisitor<GeneratorNode>{

  private final ParseUnit parseUnit;

  public GeneratorBuilder(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  @Override
  public GeneratorNode visitGenerator(TweakFlowParser.GeneratorContext ctx) {
    Type declaredType = type(ctx.dataType());

    ExpressionBuilder expressionBuilder = new ExpressionBuilder(parseUnit);
    ExpressionNode expression = expressionBuilder.visit(ctx.expression());
    expression = expressionBuilder.addImplicitCast(declaredType, expression);

    GeneratorNode generator = new GeneratorNode()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setSymbolName(identifier(ctx.identifier().getText()))
        .setDeclaredType(declaredType)
        .setValueExpression(expression);

    return generator;
  }

}
