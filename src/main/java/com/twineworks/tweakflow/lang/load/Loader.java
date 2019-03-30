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

package com.twineworks.tweakflow.lang.load;

import com.twineworks.tweakflow.lang.analysis.AnalysisStage;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.ast.UnitNode;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.StringNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;
import com.twineworks.tweakflow.lang.load.relative.Resolved;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.Parser;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loader {


  private static AnalysisUnit load(LoadPath loadPath, String modulePath, LoadPathLocation pathLocation, Map<String, AnalysisUnit> workSet, boolean collectImports){

    long loadStart = System.currentTimeMillis();

    if (!pathLocation.pathExists(modulePath)){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+modulePath+" on load path");
    }
    ParseUnit parseUnit = pathLocation.getParseUnit(modulePath);

    // already done?
    if (workSet.containsKey(parseUnit.getPath())){
      return workSet.get(parseUnit.getPath());
    }

    Parser parser = new Parser(parseUnit);
    ParseResult parseResult = parser.parseUnit();

    if (!parseResult.isSuccess()){
      throw parseResult.getException();
    }

    UnitNode unitNode = (UnitNode) parseResult.getNode();

    AnalysisUnit unit = new AnalysisUnit()
        .setLocation(pathLocation)
        .setPath(parseUnit.getPath())
        .setUnit(unitNode)
        .setStage(AnalysisStage.PARSED)
        .setParseDurationMillis(parseResult.getParseDurationMillis())
        .setBuildDurationMillis(parseResult.getBuildDurationMillis());

    workSet.put(parseUnit.getPath(), unit);

    long loadEnd = System.currentTimeMillis();
    unit.setLoadDurationMillis(loadEnd-loadStart);

    // if it's a regular module, collect imports
    if (collectImports && unitNode instanceof ModuleNode){

      // recursively collect all imports
      List<ImportNode> imports = ((ModuleNode)unitNode).getImports();
      for (ImportNode anImport : imports) {
        ExpressionNode pathExp = anImport.getModulePath();

        // expect path expression to be a constant string
        if (!(pathExp instanceof StringNode)){
          throw new LangException(LangError.INVALID_IMPORT_PATH, pathExp.getSourceInfo());
        }

        String importPath = ((StringNode) pathExp).getStringVal();

        AnalysisUnit collectResult;

        // relative?
        if (importPath.startsWith(".")){
          Resolved resolved = loadPath.resolve(modulePath, pathLocation, importPath);
          collectResult = load(loadPath, resolved.path, resolved.location, workSet, true);
        }
        else{
          collectResult = load(loadPath, importPath, workSet, collectImports);
        }

        anImport.setImportedUnit(collectResult);

      }

    }

    long totalLoadEnd = System.currentTimeMillis();
    unit.setTotalLoadDurationMillis(totalLoadEnd-loadStart);

    return unit;

  }

  // load finding the file in load path first
  public static AnalysisUnit load(LoadPath loadPath, String modulePath, Map<String, AnalysisUnit> workSet, boolean collectImports){

    LoadPathLocation pathLocation = loadPath.pathLocationFor(modulePath);

    if (pathLocation == null){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+modulePath+" on load path");
    }

    return load(loadPath, modulePath, pathLocation, workSet, collectImports);

  }

  public static AnalysisUnit load(LoadPath loadPath, String modulePath){

    LoadPathLocation pathLocation = loadPath.pathLocationFor(modulePath);

    if (pathLocation == null){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+modulePath+" on load path");
    }

    return load(loadPath, modulePath, pathLocation, new HashMap<>(), false);

  }

  public static Map<String, AnalysisUnit> load(LoadPath loadPath, List<String> modulePaths, Map<String, AnalysisUnit> workSet, boolean collectImports){

    for (String modulePath : modulePaths) {
      load(loadPath, modulePath, workSet, collectImports);
    }

    return workSet;

  }


}
