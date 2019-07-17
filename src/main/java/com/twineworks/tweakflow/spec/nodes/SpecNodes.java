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
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.DictValue;
import com.twineworks.tweakflow.lang.values.ListValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.spec.effects.SpecEffect;

import java.util.*;

public class SpecNodes {

  public static SpecNode fromValue(Value value, HashMap<String, SpecEffect> effects, Runtime runtime) {
    Objects.requireNonNull(value, "value cannot be null");
    if (!value.isDict())
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "spec node must be a dict, found: " + value);
    DictValue d = value.dict();

    Value vType = d.get("type");
    if (vType.isNil() || !vType.isString())
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "spec node must have a valid type: " + value);

    String type = vType.string();

    switch (type) {
      case "describe":
        return makeDescribeNode(value, effects, runtime);
      case "it":
        return makeItNode(value);
      case "effect":
        return makeEffectNode(value, effects);
      case "subject":
        return makeSubjectNode(value);
      case "subject_transform":
        return makeSubjectTransformNode(value);
      case "subject_effect":
        return makeSubjectEffectNode(value, effects);
      case "before":
        return makeBeforeNode(value, effects);
      case "after":
        return makeAfterNode(value, effects);
      default:
        throw new LangException(LangError.ILLEGAL_ARGUMENT, "spec node must have a valid type: " + value);
    }

  }

  private static EffectNode makeEffectNode(Value value, HashMap<String, SpecEffect> effects) {
    if (value.isNil()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "effect node must not be nil");
    }
    DictValue d = value.dict();
    Value effectNode = d.get("effect");
    if (!effectNode.isDict()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "effect node must be a dict: " + effectNode);
    }
    DictValue dEffect = effectNode.dict();

    Value vType = dEffect.get("type");
    if (!vType.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "effect node must have a valid type: " + effectNode);
    }
    String type = vType.string();
    if (!effects.containsKey(type)) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "effect node has unknown type: " + effectNode);
    }

    return new EffectNode().setSource(value).setEffect(effectNode, effects.get(type));
  }

  private static SubjectEffectNode makeSubjectEffectNode(Value value, HashMap<String, SpecEffect> effects) {
    DictValue d = value.dict();
    EffectNode effectNode = makeEffectNode(d.get("effect"), effects);

    Value vAt = d.get("at");
    if (!vAt.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "subject_effect node must have valid at: " + value);
    }
    String at = vAt.string();

    return new SubjectEffectNode().setSource(value).setAt(NodeLocation.at(at)).setEffect(effectNode);
  }

  private static BeforeNode makeBeforeNode(Value value, HashMap<String, SpecEffect> effects) {
    DictValue d = value.dict();
    EffectNode effectNode = makeEffectNode(d.get("effect"), effects);
    Value vName = d.get("name");
    String name = "before";
    if (!vName.isNil() && vName.isString()) {
      name = vName.string();
    }

    Value vAt = d.get("at");
    if (!vAt.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "before node must have valid at: " + value);
    }
    String at = vAt.string();

    return new BeforeNode()
        .setSource(value)
        .setName(name)
        .setAt(NodeLocation.at(at))
        .setEffect(effectNode);
  }

  private static AfterNode makeAfterNode(Value value, HashMap<String, SpecEffect> effects) {
    DictValue d = value.dict();
    EffectNode effectNode = makeEffectNode(d.get("effect"), effects);
    Value vName = d.get("name");
    String name = "after";
    if (!vName.isNil() && vName.isString()) {
      name = vName.string();
    }

    Value vAt = d.get("at");
    if (!vAt.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "after node must have valid at: " + value);
    }
    String at = vAt.string();

    return new AfterNode()
        .setSource(value)
        .setName(name)
        .setAt(NodeLocation.at(at))
        .setEffect(effectNode);
  }

  private static Set<String> extractTags(Value value){
    Value tagsOnNode = value.dict().get("tags");
    if (tagsOnNode == null || tagsOnNode.isNil()) return Collections.emptySet();

    if (tagsOnNode.isDict()){
      DictValue d = tagsOnNode.dict();
      if (d.isEmpty()) return Collections.emptySet();
      HashSet<String> set = new HashSet<>();
      for (String key : d.keys()) {
        Value v = d.get(key);
        if (!v.isNil() && v.castTo(Types.BOOLEAN).bool()){
          set.add(key);
        }
      }
      return set;
    }
    else if (tagsOnNode.isList()){
      ListValue list = tagsOnNode.list();
      if (list.isEmpty()) return Collections.emptySet();
      HashSet<String> set = new HashSet<>();
      for (Value tag : list) {
        if (tag.isString()){
          set.add(tag.string());
        }
        else{
          throw new LangException(LangError.ILLEGAL_ARGUMENT, "found non-string value "+tag+" in tags list: " + value);
        }
      }
      return set;

    }
    else {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "tags must be a dict, or list of strings: " + value);
    }

  }

  private static DescribeNode makeDescribeNode(Value value, HashMap<String, SpecEffect> effects, Runtime runtime) {
    DictValue d = value.dict();
    Value vName = d.get("name");
    if (vName.isNil() || !vName.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "describe node must have a valid name: " + value);
    }
    String name = vName.string();

    Value vSpec = d.get("spec");
    if (!vSpec.isList() && !vSpec.isNil()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "describe node must have a valid spec: " + value);
    }

    Value vAt = d.get("at");
    if (!vAt.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "describe node must have valid at: " + value);
    }
    String at = vAt.string();

    Set<String> tags = extractTags(value);

    ArrayList<SpecNode> nodes = new ArrayList<>();
    ArrayList<BeforeNode> beforeNodes = new ArrayList<>();
    ArrayList<AfterNode> afterNodes = new ArrayList<>();
    SubjectSpecNode subjectNode = null;

    if (vSpec.isList()) {
      for (Value node : vSpec.list()) {
        SpecNode specNode = fromValue(node, effects, runtime);

        // resolve effect nodes
        if (specNode.getType() == SpecNodeType.EFFECT) {
          EffectNode effectNode = (EffectNode) specNode;
          try {
            specNode = fromValue(effectNode.execute(runtime), effects, runtime);
          } catch (Throwable e) {
            throw new RuntimeException("Failed to build test suite. Failed running " + node + " with error: " + e.getMessage(), e);
          }

        }

        switch (specNode.getType()) {
          case BEFORE:
            beforeNodes.add((BeforeNode) specNode);
            break;
          case AFTER:
            afterNodes.add((AfterNode) specNode);
            break;
          case DESCRIBE:
          case IT:
            nodes.add(specNode);
            break;
          case SUBJECT:
          case SUBJECT_TRANSFORM:
          case SUBJECT_EFFECT:
            if (subjectNode != null) {
              throw new LangException(LangError.ILLEGAL_ARGUMENT, "only one subject definition allowed in describe block: " + name);
            }
            if (nodes.size() > 0) {
              throw new LangException(LangError.ILLEGAL_ARGUMENT, "subject definition must precede any 'it' or nested describe blocks in describe block: " + name);
            }
            subjectNode = (SubjectSpecNode) specNode;
            break;
          default:
            throw new LangException(LangError.ILLEGAL_ARGUMENT, "Illegal node in describe block: " + specNode.getType());
        }

      }
    }
    DescribeNode node = new DescribeNode();
    node.setSource(value);
    node.setTags(tags);
    node.setBeforeNodes(beforeNodes);
    node.setAfterNodes(afterNodes);
    node.setSubjectNode(subjectNode);
    node.setName(name);
    node.setAt(NodeLocation.at(at));
    node.setNodes(nodes);
    return node;
  }

  private static ItNode makeItNode(Value value) {
    DictValue d = value.dict();
    Value vName = d.get("name");
    if (vName.isNil() || !vName.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "it node must have a valid name: " + value);
    }

    Value vSpec = d.get("spec");
    if (!vSpec.isFunction() && !vSpec.isNil()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "it node must have a valid spec: " + value);
    }

    Value vAt = d.get("at");
    if (!vAt.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "it node must have valid at: " + value);
    }

    Set<String> tags = extractTags(value);

    ItNode node = new ItNode();
    node.setSource(value);
    node.setTags(tags);
    node.setName(vName.string());
    node.setSpec(vSpec);
    node.setAt(NodeLocation.at(vAt.string()));
    return node;
  }

  private static SubjectNode makeSubjectNode(Value value) {
    DictValue d = value.dict();
    Value data = d.get("data");

    Value vAt = d.get("at");
    if (!vAt.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "subject node must have valid at: " + value);
    }
    String at = vAt.string();

    return new SubjectNode().setSource(value).setAt(NodeLocation.at(at)).setData(data);
  }

  private static SubjectTransformNode makeSubjectTransformNode(Value value) {
    DictValue d = value.dict();
    Value transform = d.get("transform");

    if (!transform.isFunction()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "transform must be a function: " + value);
    }

    Value vAt = d.get("at");
    if (!vAt.isString()) {
      throw new LangException(LangError.ILLEGAL_ARGUMENT, "subject_transform node must have valid at: " + value);
    }
    String at = vAt.string();

    return new SubjectTransformNode().setSource(value).setAt(NodeLocation.at(at)).setTransform(transform);
  }


}
