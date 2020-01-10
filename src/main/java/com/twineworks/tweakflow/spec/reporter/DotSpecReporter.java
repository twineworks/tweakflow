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

import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.spec.nodes.*;
import com.twineworks.tweakflow.spec.reporter.helpers.ConsoleHelper;
import com.twineworks.tweakflow.spec.reporter.helpers.ErrorReporter;
import com.twineworks.tweakflow.spec.reporter.helpers.HumanReadable;
import com.twineworks.tweakflow.spec.reporter.helpers.PlatformHelper;
import com.twineworks.tweakflow.spec.runner.SpecRunner;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

public class DotSpecReporter implements SpecReporter {

  private int failing = 0;
  private int passing = 0;
  private int pending = 0;
  private int errors = 0;
  private boolean color = false;
  private boolean tty = false;
  private int dots = 0;

//  private ConsoleAnimator consoleAnimator = new ConsoleAnimator();
  private PrintStream out;
  private ArrayList<SpecNode> errorNodes = new ArrayList<>();

  public DotSpecReporter() {
  }

  @Override
  public void onEnterDescribe(DescribeNode node) {
  }

  @Override
  public void onEnterBefore(BeforeNode node) {

  }

  @Override
  public void onLeaveBefore(BeforeNode node) {
    if (!node.isSuccess() && node.didRun()){
      errors++;
      errorNodes.add(node);
      if (color) out.print(ConsoleHelper.RED);
      dot("!");
      if (color) out.print(ConsoleHelper.RESET);
      out.flush();
    }
  }

  @Override
  public void onEnterAfter(AfterNode node) {
  }

  @Override
  public void onLeaveAfter(AfterNode node) {
    if (!node.isSuccess() && node.didRun()){
      errors++;
      errorNodes.add(node);
      if (color) out.print(ConsoleHelper.RED);
      dot("!");
      if (color) out.print(ConsoleHelper.RESET);
      out.flush();
    }
  }

  @Override
  public void onEnterSubject(SpecNode node) {

  }

  @Override
  public void onLeaveSubject(SpecNode node) {
    if (!node.isSuccess() && node.didRun()){
      errors++;
      errorNodes.add(node);
      if (color) out.print(ConsoleHelper.RED);
      dot("!");
      if (color) out.print(ConsoleHelper.RESET);
      out.flush();
    }
  }

  @Override
  public void onLeaveDescribe(DescribeNode node) {
  }

  @Override
  public void onEnterIt(ItNode node) {
  }

  private void dot(String c){
    if (dots++ % 50 == 0){
      out.println();
      out.print(getIndent());
    }
    out.print(c);

  }

  @Override
  public void onLeaveIt(ItNode node) {

    if (node.isPending()) {
      pending++;
      if (color) out.print(ConsoleHelper.YELLOW);
      dot("~");
      if (color) out.print(ConsoleHelper.RESET);
    } else if (node.isSuccess()) {
      passing++;
      if (color) out.print(ConsoleHelper.GREEN);
      dot(".");
      if (color) out.print(ConsoleHelper.RESET);
    } else {
      if (node.didRun()) {
        errorNodes.add(node);
      }
      failing++;
      if (color) out.print(ConsoleHelper.RED);
      dot("!");
      if (color) out.print(ConsoleHelper.RESET);
    }
    out.flush();

  }

  @Override
  public void onFoundSpecModules(SpecRunner specRunner) {
    int moduleCount = specRunner.getModules().size();
    out.println();
    printIndent();
    out.print("running "+moduleCount+" spec modules ");
    out.println();

    // little waiting animation
    //    consoleAnimator.startAnimation(tty ? new DotBarAnimation(out, Math.min(Math.max(moduleCount+2, 5), 12)) : new DotWaitAnimation(out));
    out.flush();
  }

  @Override
  public void onEnterSuite(SuiteNode node) {
  }

  @Override
  public void onLeaveSuite(SuiteNode node) {

    out.println();
    out.println();
    printIndent();
    if (color) out.print(ConsoleHelper.GREEN);
    out.print(passing + " passing");
    if (color) out.print(ConsoleHelper.RESET);
    if (pending > 0) {
      out.print(", ");
      if (color) out.print(ConsoleHelper.YELLOW);
      out.print(pending + " pending");
      if (color) out.print(ConsoleHelper.RESET);
    }
    if (failing > 0) {
      out.print(", ");
      if (color) out.print(ConsoleHelper.RED);
      out.print(failing + " failing");
      if (color) out.print(ConsoleHelper.RESET);
    }
    if (errors > 0){
      out.print(" and ");
      if (color) out.print(ConsoleHelper.RED);
      out.print(errors + " error");
      if (errors > 1) out.print("s");
      if (color) out.print(ConsoleHelper.RESET);
    }

    if (color) out.print(ConsoleHelper.FAINT);
    out.print(" (");
    out.print(HumanReadable.formatDuration(node.getDurationMillis()));
    out.print(")");
    if (color) out.print(ConsoleHelper.RESET);
    out.println();

    int i = 1;
    for (SpecNode errorNode : errorNodes) {
      out.println();
      out.println(ErrorReporter.errorFor(i, errorNode, color));
      i++;
    }

    out.println();
    out.flush();
  }

  @Override
  public void onEnterFile(FileNode node) {
  }

  @Override
  public void onLeaveFile(FileNode node) {
  }

  private void printIndent() {
    out.print("  ");
  }

  private String getIndent(){
    return "  ";
  }

  @Override
  public void setOptions(Map<String, String> options) {

    // turn on colored output?
    if (options.getOrDefault("color", "false").equalsIgnoreCase("true")){
      color = true;
    }

    // turn on ansi tty animations?
    if (options.getOrDefault("tty", "false").equalsIgnoreCase("true")){
      tty = true;
    }

    if ((color || tty) && PlatformHelper.isWindows()){
      this.out = AnsiConsole.out;
    }
    else {
      this.out = System.out;
    }
  }

  @Override
  public void onModuleCompiled(String module, Runtime runtime) {

  }

  @Override
  public void onModuleFailedToCompile(FileNode node, Throwable error) {
    errors++;
    errorNodes.add(node);
    if (color) out.print(ConsoleHelper.RED);
    dot("!");
    if (color) out.print(ConsoleHelper.RESET);
    out.flush();
  }

}
