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

package com.twineworks.util;

import com.twineworks.collections.shapemap.ConstShapeMap;
import org.assertj.core.api.AbstractAssert;

public class ShapeMapAssert<T>  extends AbstractAssert<ShapeMapAssert<T>, ConstShapeMap> {

  public ShapeMapAssert(ConstShapeMap<T> actual) {
    super(actual, ShapeMapAssert.class);
  }

  public static <W> ShapeMapAssert<W> assertThat(ConstShapeMap<W> actual){
    return new ShapeMapAssert<>(actual);
  }

  @SafeVarargs
  public final ShapeMapAssert<T> containsOnlyKeys(T... keys){

    isNotNull();

    if (actual.size() != keys.length){
      failWithMessage("actual keys: "+actual+" do not match expected keys. Key set size is different.");
    }

    for (T key : keys) {
      if (!actual.containsKey(key)){
        failWithMessage("actual: "+actual+" does not have expected key: "+key);
      }
    }

    return this;
  }

  @SafeVarargs
  public final ShapeMapAssert<T> containsKeys(T... keys){

    isNotNull();

    for (T key : keys) {
      if (!actual.containsKey(key)){
        failWithMessage("actual: "+actual+" does not have expected key: "+key);
      }
    }

    return this;
  }

  public final ShapeMapAssert<T> containsOnlyStrKeys(String ... keys){

    isNotNull();

    if (actual.size() != keys.length){
      failWithMessage("actual keys: "+actual+" do not match expected keys. Key set size is different.");
    }

    for (String key : keys) {
      if (!actual.containsStrKey(key)){
        failWithMessage("actual: "+actual+" does not have expected key: "+key);
      }
    }

    return this;
  }

  public final ShapeMapAssert<T> containsStrKeys(String ... keys){

    isNotNull();

    for (String key : keys) {
      if (!actual.containsStrKey(key)){
        failWithMessage("actual keys: "+actual+" does not have expected key: "+key);
      }
    }

    return this;
  }

  public final ShapeMapAssert<T> isEmpty(){

    isNotNull();

    if (actual.size() != 0){
      failWithMessage("actual "+actual+" is not empty");
    }

    return this;
  }


}
