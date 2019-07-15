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

package com.twineworks.tweakflow.spec.nodes;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.spec.effects.SpecEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SpecNodes {

  public static SpecNode fromValue(Value value, HashMap<String, SpecEffect> effects, Runtime runtime){
    Objects.requireNonNull(value, "value cannot be null");
    if (!value.isDict()) throw new LangException(LangError.ILLEGAL_ARGUMENT, "spec node must be a dict, found: "+value);
    DictValue d = value.dict();

    Value vType = d.get("type");
    if (vType.isNil() || !vType.isString()) throw new LangException(LangError.ILLEGAL_ARGUMENT, "spec node must have a valid type: "+value);

    String type = vType.string();

    switch (type){
      case "describe":
        return makeDescribeNode(value, effects, runtime);
      case "it":
        return makeItNode(value);
      case "before":
      case "after":
      case "effect":
        return makeEffectNode(value, effects, runtime);
      case "subject":
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "spec node type: "+type+" not supported yet");
      default:
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "spec node must have a valid type: "+value);
    }

  }

  private static SpecNode makeEffectNode(Value value, HashMap<String, SpecEffect> effects, Runtime runtime) {
    DictValue d = value.dict();
    Value effectNode = d.get("effect");
    if (!effectNode.isDict()){
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "effect node must be a dict: "+effectNode);
    }
    DictValue dEffect = effectNode.dict();

    Value vType = dEffect.get("type");
    if (!vType.isString()){
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "effect node must have a valid type: "+effectNode);
    }
    String type = vType.string();
    if (!effects.containsKey(type)){
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "effect node has unknown type: "+effectNode);
    }

    return fromValue(effects.get(type).execute(runtime, effectNode), effects, runtime);
  }

  private static DescribeNode makeDescribeNode(Value value, HashMap<String, SpecEffect> effects, Runtime runtime){
    DictValue d = value.dict();
    Value vName = d.get("name");
    if (vName.isNil() || !vName.isString()){
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "describe node must have a valid name: "+value);
    }

    Value vSpec = d.get("spec");
    if (!vSpec.isList() && !vSpec.isNil()){
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "describe node must have a valid spec: "+value);
    }
    ArrayList<SpecNode> nodes = new ArrayList<>();
    if (vSpec.isList()){
      for (Value node : vSpec.list()) {
        nodes.add(fromValue(node, effects, runtime));
      }
    }
    DescribeNode node = new DescribeNode();
    node.setName(vName.string());
    node.setNodes(nodes);
    return node;
  }

  private static ItNode makeItNode(Value value){
    DictValue d = value.dict();
    Value vName = d.get("name");
    if (vName.isNil() || !vName.isString()){
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "it node must have a valid name: "+value);
    }

    Value vSpec = d.get("spec");
    if (!vSpec.isFunction() && !vSpec.isNil()){
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "it node must have a valid spec: "+value);
    }

    ItNode node = new ItNode();
    node.setName(vName.string());
    node.setSpec(vSpec);
    return node;
  }


}
