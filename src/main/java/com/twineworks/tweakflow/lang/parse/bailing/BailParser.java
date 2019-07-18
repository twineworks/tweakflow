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

package com.twineworks.tweakflow.lang.parse.bailing;

import com.twineworks.tweakflow.grammar.TweakFlowLexer;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.structure.EmptyNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.builders.ExpressionBuilder;
import com.twineworks.tweakflow.lang.parse.builders.ModuleHeadBuilder;
import com.twineworks.tweakflow.lang.parse.builders.UnitBuilder;
import com.twineworks.tweakflow.lang.parse.builders.VarDefBuilder;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.parse.util.NullParserErrorListener;
import com.twineworks.tweakflow.lang.parse.util.ParseErrorHelper;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class BailParser {

  private ParseUnit parseUnit;

  public BailParser(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  public ParseResult parseExpression() {
    return parse(
        (TweakFlowParser p) -> p.standaloneExpression().expression(),
        (ParserRuleContext ctx) -> new ExpressionBuilder(parseUnit).visit(ctx)
    );
  }

  public ParseResult parseUnit() {
    return parse(
        TweakFlowParser::unit,
        (ParserRuleContext ctx) -> new UnitBuilder(parseUnit).visit(ctx)
    );
  }

  public ParseResult parseReference() {
    return parse(
        (TweakFlowParser p) -> p.standaloneReference().reference(),
        (ParserRuleContext ctx) -> new ExpressionBuilder(parseUnit).visit(ctx)
    );
  }

  public ParseResult parseInteractiveInput() {
    return parse(
        TweakFlowParser::interactiveInput,
        (ParserRuleContext ctx) -> {
          TweakFlowParser.InteractiveInputContext parseTree = (TweakFlowParser.InteractiveInputContext) ctx;
          if (parseTree.expression() != null) {
            return new ExpressionBuilder(parseUnit).visit(parseTree.expression());
          } else if (parseTree.varDef() != null) {
            return new VarDefBuilder(parseUnit).visitVarDef(parseTree.varDef());
          } else if (parseTree.empty() != null) {
            return new EmptyNode().setSourceInfo(srcOf(parseUnit, parseTree.empty()));
          } else {
            throw new AssertionError("Unknown interactive input encountered: " + parseTree.getClass().getName());
          }
        }
    );
  }

  private ParseResult parse(RuleInvoker ruleInvoker, NodeBuilder nodeBuilder) {

    long parseStart = System.currentTimeMillis();

    CodePointCharStream input = CharStreams.fromString(parseUnit.getProgramText());
    TweakFlowLexer lexer = new BailLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    NullParserErrorListener errorListener = new NullParserErrorListener();

    // default listeners cause noise on stdout
    lexer.removeErrorListeners();
    lexer.addErrorListener(errorListener);

    // consume tokens
    try {
      tokens.fill();
    } catch (ParseCancellationException e) {
      LangException exception = ParseErrorHelper.exceptionFor(parseUnit, lexer, tokens, e);
      long parseEnd = System.currentTimeMillis();
      return ParseResult.error(exception, parseEnd - parseStart, 0);
    }

    // parse them
    TweakFlowParser parser = new TweakFlowParser(tokens);

    // bail on errors, we're in non-IDE mode
    parser.setErrorHandler(new BailErrorStrategy());

    // default listeners cause noise on stdout:
    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);

    ParserRuleContext parseTree;

    try {
      parseTree = ruleInvoker.invokeRule(parser);
    } catch (ParseCancellationException e) {
      LangException exception = ParseErrorHelper.exceptionFor(parseUnit, parser, e);
      long parseEnd = System.currentTimeMillis();
      return ParseResult.error(exception, parseEnd - parseStart, 0);
    }

    long parseEnd = System.currentTimeMillis();
    long buildStart = parseEnd;

    if (parseTree == null) {
      throw new AssertionError("ruleInvoker did not produce a parse tree");
    }

    // build AST nodes
    try {
      Node result = nodeBuilder.buildNode(parseTree);
      long buildEnd = System.currentTimeMillis();
      return ParseResult.ok(result, parseEnd - parseStart, buildEnd - buildStart);
    } catch (LangException e) {
      long buildEnd = System.currentTimeMillis();
      return ParseResult.error(e, parseEnd - parseStart, buildEnd - buildStart);
    } catch (RuntimeException e) {
      long buildEnd = System.currentTimeMillis();
      return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd - parseStart, buildEnd - buildStart);
    }


  }

  public ParseResult parseModuleHead() {
    return parse(
        TweakFlowParser::moduleHead,
        (ParserRuleContext ctx) -> new ModuleHeadBuilder(parseUnit).visit(ctx)
    );
  }
}
