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

package com.twineworks.tweakflow.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class InOut {

  public static final int KiB = 1024;
  public static final int MiB = 1024*1024;
  public static final int GiB = 1024*1024*1024;

  public static String readResourceAsString(String location, Charset charset, ClassLoader classLoader) throws IOException {

    try(
        InputStream resourceStream = new BufferedInputStream(
            classLoader.getResourceAsStream(location)
        )
    ){

      ByteArrayOutputStream out = new ByteArrayOutputStream(32*KiB);
      byte[] buffer = new byte[32*KiB];

      int bytesRead;
      while ((bytesRead = resourceStream.read(buffer)) != -1){
        out.write(buffer, 0, bytesRead);
      }

      return out.toString(charset.name());

    }

  }

  public static boolean resourceExists(String location, ClassLoader classLoader){
    return (classLoader.getResource(location) != null);
  }

  public static boolean resourceExists(String location){
    return resourceExists(location, InOut.class.getClassLoader());
  }

  public static String readResourceAsString(String location, Charset charset) throws IOException {
    return readResourceAsString(location, charset, InOut.class.getClassLoader());
  }

}
