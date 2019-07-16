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

package com.twineworks.tweakflow.spec.reporter.helpers;

import com.twineworks.tweakflow.spec.nodes.ItNode;
import com.twineworks.tweakflow.spec.nodes.SpecNode;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ErrorReporter {

  private static String indented(String message){
    if (message == null) return "null";
    String[] parts = message.split("\\r?\\n");
    String nl = System.lineSeparator();
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      if (i > 0) out.append(nl).append("         ");
      String part = parts[i];
      out.append(part);
    }
    return out.toString();
  }

  public static String errorFor(int errorNr, SpecNode node, boolean color){
    if (node instanceof ItNode){
      return errorForItNode(errorNr, (ItNode) node, color);
    }
    else {
      return node.getErrorMessage();
    }
  }

  private static String errorForItNode(int errorNr, ItNode node, boolean color){
    StringBuilder sb = new StringBuilder();
    String nl = System.lineSeparator();
    String errorLocation = node.getErrorLocation();

    try {
      Path currentDir = Paths.get(".").toAbsolutePath();
      errorLocation = currentDir.relativize(Paths.get(node.getErrorLocation())).toString();
    } catch (Exception ignored){}

    if (color) sb.append(ConsoleColors.RED);
    sb.append("Spec failure #").append(errorNr).append(nl);
    if (color) sb.append(ConsoleColors.RESET);

    if (color) sb.append(ConsoleColors.FAINT);
    sb.append("   spec: ");
    if (color) sb.append(ConsoleColors.RESET);
    sb.append(node.getFullName()).append(nl);

    if (color) sb.append(ConsoleColors.FAINT);
    sb.append("     at: ");
    if (color) sb.append(ConsoleColors.RESET);
    sb.append(errorLocation).append(nl);

    if (color) sb.append(ConsoleColors.FAINT);
    sb.append("  error: ");
    if (color) sb.append(ConsoleColors.RESET);

    sb.append(indented(node.getErrorMessage()));

    return sb.toString();
  }

}
