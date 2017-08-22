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
import com.twineworks.tweakflow.lang.load.user.UserObjectFactory;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import com.twineworks.tweakflow.util.InOut;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceLocation implements LoadPathLocation {

  private final Path rootPath;
  private final UserObjectFactory userObjectFactory;
  private final static Path BASE_PATH = Paths.get("");
  private final String defaultExtension;
  private final boolean allowNativeFunctions;


  public ResourceLocation() {
    this(BASE_PATH, true, ".tf");
  }
  public ResourceLocation(Path rootPath) {
    this(rootPath, true, ".tf");
  }


  public ResourceLocation(Path rootPath, String defaultExtension){
    this(rootPath, true, defaultExtension);
  }

  public ResourceLocation(Path rootPath, boolean allowNativeFunctions, String defaultExtension){
    this.rootPath = rootPath;
    this.defaultExtension = defaultExtension;
    this.allowNativeFunctions = allowNativeFunctions;
    this.userObjectFactory =  allowNativeFunctions ? new UserObjectFactory() : null;
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

    if (rootPath.equals(BASE_PATH)){
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
    if (rootPath.equals(BASE_PATH)){
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
