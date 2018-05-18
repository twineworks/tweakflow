/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Twineworks GmbH
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
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import com.twineworks.tweakflow.util.VarTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;


/**
 * This class demonstrates providing data to user scripts only when they are needed.
 *
 * Try running main and entering an expression like:
 * (if year_of_birth < 1985 then "old" else "little") .. " " .. first_name .. " " .. year_of_birth % 100
 *
 */
public class LazilyProvidedVars {

  private static Pilot[] pilots = {
      new Pilot("Gordon", "Christian", "Brenner", 1981),
      new Pilot("Michael", "Frank", "Ramsey", 1983),
      new Pilot("Tina", "Joan", "Gunner", 1983),
      new Pilot("Karol", "Jerome", "Black", 1984),
      new Pilot("Cindy", "Clara", "Redfoot", 1985),
      new Pilot("Herald", "Franz", "Scirocco", 1985),
      new Pilot("John", "Jess", "Knox", 1986),
      new Pilot("Kareem", "Antwan", "Hastings", 1986),
      new Pilot("Jill", "Maria", "Green", 1988),
      new Pilot("Jane", "Fiona", "Stacks", 1992)
  };
  private VarTable table;
  private Runtime runtime;
  private String callSignExp;
  private String nl = System.lineSeparator();
  private String usageBanner = "You can use the following pilot data in your expression: " + nl +
      "first_name" + nl +
      "middle_name" + nl +
      "last_name" + nl +
      "year_of_birth" + nl + nl +
      "The standard library is available as std";

  private Runtime.Var varFirstName;
  private Runtime.Var varMiddleName;
  private Runtime.Var varLastName;
  private Runtime.Var varYearOfBirth;
  private boolean needsFirstName;
  private boolean needsMiddleName;
  private boolean needsLastName;
  private boolean needsYearOfBirth;
  private Runtime.Var varCallSign;

  public static void main(String[] args) {

    LazilyProvidedVars me = new LazilyProvidedVars();
    while (!me.tableOK()) {
      me.askFormula();
      me.makeTable();
    }
    me.extractVars();
    me.processRoster();


  }

  private void askFormula() {

    System.out.println(
        "Welcome Commander," + nl +
            "as the new Commander of star ship 'Dawn of Peace' you have the privilege" + nl +
            "and duty to assign call signs to your fighter pilots." + nl +
            "As per fleet regulations, the call sign of each pilot must be uniformly" + nl +
            "derived from one or more of the following pilot properties: " + nl + nl +
            "first_name" + nl +
            "middle_name" + nl +
            "last_name" + nl +
            "year_of_birth" + nl + nl +
            "Please enter the formula determining a pilot's call sign." + nl
    );
    System.out.print("call sign: ");

    Scanner scanner = new Scanner(System.in);
    callSignExp = scanner.nextLine().trim();

    System.out.println();
    System.out.println("Processing..." + nl);

  }

  private void makeTable() {

    table = new VarTable.Builder()
        .setPrologue(
            "import * as std from 'std';\n" +
                "alias pilot.first_name as first_name;\n" +
                "alias pilot.middle_name as middle_name;\n" +
                "alias pilot.last_name as last_name;\n" +
                "alias pilot.year_of_birth as year_of_birth;\n" +
                "library pilot {\n" +
                "  provided first_name;\n" +
                "  provided middle_name;\n" +
                "  provided last_name;\n" +
                "  provided year_of_birth;\n" +
                "}"
        )
        // formula provided by the user
        .addVar("call_sign", callSignExp)
        .build();

  }

  private boolean tableOK() {

    // table not present yet?
    if (table == null) return false;

    // parses ok?
    if (table.hasParseErrors()) {
      LinkedHashMap<String, LangException> parseErrors = table.getVarParseErrors();
      for (String errorVar : parseErrors.keySet()) {
        System.out.println("Something is wrong with " + errorVar + ": Can't understand input." + nl + parseErrors.get(errorVar).getDigestMessage() + nl + usageBanner + nl);
      }
      System.out.flush();
      return false;
    }

    // compilation error?
    try {
      runtime = table.compile();
    } catch (LangException e) {
      String errorVar = table.varNameFor(e.getSourceInfo());
      System.out.println("Something is wrong with " + errorVar + "." + nl + e.getDigestMessage() + nl + usageBanner);
      System.out.flush();
      return false;
    }

    return true;
  }

  private void extractVars() {

    Runtime.Module module = runtime.getModules().get(table.getModulePath());

    // extract provided variables
    Runtime.Library pilot = module.getLibrary("pilot");

    varFirstName = pilot.getVar("first_name");
    needsFirstName = varFirstName.isReferenced();

    varMiddleName = pilot.getVar("middle_name");
    needsMiddleName = varMiddleName.isReferenced();

    varLastName = pilot.getVar("last_name");
    needsLastName = varLastName.isReferenced();

    varYearOfBirth = pilot.getVar("year_of_birth");
    needsYearOfBirth = varYearOfBirth.isReferenced();

    // extract user variables
    Runtime.Library userVars = module.getLibrary(table.getVarLibraryName());
    varCallSign = userVars.getVar("call_sign");

  }

  private void processRoster() {

    // report on which properties are used
    System.out.println("Your formula references pilot properties as follows: ");
    System.out.println("first_name       " + (needsFirstName ? "Y" : "N"));
    System.out.println("middle_name      " + (needsMiddleName ? "Y" : "N"));
    System.out.println("last_name        " + (needsLastName ? "Y" : "N"));
    System.out.println("year_of_birth    " + (needsYearOfBirth ? "Y" : "N"));
    System.out.println();

    // determine relevant vars the call sign depends on
    ArrayList<Runtime.Var> relevantVars = new ArrayList<>();
    if (needsFirstName) relevantVars.add(varFirstName);
    if (needsMiddleName) relevantVars.add(varMiddleName);
    if (needsLastName) relevantVars.add(varLastName);
    if (needsYearOfBirth) relevantVars.add(varYearOfBirth);

    boolean callSignIsConstant = relevantVars.isEmpty();

    // if the call sign is a constant, evaluate it once
    // instead of for every pilot
    if (callSignIsConstant) {
      varCallSign.evaluate();
    }

    ArrayList<Value> values = new ArrayList<>();

    System.out.println("Pilot roster: ");

    System.out.println(
        String.format(
            "%-12s %-12s %-12s %-8s %s",
            "First Name",
            "Middle Name",
            "Last Name",
            "Born",
            "Call Sign"
        )
    );


    for (Pilot pilot : pilots) {

      try {

        // only provide values and recalculate call sign if needed
        if (!callSignIsConstant) {

          values.clear();

          // conditionally provide individual values if needed by call sign expression
          if (needsFirstName) values.add(Values.make(pilot.firstName));
          if (needsMiddleName) values.add(Values.make(pilot.middleName));
          if (needsLastName) values.add(Values.make(pilot.lastName));
          if (needsYearOfBirth) values.add(Values.make(pilot.yearOfBirth));

          // updating vars triggers the evaluation of dependent variables,
          // so the call sign value automatically updates
          runtime.updateVars(relevantVars, values);
        }

        // cast in case the user expressino generated a number
        String callSign = varCallSign.getValue().castTo(Types.STRING).string();
        System.out.println(
            String.format(
                "%-12s %-12s %-12s %-8d %s",
                pilot.firstName,
                pilot.middleName,
                pilot.lastName,
                pilot.yearOfBirth,
                callSign
            )
        );

      } catch (LangException e) {
        // evaluation errors indicate a throw
        System.out.println("Evaluation error for pilot" + nl +
            "first_name: " + pilot.firstName + nl +
            "middle_name: " + pilot.middleName + nl +
            "last_name:   " + pilot.lastName + nl +
            "year_of_birth: " + pilot.yearOfBirth + nl +
            e.getDigestMessage()
        );
        System.out.flush();
      }

    }

  }

  private static class Pilot {
    public String firstName;
    public String middleName;
    public String lastName;
    public int yearOfBirth;

    public Pilot(String firstName, String middleName, String lastName, int yearOfBirth) {
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
      this.yearOfBirth = yearOfBirth;
    }
  }

}
