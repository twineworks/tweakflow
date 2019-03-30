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

package com.twineworks.tweakflow.repl.console;

import jline.console.UserInterruptException;

import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Consumer;

public class SystemTextTerminal implements TextTerminal {

  private final Scanner scanner;
  private final PrintStream out;

  public SystemTextTerminal() {
    this.scanner = new Scanner(System.in);
    this.out = System.out;
  }

  @Override
  public String read() {
    return this.scanner.nextLine();
  }

  @Override
  public String read(String prompt) {
    print(prompt);
    return read();
  }

  @Override
  public void print(String message) {
    this.out.print(message);
    this.out.flush();
  }

  @Override
  public void println(String message) {
    this.out.println(message);
    this.out.flush();
  }

  @Override
  public void println() {
    this.out.println();
    this.out.flush();
  }

  @Override
  public void setUserInterruptHandler(Consumer<UserInterruptException> userInterruptHandler) {
    throw new UnsupportedOperationException("Cannot set an interrupt handler");
  }

  @Override
  public void dispose() {

  }
}
