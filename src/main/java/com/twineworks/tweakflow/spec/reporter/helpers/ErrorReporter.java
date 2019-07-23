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

import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.spec.nodes.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ErrorReporter {

  public static String indented(String indent, String message){
    if (message == null) return "null";
    String[] parts = message.split("\\r?\\n");
    String nl = System.lineSeparator();
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      if (i > 0) out.append(nl).append(indent);
      String part = parts[i];
      out.append(part);
    }
    return out.toString();
  }

  public static String errorFor(int errorNr, SpecNode node, boolean color){
    if (node instanceof ItNode){
      return errorForItNode(errorNr, (ItNode) node, color);
    }
    else if (node instanceof BeforeNode){
      return errorForBeforeNode(errorNr, (BeforeNode) node, color);
    }
    else if (node instanceof SubjectSpecNode){
      return errorForSubjectSpecNode(errorNr, (SubjectSpecNode) node, color);
    }
    else if (node instanceof FileNode){
      return errorForFileNode(errorNr, (FileNode) node, color);
    }
    else {
      return node.getErrorMessage();
    }
  }


  private static String errorForSubjectSpecNode(int errorNr, SubjectSpecNode node, boolean color){
    StringBuilder sb = new StringBuilder();
    String nl = System.lineSeparator();
    String errorLocation = node.getErrorLocation();

    try {
      Path currentDir = Paths.get(".").toAbsolutePath();
      errorLocation = currentDir.relativize(Paths.get(errorLocation)).toString();
    } catch (Exception ignored){}

    if (color) sb.append(ConsoleHelper.RED);
    sb.append("  #").append(errorNr).append(" Subject evaluation failure").append(nl);
    if (color) sb.append(ConsoleHelper.RESET);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("     spec: ");
    if (color) sb.append(ConsoleHelper.RESET);
    sb.append(node.getParent().getFullName()).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("       at: ");
    if (color) sb.append(ConsoleHelper.RESET);
    sb.append(errorLocation).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("    error: ");
    if (color) sb.append(ConsoleHelper.RESET);

    sb.append(indented("           ", errorMessageForNode(node)));

    return sb.toString();
  }

  private static String errorForBeforeNode(int errorNr, BeforeNode node, boolean color){
    StringBuilder sb = new StringBuilder();
    String nl = System.lineSeparator();
    String errorLocation = node.at().file+":"+node.at().line;

    try {
      Path currentDir = Paths.get(".").toAbsolutePath();
      errorLocation = currentDir.relativize(Paths.get(node.at().file)).toString()+":"+node.at().line;
    } catch (Exception ignored){}

    if (color) sb.append(ConsoleHelper.RED);
    sb.append("  #").append(errorNr).append(" Before hook failure").append(nl);
    if (color) sb.append(ConsoleHelper.RESET);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("     spec: ");
    if (color) sb.append(ConsoleHelper.RESET);
    sb.append(node.getParent().getFullName()).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("       at: ");
    if (color) sb.append(ConsoleHelper.RESET);
    sb.append(errorLocation).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("    error: ");
    if (color) sb.append(ConsoleHelper.RESET);

    sb.append(indented("           ", errorMessageForNode(node)));

    return sb.toString();
  }

  private static String errorForFileNode(int errorNr, FileNode node, boolean color){
    StringBuilder sb = new StringBuilder();
    String nl = System.lineSeparator();
    String errorLocation = node.getName();

    try {
      Path currentDir = Paths.get(".").toAbsolutePath();
      errorLocation = currentDir.relativize(Paths.get(errorLocation)).toString();
    } catch (Exception ignored){}

    if (color) sb.append(ConsoleHelper.RED);
    sb.append("  #").append(errorNr).append(" Problem processing spec file").append(nl);
    if (color) sb.append(ConsoleHelper.RESET);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("     file: ");
    if (color) sb.append(ConsoleHelper.RESET);
    sb.append(node.getName()).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("       at: ");
    if (color) sb.append(ConsoleHelper.RESET);
    sb.append(errorLocation).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("    error: ");
    if (color) sb.append(ConsoleHelper.RESET);

    String errorMessage = errorMessageForNode(node);
    sb.append(indented("           ", errorMessage));

    return sb.toString();
  }

  public static String errorMessageForNode(SpecNode node){
    // ItNodes construct error messages taking into account
    // matcher errors

    if (node instanceof ItNode) return node.getErrorMessage();
    String errorMessage = node.getErrorMessage();
    Throwable t = node.getCause();
    if (t instanceof LangException){
      LangException e = (LangException) t;
      errorMessage = e.getDigestMessage();
    }
    return errorMessage;
  }

  private static String errorForItNode(int errorNr, ItNode node, boolean color){
    StringBuilder sb = new StringBuilder();
    String nl = System.lineSeparator();
    String errorLocation = node.getErrorLocation();

    try {
      Path currentDir = Paths.get(".").toAbsolutePath();
      errorLocation = currentDir.relativize(Paths.get(node.getErrorLocation())).toString();
    } catch (Exception ignored){}

    String ind = "           ";

    if (color) sb.append(ConsoleHelper.RED);
    sb.append("  #").append(errorNr);
    sb.append(" Spec failure").append(nl);
    if (color) sb.append(ConsoleHelper.RESET);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("     spec: ");
    if (color) sb.append(ConsoleHelper.RESET);
    sb.append(node.getFullName()).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("       at: ");
    if (color) sb.append(ConsoleHelper.RESET);
    sb.append(errorLocation).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("    error: ");
    if (color) sb.append(ConsoleHelper.RESET);

    sb.append(indented(ind, node.getErrorMessage())).append(nl);

    if (color) sb.append(ConsoleHelper.FAINT);
    sb.append("     body: ");
    if (color) sb.append(ConsoleHelper.RESET);

    sb.append(indented(ind, node.getBody()));

    return sb.toString();
  }

}
