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

import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

public class CodeParseHelper {

  public static SourceInfo srcOf(ParseUnit unit, Token token){

    SourceInfo sourceInfo = new SourceInfo(
        unit,
        token.getLine(),
        token.getCharPositionInLine() + 1,
        token.getStartIndex(),
        token.getStopIndex()
    );

    return sourceInfo;
  }

  public static SourceInfo srcOf(ParseUnit unit, Token from, Token to){

    SourceInfo sourceInfo = new SourceInfo(
        unit,
        from.getLine(),
        from.getCharPositionInLine() + 1,
        from.getStartIndex(),
        to.getStopIndex()
    );

    return sourceInfo;
  }

  public static SourceInfo srcOf(ParseUnit unit, ParserRuleContext treeNode){

    SourceInfo sourceInfo = new SourceInfo(
        unit,
        treeNode.getStart().getLine(),
        treeNode.getStart().getCharPositionInLine() + 1,
        treeNode.getStart().getStartIndex(),
        treeNode.getStop().getStopIndex()

    );

    // units are always stretching the entire file
    // regardless of where the parse tree actually finds the first token
    if (treeNode instanceof TweakFlowParser.InteractiveContext
        || treeNode instanceof TweakFlowParser.ModuleContext){
      sourceInfo
          .setLine(1)
          .setCharWithinLine(1)
          .setSourceIdxStart(0)
          .setSourceIdxEnd(unit.getProgramText().length()-1);
    }

    return sourceInfo;
  }

  public static String identifier(String optionallyEscapedIdentifier){
    String id = optionallyEscapedIdentifier;
    if (id.startsWith("`")){
      return id.substring(1, id.length()-1);
    }
    return id;
  }

  public static Type type(ParseTree declaration){
    if (declaration == null){
      return Types.ANY;
    }
    else {
      return Types.byName(declaration.getText());
    }
  }

  public static String key(ParseTree token){
    return identifier(token.getText().substring(1));
  }

  private static byte nibbleForChar(char c){
    if (c >= '0' && c <='9'){
      return (byte) (c-'0');
    } else if (c >= 'a' && c <= 'f'){
      return (byte) (c-'a'+10);
    }
    else {
      return (byte) (c-'A'+10);
    }
  }

  // converts hex string of the form 0b03DaFaf9.. to byte array
  // no validation on the input string is done
  // this is assuming the lexer let through only good values
  public static byte[] hexToBytes(String hex){

    int strLen = hex.length();

    int bytesLen = (strLen-2)/2;
    byte[] bytes = new byte[bytesLen];

    int p = 0;
    for (int i = 2; i < strLen; i+=2){
      byte u = nibbleForChar(hex.charAt(i));
      byte l = nibbleForChar(hex.charAt(i+1));
      bytes[p] = (byte) (u << 4 | l);
      p++;
    }

    return bytes;
  }
}
