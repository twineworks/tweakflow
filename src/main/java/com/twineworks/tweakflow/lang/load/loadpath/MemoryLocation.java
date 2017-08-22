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

package com.twineworks.tweakflow.lang.load.loadpath;

import com.twineworks.tweakflow.lang.parse.units.MemoryParseUnit;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MemoryLocation implements LoadPathLocation {

  public static class Builder implements com.twineworks.tweakflow.util.Builder<LoadPathLocation> {

    private boolean allowNativeFunctions = true;
    private Map<String, String> content = new HashMap<>();

    public Builder() { }

    @Override
    public MemoryLocation build() {
      return new MemoryLocation(allowNativeFunctions, content);
    }

    public MemoryLocation.Builder allowNativeFunctions(boolean allowNativeFunctions) {
      this.allowNativeFunctions = allowNativeFunctions;
      return this;
    }

    public MemoryLocation.Builder add(String name, String programText) {
      if (content.containsKey(name)){
        throw new IllegalArgumentException("name "+name+" is already present");
      }
      content.put(name, programText);
      return this;
    }

  }

  final private Map<String, MemoryParseUnit> parseUnits;
  final private boolean allowNativeFunctions;

  public MemoryLocation(boolean allowNativeFunctions, Map<String, String> content){
    this.allowNativeFunctions = allowNativeFunctions;
    Map<String, MemoryParseUnit> units = new HashMap<>();
    for (String name : content.keySet()) {
      units.put(name, new MemoryParseUnit(this, content.get(name), name));
    }

    this.parseUnits = Collections.unmodifiableMap(units);
  }

  public Map<String, MemoryParseUnit> getParseUnits() {
    return parseUnits;
  }

  @Override
  public boolean pathExists(String path) {
    Objects.requireNonNull(path);
    return parseUnits.containsKey(path);
  }

  @Override
  public ParseUnit getParseUnit(String path) {
    return parseUnits.get(path);
  }

  @Override
  public String resolve(String path) {
    return path;
  }

  @Override
  public boolean allowsNativeFunctions() {
    return allowNativeFunctions;
  }


}
