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

package com.twineworks.tweakflow.repl;

import com.twineworks.tweakflow.lang.analysis.Analysis;
import com.twineworks.tweakflow.lang.analysis.AnalysisResult;
import com.twineworks.tweakflow.lang.analysis.AnalysisSet;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.Loader;
import com.twineworks.tweakflow.lang.load.loadpath.*;
import com.twineworks.tweakflow.lang.parse.units.MemoryParseUnit;
import com.twineworks.tweakflow.interpreter.EvaluationResult;
import com.twineworks.tweakflow.interpreter.Interpreter;
import com.twineworks.tweakflow.interpreter.memory.Cell;
import com.twineworks.tweakflow.interpreter.memory.GlobalMemorySpace;
import com.twineworks.tweakflow.interpreter.memory.MemorySpace;
import com.twineworks.tweakflow.util.LangUtil;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReplState {

  private final String stdlibPath = "std.tf";

  // currently loaded module
  private String modulePath = stdlibPath;

  // file system load path
  private List<String> loadPath = new ArrayList<>();

  // interactive expression evaluation and variable prompt
  private final String interactivePath = "[interactive]";
  private final String promptVarName = "$";
  private String promptInput;
  private Map<String, String> varDefs = new LinkedHashMap<>();

  // memory location for interactive module
  private MemoryLocation interactiveModuleLocation;
  private Loader loader;

  // evaluation results and convenient derivatives
  private EvaluationResult evaluationResult;
  private AnalysisResult analysisResult;
  private MemorySpace moduleSpace;
  private MemorySpace interactiveSpace;

  // exit indicator
  private boolean shouldQuit = false;

  // multiLine mode indicator
  private boolean multiLine = false;

  private String input = "";

  public List<String> getLoadPath() {
    return loadPath;
  }

  public String getModulePath() {
    return modulePath;
  }

  public String getPromptVarName() {
    return promptVarName;
  }

  public ReplState setModulePath(String modulePath) {
    this.modulePath = modulePath;
    return this;
  }

  public AnalysisResult getAnalysisResult() {
    return analysisResult;
  }

  public ReplState setAnalysisResult(AnalysisResult analysisResult) {
    this.analysisResult = analysisResult;
    return this;
  }

  public long getAnalysisDurationMillis(){
    if (analysisResult == null) return 0;
    return analysisResult.getAnalysisDurationMillis();
  }

  public long getLoadDurationMillis(){
    if (analysisResult == null) return 0;
    AnalysisSet analysisSet = analysisResult.getAnalysisSet();
    if (analysisSet == null) return 0;
    AnalysisUnit analysisUnit = analysisSet.getUnits().get(getModulePath());
    if (analysisUnit == null) return 0;
    return analysisUnit.getLoadDurationMillis();
  }

  public MemoryLocation getInteractiveModuleLocation() {
    return interactiveModuleLocation;
  }

  public ReplState setInteractiveModuleLocation(MemoryLocation interactiveModuleLocation) {
    this.interactiveModuleLocation = interactiveModuleLocation;
    return this;
  }

  public Loader getLoader() {
    return loader;
  }

  public ReplState setLoader(Loader loader) {
    this.loader = loader;
    return this;
  }

  public String getInteractivePath() {
    return interactivePath;
  }

  public String getStdlibPath() {
    return stdlibPath;
  }

  public Map<String, String> getVarDefs() {
    return varDefs;
  }

  public EvaluationResult getEvaluationResult() {
    return evaluationResult;
  }

  public ReplState setEvaluationResult(EvaluationResult evaluationResult) {
    this.evaluationResult = evaluationResult;
    return this;
  }

  public boolean shouldQuit() {
    return shouldQuit;
  }

  public ReplState setShouldQuit(boolean shouldQuit) {
    this.shouldQuit = shouldQuit;
    return this;
  }

  public Cell getInteractiveSpace(){
    return evaluationResult
        .getRuntimeSet()
        .getGlobalMemorySpace()
        .getUnitSpace()
        .getCells()
        .gets(getInteractivePath())
        .getCells()
        .gets(getModulePath());
  }

  public Cell getModuleSpace(){
    return evaluationResult
        .getRuntimeSet()
        .getGlobalMemorySpace()
        .getUnitSpace()
        .getCells()
        .gets(getModulePath());
  }

  public MemorySpace getUnitSpace(){
    return evaluationResult
        .getRuntimeSet()
        .getGlobalMemorySpace()
        .getUnitSpace();
  }

  public MemorySpace getExportSpace(){
    return evaluationResult
        .getRuntimeSet()
        .getGlobalMemorySpace()
        .getExportSpace();
  }

  public GlobalMemorySpace getGlobalSpace(){
    return evaluationResult
        .getRuntimeSet()
        .getGlobalMemorySpace();
  }

  public ReplState copy() {
    ReplState copy = new ReplState()
        .setShouldQuit(shouldQuit)
        .setAnalysisResult(analysisResult)
        .setEvaluationResult(evaluationResult)
        .setLoader(loader)
        .setInteractiveModuleLocation(interactiveModuleLocation)
        .setModulePath(modulePath)
        .setMultiLine(multiLine)
        .setPromptInput(promptInput);


    copy.varDefs.putAll(varDefs);
    copy.loadPath.addAll(loadPath);
    return copy;
  }

  private void supplyLoader(){

    LoadPath lp = new LoadPath();
    List<LoadPathLocation> locations = lp.getLocations();

    // std resources are first
    locations.add(new ResourceLocation(Paths.get("com/twineworks/tweakflow/std")));

    // the memory location for the interactive module
    MemoryLocation interactiveLocation = new MemoryLocation();
    setInteractiveModuleLocation(interactiveLocation);
    locations.add(interactiveLocation);

    // all file system loading locations mentioned in state
    for (String s : getLoadPath()) {
      locations.add(new FilesystemLocation(Paths.get(s)));
    }

    setLoader(new Loader(lp));
    modulePath = loader.getLoadPath().findParseUnit(modulePath).getPath();

  }

  private void supplyInteractiveUnit(){

    StringBuilder builder = new StringBuilder(100);

    // interactive unit for scope of loaded module
    builder
        .append("interactive\n")
        .append("  in_scope ").append(LangUtil.escapeIdentifier(modulePath)).append("\n");

    // add defined variables
    Map<String, String> varDefs = getVarDefs();

    for (String var : varDefs.keySet()) {
      builder
          .append("    ")
          .append(varDefs.get(var))
          .append(";")
          .append("\n");
    }

    // add input expression, if any
    if (getPromptInput() != null){
      builder
          .append("    ")
          .append(LangUtil.escapeIdentifier(getPromptVarName())).append(": ").append(getPromptInput())
          .append(";")
          .append("\n");
    }

    String programText = builder.toString();

    MemoryLocation location = getInteractiveModuleLocation();
    MemoryParseUnit interactiveUnit = new MemoryParseUnit(location, programText, getInteractivePath());

    location.getParseUnits().clear();
    location.getParseUnits().put(getInteractivePath(), interactiveUnit);

  }

  public void evaluate(){

    try {

      supplyLoader();
      supplyInteractiveUnit();

    } catch (LangException e){
      analysisResult = null;
      setEvaluationResult(EvaluationResult.error(e));
      return;
    }

    List<String> pathList = new ArrayList<>();
    pathList.add(getStdlibPath());
    pathList.add(getModulePath());
    pathList.add(getInteractivePath());

    // compile
    analysisResult = Analysis.analyze(pathList, getLoader());
    if (analysisResult.isError()){
      // compilation went wrong, wrap as evaluation result
      setEvaluationResult(EvaluationResult.error(LangException.wrap(analysisResult.getException())));
    }
    else {
      // compilation was fine, evaluate and set result
      Interpreter interpreter = new Interpreter(analysisResult.getAnalysisSet());
      setEvaluationResult(interpreter.evaluate());
    }

  }

  public String getPromptInput() {
    return promptInput;
  }

  public ReplState setPromptInput(String promptInput) {
    this.promptInput = promptInput;
    return this;
  }

  public boolean isMultiLine() {
    return multiLine;
  }

  public ReplState setMultiLine(boolean multiLine) {
    this.multiLine = multiLine;
    return this;
  }

  public String getInput() {
    return input;
  }

  public ReplState setInput(String input) {
    this.input = input;
    return this;
  }

  public ReplState addInputLine(String line){
    if (input.isEmpty()){
      this.input = line;
    }
    else{
      this.input += "\n"+line;
    }
    return this;
  }
}

