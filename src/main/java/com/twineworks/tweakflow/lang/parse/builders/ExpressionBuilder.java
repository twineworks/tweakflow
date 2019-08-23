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
import com.twineworks.tweakflow.lang.ast.args.*;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.meta.ViaNode;
import com.twineworks.tweakflow.lang.ast.partial.PartialArgumentNode;
import com.twineworks.tweakflow.lang.ast.partial.PartialArguments;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.ast.structure.match.DefaultPatternNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLineNode;
import com.twineworks.tweakflow.lang.ast.structure.match.MatchLines;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.DateTimeValue;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.*;

public class ExpressionBuilder extends TweakFlowParserBaseVisitor<ExpressionNode> {

  private final ParseUnit parseUnit;

  public ExpressionBuilder(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  public ExpressionNode addImplicitCast(Type targetType, ExpressionNode node) {
    if (targetType == Types.ANY) return node;
    if (targetType == node.getValueType()) return node;

    if (node.getValueType().canAttemptCastTo(targetType)) {
      return new CastNode()
          .setExpression(node)
          .setTargetType(targetType)
          .setSourceInfo(node.getSourceInfo());
    } else {
      throw new LangException(LangError.INCOMPATIBLE_TYPES, "cannot cast " + node.getValueType().name() + " to " + targetType.name(), node.getSourceInfo());
    }
  }

  @Override
  public ExpressionNode visitNilLiteral(TweakFlowParser.NilLiteralContext ctx) {
    return new NilNode().setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitStringHereDoc(TweakFlowParser.StringHereDocContext ctx) {
    String text = ctx.HEREDOC_STRING().getText();
    // empty heredoc?
    if ("~~~\r\n~~~".equals(text) || "~~~\n~~~".equals(text)) {
      text = "";
    } else {

      if (text.startsWith("~~~\n")) {
        text = text.substring(4);
      } else if (text.startsWith("~~~\r\n")) {
        text = text.substring(5);
      }

      if (text.endsWith("\r\n~~~")) {
        text = text.substring(0, text.length() - 5);
      } else if (text.endsWith("\n~~~")) {
        text = text.substring(0, text.length() - 4);
      }
    }

    return new StringNode(text).setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitStringVerbatim(TweakFlowParser.StringVerbatimContext ctx) {
    String text = ctx.VSTRING().getText();
    // strip quotes
    text = text.substring(1, text.length() - 1);
    // replace escape sequence
    return new StringNode(text.replaceAll("''", "'")).setSourceInfo(srcOf(parseUnit, ctx));
  }
//
//  @Override
//  public ExpressionNode visitStringQuoted(TweakFlowParser.StringQuotedContext ctx) {
//
//    SourceInfo sourceInfo = (srcOf(parseUnit, ctx));
//    StringBuilder builder = new StringBuilder();
//
//    // skip opening '"' and closing '"'
//    int i = 1;
//    int n = ctx.getChildCount()-1;
//
//    for (;i<n;i++){
//
//      ParserRuleContext child = (ParserRuleContext) ctx.children.get(i);
//
//      if (child instanceof TweakFlowParser.StringTextContext){
//        builder.append(child.getText());
//
//      }
//      else if (child instanceof TweakFlowParser.StringEscapeSequenceContext){
//        String escapeSequence = child.getText();
//        builder.append(convertEscapeSequence(escapeSequence));
//      }
//      else {
//        throw new AssertionError("Unknown child node in quoted string: "+child.toString());
//      }
//
//    }
//
//    return new StringNode(builder.toString()).setSourceInfo(sourceInfo);
//
//  }

  @Override
  public ExpressionNode visitLongDecLiteral(TweakFlowParser.LongDecLiteralContext ctx) {

    SourceInfo sourceInfo = srcOf(parseUnit, ctx);

    try {
      Long decLiteral = parseLongLiteral(ctx.getText().replace("_", ""));
      return new LongNode(decLiteral).setSourceInfo(sourceInfo);
    } catch (NumberFormatException e) {
      throw new LangException(LangError.NUMBER_OUT_OF_BOUNDS, "Number out of bounds.", sourceInfo);
    }

  }

  @Override
  public ExpressionNode visitHexLiteral(TweakFlowParser.HexLiteralContext ctx) {
    return new LongNode(parseHexLiteral(ctx.getText())).setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitDoubleLiteralExp(TweakFlowParser.DoubleLiteralExpContext ctx) {
    return new DoubleNode(parseDoubleLiteral(ctx.getText())).setSourceInfo(srcOf(parseUnit, ctx));
  }

  private Long parseLongLiteral(String text) {
    return Long.parseLong(text);
  }

  private Double parseDoubleLiteral(String text) {
    return Double.parseDouble(text.replace("_", ""));
  }

  Long parseHexLiteral(String text) {
    String hex = text.substring(2); // drop leading 0x
    if (hex.length() == 8 * 2) {
      // if all 8 bytes given,
      // force parsing a two's complement representation
      return new BigInteger(hex, 16).longValue();
    } else {
      return Long.parseLong(hex, 16); // regular parse
    }

  }

  @Override
  public ExpressionNode visitDecimalLiteralExp(TweakFlowParser.DecimalLiteralExpContext ctx) {
    String text = ctx.getText();
    // remove underscores
    text = text.replace("_", "");
    // remove trailing D
    text = text.substring(0, text.length()-1);
    // parse the decimal
    BigDecimal decimal = new BigDecimal(text);
    return new DecimalNode(decimal).setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitDateTimeLiteralExp(TweakFlowParser.DateTimeLiteralExpContext ctx) {

    // 2017-03-17T16:04:02.123456789+01:00@`Europe/Berlin`

    // lexer provides a token that may not be valid
    // the parser can provide more helpful error messages in case of an 'almost right' datetime notation
    // here's the guarantees:

    // some digits for year -> year must have at least 4 digits to prevent usage of things like 19-02-23T
    //                         as this would not be 2019, it would be 19 CE, need to write 0019-02-23T to mean 19 CE
    //                         year can have a maximum of 9 digits
    // some digits for month          -> one or two digits are allowed
    // some digits for day_of month   -> one or two digits are allowed

    // T separator is guaranteed to be present

    // time is optional
    // some digits for hour           -> one or two digits are allowed
    // some digits for minute         -> one or two digits are allowed
    // some digits for second         -> one or two digits are allowed

    // . fraction is optional
    // some digits for nano_of_second -> one to nine digits are allowed

    // offset is optional, if it is present, it can be
    // Z
    //
    // +|- some digits for offset hour    -> 1 or 2 digits are allowed
    // +|- some digits for offset minute  -> 1 or 2 digits are allowed


    String str = ctx.getText();
    int idxT = str.indexOf('T');

    // default time
    int hour = 0;
    int minute = 0;
    int second = 0;
    int nanoOfSecond = 0;

    // -- date part --

    // date part are first digits up to T
    String date = str.substring(0, idxT);

    boolean isNeg = date.startsWith("-");

    // temporarily strip off the leading '-' for processing convenience
    if (isNeg) {
      date = date.substring(1);
    }
    // strip off unnecessary + at year beginning, if present
    else if (date.startsWith("+")) {
      date = date.substring(1);
    }

    String[] parts = date.split("-");
    String yearPart = parts[0];
    String monthPart = parts[1];
    String dayOfMonthPart = parts[2];

    if (yearPart.length() < 4) {
      throw new LangException(LangError.INVALID_DATETIME, "invalid year: year must consist of at least four digits, provide the century or leading zeros", srcOf(parseUnit, ctx));
    } else if (yearPart.length() > 9) {
      throw new LangException(LangError.INVALID_DATETIME, "invalid year: year must not exceed nine digits", srcOf(parseUnit, ctx));
    }

    int year = Integer.parseInt(yearPart, 10);

    if (monthPart.length() > 2) {
      throw new LangException(LangError.INVALID_DATETIME, "invalid month: month must consist of one or two digits", srcOf(parseUnit, ctx));
    }

    int month = Integer.parseInt(monthPart, 10);

    if (dayOfMonthPart.length() > 2) {
      throw new LangException(LangError.INVALID_DATETIME, "invalid day of month: day of month must consist of one or two digits", srcOf(parseUnit, ctx));
    }
    int dayOfMonth = Integer.parseInt(dayOfMonthPart, 10);

    if (isNeg) {
      year = -year;
    }

    // -- time part --

    // defaults
    String offset = null;
    String tz = "UTC";

    int len = str.length();

    // optional parts
    if (len > idxT + 1) {
      // scan until +-h+:m+ offset, Z offset, or @tzName
      String timeSection = str.substring(idxT + 1);
      int timeLen = timeSection.length();

      // shortest time sig: 0:0:0
      int clockTimeLen = 5;
      for (int i=5;i<timeLen;i++){
        char c = timeSection.charAt(i);
        // still in clockTime?
        if (c >= '0' && c <= '9' || c == ':' || c == '.'){
          clockTimeLen = i+1;
        }
        else{
          // out of clockTime
          break;
        }
      }
      // got h+:m+:s+(.f+) in clockTime
      String clockTime = timeSection.substring(0, clockTimeLen);
      String[] clockTimeParts = clockTime.split(":");
      String hourPart = clockTimeParts[0];
      String minutePart = clockTimeParts[1];
      String secondPart = clockTimeParts[2];
      String fractionPart = null;

      int dotPos = secondPart.indexOf(".");
      if (dotPos >= 0){
        fractionPart = secondPart.substring(dotPos+1);
        secondPart = secondPart.substring(0, dotPos);
      }

      if (hourPart.length() > 2){
        throw new LangException(LangError.INVALID_DATETIME, "invalid hour: hour must consist of one or two digits", srcOf(parseUnit, ctx));
      }

      if (minutePart.length() > 2){
        throw new LangException(LangError.INVALID_DATETIME, "invalid minute: minute must consist of one or two digits", srcOf(parseUnit, ctx));
      }

      if (secondPart.length() > 2){
        throw new LangException(LangError.INVALID_DATETIME, "invalid second: second must consist of one or two digits", srcOf(parseUnit, ctx));
      }

      if (fractionPart != null && fractionPart.length() > 9){
        throw new LangException(LangError.INVALID_DATETIME, "invalid fraction of second: fraction of second must not exceed nine digits", srcOf(parseUnit, ctx));
      }

      hour = Integer.parseInt(hourPart, 10);
      minute = Integer.parseInt(minutePart, 10);
      second = Integer.parseInt(secondPart, 10);

      // need to convert fraction digits to nano digits
      // i.e. given fraction 999 -> 999_000_000ns
      if (fractionPart != null){
        int additionalDigits = 9 - fractionPart.length();
        if (additionalDigits > 0){
          // quick and dirty addition of additionalDigits 0s
          char[] zeros = new char[additionalDigits];
          Arrays.fill(zeros, '0');
          fractionPart = fractionPart+new String(zeros);
        }
      }

      nanoOfSecond = fractionPart == null ?  0 : Integer.parseInt(fractionPart, 10);


      String offsetTime = timeSection.substring(clockTimeLen);

      if (offsetTime.length() > 0) {

        // -- offset part --
        // possibilities are +-h+:m+, Z, or empty implying Z
        char offsetStartChar = offsetTime.charAt(0);
        int offsetLen = 0;
        if (offsetStartChar == '+' || offsetStartChar=='-'){

          // offset given by +-digits:digits
          offsetLen = 4;
          for (int i=4;i<offsetTime.length();i++){
            char c = offsetTime.charAt(i);
            // still in offsetTime?
            if (c >= '0' && c <= '9' || c == ':'){
              offsetLen = i+1;
            }
            else{
              // out of offsetTime
              break;
            }
          }

          // contains (+|-)h+:m+
          String offsetClock = offsetTime.substring(0, offsetLen);
          // require length of 2 for both hours and minutes
          int splitPos = offsetClock.indexOf(':');
          String offsetHour = offsetClock.substring(1, splitPos);
          String offsetMinute = offsetClock.substring(splitPos+1);

          if (offsetHour.length() > 2){
            throw new LangException(LangError.INVALID_DATETIME, "invalid offset hour: offset hour must consist of one or two digits", srcOf(parseUnit, ctx));
          }

          if (offsetMinute.length() > 2){
            throw new LangException(LangError.INVALID_DATETIME, "invalid offset minute: offset minute must consist of one or two digits", srcOf(parseUnit, ctx));
          }

          // normalize offset hour and minute to length 2
          if (offsetHour.length() < 2){
            offsetHour = "0"+offsetHour;
          }

          if (offsetMinute.length() < 2){
            offsetMinute = "0"+offsetMinute;
          }

          // construct offset
          offset = offsetClock.charAt(0)+offsetHour+":"+offsetMinute;

        }
        else if (offsetStartChar == 'Z'){
          // offset is 00:00
          offset = "Z";
          offsetLen = 1;
        }
        else if (offsetStartChar == '@'){
          // offset is empty, implying default zone offset
          offset = null;
          offsetLen = 0;
        }

        String tzNameTime = offsetTime.substring(offsetLen);

        // offset & timezone
        if (tzNameTime.length() > 0) {
          // time zone given?
          tz = identifier(tzNameTime.substring(1));
        }
        // no timezone given, but an offset is there
        else{
          if (offset == null || offset.equals("Z")) {
            tz = "UTC";
          } else {
            tz = "UTC" + offset;
          }
        }

      }

    }


    try {
      // create a zoned time, using strict rules for all parts
      LocalDate localDate = LocalDate.of(year, month, dayOfMonth);
      LocalTime localTime = LocalTime.of(hour, minute, second, nanoOfSecond);
      LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
      ZonedDateTime zonedDateTime;
      if (offset == null){
        zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(tz));
      }
      else{
        zonedDateTime = ZonedDateTime.ofStrict(localDateTime, ZoneOffset.of(offset), ZoneId.of(tz));
      }

      DateTimeValue dateTime = new DateTimeValue(zonedDateTime);

      return new DateTimeNode(dateTime).setSourceInfo(srcOf(parseUnit, ctx));

    } catch (DateTimeException e) {
      throw new LangException(LangError.INVALID_DATETIME, e.getMessage(), srcOf(parseUnit, ctx));
    }
  }

  @Override
  public ExpressionNode visitForExp(TweakFlowParser.ForExpContext ctx) {
    ForNode forNode = new ForNode();
    forNode.setSourceInfo(srcOf(parseUnit, ctx));

    ForHead head = forNode.getHead();
    TweakFlowParser.ForHeadContext headContext = ctx.forHead();
    int childCount = headContext.getChildCount();

    for (int i = 0; i < childCount; i++) {
      ParseTree child = headContext.getChild(i);
      // generator
      if (child instanceof TweakFlowParser.GeneratorContext) {
        GeneratorNode generatorNode = new GeneratorBuilder(parseUnit).visitGenerator((TweakFlowParser.GeneratorContext) child);
        head.getElements().add(generatorNode);
      }
      // local
      else if (child instanceof TweakFlowParser.VarDefContext) {
        VarDefNode varDefNode = new VarDefBuilder(parseUnit).visitVarDef((TweakFlowParser.VarDefContext) child);
        head.getElements().add(varDefNode);
      }
      // expression
      else if (child instanceof TweakFlowParser.ExpressionContext) {
        head.getElements().add(visit(child));
      }
      // comma separator token children are skipped
    }

    forNode.setExpression(visit(ctx.expression()));
    return forNode;
  }

  @Override
  public ExpressionNode visitMatchExp(TweakFlowParser.MatchExpContext ctx) {

    MatchNode matchNode = new MatchNode();
    matchNode.setSourceInfo(srcOf(parseUnit, ctx));
    matchNode.setSubject(visit(ctx.expression()));

    MatchLines matchLines = matchNode.getMatchLines();

    TweakFlowParser.MatchBodyContext matchBodyContext = ctx.matchBody();
    List<TweakFlowParser.MatchLineContext> matchLineContexts = matchBodyContext.matchLine();

    int size = matchLineContexts.size();

    for (int i = 0; i < size; i++) {
      TweakFlowParser.MatchLineContext matchLineContext = matchLineContexts.get(i);
      MatchLineNode matchLineNode = new MatchLineBuilder(parseUnit).visit(matchLineContext);

      // ensure the default pattern appears last, if present
      if (matchLineNode.getPattern() instanceof DefaultPatternNode) {
        if (i != size - 1)
          throw new LangException(LangError.DEFAULT_PATTERN_NOT_LAST, matchLineNode.getPattern().getSourceInfo());
      }
      matchLines.getElements().add(matchLineNode);
    }

    return matchNode;
  }

  @Override
  public ExpressionNode visitDefaultExp(TweakFlowParser.DefaultExpContext ctx) {
    return new DefaultNode()
        .setExpression(visit(ctx.expression(0)))
        .setDefaultExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitTypeOfExp(TweakFlowParser.TypeOfExpContext ctx) {
    return new TypeOfNode()
        .setExpression(visit(ctx.expression()))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }


  @Override
  public ExpressionNode visitDebugExp(TweakFlowParser.DebugExpContext ctx) {

    ArrayList<ExpressionNode> nodes = new ArrayList<>(ctx.expression().size());
    for (TweakFlowParser.ExpressionContext expressionContext : ctx.expression()) {
      ExpressionNode n = visit(expressionContext);
      nodes.add(n);
    }

    return new DebugNode()
        .setExpressions(nodes)
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitBitwiseNotExp(TweakFlowParser.BitwiseNotExpContext ctx) {
    return new BitwiseNotNode()
        .setExpression(addImplicitCast(Types.LONG, visit(ctx.expression())))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitBitwiseXorExp(TweakFlowParser.BitwiseXorExpContext ctx) {
    return new BitwiseXorNode()
        .setLeftExpression(addImplicitCast(Types.LONG, visit(ctx.expression(0))))
        .setRightExpression(addImplicitCast(Types.LONG, visit(ctx.expression(1))))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitShiftLeftExp(TweakFlowParser.ShiftLeftExpContext ctx) {
    return new BitwiseShiftLeftNode()
        .setLeftExpression(addImplicitCast(Types.LONG, visit(ctx.expression(0))))
        .setRightExpression(addImplicitCast(Types.LONG, visit(ctx.expression(1))))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitBitwiseOrExp(TweakFlowParser.BitwiseOrExpContext ctx) {
    return new BitwiseOrNode()
        .setLeftExpression(addImplicitCast(Types.LONG, visit(ctx.expression(0))))
        .setRightExpression(addImplicitCast(Types.LONG, visit(ctx.expression(1))))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitZeroShiftRightExp(TweakFlowParser.ZeroShiftRightExpContext ctx) {
    return new BitwiseZeroShiftRightNode()
        .setLeftExpression(addImplicitCast(Types.LONG, visit(ctx.expression(0))))
        .setRightExpression(addImplicitCast(Types.LONG, visit(ctx.expression(1))))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitPreservingShiftRightExp(TweakFlowParser.PreservingShiftRightExpContext ctx) {
    return new BitwisePreservingShiftRightNode()
        .setLeftExpression(addImplicitCast(Types.LONG, visit(ctx.expression(0))))
        .setRightExpression(addImplicitCast(Types.LONG, visit(ctx.expression(1))))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitBitwiseAndExp(TweakFlowParser.BitwiseAndExpContext ctx) {
    return new BitwiseAndNode()
        .setLeftExpression(addImplicitCast(Types.LONG, visit(ctx.expression(0))))
        .setRightExpression(addImplicitCast(Types.LONG, visit(ctx.expression(1))))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitStringInterpolation(TweakFlowParser.StringInterpolationContext ctx) {

    SourceInfo sourceInfo = (srcOf(parseUnit, ctx));
    ArrayList<ExpressionNode> nodes = new ArrayList<>();

    // skip opening '"' and closing '"'
    int i = 1;
    int n = ctx.getChildCount() - 1;

    for (; i < n; i++) {

      ParseTree item = ctx.children.get(i);
      if (item instanceof TerminalNode) {
        // skip #{ and } interpolation start and end tokens
        continue;
      }

      ParserRuleContext child = (ParserRuleContext) item;

      if (child instanceof TweakFlowParser.StringTextContext) {
        String text = child.getText();
        nodes.add(new StringNode(text).setSourceInfo(srcOf(parseUnit, child)));
      } else if (child instanceof TweakFlowParser.StringEscapeSequenceContext) {
        String escapeSequence = child.getText();
        nodes.add(new StringNode(convertEscapeSequence(escapeSequence)).setSourceInfo(srcOf(parseUnit, child)));
      } else if (child instanceof TweakFlowParser.ExpressionContext) {
        nodes.add(addImplicitCast(Types.STRING, visit(child)));

      } else {
        throw new AssertionError("Unknown child node in string interpolation: " + child.toString());
      }

    }

    List<ExpressionNode> compacted = compactStringNodes(nodes);

    // compacted to nothing is an empty string
    if (compacted.size() == 0) {
      return new StringNode("").setSourceInfo(sourceInfo);
    }

    // a single node is returned directly, it might be a string or an expression
    if (compacted.size() == 1) {
      ExpressionNode c = compacted.get(0);
      c.setSourceInfo(sourceInfo);
      return c;
    }

    // multiple nodes go into concat nodes
    ExpressionNode left = compacted.get(0);

    for (int si = 1; si < compacted.size(); si++) {
      ExpressionNode right = compacted.get(si);
      left = new StringConcatNode()
          .setLeftExpression(left)
          .setRightExpression(right)
          .setSourceInfo(left.getSourceInfo().copy());
    }

    return left.setSourceInfo(sourceInfo);

  }

//  @Override
//  public ExpressionNode visitStringReferenceInterpolation(TweakFlowParser.StringReferenceInterpolationContext ctx) {
//    // re-parse as expression to get proper reference nodes out of interpolated text
//    String txt = ctx.getText();
//    // skip opening and ending markers in #{my.interpolated.expression}
//    String exp = txt.substring(2, txt.length()-1).trim();
//
//    CodePointCharStream input = CharStreams.fromString(exp);
//    TweakFlowLexer lexer = new TweakFlowLexer(input);
//    CommonTokenStream tokens = new CommonTokenStream(lexer);
//
//    try {
//      // consume tokens
//      tokens.fill();
//
//      // parse them
//      TweakFlowParser parser = new TweakFlowParser(tokens);
//      // build AST nodes
//      ExpressionNode expressionNode = visit(parser.reference());
//      // ensure it's a reference
//      if (!(expressionNode instanceof ReferenceNode)){
//        throw new LangException(LangError.PARSE_ERROR, "Only references allowed in string interpolation");
//      }
//      // and update the source info on it to match where it was found
//      ReferenceNode refNode = (ReferenceNode) expressionNode;
//      refNode.setSourceInfo(srcOf(parseUnit, ctx));
//      // adjust the char within line to point to the actual reference, not the marker in #{my.ref}
//      return refNode;
//    }
//    catch (RecognitionException e){
//      throw LangException.wrap(e, LangError.PARSE_ERROR)
//          .put("location", srcOf(parseUnit, ctx));
//    }
//
//  }

  private ExpressionNode splatEnabledListExpression(SourceInfo src, List<ParseTree> children) {

    // phase 1: collect subLists
    // [a, b, ...c, d] -> [[a, b], c, [d]]
    ArrayList<ExpressionNode> subLists = new ArrayList<>();

    ListNode currentSubList = null;

    for (ParseTree child : children) {

      if (child instanceof TerminalNode) {
        // skip ',' optionally separating entries
        continue;
      }

      if (child instanceof TweakFlowParser.SplatContext) {
        // a splat closes any current sublist and enters itself as sublist
        // close sublist
        if (currentSubList != null) {
          subLists.add(currentSubList);
          currentSubList = null;
        }
        // enter its expression as sublist
        TweakFlowParser.SplatContext splatContext = (TweakFlowParser.SplatContext) child;
        subLists.add(visit(splatContext.expression()));
      } else {

        // a regular list item enters itself into the current sublist, creating one if necessary
        if (currentSubList == null) {
          currentSubList = new ListNode();
          currentSubList.setSourceInfo(srcOf(parseUnit, (ParserRuleContext) child));
        }

        currentSubList.getElements().add(visit(child));

      }

    }

    // at the end, any open sublist is closed
    if (currentSubList != null) {
      subLists.add(currentSubList);
    }

    // phase 2: generate subLists for concatenation
    // [[a, b], c, [d]] -> concat(concat([a, b], [c]), [d])

    // direct returns if no concat necessary
    if (subLists.size() == 0) {
      return new ListNode().setSourceInfo(src);
    }

    if (subLists.size() == 1) {
      ExpressionNode result = subLists.get(0);
      // for cosmetics, let source info point to the list definition, as opposed to the element in it
      result.setSourceInfo(src);
      return result;
    }

    // fold subLists into list concat nodes
    ExpressionNode left = subLists.get(0);

    for (int si = 1; si < subLists.size(); si++) {
      ExpressionNode right = subLists.get(si);
      left = new ListConcatNode()
          .setLeftExpression(left)
          .setRightExpression(right)
          .setSourceInfo(left.getSourceInfo().copy());
    }

    return left;

  }

  @Override
  public ExpressionNode visitListLiteral(TweakFlowParser.ListLiteralContext ctx) {
    return splatEnabledListExpression(srcOf(parseUnit, ctx), ctx.children);
  }


  /**
   * Compacts consecutive StringNode elements in the given list. All sequences of StringNode elements
   * are compacted into a single StringNode element in the returned list. Source information of the first
   * StringNode is preserved in the compacted StringNode.
   * All StringNodes in the returned list are newly constructed objects. Original StringNodes are not
   * modified. Nodes that are not StringNodes are carried over in the returned list by reference. There
   * are no copies created for them.
   *
   * @param in
   * @return The list of nodes where any sub-sequences of StringNodes should be compacted.
   */

  List<ExpressionNode> compactStringNodes(List<ExpressionNode> in) {

    ArrayList<ExpressionNode> results = new ArrayList<>(in.size());

    StringNode currentBase = null;
    StringBuilder builder = null;

    for (int i = 0; i < in.size(); i++) {
      ExpressionNode node = in.get(i);

      // string-node
      if (node instanceof StringNode) {
        StringNode strNode = (StringNode) node;

        // new sub-sequence?
        if (currentBase == null) {
          currentBase = strNode;
          builder = new StringBuilder().append(currentBase.getStringVal());
        }
        // continuing sub-sequence
        else {
          builder.append(strNode.getStringVal());
        }
      }

      // non-string node
      else {
        // closing a sub-sequence?
        if (currentBase != null) {
          results.add(currentBase.dup().setStringVal(builder.toString()));
          currentBase = null;
          builder = null;
        }
        results.add(node);
      }

    }

    // closing a trailing sequence?
    if (currentBase != null) {
      results.add(currentBase.dup().setStringVal(builder.toString()));
    }

    return results;
  }

  /**
   * Converts a string escape sequence to the string it represents.
   *
   * @param escapeSequence the escape sequence
   * @return the string it represents
   */
  String convertEscapeSequence(String escapeSequence) {

    String str;
    switch (escapeSequence) {
      case "\\\\":
        str = "\\";
        break;
      case "\\r":
        str = "\r";
        break;
      case "\\n":
        str = "\n";
        break;
      case "\\t":
        str = "\t";
        break;
      case "\\\"":
        str = "\"";
        break;
      case "\\#{":
        str = "#{";
        break;
      default:
        if (escapeSequence.startsWith("\\u")) { // 2 byte variant
          int codePoint = Integer.parseInt(escapeSequence.substring(2), 16);
          char[] ch = Character.toChars(codePoint);
          str = new String(ch);
        } else if (escapeSequence.startsWith("\\U")) { // 4 byte variant
          int codePoint = Integer.parseInt(escapeSequence.substring(2), 16);
          char[] ch = Character.toChars(codePoint);
          str = new String(ch);
        } else {
          throw new AssertionError("Unknown escape sequence: " + escapeSequence);
        }
    }
    return str;
  }

  @Override
  public ExpressionNode visitDictLiteral(TweakFlowParser.DictLiteralContext ctx) {

    // phase 1: collect subMaps
    // {:a a,:b b, ...c, :d d} -> [{:a a,:b b}, c, {:d d}]
    ArrayList<ExpressionNode> subMaps = new ArrayList<>();

    DictNode currentSubMap = null;

    List<ParseTree> children = ctx.children;

    for (int i = 0; i < children.size(); i++) {
      ParseTree child = children.get(i);

      if (child instanceof TerminalNode) {
        // skip ',' optionally separating entries and opening/closing {}
        continue;
      }

      if (child instanceof TweakFlowParser.SplatContext) {
        // a splat closes any current sublist and enters itself as sublist
        // close sublist
        if (currentSubMap != null) {
          subMaps.add(currentSubMap);
          currentSubMap = null;
        }
        // enter its expression as sublist
        TweakFlowParser.SplatContext splatContext = (TweakFlowParser.SplatContext) child;
        subMaps.add(visit(splatContext.expression()));
      } else {

        // a regular map entry enters itself into the current sublist, creating one if necessary
        if (currentSubMap == null) {
          currentSubMap = new DictNode();
          currentSubMap.setSourceInfo(srcOf(parseUnit, (ParserRuleContext) child));
        }

        ExpressionNode key = addImplicitCast(Types.STRING, visit(child));
        ExpressionNode value = visit(ctx.children.get(++i));

        DictEntryNode entry = new DictEntryNode().setSourceInfo(key.getSourceInfo().copy());
        entry.setKey(key);
        entry.setValue(value);
        currentSubMap.getEntries().add(entry);

      }

    }

    // at the end, any open subMap is closed
    if (currentSubMap != null) {
      subMaps.add(currentSubMap);
    }

    // phase 2: generate merge call for subMaps
    // [{:a a, :b b}, c, {:d d}] -> merge(merge({:a a, :b b}, c), {:d d})

    // direct returns if no concat necessary
    if (subMaps.size() == 0) {
      return new DictNode().setSourceInfo(srcOf(parseUnit, ctx));
    }

    if (subMaps.size() == 1) {
      ExpressionNode result = subMaps.get(0);
      // for cosmetics, let source info point to the map definition, as opposed to the entry in it
      result.setSourceInfo(srcOf(parseUnit, ctx));
      return result;
    }

    // fold subLists into calls to $std.data.concat
    ExpressionNode left = subMaps.get(0);

    for (int si = 1; si < subMaps.size(); si++) {
      ExpressionNode right = subMaps.get(si);
      left = new DictMergeNode()
          .setLeftExpression(left)
          .setRightExpression(right)
          .setSourceInfo(left.getSourceInfo().copy());
    }

    return left;

  }

  @Override
  public BooleanNode visitBooleanLiteral(TweakFlowParser.BooleanLiteralContext ctx) {
    if (ctx.TRUE() != null) {
      return new BooleanNode(Boolean.TRUE).setSourceInfo(srcOf(parseUnit, ctx));
    } else {
      return new BooleanNode(Boolean.FALSE).setSourceInfo(srcOf(parseUnit, ctx));
    }
  }

  @Override
  public ExpressionNode visitLetExp(TweakFlowParser.LetExpContext ctx) {

    BindingsNode bindings = new BindingsNode()
        .setSourceInfo(srcOf(parseUnit, ctx));

    Map<String, VarDefNode> varDefs = bindings.getVars().getMap();
    for (TweakFlowParser.VarDefContext varDefContext : ctx.varDef()) {
      VarDefNode varDef = new VarDefBuilder(parseUnit).visitVarDef(varDefContext);
      if (varDefs.containsKey(varDef.getSymbolName())) {
        throw new LangException(LangError.ALREADY_DEFINED, varDef.getSymbolName() + " defined more than once", varDef.getSourceInfo());
      }
      varDefs.put(varDef.getSymbolName(), varDef);
    }
    bindings.getVars().cook();

    ExpressionNode expression = new ExpressionBuilder(parseUnit).visit(ctx.expression());

    return new LetNode()
        .setBindings(bindings)
        .setExpression(expression)
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitNestedExp(TweakFlowParser.NestedExpContext ctx) {
    return visit(ctx.expression());
  }

  @Override
  public ExpressionNode visitFunctionLiteral(TweakFlowParser.FunctionLiteralContext ctx) {

    TweakFlowParser.FunctionHeadContext head = ctx.functionHead();

    Type declaredReturnType;
    if (head.dataType() == null) {
      declaredReturnType = Types.ANY;
    } else {
      declaredReturnType = Types.byName(head.dataType().getText());
    }

    LinkedHashMap<String, ParameterNode> paramMap = new LinkedHashMap<>();
    for (TweakFlowParser.ParamDefContext defContext : head.paramsList().paramDef()) {

      Type declaredType;
      if (defContext.dataType() == null) {
        declaredType = Types.ANY;
      } else {
        declaredType = Types.byName(defContext.dataType().getText());
      }

      ExpressionNode defaultValue;
      if (defContext.expression() == null) {
        defaultValue = new NilNode().setSourceInfo(srcOf(parseUnit, defContext.identifier()));
      } else {
        defaultValue = visit(defContext.expression());
      }

      String name = identifier(defContext.identifier().getText());

      if (paramMap.containsKey(name)) {
        throw new LangException(LangError.ALREADY_DEFINED, name + " defined more than once", srcOf(parseUnit, defContext));
      }

      paramMap.put(name, new ParameterNode()
          .setSymbolName(name)
          .setIndex(paramMap.size())
          .setDeclaredType(declaredType)
          .setDefaultValue(defaultValue)
          .setSourceInfo(srcOf(parseUnit, defContext)));
    }

    Parameters parameters = new Parameters()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setMap(paramMap);

    if (ctx.expression() != null) {
      ExpressionNode expression = visit(ctx.expression());

      return new FunctionNode()
          .setExpression(addImplicitCast(declaredReturnType, expression))
          .setParameters(parameters)
          .setDeclaredReturnType(declaredReturnType)
          .setSourceInfo(srcOf(parseUnit, ctx));

    } else if (ctx.viaDec() != null) {

      ViaNode via = new ViaBuilder(parseUnit).visit(ctx.viaDec());

      return new FunctionNode()
          .setVia(via)
          .setParameters(parameters)
          .setDeclaredReturnType(declaredReturnType)
          .setSourceInfo(srcOf(parseUnit, ctx));

    }

    throw new AssertionError("unknown function definition: " + ctx);

  }

  @Override
  public ExpressionNode visitLocalReference(TweakFlowParser.LocalReferenceContext ctx) {

    ArrayList<String> elements = new ArrayList<>();
    for (TweakFlowParser.IdentifierContext identifierContext : ctx.identifier()) {
      elements.add(identifier(identifierContext.getText()));
    }

    return new ReferenceNode()
        .setAnchor(ReferenceNode.Anchor.LOCAL)
        .setElements(elements)
        .setSourceInfo(srcOf(parseUnit, ctx));
  }


  @Override
  public ExpressionNode visitGlobalReference(TweakFlowParser.GlobalReferenceContext ctx) {

    ArrayList<String> elements = new ArrayList<>();
    for (TweakFlowParser.IdentifierContext identifierContext : ctx.identifier()) {
      elements.add(identifier(identifierContext.getText()));
    }

    return new ReferenceNode()
        .setAnchor(ReferenceNode.Anchor.GLOBAL)
        .setElements(elements)
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitLibraryReference(TweakFlowParser.LibraryReferenceContext ctx) {

    ArrayList<String> elements = new ArrayList<>();
    for (TweakFlowParser.IdentifierContext identifierContext : ctx.identifier()) {
      elements.add(identifier(identifierContext.getText()));
    }

    return new ReferenceNode()
        .setAnchor(ReferenceNode.Anchor.LIBRARY)
        .setElements(elements)
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitModuleReference(TweakFlowParser.ModuleReferenceContext ctx) {

    ArrayList<String> elements = new ArrayList<>();
    for (TweakFlowParser.IdentifierContext identifierContext : ctx.identifier()) {
      elements.add(identifier(identifierContext.getText()));
    }

    return new ReferenceNode()
        .setAnchor(ReferenceNode.Anchor.MODULE)
        .setElements(elements)
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitTryCatchExp(TweakFlowParser.TryCatchExpContext ctx) {

    ExpressionNode tryExpression = visit(ctx.expression(0));
    ExpressionNode catchExpression = visit(ctx.expression(1));
    VarDecNode caughtException = null;
    VarDecNode caughtTrace = null;
    if (ctx.catchDeclaration() instanceof TweakFlowParser.CatchErrorContext) {
      TweakFlowParser.CatchErrorContext catchDeclaration = (TweakFlowParser.CatchErrorContext) ctx.catchDeclaration();
      String exceptionName = identifier(catchDeclaration.identifier().getText());
      caughtException = new VarDecNode()
          .setDeclaredType(Types.ANY)
          .setSymbolName(exceptionName)
          .setSourceInfo(srcOf(parseUnit, catchDeclaration.identifier()));

    } else if (ctx.catchDeclaration() instanceof TweakFlowParser.CatchErrorAndTraceContext) {
      TweakFlowParser.CatchErrorAndTraceContext catchDeclaration = (TweakFlowParser.CatchErrorAndTraceContext) ctx.catchDeclaration();

      String exceptionName = identifier(catchDeclaration.identifier().get(0).getText());
      caughtException = new VarDecNode()
          .setDeclaredType(Types.ANY)
          .setSymbolName(exceptionName)
          .setSourceInfo(srcOf(parseUnit, catchDeclaration.identifier().get(0)));

      String traceName = identifier(catchDeclaration.identifier().get(1).getText());
      caughtTrace = new VarDecNode()
          .setDeclaredType(Types.ANY)
          .setSymbolName(traceName)
          .setSourceInfo(srcOf(parseUnit, catchDeclaration.identifier().get(1)));

    }

    return new TryCatchNode()
        .setTryExpression(tryExpression)
        .setCatchExpression(catchExpression)
        .setCaughtException(caughtException)
        .setCaughtTrace(caughtTrace)
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitThrowErrorExp(TweakFlowParser.ThrowErrorExpContext ctx) {

    ExpressionNode exception = visit(ctx.expression());
    return new ThrowNode()
        .setExceptionExpression(exception)
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitCastExp(TweakFlowParser.CastExpContext ctx) {
    ExpressionNode exp = visit(ctx.expression());
    Type type = type(ctx.dataType());

    if (exp.getValueType().canAttemptCastTo(type)) {
      return new CastNode()
          .setExpression(exp)
          .setTargetType(type)
          .setSourceInfo(srcOf(parseUnit, ctx));
    } else {
      throw new LangException(LangError.INCOMPATIBLE_TYPES, "cannot cast " + exp.getValueType().name() + " to " + type.name(), srcOf(parseUnit, ctx));
    }

  }

  @Override
  public ExpressionNode visitIfExp(TweakFlowParser.IfExpContext ctx) {

    return new IfNode()
        .setCondition(addImplicitCast(Types.BOOLEAN, visit(ctx.expression(0))))
        .setThenExpression(visit(ctx.expression(1)))
        .setElseExpression(visit(ctx.expression(2)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitIsExp(TweakFlowParser.IsExpContext ctx) {
    return new IsNode()
        .setExpression(visit(ctx.expression()))
        .setCompareType(type(ctx.dataType()))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitConcatExp(TweakFlowParser.ConcatExpContext ctx) {
    return new StringConcatNode()
        .setLeftExpression(addImplicitCast(Types.STRING, visit(ctx.expression(0))))
        .setRightExpression(addImplicitCast(Types.STRING, visit(ctx.expression(1))))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitBoolAndExp(TweakFlowParser.BoolAndExpContext ctx) {
    return new AndNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitBoolOrExp(TweakFlowParser.BoolOrExpContext ctx) {
    return new OrNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitEqualExp(TweakFlowParser.EqualExpContext ctx) {
    return new EqualNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitValueAndTypeEqualsExp(TweakFlowParser.ValueAndTypeEqualsExpContext ctx) {
    return new ValueAndTypeEqualsNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitNotValueAndTypeEqualsExp(TweakFlowParser.NotValueAndTypeEqualsExpContext ctx) {
    return new NotValueAndTypeEqualsNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitLessThanExp(TweakFlowParser.LessThanExpContext ctx) {
    return new LessThanNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitLessThanOrEqualToExp(TweakFlowParser.LessThanOrEqualToExpContext ctx) {
    return new LessThanOrEqualNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitGreaterThanExp(TweakFlowParser.GreaterThanExpContext ctx) {
    return new GreaterThanNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitGreaterThanOrEqualToExp(TweakFlowParser.GreaterThanOrEqualToExpContext ctx) {
    return new GreaterThanOrEqualNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitUnaryMinusExp(TweakFlowParser.UnaryMinusExpContext ctx) {
    return new NegateNode()
        .setExpression(visit(ctx.expression()))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitAddExp(TweakFlowParser.AddExpContext ctx) {
    return new PlusNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitMultExp(TweakFlowParser.MultExpContext ctx) {
    return new MultNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitPowExp(TweakFlowParser.PowExpContext ctx) {
    return new PowNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitContainerAccessExp(TweakFlowParser.ContainerAccessExpContext ctx) {
    // a[:foo 0 "bar"]
    TweakFlowParser.ContainerAccessKeySequenceContext seq = ctx.containerAccessKeySequence();

    // make keys list from the key sequence
    ExpressionNode keys = splatEnabledListExpression(srcOf(parseUnit, seq), seq.children);

    return new ContainerAccessNode()
        .setKeysExpression(keys)
        .setContainerExpression(visit(ctx.expression()))
        .setSourceInfo(srcOf(parseUnit, ctx));

  }

  @Override
  public ExpressionNode visitNotEqualExp(TweakFlowParser.NotEqualExpContext ctx) {

    return new NotEqualNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));

  }

  @Override
  public ExpressionNode visitBoolNotExp(TweakFlowParser.BoolNotExpContext ctx) {
    return new NotNode()
        .setExpression(visit(ctx.expression()))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitSubExp(TweakFlowParser.SubExpContext ctx) {

    return new MinusNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitModExp(TweakFlowParser.ModExpContext ctx) {
    return new ModNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitDivExp(TweakFlowParser.DivExpContext ctx) {
    return new DivNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitIntDivExp(TweakFlowParser.IntDivExpContext ctx) {
    return new IntDivNode()
        .setLeftExpression(visit(ctx.expression(0)))
        .setRightExpression(visit(ctx.expression(1)))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitThreadExp(TweakFlowParser.ThreadExpContext ctx) {

    // ->> (arg) f g h
    // h(g(f(arg)))

    // functions in order f, g, h
    List<ExpressionNode> functions = ctx.expression().stream()
        .map(this::visit)
        .collect(Collectors.toList());

    ExpressionNode arg = visit(ctx.threadArg());

    // fold into nested calls
    ExpressionNode left = arg;

    for (ExpressionNode right : functions) {
      left = new CallNode()
          .setExpression(right)
          .setArguments(new Arguments()
              .setSourceInfo(left.getSourceInfo().copy())
              .setList(Collections.singletonList(
                  new PositionalArgumentNode()
                      .setSourceInfo(left.getSourceInfo().copy())
                      .setIndex(0)
                      .setExpression(left)
              )))
          .setSourceInfo(right.getSourceInfo().copy());

    }

    return left;

  }

  // arguments are not an expression
  // hence visitArgs cannot participate in typed visitor pattern directly
  private PartialArguments makePartialArgs(TweakFlowParser.PartialArgsContext ctx) {

    ArrayList<PartialArgumentNode> args = new ArrayList<>();
    HashMap<String, PartialArgumentNode> argMap = new HashMap<>();

    if (ctx.children != null) {

      for (ParseTree child : ctx.children) {

        PartialArgumentNode arg;

        if (child instanceof TweakFlowParser.NamedPartialArgContext) {
          TweakFlowParser.NamedPartialArgContext nArg = (TweakFlowParser.NamedPartialArgContext) child;
          arg = new PartialArgumentNode()
              .setSourceInfo(srcOf(parseUnit, nArg))
              .setExpression(visit(nArg.expression()))
              .setName(identifier(nArg.identifier().getText()));

          if (argMap.containsKey(arg.getName())) {
            throw new LangException(LangError.ALREADY_DEFINED, arg.getName() + " defined more than once", arg.getSourceInfo());
          } else {
            argMap.put(arg.getName(), arg);
          }
        } else if (child instanceof TerminalNode && child.getText().equals(",")) { // skip ',' separators
          continue;
        } else {
          throw new AssertionError("unknown partial argument type: " + child);
        }
        args.add(arg);

      }
    }

    return new PartialArguments()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setList(args);

  }

  @Override
  public ExpressionNode visitKeyLiteral(TweakFlowParser.KeyLiteralContext ctx) {
    return new StringNode(key(ctx)).setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitCallExp(TweakFlowParser.CallExpContext ctx) {

    ExpressionNode expression = visit(ctx.expression());

    return new CallNode()
        .setExpression(expression)
        .setArguments(makeArgs(ctx.args()))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }

  // arguments are not an expression
  // hence visitArgs cannot participate in typed visitor pattern directly
  private Arguments makeArgs(TweakFlowParser.ArgsContext ctx) {

    ArrayList<ArgumentNode> args = new ArrayList<>();

    if (ctx.children != null) {
      int idx = 0;

      for (ParseTree child : ctx.children) {

        ArgumentNode arg;

        if (child instanceof TweakFlowParser.PositionalArgContext) {
          TweakFlowParser.PositionalArgContext pArg = (TweakFlowParser.PositionalArgContext) child;

          arg = new PositionalArgumentNode()
              .setSourceInfo(srcOf(parseUnit, pArg))
              .setExpression(visit(pArg.expression()))
              .setIndex(idx);

        } else if (child instanceof TweakFlowParser.NamedArgContext) {
          TweakFlowParser.NamedArgContext nArg = (TweakFlowParser.NamedArgContext) child;
          arg = new NamedArgumentNode()
              .setSourceInfo(srcOf(parseUnit, nArg))
              .setExpression(visit(nArg.expression()))
              .setName(identifier(nArg.identifier().getText()));
        } else if (child instanceof TweakFlowParser.SplatArgContext) {
          TweakFlowParser.SplatArgContext sArg = (TweakFlowParser.SplatArgContext) child;
          arg = new SplatArgumentNode()
              .setSourceInfo(srcOf(parseUnit, sArg))
              .setExpression(visit(sArg.splat().expression()))
              .setIndex(idx);
        } else if (child instanceof TerminalNode && child.getText().equals(",")) { // skip ',' separators
          continue;
        } else {
          throw new AssertionError("unknown argument type: " + child);
        }
        args.add(arg);
        idx += 1;
      }
    }

    return new Arguments()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setList(args);

  }

  byte[] parseBinLiteral(String hexString){
    return hexToBytes(hexString.replace("_", ""));
  }

  @Override
  public ExpressionNode visitBinaryLiteralExp(TweakFlowParser.BinaryLiteralExpContext ctx) {
    String hexString = ctx.binaryLiteral().getText();
    return new BinaryNode(parseBinLiteral(hexString)).setSourceInfo(srcOf(parseUnit, ctx));
  }

  @Override
  public ExpressionNode visitPartialExp(TweakFlowParser.PartialExpContext ctx) {
    ExpressionNode expression = visit(ctx.expression());

    return new PartialApplicationNode()
        .setExpression(expression)
        .setArguments(makePartialArgs(ctx.partialArgs()))
        .setSourceInfo(srcOf(parseUnit, ctx));
  }
}
