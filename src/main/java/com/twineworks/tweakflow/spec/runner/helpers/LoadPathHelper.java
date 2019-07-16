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

import com.twineworks.tweakflow.lang.load.loadpath.FilesystemLocation;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.spec.runner.LoadPathOptions;

import java.nio.file.Paths;

public class LoadPathHelper {

  private final LoadPathOptions loadPathOptions;

  public LoadPathHelper(LoadPathOptions loadPathOptions) {
    this.loadPathOptions = loadPathOptions;
  }

  public LoadPath build(){
    LoadPath.Builder builder = new LoadPath.Builder().addStdLocation();

    // add resource locations to loadpath
    for (String loc : loadPathOptions.resourceLoadPath) {
      ResourceLocation location = new ResourceLocation.Builder()
          .path(Paths.get(loc))
          .allowCaching(true)
          .allowNativeFunctions(true)
          .build();

      builder.add(location);
    }

    // add regular locations to loadpath
    if (loadPathOptions.loadPath.size() == 0) {
      // default load path
      builder.addCurrentWorkingDirectory();
    } else {
      // custom load path
      for (String loc : loadPathOptions.loadPath) {
        FilesystemLocation location = new FilesystemLocation.Builder(Paths.get(loc))
            .allowCaching(true)
            .allowNativeFunctions(true)
            .confineToPath(false)
            .build();
        builder.add(location);
      }
    }

    return builder.build();
  }


}
