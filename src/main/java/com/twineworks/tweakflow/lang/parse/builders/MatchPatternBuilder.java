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
import com.twineworks.tweakflow.lang.ast.expressions.StringNode;
import com.twineworks.tweakflow.lang.ast.structure.match.*;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.util.LangUtil;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.identifier;
import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class MatchPatternBuilder extends TweakFlowParserBaseVisitor<MatchPatternNode>{

  private final ParseUnit parseUnit;

  public MatchPatternBuilder(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  @Override
  public MatchPatternNode visitExpPattern(TweakFlowParser.ExpPatternContext ctx) {
    ExpressionPatternNode patternNode = new ExpressionPatternNode();
    patternNode.setSourceInfo(srcOf(parseUnit, ctx));
    patternNode.setExpression(new ExpressionBuilder(parseUnit).visit(ctx.expression()));

    if (ctx.varCapture() != null){
      patternNode.setCapture(visitVarCapture(ctx.varCapture()));
    }

    return patternNode;
  }

  @Override
  public MatchPatternNode visitDataTypePattern(TweakFlowParser.DataTypePatternContext ctx) {
    DataTypePatternNode patternNode = new DataTypePatternNode();
    patternNode.setSourceInfo(srcOf(parseUnit, ctx));
    patternNode.setType(Types.byName(ctx.dataType().getText()));
    if (ctx.varCapture() != null){
      patternNode.setCapture(visitVarCapture(ctx.varCapture()));
    }
    return patternNode;
  }

  @Override
  public CapturePatternNode visitVarCapture(TweakFlowParser.VarCaptureContext ctx) {
    CapturePatternNode capturePatternNode = new CapturePatternNode();
    capturePatternNode.setSourceInfo(srcOf(parseUnit, ctx));
    if (ctx.identifier() != null){
      capturePatternNode.setSymbolName(identifier(ctx.identifier().getText()));
    }
    return capturePatternNode;
  }

  @Override
  public CapturePatternNode visitCapturePattern(TweakFlowParser.CapturePatternContext ctx) {
    CapturePatternNode capturePatternNode = new CapturePatternNode();
    capturePatternNode.setSourceInfo(srcOf(parseUnit, ctx));
    if (ctx.varCapture().identifier() != null){
      capturePatternNode.setSymbolName(identifier(ctx.varCapture().identifier().getText()));
    }
    return capturePatternNode;
  }

  @Override
  public CapturePatternNode visitSplatCapture(TweakFlowParser.SplatCaptureContext ctx) {
    CapturePatternNode capturePatternNode = new CapturePatternNode();
    capturePatternNode.setSourceInfo(srcOf(parseUnit, ctx));
    if (ctx.identifier() != null){
      capturePatternNode.setSymbolName(identifier(ctx.identifier().getText()));
    }
    return capturePatternNode;
  }

  @Override
  public MatchPatternNode visitListPattern(TweakFlowParser.ListPatternContext ctx) {
    ListPatternNode node = new ListPatternNode();
    node.setSourceInfo(srcOf(parseUnit, ctx));

    for (TweakFlowParser.MatchPatternContext pattern : ctx.matchPattern()) {
      node.getElements().add(visit(pattern));
    }

    if (ctx.varCapture() != null){
      node.setCapture(visitVarCapture(ctx.varCapture()));
    }

    return node;
  }

  @Override
  public MatchPatternNode visitHeadTailListPattern(TweakFlowParser.HeadTailListPatternContext ctx) {

    HeadTailListPatternNode node = new HeadTailListPatternNode();
    node.setSourceInfo(srcOf(parseUnit, ctx));

    for (TweakFlowParser.MatchPatternContext pattern : ctx.matchPattern()) {
      node.getElements().add(visit(pattern));
    }

    node.setTailCapture(visitSplatCapture(ctx.splatCapture()));

    if (ctx.varCapture() != null){
      node.setCapture(visitVarCapture(ctx.varCapture()));
    }

    return node;
  }

  @Override
  public MatchPatternNode visitInitLastListPattern(TweakFlowParser.InitLastListPatternContext ctx) {

    InitLastListPatternNode node = new InitLastListPatternNode();
    node.setSourceInfo(srcOf(parseUnit, ctx));

    for (TweakFlowParser.MatchPatternContext pattern : ctx.matchPattern()) {
      node.getElements().add(visit(pattern));
    }

    node.setInitCapture(visitSplatCapture(ctx.splatCapture()));

    if (ctx.varCapture() != null){
      node.setCapture(visitVarCapture(ctx.varCapture()));
    }

    return node;
  }

  @Override
  public MatchPatternNode visitMidListPattern(TweakFlowParser.MidListPatternContext ctx) {

    MidListPatternNode node = new MidListPatternNode();
    node.setSourceInfo(srcOf(parseUnit, ctx));

    ArrayList<MatchPatternNode> elements = node.getHeadElements();
    for (ParseTree child : ctx.children) {
      if (child instanceof TweakFlowParser.MatchPatternContext){
        elements.add(visit(child));
      }
      else if (child instanceof TweakFlowParser.SplatCaptureContext){
        node.setMidCapture(visitSplatCapture((TweakFlowParser.SplatCaptureContext) child));
        elements = node.getLastElements();
      }
    }

    if (ctx.varCapture() != null){
      node.setCapture(visitVarCapture(ctx.varCapture()));
    }

    return node;
  }

  @Override
  public MatchPatternNode visitOpenDictPattern(TweakFlowParser.OpenDictPatternContext ctx) {

    OpenDictPatternNode node = new OpenDictPatternNode();
    node.setSourceInfo(srcOf(parseUnit, ctx));
    ExpressionBuilder expressionBuilder = new ExpressionBuilder(parseUnit);

    List<TweakFlowParser.MatchPatternContext> matchPattern = ctx.matchPattern();
    List<TweakFlowParser.StringConstantContext> keys = ctx.stringConstant();

    for (int i = 0; i < matchPattern.size(); i++) {
      TweakFlowParser.MatchPatternContext pattern = matchPattern.get(i);

      StringNode keyNode = (StringNode) expressionBuilder.visit(keys.get(i));
      String key = keyNode.getStringVal();
      if (node.getElements().containsKey(key)){
        throw new LangException(LangError.ALREADY_DEFINED, "key "+ LangUtil.getKeyLiteral(key)+" already defined in this pattern", srcOf(parseUnit, keys.get(i)));
      }
      node.getElements().put(key, visit(pattern));
    }

    // exactly one rest capture allowed
    if (ctx.splatCapture().size() != 1){
      throw new LangException(LangError.ALREADY_DEFINED, "splat capture already defined in this pattern", srcOf(parseUnit, ctx.splatCapture(1)));
    }
    node.setRestCapture(visitSplatCapture(ctx.splatCapture(0)));


    if (ctx.varCapture() != null){
      node.setCapture(visitVarCapture(ctx.varCapture()));
    }

    return node;

  }

  @Override
  public MatchPatternNode visitDictPattern(TweakFlowParser.DictPatternContext ctx) {

    DictPatternNode node = new DictPatternNode();
    node.setSourceInfo(srcOf(parseUnit, ctx));
    ExpressionBuilder expressionBuilder = new ExpressionBuilder(parseUnit);

    List<TweakFlowParser.MatchPatternContext> matchPattern = ctx.matchPattern();
    List<TweakFlowParser.StringConstantContext> keys = ctx.stringConstant();

    for (int i = 0; i < matchPattern.size(); i++) {
      TweakFlowParser.MatchPatternContext pattern = matchPattern.get(i);

      StringNode keyNode = (StringNode) expressionBuilder.visit(keys.get(i));
      String key = keyNode.getStringVal();

      if (node.getElements().containsKey(key)){
        throw new LangException(LangError.ALREADY_DEFINED, "key "+LangUtil.getKeyLiteral(key), srcOf(parseUnit, keys.get(i)));
      }
      node.getElements().put(key, visit(pattern));
    }

    if (ctx.varCapture() != null){
      node.setCapture(visitVarCapture(ctx.varCapture()));
    }

    return node;
  }
}
