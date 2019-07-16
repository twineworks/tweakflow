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

package com.twineworks.tweakflow.spec.reporter.helpers;

import com.twineworks.tweakflow.spec.reporter.DocSpecReporter;
import com.twineworks.tweakflow.spec.reporter.DotSpecReporter;
import com.twineworks.tweakflow.spec.reporter.SpecReporter;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

public class ReporterFactory {

  public static SpecReporter makeReporter(String name, Map<String, String> options){
    Class<? extends SpecReporter> clazz = ensureClassLoaded(name);
    try {
      SpecReporter reporter = clazz.getDeclaredConstructor().newInstance();
      reporter.setOptions(options);
      return reporter;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends SpecReporter> ensureClassLoaded(String name){
    Objects.requireNonNull(name, "reporter class name cannot be null");
    switch(name.toLowerCase()){
      case "doc":
        return DocSpecReporter.class;
      case "dot":
        return DotSpecReporter.class;
      default: {
        try {
          Class<?> aClass = ReporterFactory.class.getClassLoader().loadClass(name);
          if (SpecReporter.class.isAssignableFrom(aClass)){
            return (Class<? extends SpecReporter>) aClass;
          }
          else{
            throw new IllegalArgumentException("Class "+name+" does not implement the SpecReporter interface");
          }
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      }

    }
  }

}
