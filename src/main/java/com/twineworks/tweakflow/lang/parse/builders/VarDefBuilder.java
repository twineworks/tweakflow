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
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.NilNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.types.Type;

import java.util.List;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.*;

public class VarDefBuilder extends TweakFlowParserBaseVisitor<VarDefNode>{

  private final ParseUnit parseUnit;
  private final boolean recovery;
  private final List<LangException> recoveryErrors;

  public VarDefBuilder(ParseUnit parseUnit, boolean recovery, List<LangException> recoveryErrors) {
    this.parseUnit = parseUnit;
    this.recovery = recovery;
    this.recoveryErrors = recoveryErrors;
  }

  @Override
  public VarDefNode visitVarDef(TweakFlowParser.VarDefContext ctx) {
    Type declaredType = type(ctx.dataType());

    ExpressionBuilder expressionBuilder = new ExpressionBuilder(parseUnit, recovery, recoveryErrors);

    ExpressionNode expression = expressionBuilder.visit(ctx.expression());

    if (recovery && expression == null){
      // if the expression comes back broken, replace it with a nil node
      expression = new NilNode().setSourceInfo(srcOf(parseUnit, ctx.expression()));
    }

    expression = expressionBuilder.addImplicitCast(declaredType, expression);

    VarDefNode varDef = new VarDefNode()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setSymbolName(identifier(ctx.identifier().getText()))
        .setDeclaredType(declaredType)
        .setValueExpression(expression)
        .setDeclaredProvided(false);

    TweakFlowParser.DocContext docContext = getDocContext(ctx);
    if (docContext != null){
      varDef.setDoc(new DocBuilder(parseUnit, recovery, recoveryErrors).visitDoc(docContext));
    }

    TweakFlowParser.MetaContext metaContext = getMetaContext(ctx);
    if (metaContext != null){
      varDef.setMeta(new MetaBuilder(parseUnit, recovery, recoveryErrors).visitMeta(metaContext));
    }

    return varDef;
  }

  @Override
  public VarDefNode visitVarDec(TweakFlowParser.VarDecContext ctx) {
    Type declaredType = type(ctx.dataType());

    ExpressionBuilder expressionBuilder = new ExpressionBuilder(parseUnit, recovery, recoveryErrors);

    // expression is missing on provided vars, it is implicitly nil
    ExpressionNode expression = new NilNode().setSourceInfo(srcOf(parseUnit, ctx));

    VarDefNode varDef = new VarDefNode()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setSymbolName(identifier(ctx.identifier().getText()))
        .setDeclaredType(declaredType)
        .setValueExpression(expression)
        .setDeclaredProvided(true);

    TweakFlowParser.DocContext docContext = getDocContext(ctx);
    if (docContext != null){
      varDef.setDoc(new DocBuilder(parseUnit, recovery, recoveryErrors).visitDoc(docContext));
    }

    TweakFlowParser.MetaContext metaContext = getMetaContext(ctx);
    if (metaContext != null){
      varDef.setMeta(new MetaBuilder(parseUnit, recovery, recoveryErrors).visitMeta(metaContext));
    }

    return varDef;
  }

  /**
   * Helper for null safe extraction of DocContext from a VarDefContext.
   * @param ctx the VarDefContext to extract DocContext from
   * @return DocContext of given VarDefContext or null
   */

  private TweakFlowParser.DocContext getDocContext(TweakFlowParser.VarDefContext ctx){

    TweakFlowParser.MetaDefContext metaDef = ctx.metaDef();
    if (metaDef == null) return null;

    return metaDef.doc();
  }

  private TweakFlowParser.DocContext getDocContext(TweakFlowParser.VarDecContext ctx){

    TweakFlowParser.MetaDefContext metaDef = ctx.metaDef();
    if (metaDef == null) return null;

    return metaDef.doc();
  }


  /**
   * Helper for null safe extraction of MetaContext from a VarDefContext.
   * @param ctx the VarDefContext to extract MetaContext from
   * @return MetaContext of given VarDefContext or null
   */

  private TweakFlowParser.MetaContext getMetaContext(TweakFlowParser.VarDefContext ctx){

    TweakFlowParser.MetaDefContext metaDef = ctx.metaDef();
    if (metaDef == null) return null;

    return metaDef.meta();
  }

  private TweakFlowParser.MetaContext getMetaContext(TweakFlowParser.VarDecContext ctx){

    TweakFlowParser.MetaDefContext metaDef = ctx.metaDef();
    if (metaDef == null) return null;

    return metaDef.meta();
  }

}
