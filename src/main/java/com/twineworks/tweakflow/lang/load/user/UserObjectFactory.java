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
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.values.*;

public class UserObjectFactory {

  public FunctionValue createUserFunction(FunctionSignature functionSignature, Value viaConf) {
    UserFunctionDesc desc = new UserFunctionDesc(viaConf);
    ensureClassLoaded(desc);
    return new UserFunctionValue(functionSignature, createInstance(desc));
  }

  private UserFunction createInstance(UserFunctionDesc desc) {
    ensureClassLoaded(desc);
    Class<UserFunction> clazz = desc.getClazz();
    try {
      return clazz.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private void ensureClassLoaded(UserFunctionDesc desc){

    if (desc.getClazz() != null) return;

    try {
      // TODO: user classloader support in desc
      Class<?> aClass = this.getClass().getClassLoader().loadClass(desc.getClassName());
      if (UserFunction.class.isAssignableFrom(aClass)){
        desc.setClazz((Class<UserFunction>) aClass);
      }
      else{
        throw new LangException(LangError.USER_CLASS_INCOMPATIBLE, "user function classes must implement the UserFunction interface");
      }

    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}
