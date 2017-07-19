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

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.structure.InteractiveNode;
import com.twineworks.tweakflow.lang.ast.structure.InteractiveSectionNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefs;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseVisitor;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.util.List;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class InteractiveBuilder extends TweakFlowParserBaseVisitor<Node>{

  private final ParseUnit parseUnit;

  public InteractiveBuilder(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  @Override
  public InteractiveNode visitInteractive(TweakFlowParser.InteractiveContext ctx) {

    InteractiveNode interactiveNode = new InteractiveNode()
        .setSourceInfo(srcOf(parseUnit, ctx));

    List<InteractiveSectionNode> sections = interactiveNode.getSections();
    for (TweakFlowParser.InteractiveSectionContext sectionContext : ctx.interactiveSection()) {
      sections.add(visitInteractiveSection(sectionContext));
    }

    return interactiveNode;
  }

  @Override
  public InteractiveSectionNode visitInteractiveSection(TweakFlowParser.InteractiveSectionContext ctx) {

    ReferenceNode inScope = (ReferenceNode) new ExpressionBuilder(parseUnit).visit(ctx.reference());

    // can only reference modules
    if (inScope.getElements().size() != 1){
      throw new LangException(LangError.INVALID_REFERENCE_TARGET, "must reference a module in unit space", srcOf(parseUnit, ctx.reference()));
    }

    InteractiveSectionNode sectionNode = new InteractiveSectionNode()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setInScopeRef(inScope);

    buildVars(sectionNode.getVars(), ctx.varDef());

    return sectionNode;
  }

  private void buildVars(VarDefs vars, List<TweakFlowParser.VarDefContext> varDefContexts){

    for (TweakFlowParser.VarDefContext varDefContext : varDefContexts) {
      VarDefNode varDef = new VarDefBuilder(parseUnit).visitVarDef(varDefContext);
      if (vars.getMap().containsKey(varDef.getSymbolName())){
        throw new LangException(LangError.ALREADY_DEFINED, varDef.getSymbolName()+" already defined", srcOf(parseUnit, varDefContext));
      }
      vars.getMap().put(varDef.getSymbolName(), varDef);
    }
    vars.cook();
  }

}
