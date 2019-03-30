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

package com.twineworks.tweakflow.lang.load.loadpath;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import com.twineworks.tweakflow.util.InOut;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class ResourceLocation implements LoadPathLocation {

  public static class Builder implements com.twineworks.tweakflow.util.Builder<LoadPathLocation> {

    private Path path = ROOT_PATH;
    private boolean allowNativeFunctions = true;
    private String defaultExtension = ".tf";

    public Builder() {
    }

    @Override
    public ResourceLocation build() {
      return new ResourceLocation(path, allowNativeFunctions, defaultExtension);
    }

    public ResourceLocation.Builder path(Path path){
      Objects.requireNonNull(path, "path cannot be null");
      this.path = path;
      return this;
    }

    public ResourceLocation.Builder allowNativeFunctions(boolean allowNativeFunctions) {
      this.allowNativeFunctions = allowNativeFunctions;
      return this;
    }

    public ResourceLocation.Builder defaultExtension(String defaultExtension) {
      this.defaultExtension = defaultExtension;
      if (this.defaultExtension == null){
        this.defaultExtension = "";
      }
      return this;
    }
  }

  private final Path rootPath;
  private final static Path ROOT_PATH = Paths.get("");
  private final String defaultExtension;
  private final boolean allowNativeFunctions;

  public ResourceLocation(Path rootPath, boolean allowNativeFunctions, String defaultExtension){
    this.rootPath = rootPath;
    this.defaultExtension = defaultExtension;
    this.allowNativeFunctions = allowNativeFunctions;
  }

  public Path getRootPath() {
    return rootPath;
  }

  @Override
  public boolean pathExists(String path) {

    path = applyDefaultExtension(path);
    path = pathToString(rootPath.resolve(path).normalize());
    return pathAccessible(path) && InOut.resourceExists(path);
  }

  private boolean pathAccessible(String path){
    return path.startsWith(pathToString(rootPath));
  }

  @Override
  public ParseUnit getParseUnit(String path) {

    path = applyDefaultExtension(path);

    Path normalized = rootPath.resolve(path).normalize();
    Path relative;

    if (rootPath.equals(ROOT_PATH)){
      relative = normalized;
    }
    else{
      relative = rootPath.relativize(normalized).normalize();
    }

    if (!pathAccessible(pathToString(normalized))){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "cannot access path "+path+" from resource location "+pathToString(rootPath));
    }

    return new ResourceParseUnit(this, pathToString(relative), pathToString(normalized), this.getClass().getClassLoader());
  }

  @Override
  public String resolve(String path) {

    path = applyDefaultExtension(path);

    Path normalized = rootPath.resolve(path).normalize();

    Path relative;
    if (rootPath.equals(ROOT_PATH)){
      relative = normalized;
    }
    else{
      relative = rootPath.relativize(normalized);
    }

    return pathToString(relative);
  }

  private String applyDefaultExtension(String path) {
    if (!path.endsWith(defaultExtension)){
      path = path + defaultExtension;
    }
    return path;
  }

  @Override
  public boolean allowsNativeFunctions() {
    return allowNativeFunctions;
  }


  private String pathToString(Path path){
    return path.toString().replace('\\', '/');
  }

}
