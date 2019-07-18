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

package com.twineworks.tweakflow.repl;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.analysis.AnalysisSet;
import com.twineworks.tweakflow.lang.analysis.AnalysisUnit;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.EvaluationResult;
import com.twineworks.tweakflow.lang.load.loadpath.FilesystemLocation;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.util.LangUtil;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReplState {

  private ConcurrentHashMap<String, ParseResult> parseCache = new ConcurrentHashMap<>();
  private final String stdlibPath = "std.tf";

  // currently loaded module
  private String mainModulePath = stdlibPath;
  private List<String> modulePaths = Collections.singletonList(mainModulePath);
  private String mainModuleKey = mainModulePath;

  // file system load path
  private List<String> loadPathElements = new ArrayList<>();
  // resources load path
  private List<String> resourceLoadPathElements = new ArrayList<>();

  // interactive expression evaluation and variable prompt
  private final String interactivePath = "[interactive]";
  private final String promptVarName = "$";
  private final Map<String, String> varDefs = new LinkedHashMap<>();
  private String promptInput;

  private LoadPath loadPath;

  // evaluation results and convenient derivatives
  private EvaluationResult evaluationResult;
  private Runtime runtime;

  // exit indicator
  private boolean shouldQuit = false;

  // multiLine mode indicator
  private boolean multiLine = false;

  private String input = "";

  public LoadPath getLoadPath() {
    return loadPath;
  }

  public ReplState setLoadPath(LoadPath loadPath) {
    this.loadPath = loadPath;
    return this;
  }

  public List<String> getLoadPathElements() {
    return loadPathElements;
  }

  public String getMainModulePath() {
    return mainModulePath;
  }

  public String getMainModuleKey() {
    return mainModuleKey;
  }

  public Runtime getRuntime(){
    return runtime;
  }

  public ReplState setRuntime(Runtime runtime){
    this.runtime = runtime;
    return this;
  }

  private ReplState setMainModuleKey(String mainModuleKey) {
    this.mainModuleKey = mainModuleKey;
    return this;
  }

  public String getPromptVarName() {
    return promptVarName;
  }

  public ReplState setModulePaths(List<String> modulePaths){
    Objects.requireNonNull(modulePaths);
    if (modulePaths.size() == 0) throw new IllegalArgumentException("modulePaths cannot be empty");
    this.modulePaths = modulePaths;
    mainModulePath = modulePaths.get(0);
    return this;
  }

  public long getAnalysisDurationMillis(){
    if (runtime == null) return 0;
    return runtime.getAnalysisResult().getAnalysisDurationMillis();
  }

  public long getLoadDurationMillis(){
    if (runtime == null) return 0;
    AnalysisSet analysisSet = runtime.getAnalysisResult().getAnalysisSet();
    long loadDuration = 0;
    for (AnalysisUnit analysisUnit : analysisSet.getUnits().values()) {
      loadDuration += analysisUnit.getLoadDurationMillis();
    }
    return loadDuration;
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

  public Runtime.Module getMainModule(){
    return runtime.getModules().get(mainModuleKey);
  }

  public Runtime.InteractiveUnit getInteractiveUnit(){
    return (Runtime.InteractiveUnit) runtime.getUnits().getChildren().get(getInteractivePath());
  }

  public Runtime.InteractiveSection getInteractiveSection() {
    return getInteractiveUnit().getSection(mainModuleKey);
  }

  public Runtime.Globals getGlobals(){
    return runtime.getGlobals();
  }

  public Runtime.Units getUnits(){
    return runtime.getUnits();
  }

  public Runtime.Exports getExports(){
    return runtime.getExports();
  }

  public ReplState copy() {
    ReplState copy = new ReplState()
        .setShouldQuit(shouldQuit)
        .setLoadPath(loadPath)
        .setRuntime(runtime)
        .setEvaluationResult(evaluationResult)
        .setModulePaths(new ArrayList<>(modulePaths))
        .setMultiLine(multiLine)
        .setPromptInput(promptInput)
        .setMainModuleKey(mainModuleKey);

    copy.varDefs.putAll(varDefs);
    copy.loadPathElements.addAll(loadPathElements);
    copy.resourceLoadPathElements.addAll(resourceLoadPathElements);
    copy.parseCache.putAll(parseCache);
    return copy;
  }

  public List<String> getResourceLoadPathElements() {
    return resourceLoadPathElements;
  }

  private LoadPath.Builder nonInteractiveLoadPath(){
    LoadPath.Builder loadPathBuilder = new LoadPath.Builder();

    // std resources are first
    loadPathBuilder.add(new ResourceLocation.Builder()
        .allowCaching(true)
        .allowNativeFunctions(true)
        .path(Paths.get("com/twineworks/tweakflow/std"))
        .build()
    );

    // add resource paths
    for (String s : getResourceLoadPathElements()) {
      loadPathBuilder.add(new ResourceLocation.Builder()
          .allowCaching(true)
          .allowNativeFunctions(true)
          .path(Paths.get(s))
          .build());
    }

    // all file-system locations not cached

    // default if none given
    if (getLoadPathElements().size() == 0){
      loadPathBuilder.add(new FilesystemLocation.Builder(Paths.get(".").toAbsolutePath())
          .confineToPath(false)
          .allowCaching(false)
          .allowNativeFunctions(true)
          .build());
    }

    // all file system loading locations mentioned in state
    for (String s : getLoadPathElements()) {
      FilesystemLocation location = new FilesystemLocation.Builder(Paths.get(s))
          .allowCaching(false)
          .confineToPath(true)
          .allowNativeFunctions(true)
          .build();
      loadPathBuilder.add(location);
    }

    return loadPathBuilder;
  }

  private LoadPath makeLoadPath(){

    LoadPath.Builder loadPathBuilder = nonInteractiveLoadPath();
    LoadPath nonInteractive = loadPathBuilder.build();

    // resolved path is used as a key in data structures
    // needed for interactive section to specify scope
    mainModuleKey = nonInteractive.findParseUnit(mainModulePath).getPath();

    // the memory location for the interactive module
    MemoryLocation interactiveLocation = new MemoryLocation.Builder()
        .add(getInteractivePath(), buildInteractiveProgramText())
        .build();

    loadPathBuilder.add(interactiveLocation);

    loadPathBuilder.withParseResultCache(parseCache);
    return loadPathBuilder.build();

  }

  private String buildInteractiveProgramText(){

    StringBuilder builder = new StringBuilder(100);

    // interactive unit for scope of loaded module
    builder
        .append("interactive\n")
        .append("  in_scope ").append(LangUtil.escapeIdentifier(mainModuleKey)).append(" {\n");

    // add defined variables
    Map<String, String> varDefs = getVarDefs();

    for (String var : varDefs.keySet()) {
      builder
//          .append("    ")
          .append(varDefs.get(var))
          .append("\n")
          .append(";")
          .append("\n");
    }

    // add input expression, if any
    if (getPromptInput() != null){
      builder
//          .append("    ")
          .append(LangUtil.escapeIdentifier(getPromptVarName())).append(": ").append(getPromptInput())
          .append("\n")
          .append(";")
          .append("\n");
    }

    builder.append("}");
    return builder.toString();

  }

  public void evaluate(){

    runtime = null;

    try {
      setLoadPath(makeLoadPath());

      List<String> paths = new ArrayList<>();
      paths.addAll(modulePaths);
      paths.add(getInteractivePath());

      runtime = TweakFlow.compile(getLoadPath(), paths);
      runtime.evaluate();
      setEvaluationResult(EvaluationResult.ok());

    } catch (LangException e){
      setEvaluationResult(EvaluationResult.error(e));
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

