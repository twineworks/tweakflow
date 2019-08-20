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

package com.twineworks.tweakflow.lang.values;

import com.twineworks.tweakflow.lang.load.user.UserFunctionDesc;
import com.twineworks.tweakflow.lang.load.user.UserObjectFactory;

public class UserFunctionValue implements FunctionValue {

  private final FunctionSignature functionSignature;
  private UserFunction userFunction;
  private final UserFunctionDesc desc;
  private final UserObjectFactory factory;

  public UserFunctionValue(FunctionSignature functionSignature, UserFunction userFunction) {
    this.functionSignature = functionSignature;
    this.userFunction = userFunction;
    this.desc = null;
    this.factory = null;
  }

  public UserFunctionValue(FunctionSignature functionSignature, UserObjectFactory factory, UserFunctionDesc desc) {
    this.functionSignature = functionSignature;
    this.userFunction = null;
    this.desc = desc;
    this.factory = factory;
  }

  @Override
  public FunctionSignature getSignature() {
    return functionSignature;
  }

  @Override
  public boolean isStandard() {
    return false;
  }

  @Override
  public boolean isUser() {
    return true;
  }

  public UserFunction getUserFunction() {
    if (userFunction == null){
      userFunction = factory.createInstance(desc);
    }
    return userFunction;
  }


}
