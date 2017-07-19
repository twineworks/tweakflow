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

package com.twineworks.tweakflow.lang.load.user;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.UserFunction;
import com.twineworks.tweakflow.lang.values.Value;

public class UserFunctionDesc  {

  private final String className;
  private Class<UserFunction> clazz;

  public UserFunctionDesc(Value via) {

    if (via.type() != Types.DICT){
      throw new LangException(LangError.CAST_ERROR, "via expression must be a map");
    }

    DictValue viaConf = via.dict();

    Value classSpec = viaConf.get("class");
    if (classSpec == null || classSpec.isNil()){
      throw new LangException(LangError.USER_CLASS_SPEC_MISSING, "via expression requires class key");
    }

    if (!classSpec.isString()){
      throw new LangException(LangError.CAST_ERROR, "class must be a string");
    }

    this.className = classSpec.string();

  }

  public UserFunctionDesc(String className) {
    this.className = className;
  }

  public String getClassName() {
    return className;
  }

  public Class<UserFunction> getClazz() {
    return clazz;
  }

  public void setClazz(Class<UserFunction> clazz) {
    this.clazz = clazz;
  }
}
