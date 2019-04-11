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

package com.twineworks.tweakflow.lang.analysis.ops;

import com.twineworks.tweakflow.lang.analysis.visitors.AExpressionDescendingVisitor;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.ast.structure.match.*;

public class OpSpecializationVisitor extends AExpressionDescendingVisitor implements Visitor {

  private ExpressionNode specialize(ExpressionNode node){
    return node.setOp(node.getOp().specialize());
  }

  @Override
  public InteractiveNode visit(InteractiveNode node) {
    node.getSections().forEach(this::visit);
    return node;
  }

  @Override
  public InteractiveSectionNode visit(InteractiveSectionNode node) {
    visit(node.getVars());
    return node;
  }

  @Override
  public ModuleNode visit(ModuleNode node) {
    if (node.hasDoc()) visit(node.getDoc());
    if (node.hasMeta()) visit(node.getMeta());
    node.getImports().forEach(this::visit);
    node.getComponents().forEach(this::visit);
    return node;
  }

  @Override
  public LibraryNode visit(LibraryNode node) {
    if (node.hasDoc()) visit(node.getDoc());
    if (node.hasMeta()) visit(node.getMeta());
    visit(node.getVars());
    return node;
  }

  @Override
  public VarDefNode visit(VarDefNode node) {
    if (node.hasDoc()) visit(node.getDoc());
    if (node.hasMeta()) visit(node.getMeta());
    visit(node.getValueExpression());
    return node;
  }

  @Override
  public ExpressionNode visit(CallNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(PartialApplicationNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(LessThanNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(LessThanOrEqualNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(GreaterThanNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(GreaterThanOrEqualNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(EqualNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(NotNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(NotEqualNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(NegateNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(AndNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(OrNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(PlusNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(MultNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(StringConcatNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(ListConcatNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(DictMergeNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(PowNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(DivNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(IntDivNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(ModNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(MinusNode node) {
    super.visit(node);
    return specialize(node);
  }


  @Override
  public ExpressionNode visit(CastNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(FunctionNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(IfNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(IsNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(DefaultNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(ForNode node) {
    super.visit(node);
    return specialize(node);
  }


  @Override
  public ExpressionNode visit(MatchNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionPatternNode visit(ExpressionPatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public CapturePatternNode visit(CapturePatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public DataTypePatternNode visit(DataTypePatternNode node) {
    super.visit(node);
    return node;
  }


  @Override
  public ListPatternNode visit(ListPatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public HeadTailListPatternNode visit(HeadTailListPatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public InitLastListPatternNode visit(InitLastListPatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public MidListPatternNode visit(MidListPatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public DictPatternNode visit(DictPatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public OpenDictPatternNode visit(OpenDictPatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public DefaultPatternNode visit(DefaultPatternNode node) {
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(TypeOfNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(LetNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(ValueAndTypeEqualsNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(NotValueAndTypeEqualsNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(ListNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(DictNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(ThrowNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(TryCatchNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(ReferenceNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(ContainerAccessNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public BooleanNode visit(BooleanNode node) {
    return node;
  }

  @Override
  public LongNode visit(LongNode node) {
    return node;
  }

  @Override
  public DoubleNode visit(DoubleNode node) {
    return node;
  }

  @Override
  public NilNode visit(NilNode node) {
    return node;
  }

  @Override
  public StringNode visit(StringNode node) {
    return node;
  }

  @Override
  public DateTimeNode visit(DateTimeNode node) {
    return node;
  }

  @Override
  public ExpressionNode visit(DebugNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(BitwiseNotNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(BitwiseAndNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(BitwiseOrNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(BitwiseXorNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(BitwiseShiftLeftNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(BitwisePreservingShiftRightNode node) {
    super.visit(node);
    return specialize(node);
  }

  @Override
  public ExpressionNode visit(BitwiseZeroShiftRightNode node) {
    super.visit(node);
    return specialize(node);
  }



}
