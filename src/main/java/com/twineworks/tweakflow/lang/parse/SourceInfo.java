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

import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

public class SourceInfo {

  private ParseUnit parseUnit;
  private int line;
  private int charWithinLine;

  private int sourceIdxStart;
  private int sourceIdxEnd;

  public SourceInfo(ParseUnit parseUnit, int line, int charWithinLine, int sourceIdxStart, int sourceIdxEnd) {
    this.parseUnit = parseUnit;
    this.line = line;
    this.charWithinLine = charWithinLine;
    this.sourceIdxStart = sourceIdxStart;
    this.sourceIdxEnd = sourceIdxEnd;
  }

  public ParseUnit getParseUnit() {
    return parseUnit;
  }

  public void setParseUnit(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  public int getLine() {
    return line;
  }

  public SourceInfo setLine(int line) {
    this.line = line;
    return this;
  }

  public int getCharWithinLine() {
    return charWithinLine;
  }

  public SourceInfo setCharWithinLine(int charWithinLine) {
    this.charWithinLine = charWithinLine;
    return this;
  }

  public SourceInfo copy(){
    return new SourceInfo(parseUnit, line, charWithinLine, sourceIdxStart, sourceIdxEnd);
  }

  public int getSourceIdxStart() {
    return sourceIdxStart;
  }

  public int getSourceIdxEnd() {
    return sourceIdxEnd;
  }

  public SourceInfo setSourceIdxStart(int sourceIdxStart) {
    this.sourceIdxStart = sourceIdxStart;
    return this;
  }

  public SourceInfo setSourceIdxEnd(int sourceIdxEnd) {
    this.sourceIdxEnd = sourceIdxEnd;
    return this;
  }

  public String toString(){
    return getFullLocation();
  }

  public String getFullLocation(){
    return parseUnit.getPath()+":"+line+":"+charWithinLine;
  }

  public String getSourceCode(){
    String programText = parseUnit.getProgramText();
    if (programText == null) return null;
    if (sourceIdxStart == 0 && sourceIdxEnd == 0) return null;
    if (sourceIdxStart < programText.length() &&  sourceIdxEnd < programText.length()){
        return programText.substring(sourceIdxStart, sourceIdxEnd+1);
    } else {
      return null;
    }

  }

  public boolean precedes(SourceInfo other){
    return parseUnit == other.parseUnit &&
        (getLine() < other.getLine() || getLine() == other.getLine() && getCharWithinLine() < other.getCharWithinLine());

  }

}
