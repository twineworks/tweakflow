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
import com.twineworks.tweakflow.spec.reporter.anim.ConsoleAnimator;
import com.twineworks.tweakflow.spec.reporter.helpers.ConsoleHelper;
import com.twineworks.tweakflow.spec.reporter.helpers.ErrorReporter;
import com.twineworks.tweakflow.spec.reporter.helpers.HumanReadable;
import com.twineworks.tweakflow.spec.reporter.helpers.PlatformHelper;
import com.twineworks.tweakflow.spec.runner.SpecRunner;
import org.fusesource.jansi.AnsiConsole;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class DocSpecReporter implements SpecReporter {

  private int depth = 0;
  private int failing = 0;
  private int passing = 0;
  private int pending = 0;
  private int errors = 0;
  private boolean color = false;

  private PrintStream out;
  private ArrayList<SpecNode> errorNodes = new ArrayList<>();
  private boolean tty;
//  private ConsoleAnimator consoleAnimator = new ConsoleAnimator();

  private final boolean isWindows = PlatformHelper.isWindows();

  private final String OK = isWindows ? "√" : "✓";
  private final String ERR = isWindows ? "×" : "✗";
  private final String PEN = "~";

  public DocSpecReporter() {
  }

  @Override
  public void onEnterDescribe(DescribeNode node) {

    if (depth == 1) {
      out.println();
    }

    printIndent();
    out.print(node.getName());
    printTags(node);
    out.println();
    depth++;
    out.flush();

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
      if (color) out.print(ConsoleHelper.RED);
      out.print("#");
      out.print(errorNodes.size());
      out.print(" before hook failed: ");
      out.print(ErrorReporter.indented(getIndent(), node.getErrorMessage()));
      if (color) out.print(ConsoleHelper.RESET);
      out.println();

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
      depth--;
      printIndent();
      depth++;
      if (color) out.print(ConsoleHelper.RED);
      out.print("#");
      out.print(errorNodes.size());
      out.print(" after hook failed, aborting: ");
      out.print(ErrorReporter.indented(getIndent(), node.getErrorMessage()));
      if (color) out.print(ConsoleHelper.RESET);
      out.println();
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
      depth--;
      printIndent();
      if (color) out.print(ConsoleHelper.RED);
      out.print("#");
      out.print(errorNodes.size());
      out.print(" subject evaluation failed: ");
      out.print(ErrorReporter.indented(getIndent(), node.getErrorMessage()));
      if (color) out.print(ConsoleHelper.RESET);
      out.println();
      depth++;
    }
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
      if (color) out.print(ConsoleHelper.YELLOW);
      out.print(PEN);
      out.print(" ");
      if (color) out.print(ConsoleHelper.RESET);
      if (color) out.print(ConsoleHelper.FAINT);
      out.print(node.getName());
      if (color) out.print(ConsoleHelper.RESET);
    } else if (node.isSuccess()) {
      passing++;
      if (color) out.print(ConsoleHelper.GREEN);
      out.print(OK);
      out.print(" ");
      if (color) out.print(ConsoleHelper.RESET);
      if (color) out.print(ConsoleHelper.FAINT);
      out.print(node.getName());
      if (color) out.print(ConsoleHelper.RESET);
    } else {
      if (node.didRun()) {
        errorNodes.add(node);
      }
      failing++;
      if (color) out.print(ConsoleHelper.RED);
      out.print(ERR);
      out.print(" ");
      if (node.didRun()){
        out.print("#");
        out.print(errorNodes.size());
        out.print(" ");
      }
      if (color) out.print(ConsoleHelper.RESET);
      if (color) out.print(ConsoleHelper.FAINT);
      out.print(node.getName());
      if (color) out.print(ConsoleHelper.RESET);
    }

    printTags(node);
    out.println();
    out.flush();

  }

  @Override
  public void onFoundSpecModules(SpecRunner specRunner) {
    int moduleCount = specRunner.getModules().size();
    out.println();
    printIndent();
    out.print("running "+moduleCount+" spec modules ");
    out.println();
    out.flush();
  }

  @Override
  public void onEnterSuite(SuiteNode node) {
    depth++;
  }

  @Override
  public void onLeaveSuite(SuiteNode node) {

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
  }

  @Override
  public void onModuleCompiled(String module, Runtime runtime) {

  }

  @Override
  public void onModuleFailedToCompile(FileNode node, Throwable error) {

    out.println();
    errors++;
    errorNodes.add(node);
    printIndent();
    if (color) out.print(ConsoleHelper.RED);
    out.print("#");
    out.print(errorNodes.size());
    out.println(" error processing file: "+node.getName());
    out.println();
    depth++;
    printIndent();
    out.print(ErrorReporter.indented(getIndent(), ErrorReporter.errorMessageForNode(node)));
    depth--;
    if (color) out.print(ConsoleHelper.RESET);
    out.println();
    out.flush();
  }

  @Override
  public void onEnterFile(FileNode node) {
    out.println();
    printIndent();
    out.print(node.getName());
    if (color) out.print(ConsoleHelper.FAINT);
    out.print(" (");
    out.print(HumanReadable.formatDuration(node.getAnalysisResult().getAnalysisDurationMillis()));
    out.print(")");
    if (color) out.print(ConsoleHelper.RESET);
    out.println();
    out.println();
    depth++;

  }

  @Override
  public void onLeaveFile(FileNode node) {
    depth--;
  }

  private void printTags(TaggableSpecNode node){
    Set<String> tags = node.getOwnTags();

    if (tags.isEmpty()) return;

    if (color) out.print(ConsoleHelper.YELLOW);
    out.print(" {");
    int i=0;
    for (String tag : tags) {
      if (i>0) out.print(", ");
      out.print(tag);
      i++;
    }
    out.print("}");
    if (color) out.print(ConsoleHelper.RESET);
  }

  private void printIndent() {
    String indent = "  ";
    for (int i = 0; i < depth; i++) out.print(indent);
  }

  private String getIndent(){
    switch(depth){
      case 0: return "";
      case 1: return "  ";
      case 2: return "    ";
      case 3: return "      ";
      case 4: return "        ";
      case 5: return "          ";
      case 6: return "            ";
      case 7: return "              ";
      case 8: return "                ";
      case 9: return "                  ";
      case 10: return "                    ";
      default:{
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<depth;i++){
          sb.append("  ");
        }
        return sb.toString();
      }
    }
  }

  @Override
  public void setOptions(Map<String, String> options) {

    // turn on colored output?
    if (options.getOrDefault("color", "false").equalsIgnoreCase("true")){
      color = true;
    }

    // turn on tty animation?
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



}
