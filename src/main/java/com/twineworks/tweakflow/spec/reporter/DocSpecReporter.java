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

package com.twineworks.tweakflow.spec.reporter;

import com.twineworks.tweakflow.spec.nodes.*;
import com.twineworks.tweakflow.spec.reporter.helpers.ConsoleColors;
import com.twineworks.tweakflow.spec.reporter.helpers.ErrorReporter;
import com.twineworks.tweakflow.spec.reporter.helpers.HumanReadable;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

public class DocSpecReporter implements SpecReporter {

  private String indent = "  ";
  private int depth = 0;
  private int failing = 0;
  private int passing = 0;
  private int pending = 0;
  private int errors = 0;

  private DescribeNode currentDescribe;

  private PrintStream out;
  private ArrayList<SpecNode> errorNodes = new ArrayList<>();

  public DocSpecReporter() {
    this.out = System.out;
  }

  @Override
  public void onEnterSuite(SuiteNode node) {
    depth++;
  }

  @Override
  public void onEnterDescribe(DescribeNode node) {

    currentDescribe = node;

    if (depth == 1) {
      out.println();
    }

    printIndent();
    out.println(node.getName());
    depth++;
  }

  @Override
  public void onEnterBefore(BeforeNode node) {

  }

  @Override
  public void onLeaveBefore(BeforeNode node) {
    if (!node.isSuccess() && node.didRun()){
      errors++;
      errorNodes.add(node);
      depth--;
      printIndent();
      depth++;
      out.print(ConsoleColors.RED);
      out.print("✗");
      out.print(" ");
      out.println("before hook failed: "+node.getErrorMessage());
      out.print(ConsoleColors.RESET);
    }
  }

  private void printIndent() {
    for (int i = 0; i < depth; i++) out.print(indent);
  }

  @Override
  public void onLeaveDescribe(DescribeNode node) {
    depth--;
  }

  @Override
  public void onEnterIt(ItNode node) {
  }

  @Override
  public void onLeaveIt(ItNode node) {

    printIndent();
    if (node.isPending()) {
      pending++;
      out.print(ConsoleColors.YELLOW);
      out.print("~");
      out.print(" ");
      out.print(ConsoleColors.RESET);
      out.print(ConsoleColors.FAINT);
      out.println(node.getName());
      out.print(ConsoleColors.RESET);
    } else if (node.isSuccess()) {
      passing++;
      out.print(ConsoleColors.GREEN);
      out.print("✓");
      out.print(" ");
      out.print(ConsoleColors.RESET);
      out.print(ConsoleColors.FAINT);
      out.println(node.getName());
      out.print(ConsoleColors.RESET);
    } else {
      if (node.didRun()) {
        errorNodes.add(node);
      }
      failing++;
      out.print(ConsoleColors.RED);
      out.print("✗");
      out.print(" ");
      out.print(ConsoleColors.RESET);
      out.print(ConsoleColors.FAINT);
      out.println(node.getName());
      out.print(ConsoleColors.RESET);
    }

  }

  @Override
  public void onLeaveSuite(SuiteNode node) {

    out.println();
    printIndent();
    depth--;
    out.print(ConsoleColors.GREEN);
    out.print(passing + " passing");
    out.print(ConsoleColors.RESET);
    if (pending > 0) {
      out.print(", ");
      out.print(ConsoleColors.YELLOW);
      out.print(pending + " pending");
      out.print(ConsoleColors.RESET);
    }
    if (failing > 0) {
      out.print(", ");
      out.print(ConsoleColors.RED);
      out.print(failing + " failing");
      out.print(ConsoleColors.RESET);
    }
    if (errors > 0){
      out.print(" and ");
      out.print(ConsoleColors.RED);
      out.print(errors + " error");
      if (errors > 1) out.print("s");
      out.print(ConsoleColors.RESET);
    }

    out.print(" in ");
    out.print(HumanReadable.formatDuration(node.getDurationMillis()));
    out.println();

    int i = 1;
    for (SpecNode errorNode : errorNodes) {
      out.println();
      out.println(ErrorReporter.errorFor(i, errorNode, true));
      i++;
    }

  }

  @Override
  public void setOptions(Map<String, String> options) {

  }
}
