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

package com.twineworks.tweakflow.lang.parse.units;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;
import com.twineworks.tweakflow.util.InOut;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResourceParseUnit implements ParseUnit {

  private String programText;
  private String path;
  private String absPath;
  private LoadPathLocation location;
  private ClassLoader classLoader;

  public ResourceParseUnit(LoadPathLocation location, String path, String absPath, ClassLoader classLoader) {
    this.path = path;
    this.classLoader = classLoader;
    this.location = location;
    this.absPath = absPath;
  }

  public ResourceParseUnit(LoadPathLocation location, String path, String absPath) {
    this(location, path, absPath, ResourceParseUnit.class.getClassLoader());
  }

  public ResourceParseUnit(LoadPathLocation location, String absPath) {
    this(location, absPath, absPath, ResourceParseUnit.class.getClassLoader());
  }

  @Override
  synchronized public String getProgramText() {

    if (programText == null){

      try{
        programText = InOut.readResourceAsString(
            absPath,
            StandardCharsets.UTF_8,
            classLoader
        );

      } catch (IOException e) {
        throw new LangException(LangError.IO_ERROR, "I/O error loading "+path+" as resource.");
      }

    }

    return programText;

  }

  public String getPath() {
    return path;
  }

  @Override
  public ParseUnitType getParsingUnitType() {
    return ParseUnitType.RESOURCE;
  }

  @Override
  public LoadPathLocation getLocation() {
    return location;
  }
}
