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

import com.twineworks.tweakflow.lang.load.user.UserObjectFactory;
import com.twineworks.tweakflow.lang.parse.units.FilesystemParseUnit;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesystemLocation implements LoadPathLocation {

  private final Path rootPath;
  private final UserObjectFactory userObjectFactory = new UserObjectFactory();

  public FilesystemLocation(Path rootPath) {
    this.rootPath = rootPath;
  }

  public Path getRootPath() {
    return rootPath;
  }

  @Override
  public boolean pathExists(String path) {

    path = resolve(path);
    // TODO: ensure resolved path is still within root path
    return Paths.get(path).toFile().exists();

  }

  @Override
  public ParseUnit getParseUnit(String path) {
    // TODO: ensure resolved path still starts with root path
    return new FilesystemParseUnit(this, resolve(path));
  }

  @Override
  public String resolve(String path) {

    if (!path.endsWith(".tf")){
      path = path + ".tf";
    }

    return rootPath.resolve(path).toAbsolutePath().normalize().toString().replace('\\','/');
  }

  @Override
  public UserObjectFactory getUserObjectFactory() {
    return userObjectFactory;
  }

}
