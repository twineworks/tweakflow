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

  private static AnalysisUnit load(
      LoadPath loadPath,
      String modulePath,
      LoadPathLocation pathLocation,
      Map<String, AnalysisUnit> workSet,
      boolean collectImports,
      boolean recovery,
      List<LangException> recoveryErrors
  ){

    long loadStart = System.currentTimeMillis();

    ParseUnit parseUnit;
    if (!pathLocation.pathExists(modulePath)){
      LangException e = new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+modulePath+" on load path");
      if (recovery){
        recoveryErrors.add(e);
        parseUnit = pathLocation.makeRecoveryUnit(modulePath);
      }
      else{
        throw e;
      }
    }
    else{
      parseUnit = pathLocation.getParseUnit(modulePath);
    }


    String parseUnitKey = parseUnit.getPath();

    // already done?
    if (workSet.containsKey(parseUnitKey)){
      return workSet.get(parseUnitKey);
    }

    ParseResult parseResult = null;
    UnitNode unitNode;

    Map<String, ParseResult> parseResultCache = loadPath.getParseResultCache();
    if (parseResultCache != null){
      parseResult = parseResultCache.get(parseUnitKey);
    }

    if (parseResult == null){
      Parser parser = new Parser(parseUnit, recovery);
      parseResult = parser.parseUnit();
      if (!parseResult.isSuccess()){
        throw parseResult.getException();
      }

      if (recovery && recoveryErrors != null){
        recoveryErrors.addAll(parseResult.getRecoveryErrors());
      }

      if (parseResultCache != null && pathLocation.allowsCaching()){
        parseResultCache.put(parseUnitKey, parseResult);
        // cached the result, need to work with a copy of the parsed node
        unitNode = (UnitNode) parseResult.getNode().copy();
      }
      else{
        // not caching the result, use the node directly
        unitNode = (UnitNode) parseResult.getNode();
      }
    }
    else{
      // found a result in cache, work with a copy of the parsed node
      unitNode = (UnitNode) parseResult.getNode().copy();
    }

    AnalysisUnit unit = new AnalysisUnit()
        .setLocation(pathLocation)
        .setPath(parseUnit.getPath())
        .setUnit(unitNode)
        .setStage(AnalysisStage.PARSED)
        .setParseDurationMillis(parseResult.getParseDurationMillis())
        .setBuildDurationMillis(parseResult.getBuildDurationMillis());

    workSet.put(parseUnitKey, unit);

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
          collectResult = load(loadPath, resolved.path, resolved.location, workSet, true, recovery, recoveryErrors);
        }
        else{
          collectResult = load(loadPath, importPath, workSet, collectImports, recovery, recoveryErrors);
        }

        anImport.setImportedUnit(collectResult);

      }

    }

    long totalLoadEnd = System.currentTimeMillis();
    unit.setTotalLoadDurationMillis(totalLoadEnd-loadStart);

    return unit;

  }


  // load finding the file in load path first
  public static AnalysisUnit load(LoadPath loadPath, String modulePath, Map<String, AnalysisUnit> workSet, boolean collectImports, boolean recovery, List<LangException> recoveryErrors){

    LoadPathLocation pathLocation = loadPath.pathLocationFor(modulePath);

    if (pathLocation == null){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+modulePath+" on load path");
    }

    return load(loadPath, modulePath, pathLocation, workSet, collectImports, recovery, recoveryErrors);

  }

  public static AnalysisUnit load(LoadPath loadPath, String modulePath, Map<String, AnalysisUnit> workSet, boolean collectImports){
    return load(loadPath, modulePath, workSet, collectImports, false, null);
  }

  public static AnalysisUnit load(LoadPath loadPath, String modulePath, boolean recovery, List<LangException> recoveryErrors){

    LoadPathLocation pathLocation = loadPath.pathLocationFor(modulePath);

    if (pathLocation == null){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+modulePath+" on load path");
    }

    return load(loadPath, modulePath, pathLocation, new HashMap<>(), false, recovery, recoveryErrors);

  }

  public static AnalysisUnit load(LoadPath loadPath, String modulePath){
    return load(loadPath, modulePath, false, null);
  }

  public static Map<String, AnalysisUnit> load(LoadPath loadPath, List<String> modulePaths, Map<String, AnalysisUnit> workSet, boolean collectImports, boolean recovery, List<LangException> recoveryErrors){

    for (String modulePath : modulePaths) {
      load(loadPath, modulePath, workSet, collectImports, recovery, recoveryErrors);
    }

    return workSet;

  }

  public static Map<String, AnalysisUnit> load(LoadPath loadPath, List<String> modulePaths, Map<String, AnalysisUnit> workSet, boolean collectImports){
    return load(loadPath, modulePaths, workSet, collectImports, false, null);
  }


}
