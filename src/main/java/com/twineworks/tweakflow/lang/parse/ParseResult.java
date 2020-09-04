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

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.errors.LangException;

import java.util.ArrayList;

public class ParseResult {

  private final Node node;
  private final LangException exception;
  private final long parseDurationMillis;
  private final long buildDurationMillis;
  private final boolean isRecovery;
  private final ArrayList<LangException> recoveryErrors;

  private ParseResult(boolean recovery, ArrayList<LangException> recoveryErrors, LangException exception, Node node, long parseDurationMillis, long buildDurationMillis) {
    this.isRecovery = recovery;
    this.recoveryErrors = recoveryErrors;
    this.node = node;
    this.exception = exception;
    this.parseDurationMillis = parseDurationMillis;
    this.buildDurationMillis = buildDurationMillis;
  }

  static public ParseResult ok(Node node, long parseDurationMillis, long buildDurationMillis) {
    return new ParseResult(false, null, null, node, parseDurationMillis, buildDurationMillis);
  }

  static public ParseResult error(LangException exception, long parseDurationMillis, long buildDurationMillis) {
    return new ParseResult(false, null, exception, null, parseDurationMillis, buildDurationMillis);
  }

  static public ParseResult recovery(ArrayList<LangException> errors, Node node, long parseDurationMillis, long buildDurationMillis) {
    return new ParseResult(true, errors, null, node, parseDurationMillis, buildDurationMillis);
  }

  public boolean isSuccess() {
    if (isRecovery()){
      return node != null;
    }
    else {
      return exception == null;
    }
  }

  public boolean isError() {
    return !isSuccess();
  }

  public boolean isRecovery() {
    return isRecovery;
  }

  public Node getNode() {
    return node;
  }

  public LangException getException() {
    if (isRecovery){
      if (recoveryErrors != null && recoveryErrors.size() > 0){
        return recoveryErrors.get(0);
      }
      return null;
    }
    else{
      return exception;
    }

  }

  public boolean hasRecoveryErrors() {
    return recoveryErrors != null && recoveryErrors.size() > 0;
  }

  public ArrayList<LangException> getRecoveryErrors() {
    return recoveryErrors;
  }

  public long getParseDurationMillis() {
    return parseDurationMillis;
  }

  public long getBuildDurationMillis() {
    return buildDurationMillis;
  }
}
