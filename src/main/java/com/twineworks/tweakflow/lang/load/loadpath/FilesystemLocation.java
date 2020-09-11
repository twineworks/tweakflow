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
import com.twineworks.tweakflow.lang.parse.units.FilesystemParseUnit;
import com.twineworks.tweakflow.lang.parse.units.MemoryParseUnit;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import com.twineworks.tweakflow.lang.parse.units.transform.ParseUnitSourceTransformer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FilesystemLocation implements LoadPathLocation {

  public static class Builder implements com.twineworks.tweakflow.util.Builder<LoadPathLocation> {

    private final Path path;
    private boolean confineToPath = true;
    private boolean allowNativeFunctions = true;
    private boolean allowCaching = true;
    private String defaultExtension = ".tf";
    private ParseUnitSourceTransformer transformer;

    public Builder(Path path) {
      Objects.requireNonNull(path, "path cannot be null");
      this.path = path;
    }

    @Override
    public FilesystemLocation build() {
      return new FilesystemLocation(path, confineToPath, allowNativeFunctions, allowCaching, defaultExtension, transformer);
    }

    public Builder confineToPath(boolean confineToPath) {
      this.confineToPath = confineToPath;
      return this;
    }

    public Builder allowNativeFunctions(boolean allowNativeFunctions) {
      this.allowNativeFunctions = allowNativeFunctions;
      return this;
    }

    public Builder allowCaching(boolean allowCaching) {
      this.allowCaching = allowCaching;
      return this;
    }

    public Builder defaultExtension(String defaultExtension) {
      this.defaultExtension = defaultExtension;
      if (this.defaultExtension == null){
        this.defaultExtension = "";
      }
      return this;
    }

    public Builder withSourceTransformer(ParseUnitSourceTransformer transformer){
      this.transformer = transformer;
      return this;
    }
  }

  private final Path rootPath;
  private final Path absRootPath;
  private final boolean strict;
  private final String defaultExtension;
  private final boolean allowNativeFunctions;
  private final boolean allowCaching;
  private final ParseUnitSourceTransformer transformer;

  private FilesystemLocation(Path rootPath, boolean confineToPath, boolean allowNativeFunctions, boolean allowCaching, String defaultExtension, ParseUnitSourceTransformer transformer){
    Objects.requireNonNull(rootPath, defaultExtension);
    this.rootPath = rootPath;
    this.absRootPath = rootPath.toAbsolutePath();
    this.strict = confineToPath;
    this.defaultExtension = defaultExtension;
    this.allowNativeFunctions = allowNativeFunctions;
    this.allowCaching = allowCaching;
    this.transformer = transformer;
  }

  private FilesystemLocation(Path rootPath, boolean confineToPath, boolean allowNativeFunctions, String defaultExtension){
    this(rootPath, confineToPath, allowNativeFunctions, false, defaultExtension, null);
  }

  public Path getRootPath() {
    return rootPath;
  }

  @Override
  public boolean pathExists(String path) {

    path = resolve(path);
    return pathAccessible(path) && Paths.get(path).toFile().exists();

  }

  private boolean pathAccessible(String path){
    return !strict || path.startsWith(pathToString(absRootPath));
  }

  @Override
  public ParseUnit getParseUnit(String path) {

    path = resolve(path);
    if (!pathAccessible(path)){
      throw new LangException(LangError.CANNOT_FIND_MODULE, "cannot access path "+path+" from file system location "+absRootPath);
    }
    return new FilesystemParseUnit(this, resolve(path), transformer);
  }

  @Override
  public String resolve(String path) {

    path = applyDefaultExtension(path);

    return pathToString(rootPath.resolve(path).toAbsolutePath().normalize());
  }

  private String applyDefaultExtension(String path) {
    if (!path.endsWith(defaultExtension)){
      path = path + defaultExtension;
    }
    return path;
  }

  private String pathToString(Path path){
    return path.toString().replace('\\', '/');
  }

  @Override
  public boolean allowsNativeFunctions() {
    return allowNativeFunctions;
  }

  @Override
  public boolean allowsCaching() {
    return allowCaching;
  }

  @Override
  public ParseUnit makeRecoveryUnit(String path) {
    return MemoryParseUnit.makeRecoveryUnit(this, path);
  }

}
