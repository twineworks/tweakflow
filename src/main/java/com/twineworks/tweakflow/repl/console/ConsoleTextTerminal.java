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

package com.twineworks.tweakflow.repl.console;

import jline.console.ConsoleReader;
import jline.console.UserInterruptException;

import java.io.IOException;
import java.util.function.Consumer;

public class ConsoleTextTerminal implements TextTerminal {

  private final ConsoleReader reader;
  private Consumer<UserInterruptException> userInterruptHandler;

  public ConsoleTextTerminal(ConsoleReader consoleReader) {
    this.reader = consoleReader;
    reader.setExpandEvents(false);
    reader.setHandleUserInterrupt(true);
  }

  @Override
  public String read() {

    try {
      return this.reader.readLine((Character) null);
    } catch (UserInterruptException e) {
      if (userInterruptHandler != null) {
        userInterruptHandler.accept(e);
      }
      return e.getPartialLine();
    } catch (IOException e){
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  @Override
  public String read(String prompt) {
    try {
      return this.reader.readLine(prompt, (Character) null);
    } catch (UserInterruptException e) {
      if (userInterruptHandler != null) {
        userInterruptHandler.accept(e);
      }
      return e.getPartialLine();
    } catch (IOException e){
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public void print(String message) {

    try {
      this.reader.setPrompt(message);
      this.reader.drawLine();
      this.reader.flush();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      this.reader.setPrompt((String) null);
    }

  }

  @Override
  public void println(String message) {
    this.print(message);
    this.println();
  }

  @Override
  public void println() {
    try {
      this.reader.println();
      this.reader.flush();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  public ConsoleReader getReader() {
    return this.reader;
  }

  @Override
  public void setUserInterruptHandler(Consumer<UserInterruptException> userInterruptHandler) {
    this.userInterruptHandler = userInterruptHandler;
  }

  @Override
  public void dispose(){
    reader.close();
  }
}
