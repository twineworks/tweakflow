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

public class ParseResult {

  private final Node node;
  private final LangException exception;
  private final long parseDurationMillis;
  private final long buildDurationMillis;

  static public ParseResult ok(Node node, long parseDurationMillis, long buildDurationMillis){
    return new ParseResult(null, node, parseDurationMillis, buildDurationMillis);
  }

  static public ParseResult error(LangException exception, long parseDurationMillis, long buildDurationMillis){
    return new ParseResult(exception, null, parseDurationMillis, buildDurationMillis);
  }

  private ParseResult(LangException exception, Node node, long parseDurationMillis, long buildDurationMillis){
    this.node = node;
    this.exception = exception;
    this.parseDurationMillis = parseDurationMillis;
    this.buildDurationMillis = buildDurationMillis;
  }

  public boolean isSuccess() {
    return exception == null;
  }

  public boolean isError() {
    return exception != null;
  }

  public Node getNode() {
    return node;
  }

  public LangException getException() {
    return exception;
  }

  public long getParseDurationMillis() {
    return parseDurationMillis;
  }

  public long getBuildDurationMillis() {
    return buildDurationMillis;
  }
}
