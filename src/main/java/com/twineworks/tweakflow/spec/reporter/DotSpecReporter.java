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

import com.twineworks.tweakflow.spec.nodes.DescribeNode;
import com.twineworks.tweakflow.spec.nodes.ItNode;

import java.io.PrintStream;
import java.util.ArrayList;

public class DotSpecReporter implements SpecReporter {

  private String indent = "  ";
  private int total = 0;
  private PrintStream out;
  private int errors = 0;
  private int passing = 0;
  private int pending = 0;
  private ArrayList<ItNode> errorNodes = new ArrayList<>();

  public DotSpecReporter(PrintStream out) {
    this.out = out;
  }

  @Override
  public void onEnterSuite() {

  }

  @Override
  public void onEnterDescribe(DescribeNode node) {
  }

  private void printIndent(){
    out.print(indent);
  }

  @Override
  public void onLeaveDescribe(DescribeNode node) {

  }

  @Override
  public void onEnterIt(ItNode node) {
  }

  @Override
  public void onLeaveIt(ItNode node) {

    if (total % 50 == 0){
      out.println();
      printIndent();
    }

    total++;

    if (node.isPending()){
      pending++;
      out.print(ConsoleColors.YELLOW);
      out.print("~");
      out.print(ConsoleColors.RESET);
    }
    else if (node.isSuccess()){
      passing++;
      out.print(ConsoleColors.GREEN);
      out.print(".");
      out.print(ConsoleColors.RESET);
    }
    else {
      errors++;
      out.print(ConsoleColors.RED);
      out.print("✗");
      out.print(ConsoleColors.RESET);
    }

  }

  @Override
  public void onLeaveSuite() {

    out.println();
    out.println();
    printIndent();

    out.print(passing+" passing");
    if (pending > 0){
      out.print(", "+pending+" pending");
    }
    if (errors > 0){
      out.print(", "+errors+" failures");
    }

    out.println();

    int i=1;
    for (ItNode errorNode : errorNodes) {
      out.println();
      out.println("Spec failure #"+i+": ");
      out.print("  ✗ ");
      out.println(errorNode.getName());
      out.print("  at ");
      out.println(errorNode.getErrorLocation());
      out.print("  ");
      out.println(errorNode.getErrorMessage());
      i++;
    }

  }
}
