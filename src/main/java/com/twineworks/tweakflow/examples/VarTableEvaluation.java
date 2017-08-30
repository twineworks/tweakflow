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

package com.twineworks.tweakflow.examples;

import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.util.VarTable;

import java.util.LinkedHashMap;
import java.util.Scanner;

public class VarTableEvaluation {

  private VarTable table;
  private Runtime runtime;

  private String circumferenceExp;
  private String areaExp;

  private Runtime.Var varA;
  private Runtime.Var varB;
  private Runtime.Var[] providedVars;

  private Runtime.Library userVars;
  private Runtime.Var varCircumference;
  private Runtime.Var varArea;

  private void askFormulas(){

    System.out.println(
        "Given a rectangle with sides of length a and b.\n" +
            "What is the formula to calculate the circumference?");
    System.out.print("circumference: ");

    Scanner scanner = new Scanner(System.in);
    circumferenceExp = scanner.nextLine().trim();

    System.out.println(
        "And the formula for calculating surface area?");
    System.out.print("area: ");

    areaExp = scanner.nextLine().trim();

    System.out.println("Thanks. Checking answer...\n");

  }

  private void makeTable(){

    table = new VarTable.Builder()
        .setPrologue(
            "# provide a and b as references in module scope\n" +
                "alias rect.a as a;\n" +
                "alias rect.b as b;\n" +
                "library rect {\n" +
                "  provided a;\n" +
                "  provided b;\n" +
                "}"
        )
        // formulas provided by the user
        .addVar("circumference", circumferenceExp)
        .addVar("area", areaExp)
        .build();

  }

  private boolean tableOK() {

    // table not present yet?
    if (table == null) return false;

    // parses ok?
    if (table.hasParseErrors()){
      LinkedHashMap<String, LangException> parseErrors = table.getVarParseErrors();
      for (String errorVar : parseErrors.keySet()) {
        System.err.println("Something is wrong with "+errorVar+": Can't understand input. You can use a, b, and arithmetic operators +,-,*,/ in your expression.");
      }
      System.err.flush();
      return false;
    }

    // compilation error?
    try {
      runtime = table.compile();
    } catch (LangException e){
      String errorVar = table.varNameFor(e.getSourceInfo());
      System.err.println("Something is wrong with "+errorVar+": "+e.getMessage()+". You can use a, b, and arithmetic operators +,-,*,/ in your expression");
      System.err.flush();
      return false;
    }

    return true;
  }

  private void extractVars(){

    Runtime.Module module = runtime.getModules().get(table.getModulePath());

    // extract provided variables
    Runtime.Library rect = module.getLibrary("rect");
    varA = rect.getVar("a");
    varB = rect.getVar("b");
    providedVars = new Runtime.Var[] {varA, varB};

    // extract user variables
    userVars = module.getLibrary(table.getVarLibraryName());
    varCircumference = userVars.getVar("circumference");
    varArea = userVars.getVar("area");

  }

  private void verifyFormulas() {

    // superficially verify given formulas by checking against rectangles
    // of side length 0.25 -> 25 in 0.25 increments
    // that's 10.000 rectangles checked
    boolean ok = true;
    for(double a=0.25; a<=25.0d && ok; a+=0.25){
      for(double b=0.25; b<=25.0d && ok; b+=0.25){
        ok = verifyRect(a, b);
      }
    }

    if (ok){
      System.out.println("Congratulations. The formulas seem to be correct.");
    }
    else {
      System.err.println("Too bad. Better luck next time!");
      System.err.flush();
    }
  }

  private boolean verifyRect(double a, double b){

    Value[] values = {Values.make(a), Values.make(b)};
    Value expectedCircumference = Values.make(2 * a + 2 * b);
    Value expectedArea = Values.make(a*b);

    try {
      runtime.updateVars(providedVars, values);
      userVars.evaluate();
      return verifyCircumference(expectedCircumference) && verifyArea(expectedArea);
    } catch (LangException e){
      // evaluation errors indicate a throw
      System.err.println("Evaluation error for a = "+a+", and b = "+b+"\n"+e.getDigestMessage());
      System.err.flush();
      return false;
    }

  }

  private boolean verifyCircumference(Value expected){

      Value result = varCircumference.getValue();

      if (result.valueEquals(expected)){
        return true;
      }
      else{
        System.err.println("Wrong circumference of "+result.getValue()+" instead of "+expected+" for a = "+varA.getValue()+" and b = "+varB.getValue());
        System.err.flush();
        return false;
      }

  }

  private boolean verifyArea(Value expected){

    Value result = varArea.getValue();

    if (result.valueEquals(expected)){
      return true;
    }
    else{
      System.err.println("Wrong surface area of "+result.getValue()+" instead of "+expected+" for a = "+varA.getValue()+" and b = "+varB.getValue());
      System.err.flush();
      return false;
    }

  }

  public static void main(String[] args) {

    VarTableEvaluation me = new VarTableEvaluation();

    while (!me.tableOK()){
      me.askFormulas();
      me.makeTable();
    }

    me.extractVars();
    me.verifyFormulas();

  }

}
