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
import com.twineworks.tweakflow.lang.load.relative.DefaultResolver;
import com.twineworks.tweakflow.lang.load.relative.RelativeResolver;
import com.twineworks.tweakflow.lang.load.relative.Resolved;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LoadPath {

  public static class Builder implements com.twineworks.tweakflow.util.Builder<LoadPath> {

    private final List<LoadPathLocation> locations = new ArrayList<>();
    private RelativeResolver relativeResolver = new DefaultResolver();

    public Builder() { }

    @Override
    public LoadPath build() {
      return new LoadPath(locations, relativeResolver);
    }

    public LoadPath.Builder add(LoadPathLocation location){
      locations.add(location);
      return this;
    }

    public LoadPath.Builder withRelativeResolver(RelativeResolver relativeResolver){
      this.relativeResolver = relativeResolver;
      return this;
    }

    public LoadPath.Builder addStdLocation(){
      locations.add(
          new ResourceLocation.Builder()
              .path(Paths.get("com/twineworks/tweakflow/std"))
              .build()
      );
      return this;
    }

    public LoadPath.Builder addCurrentWorkingDirectory(){
      locations.add(
          new FilesystemLocation.Builder(Paths.get(".").toAbsolutePath())
              .confineToPath(false)
              .allowNativeFunctions(true)
              .build()
      );
      return this;
    }
  }

  private final List<LoadPathLocation> locations;
  private final RelativeResolver relativeResolver;

  private LoadPath(List<LoadPathLocation> locations){
    this.locations = Collections.unmodifiableList(locations);
    this.relativeResolver = new DefaultResolver();
  }

  private LoadPath(List<LoadPathLocation> locations, RelativeResolver relativeResolver){
    this.locations = Collections.unmodifiableList(locations);
    this.relativeResolver = relativeResolver;
  }

  public Resolved resolve(String modulePath, LoadPathLocation pathLocation, String importPath) {
    return relativeResolver.resolve(this, modulePath, pathLocation, importPath);
  }

  public List<LoadPathLocation> getLocations() {
    return locations;
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


}
