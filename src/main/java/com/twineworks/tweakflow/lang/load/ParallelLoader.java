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
import com.twineworks.tweakflow.lang.ast.structure.ModuleHeadNode;
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
import java.util.concurrent.*;

public class ParallelLoader {

  private final LoadPath loadPath;
  public ConcurrentHashMap<String, AnalysisUnit> analysisUnits = new ConcurrentHashMap<>();
  public ConcurrentHashMap<String, ParseUnit> parseUnits = new ConcurrentHashMap<>();
  public ConcurrentHashMap<String, Throwable> errors = new ConcurrentHashMap<>();

  private final ForkJoinPool es;

  public ParallelLoader(LoadPath loadPath) {
    this.loadPath = loadPath;
    es = (ForkJoinPool) Executors.newWorkStealingPool();
  }

  public Map<String, AnalysisUnit> load(List<String> modulePaths) {

    try {
      // resolve all given paths and their imports
      for (String modulePath : modulePaths) {
        es.submit(new Resolver(modulePath));
      }

      es.awaitQuiescence(Long.MAX_VALUE, TimeUnit.SECONDS);

      // examine errors
      if (!errors.isEmpty()){
        // pick any error and rethrow
        throw LangException.wrap(errors.values().iterator().next());
      }

      // no errors, parse all units in parallel
      for (ParseUnit parseUnit : parseUnits.values()) {
        es.submit(new Loader(parseUnit));
      }

      es.awaitQuiescence(Long.MAX_VALUE, TimeUnit.SECONDS);

      // examine errors
      if (!errors.isEmpty()){
        // pick any error and rethrow
        throw LangException.wrap(errors.values().iterator().next());
      }

      // all units are loaded, link import units
      for (AnalysisUnit unit : analysisUnits.values()) {
        if (unit.getUnit() instanceof ModuleNode){
          ModuleNode m = (ModuleNode)unit.getUnit();
          for (ImportNode anImport : m.getImports()) {
            String importPath = ((StringNode) anImport.getModulePath()).getStringVal();

            String key;
            if (importPath.startsWith(".")) {
              Resolved resolved = loadPath.resolve(unit.getPath(), unit.getLocation(), importPath);
              key = resolved.location.getParseUnit(resolved.path).getPath();
            }
            else{
              key = loadPath.findParseUnit(importPath).getPath();
            }

            anImport.setImportedUnit(analysisUnits.get(key));
          }
        }
      }

      return new HashMap<>(analysisUnits);

    } finally {
      es.shutdown();
    }

  }

  private class Loader implements Callable<Boolean> {

    private final ParseUnit parseUnit;

    private Loader(ParseUnit parseUnit) {
      this.parseUnit = parseUnit;
    }

    @Override
    public Boolean call() {

      long loadStart = System.currentTimeMillis();
      String parseUnitKey = parseUnit.getPath();

      ParseResult parseResult = null;
      UnitNode unitNode;

      Map<String, ParseResult> parseResultCache = loadPath.getParseResultCache();
      if (parseResultCache != null){
        parseResult = parseResultCache.get(parseUnitKey);
      }

      if (parseResult == null){
        Parser parser = new Parser(parseUnit);
        parseResult = parser.parseUnit();

        if (!parseResult.isSuccess()){
          errors.putIfAbsent(parseUnitKey, parseResult.getException());
          return Boolean.FALSE;
        }

        if (parseResultCache != null && parseUnit.getLocation().allowsCaching()){
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
          .setLocation(parseUnit.getLocation())
          .setPath(parseUnit.getPath())
          .setUnit(unitNode)
          .setStage(AnalysisStage.PARSED)
          .setParseDurationMillis(parseResult.getParseDurationMillis())
          .setBuildDurationMillis(parseResult.getBuildDurationMillis());

      long loadEnd = System.currentTimeMillis();

      unit.setLoadDurationMillis(loadEnd-loadStart);
      unit.setTotalLoadDurationMillis(loadEnd-loadStart);

      analysisUnits.put(parseUnit.getPath(), unit);
      return Boolean.TRUE;
    }

  }

  private class Resolver implements Callable<Boolean> {

    private final String modulePath;
    private LoadPathLocation pathLocation;

    private Resolver(String modulePath) {
      this.modulePath = modulePath;
      this.pathLocation = null;
    }

    private Resolver(String modulePath, LoadPathLocation pathLocation) {
      this.modulePath = modulePath;
      this.pathLocation = pathLocation;
    }

    @Override
    public Boolean call() {
      try {

        if (pathLocation == null){
          pathLocation = loadPath.pathLocationFor(modulePath);
          if (pathLocation == null){
            throw new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+modulePath+" on load path");
          }
        }
        else {
          if (!pathLocation.pathExists(modulePath)){
            throw new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+modulePath+" on load path");
          }
        }

        ParseUnit parseUnit = pathLocation.getParseUnit(modulePath);

        String key = parseUnit.getPath();
        ParseUnit prev = parseUnits.putIfAbsent(key, parseUnit);

        // if that unit is already taken care of, don't process
        if (prev != null) return Boolean.TRUE;

        List<ImportNode> imports = null;

        // already have this unit parsed in cache?
        Map<String, ParseResult> parseResultCache = loadPath.getParseResultCache();
        if (parseResultCache != null && parseResultCache.containsKey(key)){

          UnitNode node = (UnitNode) parseResultCache.get(key).getNode();

          if (node instanceof ModuleNode){
            ModuleNode m = (ModuleNode) node;
            imports = m.getImports();
          }
          else{
            // no imports to process
            return Boolean.TRUE;
          }
        }
        else{
          // parse just the module head
          Parser parser = new Parser(parseUnit);
          ParseResult parseResult = parser.parseModuleHead();

          if (!parseResult.isSuccess()){
            throw parseResult.getException();
          }

          // find the imports
          ModuleHeadNode head = (ModuleHeadNode) parseResult.getNode();
          imports = head.getImports();

        }

        for (ImportNode anImport : imports) {
          ExpressionNode pathExp = anImport.getModulePath();

          // expect path expression to be a constant string
          if (!(pathExp instanceof StringNode)){
            throw new LangException(LangError.INVALID_IMPORT_PATH, pathExp.getSourceInfo());
          }

          String importPath = ((StringNode) pathExp).getStringVal();

          if (importPath.startsWith(".")) {
            Resolved resolved = loadPath.resolve(modulePath, pathLocation, importPath);
            es.submit(new Resolver(resolved.path, resolved.location));
          }
          else {
            es.submit(new Resolver(importPath));
          }

        }

      } catch (Throwable e) {
        errors.put(modulePath, e);
        return Boolean.FALSE;
      }

      return Boolean.TRUE;

    }

  }


}
