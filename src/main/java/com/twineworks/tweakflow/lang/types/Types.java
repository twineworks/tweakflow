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

package com.twineworks.tweakflow.lang.types;

import java.util.HashMap;
import java.util.Map;

final public class Types {

  public static final VoidType VOID = new VoidType();
  public static final AnyType ANY = new AnyType();
  public static final BooleanType BOOLEAN = new BooleanType();
  public static final BinaryType BINARY = new BinaryType();
  public static final StringType STRING = new StringType();
  public static final LongType LONG = new LongType();
  public static final ListType LIST = new ListType();
  public static final DictType DICT = new DictType();
  public static final DoubleType DOUBLE = new DoubleType();
  public static final DateTimeType DATETIME = new DateTimeType();
  public static final FunctionType FUNCTION = new FunctionType();

  public static final Map<String, Type> byName;

  static {
    byName = new HashMap<>();
    byName.put(VOID.name(), VOID);
    byName.put(ANY.name(), ANY);
    byName.put(STRING.name(), STRING);
    byName.put(LONG.name(), LONG);
    byName.put(BOOLEAN.name(), BOOLEAN);
    byName.put(BINARY.name(), BINARY);
    byName.put(LIST.name(), LIST);
    byName.put(DICT.name(), DICT);
    byName.put(FUNCTION.name(), FUNCTION);
    byName.put(DOUBLE.name(), DOUBLE);
    byName.put(DATETIME.name(), DATETIME);
  }

  public static Type byName(String name) {
    Type t = byName.get(name);
    if (t == null){
      throw new IllegalArgumentException("Unknown type "+name);
    }
    return t;
  }

}
