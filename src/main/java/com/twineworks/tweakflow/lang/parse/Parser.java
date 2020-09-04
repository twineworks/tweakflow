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

package com.twineworks.tweakflow.lang.parse;

import com.twineworks.tweakflow.grammar.TweakFlowParserBaseListener;
import com.twineworks.tweakflow.lang.parse.bailing.BailParser;
import com.twineworks.tweakflow.lang.parse.recovery.RecoveryParser;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

final public class Parser extends TweakFlowParserBaseListener {

  private final ParseUnit parseUnit;
  private final boolean recovery;

  public Parser(ParseUnit parseUnit, boolean recovery) {
    this.parseUnit = parseUnit;
    this.recovery = recovery;
  }

  public Parser(ParseUnit parseUnit) {
    this(parseUnit, false);
  }

  public ParseResult parseUnit(){
    if (recovery){
      return new RecoveryParser(parseUnit).parseUnit();
    }
    else{
      return new BailParser(parseUnit).parseUnit();
    }

  }

  public ParseResult parseInteractiveInput(){
    return new BailParser(parseUnit).parseInteractiveInput();
  }

  public ParseResult parseReference(){
    return new BailParser(parseUnit).parseReference();
  }

  public ParseResult parseExpression(){
    return new BailParser(parseUnit).parseExpression();
  }

  public ParseResult parseModuleHead(){
    return new BailParser(parseUnit).parseModuleHead();
  }


}
