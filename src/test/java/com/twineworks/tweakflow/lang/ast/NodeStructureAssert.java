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

package com.twineworks.tweakflow.lang.ast;

import com.twineworks.tweakflow.lang.ast.args.*;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import org.assertj.core.api.AbstractAssert;

import java.util.List;
import java.util.Map;

public class NodeStructureAssert extends AbstractAssert<NodeStructureAssert, Node> {

  private Node expected;

  public NodeStructureAssert(Node actual) {
    super(actual, NodeStructureAssert.class);
  }

  public static NodeStructureAssert assertThat(Node actual){
    return new NodeStructureAssert(actual);
  }

  public NodeStructureAssert hasSameStructureAs(Node expected){

    isNotNull();

    if (expected == null){
      failWithMessage("expected node cannot be null");
    }

    this.expected = expected;

    deepCompare(actual, expected);

    return this;
  }

  private void failWithDifference(String message, Node a, Node b){
    failWithMessage(
        "actual: \n"
            + "at "
            + actual
            + " "
            + actual.getSourceInfo().toString()
            + "\n and expected: \n"
            + "at "
            + expected
            + " "
            + expected.getSourceInfo().toString()
            + "\n differ at: \n"
            + a
            + " "
            + a.getSourceInfo().toString()
            + "\n and \n"
            + " "
            + b
            + " "
            + b.getSourceInfo().toString()
            + "\n "
            + message
    );
  }

  private void compareNodes(StringNode a, StringNode b){
    if (!a.getStringVal().equals(b.getStringVal())){
      failWithDifference("node values are different", a, b);
    }
  }

  private void compareNodes(LongNode a, LongNode b){
    if (!a.getLongNum().equals(b.getLongNum())){
      failWithDifference("node values are different", a, b);
    }
  }

  private void compareNodes(DoubleNode a, DoubleNode b){
    if (!a.getDoubleNum().equals(b.getDoubleNum())){
      failWithDifference("node values are different", a, b);
    }
  }

  private void compareNodes(BooleanNode a, BooleanNode b){
    if (!a.getBoolVal().equals(b.getBoolVal())){
      failWithDifference("node values are different", a, b);
    }
  }

  private void compareNodes(ReferenceNode a, ReferenceNode b){
    if (!a.getAnchor().equals(b.getAnchor())) failWithDifference("reference anchors are different", a, b);
    if (!a.getElements().equals(b.getElements())) failWithDifference("reference elements are different", a, b);
  }

  private void compareNodes(CastNode a, CastNode b){
    if (a.getTargetType() != b.getTargetType()) failWithDifference("target types do not match", a, b);
    deepCompare(a.getExpression(), b.getExpression());
  }

  private void compareNodes(IfNode a, IfNode b){
    deepCompare(a.getCondition(), b.getCondition());
    deepCompare(a.getThenExpression(), b.getThenExpression());
    deepCompare(a.getElseExpression(), b.getElseExpression());
  }

  private void compareNodes(DictEntryNode a, DictEntryNode b){
    deepCompare(a.getKey(), b.getKey());
    deepCompare(a.getValue(), b.getValue());
  }

  private void compareNodes(IsNode a, IsNode b){
    if (a.getCompareType() != b.getCompareType()) failWithDifference("is compares to different types", a, b);
    deepCompare(a.getExpression(), b.getExpression());
  }

  private void compareNodes(CallNode a, CallNode b){
    deepCompare(a.getExpression(), b.getExpression());
    deepCompare(a.getArguments(), b.getArguments());
  }

  private void compareNodes(StringConcatNode a, StringConcatNode b){
    deepCompare(a.getLeftExpression(), b.getLeftExpression());
    deepCompare(a.getRightExpression(), b.getRightExpression());
  }

  private void compareNodes(ContainerAccessNode a, ContainerAccessNode b){
    deepCompare(a.getContainerExpression(), b.getContainerExpression());
    deepCompare(a.getKeysExpression(), b.getKeysExpression());
  }

  private void compareNodes(FunctionNode a, FunctionNode b){
    deepCompare(a.getExpression(), b.getExpression());
    deepCompare(a.getParameters(), b.getParameters());
  }

  private void compareNodes(MultNode a, MultNode b){
    deepCompare(a.getLeftExpression(), b.getLeftExpression());
    deepCompare(a.getRightExpression(), b.getRightExpression());
  }

  private void compareNodes(PlusNode a, PlusNode b){
    deepCompare(a.getLeftExpression(), b.getLeftExpression());
    deepCompare(a.getRightExpression(), b.getRightExpression());
  }

  private void compareNodes(Parameters a, Parameters b){

    Map<String, ParameterNode> aMap = a.getMap();
    Map<String, ParameterNode> bMap = b.getMap();

    if (aMap.size() != bMap.size()) failWithDifference("parameter maps have different size", a, b);
    if (!aMap.keySet().equals(bMap.keySet())) failWithDifference("parameter maps have different keys", a, b);

    for (String s : aMap.keySet()) {
      deepCompare(aMap.get(s), bMap.get(s));
    }

  }

  private void compareNodes(Arguments a, Arguments b){
    if (a.getList().size() != b.getList().size()) failWithDifference("arguments lists have different size", a, b);
    List<ArgumentNode> aList = a.getList();
    List<ArgumentNode> bList = b.getList();

    for (int i = 0; i < a.getList().size(); i++) {
      deepCompare(aList.get(i), bList.get(i));
    }
  }

  private void compareNodes(ListNode a, ListNode b){
    if (a.getElements().size() != b.getElements().size()) failWithDifference("lists have different size", a, b);
    List<ExpressionNode> aList = a.getElements();
    List<ExpressionNode> bList = b.getElements();

    for (int i = 0; i < aList.size(); i++) {
      deepCompare(aList.get(i), bList.get(i));
    }
  }

  private void compareNodes(DictNode a, DictNode b){
    if (a.getEntries().size() != b.getEntries().size()) failWithDifference("maps have different size", a, b);
    List<DictEntryNode> aEntries = a.getEntries();
    List<DictEntryNode> bEntries = b.getEntries();

    for (int i = 0; i < aEntries.size(); i++) {
      deepCompare(aEntries.get(i), bEntries.get(i));
    }
  }

  private void compareNodes(ParameterNode a, ParameterNode b) {
    if (a.getIndex() != b.getIndex()) failWithDifference("parameter indexes do not match", a, b);
    if (a.getDeclaredType() != b.getDeclaredType()) failWithDifference("parameter types do not match", a, b);
    if (!a.getSymbolName().equals(b.getSymbolName())) failWithDifference("parameter names do not match", a, b);
    deepCompare(a.getDefaultValue(), b.getDefaultValue());
  }

  private void compareNodes(PositionalArgumentNode a, PositionalArgumentNode b) {
    if (a.getIndex() != b.getIndex()) failWithDifference("argument indexes do not match", a, b);
    deepCompare(a.getExpression(), b.getExpression());
  }

  private void compareNodes(NamedArgumentNode a, NamedArgumentNode b) {
    if (a.getName().equals(b.getName())) failWithDifference("argument names do not match", a, b);
    deepCompare(a.getExpression(), b.getExpression());
  }

  private void deepCompare(Node a, Node b){

    // expect identical class
    if (a.getClass() != b.getClass()){
      failWithDifference("node classes are different", a, b);
    }

    // two nil nodes are always equal
    if (a instanceof NilNode){
      return;
    }
    else if (a instanceof StringNode){
      compareNodes((StringNode) a, (StringNode) b);
    }
    else if (a instanceof LongNode){
      compareNodes((LongNode) a, (LongNode) b);
    }
    else if (a instanceof DoubleNode){
      compareNodes((DoubleNode) a, (DoubleNode) b);
    }
    else if (a instanceof BooleanNode){
      compareNodes((BooleanNode) a, (BooleanNode) b);
    }
    else if (a instanceof CastNode){
      compareNodes((CastNode) a, (CastNode) b);
    }
    else if (a instanceof ReferenceNode){
      compareNodes((ReferenceNode) a, (ReferenceNode) b);
    }
    else if (a instanceof CallNode){
      compareNodes((CallNode) a, (CallNode) b);
    }
    else if (a instanceof IfNode){
      compareNodes((IfNode) a, (IfNode) b);
    }
    else if (a instanceof IsNode){
      compareNodes((IsNode) a, (IsNode) b);
    }
    else if (a instanceof Arguments){
      compareNodes((Arguments) a, (Arguments) b);
    }
    else if (a instanceof PositionalArgumentNode){
      compareNodes((PositionalArgumentNode) a, (PositionalArgumentNode) b);
    }
    else if (a instanceof NamedArgumentNode){
      compareNodes((NamedArgumentNode) a, (NamedArgumentNode) b);
    }
    else if (a instanceof ListNode){
      compareNodes((ListNode) a, (ListNode) b);
    }
    else if (a instanceof DictNode){
      compareNodes((DictNode) a, (DictNode) b);
    }
    else if (a instanceof DictEntryNode){
      compareNodes((DictEntryNode) a, (DictEntryNode) b);
    }
    else if (a instanceof ContainerAccessNode){
      compareNodes((ContainerAccessNode) a, (ContainerAccessNode) b);
    }
    else if (a instanceof StringConcatNode){
      compareNodes((StringConcatNode) a, (StringConcatNode) b);
    }
    else if (a instanceof FunctionNode){
      compareNodes((FunctionNode) a, (FunctionNode) b);
    }
    else if (a instanceof PlusNode){
      compareNodes((PlusNode) a, (PlusNode) b);
    }
    else if (a instanceof MultNode){
      compareNodes((MultNode) a, (MultNode) b);
    }
    else if (a instanceof Parameters){
      compareNodes((Parameters) a, (Parameters) b);
    }
    else if (a instanceof ParameterNode){
      compareNodes((ParameterNode) a, (ParameterNode) b);
    }

    else {
      failWithMessage("cannot compare "+a.getClass().getName()+" yet");
    }

  }


}
