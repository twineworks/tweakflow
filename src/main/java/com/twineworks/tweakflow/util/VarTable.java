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

package com.twineworks.tweakflow.util;

import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.DebugHandler;
import com.twineworks.tweakflow.lang.interpreter.SimpleDebugHandler;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;
import com.twineworks.tweakflow.lang.load.loadpath.MemoryLocation;
import com.twineworks.tweakflow.lang.parse.ParseResult;
import com.twineworks.tweakflow.lang.parse.Parser;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.runtime.Runtime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VarTable {

  public static class Builder implements com.twineworks.tweakflow.util.Builder<VarTable>{

    String prologue = "";
    String modulePath = "var_table_module";
    String varLibraryName = "var_table";
    LinkedHashMap<String, String> vars = new LinkedHashMap<>();
    LoadPath loadPath = new LoadPath.Builder().addStdLocation().build();

    ConcurrentHashMap<String, ParseResult> parseCache = null;
    private boolean cacheModulePath;

    DebugHandler debugHandler = new SimpleDebugHandler();


    public Builder setVarLibraryName(String varLibraryName){
      Objects.requireNonNull(varLibraryName, "varLibraryName path cannot be null");
      if (varLibraryName.isEmpty()) throw new IllegalArgumentException("varLibraryName cannot be empty");
      this.varLibraryName = varLibraryName;
      return this;
    }

    public Builder setModulePath(String modulePath){
      Objects.requireNonNull(modulePath, "module path cannot be null");
      if (modulePath.isEmpty()) throw new IllegalArgumentException("module path cannot be empty");
      this.modulePath = modulePath;
      return this;
    }

    public Builder cacheModulePath(boolean cacheModulePath){
      this.cacheModulePath = cacheModulePath;
      return this;
    }

    public Builder setDebugHandler(DebugHandler debugHandler){
      this.debugHandler = debugHandler;
      return this;
    }

    public Builder setLoadPath(LoadPath loadPath){
      Objects.requireNonNull(loadPath, "load path cannot be null");
      this.loadPath = loadPath;
      return this;
    }

    public Builder setPrologue(String prologue){
      this.prologue = prologue;
      return this;
    }

    public Builder addVar(String name, String expression){
      Objects.requireNonNull(name, "variable name cannot be null");
      if (name.isEmpty()) throw new IllegalArgumentException("variable name cannot be empty");

      if (vars.containsKey(name)){
        throw new IllegalArgumentException("var "+name+" already exists");
      }

      vars.put(name, expression);
      return this;
    }

    public Builder setParseCache(ConcurrentHashMap<String, ParseResult> cache){
      this.parseCache = cache;
      return this;
    }

    @Override
    public VarTable build() {
      return new VarTable(loadPath, modulePath, prologue, varLibraryName, vars, debugHandler, parseCache, cacheModulePath);
    }
  }

  private final String modulePath;
  private final String varLibraryName;
  private final String prologue;
  private final LinkedHashMap<String, String> vars;
  private final LoadPath loadPath;
  private final LinkedHashMap<String, Integer> varLines;
  private final LinkedHashMap<String, LangException> varParseErrors;
  private LangException prologueParseError;
  private final String moduleText;
  private final ParseUnit moduleParseUnit;
  private int lastLibraryLine;
  private DebugHandler debugHandler;

  private final ConcurrentHashMap<String, ParseResult> parseCache;

  private int lineCount(String str) {
    if (str == null || str.isEmpty()) return 0;
    int lines = 1;
    int pos = 0;
    while ((pos = str.indexOf("\n", pos) + 1) != 0) {
      lines+=1;
    }
    return lines;
  }

  private VarTable(LoadPath loadPath, String modulePath, String prologue, String varLibraryName, HashMap<String, String> vars, DebugHandler debugHandler, ConcurrentHashMap<String, ParseResult> parseCache, boolean cacheModulePath){

    this.modulePath = modulePath;
    this.prologue = prologue;
    this.varLibraryName = varLibraryName;
    this.debugHandler = debugHandler;
    this.parseCache = parseCache;

    this.vars = new LinkedHashMap<>(vars);
    this.varParseErrors = new LinkedHashMap<>();
    this.varLines = new LinkedHashMap<>();

    this.prologueParseError = prologueParseError();
    moduleText = makeModuleText();

    LoadPath.Builder loadPathBuilder = new LoadPath.Builder();
    loadPathBuilder.withParseResultCache(parseCache);

    for (LoadPathLocation loadPathLocation : loadPath.getLocations()) {
      loadPathBuilder.add(loadPathLocation);
    }

    MemoryLocation varTableLocation = new MemoryLocation.Builder()
        .add(modulePath, moduleText)
        .allowCaching(cacheModulePath)
        .build();

    this.moduleParseUnit = varTableLocation.getParseUnit(modulePath);

    this.loadPath = loadPathBuilder.add(varTableLocation).build();

  }

  public ParseUnit getModuleParseUnit() {
    return moduleParseUnit;
  }

  public LangException getPrologueParseError() {
    return prologueParseError;
  }

  public LinkedHashMap<String, LangException> getVarParseErrors() {
    return new LinkedHashMap<>(varParseErrors);
  }

  public boolean hasParseErrors(){
    return prologueParseError != null || !varParseErrors.isEmpty();
  }

  public Runtime compile() {
    return TweakFlow.compile(loadPath, modulePath, debugHandler);
  }

  public String varNameFor(SourceInfo sourceInfo){

    if (sourceInfo == null) return null;
    if (vars.size() == 0) return null;

    if (sourceInfo.getParseUnit() != moduleParseUnit) return null;
    int causeLine = sourceInfo.getLine();
    if (causeLine >= lastLibraryLine) return null;

    // source info points to var table
    ArrayList<String> varNames = new ArrayList<>(vars.keySet());
    int firstVarLine = varLines.get(varNames.get(0));
    if (firstVarLine > causeLine) return null;

    // first var before or on cause
    for (int i=varLines.size()-1;i>=0;i--){
      String varName = varNames.get(i);
      int varLine = varLines.get(varName);
      if (varLine <= causeLine) return varName;
    }

    return null;

  }

  public String getModuleText() {
    return moduleText;
  }

  public String getModulePath() {
    return modulePath;
  }

  public String getVarLibraryName() {
    return varLibraryName;
  }

  private LangException varParseException(String exp){
    // expression must parse
    ParseUnit parseUnit = new MemoryLocation.Builder()
        .add("exp", exp)
        .build()
        .getParseUnit("exp");

    ParseResult parseResult = new Parser(parseUnit).parseExpression();
    return parseResult.getException();
  }

  private LangException prologueParseError(){
    // prologue must parse as valid module
    ParseUnit parseUnit = new MemoryLocation.Builder()
        .add("prologue", prologue)
        .build()
        .getParseUnit("prologue");

    ParseResult parseResult = new Parser(parseUnit).parseUnit();
    return parseResult.getException();
  }

  private String makeModuleText(){

    StringBuilder stringBuilder = new StringBuilder();
    String prefix = "";
    if (!prologue.isEmpty()){
      prefix = prologue+"\n";
    }

    prefix = prefix+"library "+ LangUtil.escapeIdentifier(varLibraryName)+" {\n";
    stringBuilder.append(prefix);
    int currentLine = lineCount(prefix);

    for (String varName : vars.keySet()) {

      String nameLine = LangUtil.escapeIdentifier(varName)+":\n";
      stringBuilder.append(nameLine);
      currentLine += lineCount(nameLine)-1;
      String varExp = vars.get(varName);
      varLines.put(varName, currentLine);

      LangException varException = varParseException(varExp);
      if (varException != null){
        varParseErrors.put(varName, varException);
        varExp = "nil";
      }
      currentLine += lineCount(varExp);
      stringBuilder.append(varExp);

      stringBuilder.append("\n;\n\n");
      currentLine += 2;
    }
    stringBuilder.append("}\n");

    lastLibraryLine = currentLine;

    return stringBuilder.toString();
  }

  public List<String> varNames(){
    return new ArrayList<>(vars.keySet());
  }

  public String varExpression(String name){
    if (vars.containsKey(name)) return vars.get(name);
    throw new IllegalArgumentException("var "+name+" is not part of the table");
  }

  public int varExpressionLine(String name){
    if (varLines.containsKey(name)) return varLines.get(name);
    throw new IllegalArgumentException("var "+name+" is not part of the table");
  }

}
