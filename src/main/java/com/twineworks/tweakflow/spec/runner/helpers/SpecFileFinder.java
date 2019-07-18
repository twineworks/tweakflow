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

package com.twineworks.tweakflow.spec.runner.helpers;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SpecFileFinder {

  public static ArrayList<String> findModules(List<String> patterns){
    ArrayList<String> modules = new ArrayList<>();

    try {
      for (String m : patterns) {

        Path p = Paths.get(m);
        if (Files.isDirectory(p)){ /* dir, find all spec files in it */
          ArrayList<Path> paths = SpecFileFinder.find("glob:**/*.spec.tf", p);
          for (Path path : paths) {
            modules.add(path.toString());
          }
        }
        else { /* interpret as something on the load path */
          modules.add(m);
        }

      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
    return modules;
  }

  private static ArrayList<Path> find(String glob, Path rel) throws IOException {

    final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
    final ArrayList<Path> ret = new ArrayList<>();

    Files.walkFileTree(rel, new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (pathMatcher.matches(path)) {
          ret.add(path);
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
      }
    });

    return ret;
  }

}
