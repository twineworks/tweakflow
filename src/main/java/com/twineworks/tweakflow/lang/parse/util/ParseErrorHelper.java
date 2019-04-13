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

package com.twineworks.tweakflow.lang.parse.util;

import com.twineworks.tweakflow.grammar.TweakFlowLexer;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;
import java.util.Set;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;


public class ParseErrorHelper {

  public static LangException exceptionFor(ParseUnit parseUnit, Recognizer<?, ?> recognizer, Object o, int line, int charIndex, String message, RecognitionException e) {
    if (message == null) {
      message = "parse error at line: " + line;
    } else {
      message = "line: " + line + " " + message;
    }

    return new LangException(LangError.PARSE_ERROR, message, new SourceInfo(parseUnit, line, charIndex + 1, -1, -1));
  }

  public static LangException exceptionFor(ParseUnit parseUnit, TweakFlowLexer lexer, CommonTokenStream tokens, ParseCancellationException e) {
    Throwable cause = e.getCause();

    if (cause instanceof LexerNoViableAltException) {
      return exceptionFor(parseUnit, lexer, tokens, (LexerNoViableAltException) cause);
    } else {
      e.printStackTrace();
      return new LangException(LangError.PARSE_ERROR, "unhandled parse cancellation in lexer");
    }
  }

  public static LangException exceptionFor(ParseUnit parseUnit, TweakFlowLexer lexer, CommonTokenStream tokens, LexerNoViableAltException e) {

    String symbol = "";
    if (e.getStartIndex() >= 0 && e.getStartIndex() < e.getInputStream().size()) {
      symbol = e.getInputStream().getText(Interval.of(e.getStartIndex(), e.getStartIndex()));
      symbol = Utils.escapeWhitespace(symbol, false);
    }

    LexerATNSimulator interpreter = lexer.getInterpreter();
    int line = interpreter.getLine();
    int charIndex = interpreter.getCharPositionInLine();

    StringBuilder msg = new StringBuilder();

    if ("#".equals(symbol)){
      msg.append("syntax error - unterminated or malformed string interpolation of #{value} ?");
    }

    String message = msg.toString();
    if (message.isEmpty()){
      message = "unrecognized token: '" + symbol + "'";
    }

    return new LangException(LangError.PARSE_ERROR, message, new SourceInfo(parseUnit, line, charIndex + 1, -1, -1));

  }

  public static LangException exceptionFor(ParseUnit parseUnit, TweakFlowParser parser, ParseCancellationException e) {

    Throwable cause = e.getCause();
    if (cause instanceof InputMismatchException) {
      return exceptionFor(parseUnit, parser, (InputMismatchException) cause);
    } else if (cause instanceof NoViableAltException) {
      return exceptionFor(parseUnit, parser, (NoViableAltException) cause);
    } else if (cause instanceof FailedPredicateException) {
      return exceptionFor(parseUnit, parser, (FailedPredicateException) cause);
    } else {
      e.printStackTrace();
      return new LangException(LangError.PARSE_ERROR, "unhandled parse cancellation");
    }
  }

  private static String getTokenErrorDisplay(Token t) {
    if (t == null) {
      return "<no token>";
    } else {
      String s = t.getText();
      if (s == null) {
        if (t.getType() == -1) {
          s = "<EOF>";
        } else {
          s = "<" + t.getType() + ">";
        }
      }

      return escapeWSAndQuote(s);
    }
  }

  private static String escapeWSAndQuote(String s) {
    s = s.replace("\n", "\\n");
    s = s.replace("\r", "\\r");
    s = s.replace("\t", "\\t");
    return "'" + s + "'";
  }

  public static LangException exceptionFor(ParseUnit parseUnit, TweakFlowParser parser, InputMismatchException e) {

    // position
    Token offendingToken = e.getOffendingToken();
    int offendingLine = offendingToken.getLine();
    int offendingCharIndex = offendingToken.getCharPositionInLine();

    StringBuilder msg = new StringBuilder();

    RuleContext ctx = e.getCtx();
    Set<Integer> exp = e.getExpectedTokens().toSet();


    // only one expected possibility, usually some delimiter or keyword
    if (exp.size() == 1) {
      int t = exp.iterator().next();
      String expectedToken = parser.getVocabulary().getLiteralName(t);
      if (expectedToken == null) {
        expectedToken = "<EOF>";
      }
      msg.append("expecting ").append(expectedToken);

      switch (t) {
        case TweakFlowParser.EOF:
          if (ctx instanceof TweakFlowParser.ModuleContext) {
            msg.append(" - malformed library keyword or extra content at end of file?");
          }
          break;

        case TweakFlowParser.COLON:
          if (ctx instanceof TweakFlowParser.VarDefContext) {
            msg.append(" - malformed variable definition? Correct syntax is 'var_name: value;'");
          }
          break;

        case TweakFlowParser.END_OF_STATEMENT:
          if (ctx instanceof TweakFlowParser.LibraryContext) {
            msg.append(" - unterminated or malformed variable definition?");
          } else if (ctx instanceof TweakFlowParser.LetExpContext) {
            msg.append(" - unterminated or malformed variable definition?");
          }
          break;

      }
      // if offending token is a quote character, assume it's most likely a misquoted string
    } else if (offendingToken.getType() == TweakFlowLexer.SQ ||
        offendingToken.getType() == TweakFlowLexer.STRING_BEGIN ||
        offendingToken.getType() == TweakFlowLexer.STRING_END) {
      msg.append("syntax error - unterminated or misquoted string?");

    } else {
      // generic error
      msg.append("found ")
          .append(getTokenErrorDisplay(offendingToken))
          .append(" expecting ")
          .append(e.getExpectedTokens().toString(parser.getVocabulary()));
    }

    return new LangException(LangError.PARSE_ERROR, msg.toString(), new SourceInfo(parseUnit, offendingLine, offendingCharIndex + 1, -1, -1));
  }

  public static LangException exceptionFor(ParseUnit parseUnit, TweakFlowParser parser, NoViableAltException e) {

    // position
    Token offendingToken = e.getOffendingToken();
    int offendingLine = offendingToken.getLine();
    int offendingIndex = offendingToken.getCharPositionInLine();
    SourceInfo srcInfo = new SourceInfo(parseUnit, offendingLine, offendingIndex + 1, -1, -1);
    // offending input
    String input;
    TokenStream tokens = parser.getInputStream();
    if (tokens != null) {
      if (e.getStartToken().getType() == -1) {
        input = "<EOF>";
      } else {
        input = tokens.getText(e.getStartToken(), e.getOffendingToken());
      }
    } else {
      input = "<unknown input>";
    }

    StringBuilder msg = new StringBuilder();
    // the malformed section starts with a quote character
    switch (e.getStartToken().getType()) {
      case TweakFlowLexer.SQ:
      case TweakFlowLexer.STRING_BEGIN:
      case TweakFlowLexer.STRING_END: {
        msg.append("syntax error - unterminated or misquoted string?");
        srcInfo = new SourceInfo(parseUnit, e.getStartToken().getLine(), e.getStartToken().getCharPositionInLine() + 1, -1, -1);
      }
      break;

    }

    // generic error
    String message = msg.toString();
    if (message.isEmpty()) {
      message = "syntax error at " + escapeWSAndQuote(input);
    }

    return new LangException(LangError.PARSE_ERROR, message, srcInfo);

  }

  public static LangException exceptionFor(ParseUnit parseUnit, TweakFlowParser parser, FailedPredicateException e) {

    // position
    Token offendingToken = e.getOffendingToken();
    int offendingLine = offendingToken.getLine();
    int offendingIndex = offendingToken.getCharPositionInLine();
    SourceInfo sourceInfo = new SourceInfo(parseUnit, offendingLine, offendingIndex + 1, -1, -1);

    StringBuilder msg = new StringBuilder();

    // extract id, predicates are notated as {/* id: threadExpMissingSep */ false}?
    String id = e.getPredicate().substring(e.getPredicate().indexOf(':') + 1, e.getPredicate().lastIndexOf('*')).trim();

    // handle individual cases
    switch (id) {
      case "threadExpMissingSep": {
        // predicate matches after first non-separated expression
        TweakFlowParser.ThreadExpErrContext ctx = (TweakFlowParser.ThreadExpErrContext) e.getCtx();
        sourceInfo = srcOf(parseUnit, ctx.expression(ctx.expression().size() - 1));
        msg.append("expecting ',' - must separate all functions to thread through with ','");
      }
      break;
      case "threadExpMissingArg": {
        // predicate matches after at least one expression
        TweakFlowParser.ThreadExpErrContext ctx = (TweakFlowParser.ThreadExpErrContext) e.getCtx();
        sourceInfo = srcOf(parseUnit, ctx.expression(0));
        msg.append("expecting '(identifier)' - must provide argument to thread through functions i.e. '->> (x) f1, f2, f3'");
      }
      break;
      case "matchMissingLineSep": {
        // predicate matches after first non-separated matchLine
        TweakFlowParser.MatchBodyContext ctx = (TweakFlowParser.MatchBodyContext) e.getCtx();
        sourceInfo = srcOf(parseUnit, ctx.matchLine(ctx.matchLine().size() - 1));
        msg.append("expecting ',' - must separate all match lines with ','");
      }
      break;
      case "matchMissingGuardSep": {
        // predicate matches after matchLine
        TweakFlowParser.PatternLineContext ctx = (TweakFlowParser.PatternLineContext) e.getCtx();
        sourceInfo = srcOf(parseUnit, ctx.matchGuard());
        msg.append("expecting ',' - must separate match pattern from guard expression with ','");
      }
      break;
      case "matchBadListPattern": {
        // predicate matches full pattern with mixed splats and non-splats, and optional separators
        TweakFlowParser.ErrListPatternContext ctx = (TweakFlowParser.ErrListPatternContext) e.getCtx();

        // manually validate children in order
        boolean seenSplat = false; // there can only be one
        List<ParseTree> children = ctx.children;
        /* skip leading '[' and final ']' nodes */
        for (int i = 1; i < children.size() - 1; i++) {
          ParseTree item = children.get(i);
          boolean isSep = (item instanceof TerminalNode);
          boolean isSplatCapture = (item instanceof TweakFlowParser.SplatCaptureContext);
          boolean isWrongSideColonKey = (item instanceof TweakFlowParser.WrongSideColonKeyContext);

          // colon on key is on the wrong side -> f: is given when :f is correct
          if (isWrongSideColonKey) {
            sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
            TweakFlowParser.WrongSideColonKeyContext wrongSideColonKey = (TweakFlowParser.WrongSideColonKeyContext) item;
            msg.append("unexpected '" + item.getText() + "' - did you mean ':" + wrongSideColonKey.identifier().getText() + "' ?");
          }

          // separator expected on indexes 2, 4, etc.
          if (isSep) {
            if (i % 2 == 1 || i == children.size() - 2) {
              sourceInfo = srcOf(parseUnit, ((TerminalNode) item).getSymbol());
              msg.append("unexpected ','");
              break;
            }
          } else {
            if (i % 2 == 0) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("expecting ',' - must separate list pattern elements with ','");
              break;
            }
          }

          if (isSplatCapture) {
            if (seenSplat) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("unexpected splat capture - there can be only one splat capture in a list pattern");
              break;
            } else {
              seenSplat = true;
            }
          }

        }

      }
      break;
      case "matchBadDictPattern": {
        // predicate matches full pattern with mixed splats and non-splats, and optional separators
        TweakFlowParser.ErrDictPatternContext ctx = (TweakFlowParser.ErrDictPatternContext) e.getCtx();

        // manually validate children in order
        List<ParseTree> children = ctx.children;
        int seenSplats = 0;
        /* skip leading '{' and final '}' nodes */
        for (int i = 1; i < children.size() - 1; i++) {
          ParseTree item = children.get(i);
          boolean isSep = (item instanceof TerminalNode);
          boolean isSepPosition = i % 3 == (3 - seenSplats) % 3;
          boolean isKeyPosition = i % 3 == ((3 - seenSplats) % 3 + 1) % 3;
          boolean isKey = (item instanceof TweakFlowParser.StringConstantContext);
          boolean isSplat = (item instanceof TweakFlowParser.SplatCaptureContext);
          boolean isWrongSideColonKey = (item instanceof TweakFlowParser.WrongSideColonKeyContext);

          // colon on key is on the wrong side -> f: is given when :f is correct
          if (isWrongSideColonKey) {
            sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
            TweakFlowParser.WrongSideColonKeyContext wrongSideColonKey = (TweakFlowParser.WrongSideColonKeyContext) item;
            msg.append("unexpected '" + item.getText() + "' - did you mean ':" + wrongSideColonKey.identifier().getText() + "' ?");
          }

          // separator expected on indexes 3, 6, etc.
          if (isSep) {
            if (!isSepPosition || i == children.size() - 2) {
              sourceInfo = srcOf(parseUnit, ((TerminalNode) item).getSymbol());
              msg.append("unexpected ','");
              break;
            }
          } else {
            if (isSepPosition) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("expecting ',' - must separate dict pattern elements with ','");
              break;
            }
          }

          // string literal or splat expected in key positions
          if (isKeyPosition) {
            if (isSplat) {
              seenSplats++;
              continue;
            } else if (!isKey) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("expecting key - only string constants are allowed as keys in a dict pattern");
              break;
            }
          }

          // if we're in the last position, we must not be expecting a key
          if (i == children.size() - 2) {
            if (isKeyPosition) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("unexpected end of dict pattern - value for key '" + item.getText() + "' expected");
            }
          }
        }

      }
      break;
      case "badContainerAccessKeySequence": {

        TweakFlowParser.ContainerAccessExpContext ctxp = (TweakFlowParser.ContainerAccessExpContext) e.getCtx();
        TweakFlowParser.BadContainerAccessKeySequenceContext ctx = ctxp.badContainerAccessKeySequence();
        // manually validate children in order
        List<ParseTree> children = ctx.children;

        for (int i = 0; i < children.size(); i++) {
          ParseTree item = children.get(i);
          boolean isSep = (item instanceof TerminalNode);
          boolean isWrongSideColonKey = (item instanceof TweakFlowParser.WrongSideColonKeyContext);

          // separator expected on indexes 1, 3, etc.
          if (isSep) {
            if (i % 2 == 0 || i == children.size() - 1) {
              sourceInfo = srcOf(parseUnit, ((TerminalNode) item).getSymbol());
              msg.append("unexpected ','");
              break;
            }
          } else {
            if (i % 2 == 1) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("expecting ',' - must separate container access elements with ','");
              break;
            }
          }

          // colon on key is on the wrong side -> f: is given when :f is correct
          if (isWrongSideColonKey) {
            sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
            TweakFlowParser.WrongSideColonKeyContext wrongSideColonKey = (TweakFlowParser.WrongSideColonKeyContext) item;
            msg.append("unexpected '" + item.getText() + "' - did you mean ':" + wrongSideColonKey.identifier().getText() + "' ?");
          }
        }
      }
      break;
      case "badListLiteral": {

        TweakFlowParser.ListLiteralContext ctx = (TweakFlowParser.ListLiteralContext) e.getCtx();

        // manually validate children in order
        List<ParseTree> children = ctx.children;
        /* skip leading '[' and final ']' nodes */
        for (int i = 1; i < children.size() - 1; i++) {
          ParseTree item = children.get(i);
          boolean isSep = (item instanceof TerminalNode);
          boolean isWrongSideColonKey = (item instanceof TweakFlowParser.WrongSideColonKeyContext);

          // separator expected on indexes 2, 4, etc.
          if (isSep) {
            if (i % 2 == 1 || i == children.size() - 2) {
              sourceInfo = srcOf(parseUnit, ((TerminalNode) item).getSymbol());
              msg.append("unexpected ','");
              break;
            }
          } else {
            if (i % 2 == 0) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("expecting ',' - must separate list elements with ','");
              break;
            }
          }

          // colon on key is on the wrong side -> f: is given when :f is correct
          if (isWrongSideColonKey) {
            sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
            TweakFlowParser.WrongSideColonKeyContext wrongSideColonKey = (TweakFlowParser.WrongSideColonKeyContext) item;
            msg.append("unexpected '" + item.getText() + "' - did you mean ':" + wrongSideColonKey.identifier().getText() + "' ?");
          }
        }

      }
      break;
      case "badDictLiteral": {
        // predicate matches full dict with mixed splats and non-splats, and optional separators
        TweakFlowParser.DictLiteralContext ctx = (TweakFlowParser.DictLiteralContext) e.getCtx();

        // manually validate children in order
        List<ParseTree> children = ctx.children;
        int seenSplats = 0;
        /* skip leading '{' and final '}' nodes */
        for (int i = 1; i < children.size() - 1; i++) {
          ParseTree item = children.get(i);
          boolean isSep = (item instanceof TerminalNode);
          boolean isSepPosition = i % 3 == (3 - seenSplats) % 3;
          boolean isKeyPosition = i % 3 == ((3 - seenSplats) % 3 + 1) % 3;
          boolean isSplat = (item instanceof TweakFlowParser.SplatContext);
          boolean isWrongSideColonKey = (item instanceof TweakFlowParser.WrongSideColonKeyContext);

          // separator expected on indexes 3, 6, etc.
          if (isSep) {
            if (!isSepPosition || i == children.size() - 2) {
              sourceInfo = srcOf(parseUnit, ((TerminalNode) item).getSymbol());
              msg.append("unexpected ','");
              break;
            }
          } else {
            if (isSepPosition) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("expecting ',' - must separate dict pattern elements with ','");
              break;
            }
          }

          // string literal or splat expected in key positions
          if (isKeyPosition) {
            if (isSplat) {
              seenSplats++;
              continue;
            }
          } else {
            if (isSplat) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("unexpected splat - splats cannot be associated to a key, use the splat instead of a key value pair");
              break;
            }
          }

          // colon on key is on the wrong side -> f: is given when :f is correct
          if (isWrongSideColonKey) {
            sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
            TweakFlowParser.WrongSideColonKeyContext wrongSideColonKey = (TweakFlowParser.WrongSideColonKeyContext) item;
            msg.append("unexpected '" + item.getText() + "' - did you mean ':" + wrongSideColonKey.identifier().getText() + "' ?");
          }

          // if we're in the last position, we must not be expecting a key
          if (i == children.size() - 2) {
            if (isKeyPosition) {
              sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
              msg.append("unexpected end of dict - value for key '" + item.getText() + "' expected");
            }
          }
        }

      }
      break;
      case "badStringInterpolation": {
        // predicate matches full dict with mixed splats and non-splats, and optional separators
        TweakFlowParser.StringInterpolationContext ctx = (TweakFlowParser.StringInterpolationContext) e.getCtx();

        // manually validate children in order
        List<ParseTree> children = ctx.children;
        int seenSplats = 0;
        /* skip leading '"' and final '"' nodes */
        for (int i = 1; i < children.size() - 1; i++) {
          ParseTree item = children.get(i);
          boolean isUnrecognizedEscape = (item instanceof TweakFlowParser.UnrecognizedEscapeSequenceContext);
          // colon on key is on the wrong side -> f: is given when :f is correct
          if (isUnrecognizedEscape) {
            sourceInfo = srcOf(parseUnit, ((ParserRuleContext) item));
            TweakFlowParser.UnrecognizedEscapeSequenceContext badSequence = (TweakFlowParser.UnrecognizedEscapeSequenceContext) item;
            msg.append("invalid escape sequence: '" + item.getText() + "' - did you mean '\\" + badSequence.getText() + "' ?");
          }
        }

      }
      break;
    }

    // an empty string means a predicate is failing,
    // but we have not constructed a message for it
    // this should be fixed if it ever happens, give a generic message for now
    String message = msg.toString();
    if (message.isEmpty()) {
      message = "guard predicate failed: " + id;
    }

    return new LangException(LangError.PARSE_ERROR, message, sourceInfo);

  }


}
