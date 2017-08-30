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

package com.twineworks.tweakflow.lang.parse;

import com.twineworks.tweakflow.grammar.TweakFlowLexer;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseListener;
import com.twineworks.tweakflow.lang.ast.UnitNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.structure.EmptyNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.builders.ExpressionBuilder;
import com.twineworks.tweakflow.lang.parse.builders.UnitBuilder;
import com.twineworks.tweakflow.lang.parse.builders.VarDefBuilder;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.parse.util.ParserErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

final public class Parser extends TweakFlowParserBaseListener {

  private ParseUnit parseUnit;

  public Parser(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  public ParseResult parseUnit() {

    long parseStart = System.currentTimeMillis();

    CodePointCharStream input = CharStreams.fromString(parseUnit.getProgramText());
    TweakFlowLexer lexer = new TweakFlowLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    ParserErrorListener errorListener = new ParserErrorListener(parseUnit);

    try {
      // default listeners cause noise on stdout
      lexer.removeErrorListeners();
      lexer.addErrorListener(errorListener);

      // consume tokens
      tokens.fill();

      if (errorListener.hasErrors()){
        long parseEnd = System.currentTimeMillis();
        return ParseResult.error(errorListener.getException(), parseEnd-parseStart, 0);
      }

      // parse them
      TweakFlowParser parser = new TweakFlowParser(tokens);

      // default listeners cause noise on stdout:
      parser.removeErrorListeners();
      parser.addErrorListener(errorListener);

      TweakFlowParser.UnitContext parseTree = parser.unit();

      if (parser.getNumberOfSyntaxErrors() > 0){
        long parseEnd = System.currentTimeMillis();
        return ParseResult.error(errorListener.getException(), parseEnd-parseStart, 0);
      }

      long parseEnd = System.currentTimeMillis();
      long buildStart = parseEnd;
      // build AST nodes
      try {
        UnitNode unitNode = (UnitNode) new UnitBuilder(parseUnit).visit(parseTree);
        long buildEnd = System.currentTimeMillis();
        return ParseResult.ok(unitNode, parseEnd-parseStart, buildEnd-buildStart);
      }
      catch (LangException e){
        long buildEnd = System.currentTimeMillis();
        return ParseResult.error(e, parseEnd-parseStart, buildEnd-buildStart);
      }
      catch (RuntimeException e){
        long buildEnd = System.currentTimeMillis();
        return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, buildEnd-buildStart);
      }

    }
    catch (RecognitionException e){
      long parseEnd = System.currentTimeMillis();
      return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, 0);
    }

  }

  public ParseResult parseInteractiveInput(){

    long parseStart = System.currentTimeMillis();

    CodePointCharStream input = CharStreams.fromString(parseUnit.getProgramText());
    TweakFlowLexer lexer = new TweakFlowLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    ParserErrorListener errorListener = new ParserErrorListener(parseUnit);

    try {
      // default listeners cause noise on stdout
      lexer.removeErrorListeners();
      lexer.addErrorListener(errorListener);

      // consume tokens
      tokens.fill();

      if (errorListener.hasErrors()){
        long parseEnd = System.currentTimeMillis();
        return ParseResult.error(errorListener.getException(), parseEnd-parseStart, 0);
      }

      // parse them
      TweakFlowParser parser = new TweakFlowParser(tokens);

      // default listeners cause noise on stdout:
      parser.removeErrorListeners();
      parser.addErrorListener(errorListener);

      TweakFlowParser.InteractiveInputContext parseTree = parser.interactiveInput();

      if (parser.getNumberOfSyntaxErrors() > 0){
        long parseEnd = System.currentTimeMillis();
        return ParseResult.error(errorListener.getException(), parseEnd-parseStart, 0);
      }

      long parseEnd = System.currentTimeMillis();
      long buildStart = parseEnd;
      // build AST nodes
      if (parseTree.expression() != null){
        try {
          ExpressionNode result = new ExpressionBuilder(parseUnit).visit(parseTree.expression());
          long buildEnd = System.currentTimeMillis();
          return ParseResult.ok(result, parseEnd-parseStart, buildEnd-buildStart);
        }
        catch (LangException e){
          long buildEnd = System.currentTimeMillis();
          return ParseResult.error(e, parseEnd-parseStart, buildEnd-buildStart);
        }
        catch (RuntimeException e){
          long buildEnd = System.currentTimeMillis();
          return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, buildEnd-buildStart);
        }

      }

      else if (parseTree.varDef() != null){
        try {
          VarDefNode result = new VarDefBuilder(parseUnit).visitVarDef(parseTree.varDef());
          long buildEnd = System.currentTimeMillis();
          return ParseResult.ok(result, parseEnd-parseStart, buildEnd-buildStart);
        }
        catch (LangException e){
          long buildEnd = System.currentTimeMillis();
          return ParseResult.error(e, parseEnd-parseStart, buildEnd-buildStart);
        }
        catch (RuntimeException e){
          long buildEnd = System.currentTimeMillis();
          return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, buildEnd-buildStart);
        }
      }

      else if (parseTree.empty() != null){
        long buildEnd = System.currentTimeMillis();
        EmptyNode result = new EmptyNode().setSourceInfo(srcOf(parseUnit, parseTree.empty()));
        return ParseResult.ok(result, parseEnd-parseStart, buildEnd-buildStart);
      }

      else {
        throw new AssertionError("Unknown interactive input encountered: "+parseTree.getClass().getName());
      }

    }
    catch (RecognitionException e){
      long parseEnd = System.currentTimeMillis();
      return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, 0);
    }

  }

  public ParseResult parseReference(){

    long parseStart = System.currentTimeMillis();

    CodePointCharStream input = CharStreams.fromString(parseUnit.getProgramText());
    TweakFlowLexer lexer = new TweakFlowLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    ParserErrorListener errorListener = new ParserErrorListener(parseUnit);

    try {
      // default listeners cause noise on stdout
      lexer.removeErrorListeners();
      lexer.addErrorListener(errorListener);

      // consume tokens
      tokens.fill();

      if (errorListener.hasErrors()){
        long parseEnd = System.currentTimeMillis();
        return ParseResult.error(errorListener.getException(), parseEnd-parseStart, 0);
      }

      // parse them
      TweakFlowParser parser = new TweakFlowParser(tokens);

      // default listeners cause noise on stdout:
      parser.removeErrorListeners();
      parser.addErrorListener(errorListener);

      TweakFlowParser.ReferenceContext reference = parser.standaloneReference().reference();

      if (parser.getNumberOfSyntaxErrors() > 0){
        long parseEnd = System.currentTimeMillis();
        return ParseResult.error(errorListener.getException(), parseEnd-parseStart, 0);
      }

      long parseEnd = System.currentTimeMillis();
      long buildStart = parseEnd;
      // build AST nodes
      if (reference != null){
        try {
          ExpressionNode result = new ExpressionBuilder(parseUnit).visit(reference);
          long buildEnd = System.currentTimeMillis();
          return ParseResult.ok(result, parseEnd-parseStart, buildEnd-buildStart);
        }
        catch (LangException e){
          long buildEnd = System.currentTimeMillis();
          return ParseResult.error(e, parseEnd-parseStart, buildEnd-buildStart);
        }
        catch (RuntimeException e){
          long buildEnd = System.currentTimeMillis();
          return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, buildEnd-buildStart);
        }

      }

      else {
        throw new AssertionError("syntax error (not a reference): "+parseUnit.getProgramText());
      }

    }
    catch (RecognitionException e){
      long parseEnd = System.currentTimeMillis();
      return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, 0);
    }

  }

  public ParseResult parseExpression(){

    long parseStart = System.currentTimeMillis();

    CodePointCharStream input = CharStreams.fromString(parseUnit.getProgramText());
    TweakFlowLexer lexer = new TweakFlowLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    ParserErrorListener errorListener = new ParserErrorListener(parseUnit);

    try {
      // default listeners cause noise on stdout
      lexer.removeErrorListeners();
      lexer.addErrorListener(errorListener);

      // consume tokens
      tokens.fill();

      if (errorListener.hasErrors()){
        long parseEnd = System.currentTimeMillis();
        return ParseResult.error(errorListener.getException(), parseEnd-parseStart, 0);
      }

      // parse them
      TweakFlowParser parser = new TweakFlowParser(tokens);

      // default listeners cause noise on stdout:
      parser.removeErrorListeners();
      parser.addErrorListener(errorListener);

      TweakFlowParser.ExpressionContext parseTree = parser.standaloneExpression().expression();

      if (parser.getNumberOfSyntaxErrors() > 0){
        long parseEnd = System.currentTimeMillis();
        return ParseResult.error(errorListener.getException(), parseEnd-parseStart, 0);
      }

      long parseEnd = System.currentTimeMillis();
      long buildStart = parseEnd;
      // build AST nodes
      if (parseTree != null){
        try {
          ExpressionNode result = new ExpressionBuilder(parseUnit).visit(parseTree);
          long buildEnd = System.currentTimeMillis();
          return ParseResult.ok(result, parseEnd-parseStart, buildEnd-buildStart);
        }
        catch (LangException e){
          long buildEnd = System.currentTimeMillis();
          return ParseResult.error(e, parseEnd-parseStart, buildEnd-buildStart);
        }
        catch (RuntimeException e){
          long buildEnd = System.currentTimeMillis();
          return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, buildEnd-buildStart);
        }

      }

      else {
        throw new AssertionError("Unknown expression input encountered");
      }

    }
    catch (RecognitionException e){
      long parseEnd = System.currentTimeMillis();
      return ParseResult.error(LangException.wrap(e, LangError.PARSE_ERROR), parseEnd-parseStart, 0);
    }

  }


}
