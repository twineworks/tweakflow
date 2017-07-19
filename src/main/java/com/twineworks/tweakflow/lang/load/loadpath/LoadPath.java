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

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoadPath {

  private List<LoadPathLocation> locations = new ArrayList<>();

  public List<LoadPathLocation> getLocations() {
    return locations;
  }

  public void setLocations(List<LoadPathLocation> locations) {
    this.locations = locations;
  }

  public boolean pathExists(String path){

    Objects.requireNonNull(path);
    for (LoadPathLocation location : locations) {
      if (location.pathExists(path)) return true;
    }

    return false;
  }

  public LoadPathLocation pathLocationFor(String path){

    Objects.requireNonNull(path);
    for (LoadPathLocation location : locations) {
      if (location.pathExists(path)) return location;
    }

    return null;
  }

  public ParseUnit findParseUnit(String path){

    Objects.requireNonNull(path);

    for (LoadPathLocation location : locations) {
      if (location.pathExists(path)) return location.getParseUnit(path);
    }

    throw new LangException(LangError.CANNOT_FIND_MODULE, "Cannot find "+path);
  }

  public LoadPath addStdLocation(){
    getLocations().add(new ResourceLocation(Paths.get("com/twineworks/tweakflow/std")));
    return this;
  }

  public LoadPath addCurrentWorkingDirectory(){
    getLocations().add(new FilesystemLocation(Paths.get("")));
    return this;
  }

}
