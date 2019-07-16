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

package com.twineworks.tweakflow.spec.effects.helpers;

import com.twineworks.tweakflow.spec.effects.ClockEffect;
import com.twineworks.tweakflow.spec.effects.SpecEffect;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class EffectFactory {

  public static SpecEffect makeEffect(String name){
    Class<? extends SpecEffect> clazz = ensureClassLoaded(name);
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends SpecEffect> ensureClassLoaded(String name){
    Objects.requireNonNull(name, "effect class name cannot be null");
    switch(name.toLowerCase()){
      case "clock":
        return ClockEffect.class;
      default: {
        try {
          Class<?> aClass = EffectFactory.class.getClassLoader().loadClass(name);
          if (SpecEffect.class.isAssignableFrom(aClass)){
            return (Class<? extends SpecEffect>) aClass;
          }
          else{
            throw new IllegalArgumentException("Class "+name+" does not implement the SpecEffect interface");
          }
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e.getMessage(), e);
        }
      }

    }
  }

}
