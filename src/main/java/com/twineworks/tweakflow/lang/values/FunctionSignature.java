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

package com.twineworks.tweakflow.lang.values;

import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;

import java.util.*;

public class FunctionSignature {

  private final Map<String, FunctionParameter> parameterMap = new HashMap<>();
  private final List<FunctionParameter> parameterList = new ArrayList<>();
  private final FunctionParameter[] parameterArray;
  private final ConstShapeMap<Cell> parameterShapeMap;
  private final Type returnType;
  private Value value;

  public FunctionSignature(Map<String, FunctionParameter> parameterMap, Type returnType) {

    this.parameterMap.putAll(parameterMap);
    // add everything to ensure size
    parameterList.addAll(parameterMap.values());
    // ensure positions are good
    for (FunctionParameter parameter : parameterMap.values()) {
      parameterList.set(parameter.getIndex(), parameter);
    }

    parameterArray = parameterList.toArray(new FunctionParameter[parameterList.size()]);
    this.returnType = returnType;

    Set<ShapeKey> keySet = ShapeKey.getAll(parameterMap.keySet());
    parameterShapeMap = new ConstShapeMap<>(keySet);

  }

  public FunctionSignature(List<FunctionParameter> parameterList, Type returnType) {
    this.parameterList.addAll(parameterList);
    for (FunctionParameter parameter : parameterList) {
      parameterMap.put(parameter.getName(), parameter);
    }
    parameterArray = parameterList.toArray(new FunctionParameter[parameterList.size()]);
    this.returnType = returnType;

    Set<ShapeKey> keySet = ShapeKey.getAll(parameterMap.keySet());
    parameterShapeMap = new ConstShapeMap<>(keySet);

  }

  public Map<String, FunctionParameter> getParameterMap() {
    return parameterMap;
  }

  public List<FunctionParameter> getParameterList() {
    return parameterList;
  }

  public ConstShapeMap<Cell> getParameterShapeMap() {
    return parameterShapeMap;
  }

  public FunctionParameter[] getParameterArray() {
    return parameterArray;
  }

  public Type getReturnType() {
    return returnType;
  }

  public Value toValue() {
    if (value == null){
      DictValue dict = new DictValue();
      dict = dict.put("return_type", Values.make(returnType.name()));
      ListValue paramList = new ListValue();

      for (FunctionParameter parameter : parameterList) {
        DictValue paramDict = new DictValue();
        paramDict = paramDict.put("name", Values.make(parameter.getName()));
        paramDict = paramDict.put("index", Values.make(parameter.getIndex()));
        paramDict = paramDict.put("declared_type", Values.make(parameter.getDeclaredType().name()));
        paramDict = paramDict.put("default_value", parameter.getDefaultValue());
        paramList = paramList.append(Values.make(paramDict));
      }

      dict = dict.put("parameters", Values.make(paramList));
      value = Values.make(dict);
    }
    return value;
  }
}
