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

package com.twineworks.tweakflow.lang.parse.recovery;

import com.twineworks.tweakflow.grammar.TweakFlowLexer;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.structure.EmptyNode;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.NodeBuilder;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.RuleInvoker;
import com.twineworks.tweakflow.lang.parse.builders.ExpressionBuilder;
import com.twineworks.tweakflow.lang.parse.builders.ModuleHeadBuilder;
import com.twineworks.tweakflow.lang.parse.builders.UnitBuilder;
import com.twineworks.tweakflow.lang.parse.builders.VarDefBuilder;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.parse.util.CollectingParserErrorListener;
import org.antlr.v4.runtime.*;

import java.util.List;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class RecoveryParser {

  private final ParseUnit parseUnit;

  public RecoveryParser(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  public ParseResult parseExpression() {
    return parse(
        (TweakFlowParser p) -> p.standaloneExpression().expression(),
        (ParserRuleContext ctx, boolean recovery, List<LangException> recoveryErrors) -> new ExpressionBuilder(parseUnit, recovery, recoveryErrors).visit(ctx)
    );
  }

  public ParseResult parseUnit() {
    return parse(
        TweakFlowParser::unit,
        (ParserRuleContext ctx, boolean recovery, List<LangException> recoveryErrors) -> new UnitBuilder(parseUnit, recovery, recoveryErrors).visit(ctx)
    );
  }

  public ParseResult parseReference() {
    return parse(
        (TweakFlowParser p) -> p.standaloneReference().reference(),
        (ParserRuleContext ctx, boolean recovery, List<LangException> recoveryErrors) -> new ExpressionBuilder(parseUnit, recovery, recoveryErrors).visit(ctx)
    );
  }

  public ParseResult parseInteractiveInput() {
    return parse(
        TweakFlowParser::interactiveInput,
        (ParserRuleContext ctx, boolean recovery, List<LangException> recoveryErrors) -> {
          TweakFlowParser.InteractiveInputContext parseTree = (TweakFlowParser.InteractiveInputContext) ctx;
          if (parseTree.expression() != null) {
            return new ExpressionBuilder(parseUnit, recovery, recoveryErrors).visit(parseTree.expression());
          } else if (parseTree.varDef() != null) {
            return new VarDefBuilder(parseUnit, recovery, recoveryErrors).visitVarDef(parseTree.varDef());
          } else if (parseTree.empty() != null) {
            return new EmptyNode().setSourceInfo(srcOf(parseUnit, parseTree.empty()));
          } else {
            throw new AssertionError("Unknown interactive input encountered: " + parseTree.getClass().getName());
          }
        }
    );
  }

  public ParseResult parseModuleHead() {
    return parse(
        TweakFlowParser::moduleHead,
        (ParserRuleContext ctx, boolean recovery, List<LangException> recoveryErrors) -> new ModuleHeadBuilder(parseUnit, recovery, recoveryErrors).visit(ctx)
    );
  }

  private ParseResult parse(RuleInvoker ruleInvoker, NodeBuilder nodeBuilder) {

    long parseStart = System.currentTimeMillis();

    CodePointCharStream input = CharStreams.fromString(parseUnit.getProgramText());
    TweakFlowLexer lexer = new RecoveryLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    CollectingParserErrorListener errorListener = new CollectingParserErrorListener(parseUnit);

    // default listeners cause noise on stdout
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);

    // consume tokens
    tokens.fill();

    // parse them
    TweakFlowParser parser = new TweakFlowParser(tokens);

    // proceed on errors, we're taking what we can get
    parser.setErrorHandler(new DefaultErrorStrategy());

    // default listeners cause noise on stdout:
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    ParserRuleContext parseTree = ruleInvoker.invokeRule(parser);

    long parseEnd = System.currentTimeMillis();
    long buildStart = parseEnd;

    if (parseTree == null) {
      throw new AssertionError("ruleInvoker did not produce a parse tree");
    }

    // build AST nodes
    Node result = nodeBuilder.buildNode(parseTree, true, errorListener.getErrors());
    long buildEnd = System.currentTimeMillis();

    return ParseResult.recovery(errorListener.getErrors(), result, parseEnd - parseStart, buildEnd - buildStart);

  }


}
