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

package com.twineworks.tweakflow.lang.analysis.visitors;

import com.twineworks.tweakflow.lang.ast.ComponentNode;
import com.twineworks.tweakflow.lang.ast.ForHeadElementNode;
import com.twineworks.tweakflow.lang.ast.UnitNode;
import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.args.*;
import com.twineworks.tweakflow.lang.ast.partial.PartialArgumentNode;
import com.twineworks.tweakflow.lang.ast.partial.PartialArguments;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.imports.ImportMemberNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.imports.ModuleImportNode;
import com.twineworks.tweakflow.lang.ast.imports.NameImportNode;
import com.twineworks.tweakflow.lang.ast.meta.DocNode;
import com.twineworks.tweakflow.lang.ast.meta.MetaNode;
import com.twineworks.tweakflow.lang.ast.meta.ViaNode;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.ast.structure.match.*;

public class AExpressionDescendingVisitor implements Visitor {

  @Override
  public UnitNode visit(UnitNode node) {
    return node.accept(this);
  }

  @Override
  public InteractiveNode visit(InteractiveNode node) {
    return node;
  }

  @Override
  public InteractiveSectionNode visit(InteractiveSectionNode node) {
    return node;
  }

  @Override
  public ModuleNode visit(ModuleNode node) {
    return node;
  }

  @Override
  public LibraryNode visit(LibraryNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(TypeOfNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public VarDefs visit(VarDefs node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public VarDecs visit(VarDecs node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ComponentNode visit(ComponentNode node) {
    return node.accept(this);
  }

  @Override
  public VarDefNode visit(VarDefNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public VarDecNode visit(VarDecNode node) {
    return node;
  }

  @Override
  public ImportNode visit(ImportNode node) {
    return node;
  }

  @Override
  public ImportMemberNode visit(ImportMemberNode node) {
    return node.accept(this);
  }

  @Override
  public NameImportNode visit(NameImportNode node) {
    return node;
  }

  @Override
  public ModuleImportNode visit(ModuleImportNode node) {
    return node;
  }

  @Override
  public AliasNode visit(AliasNode node) {
    return node;
  }

  @Override
  public ExportNode visit(ExportNode node) {
    return node;
  }

  @Override
  public MetaNode visit(MetaNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;

  }

  @Override
  public DocNode visit(DocNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;

  }

  @Override
  public ViaNode visit(ViaNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;

  }

  @Override
  public ExpressionNode visit(ExpressionNode node) {
    return node.accept(this);
  }

  @Override
  public ExpressionNode visit(BooleanNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(BinaryNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(CallNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(PartialApplicationNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(CastNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(FunctionNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(DateTimeNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(DefaultNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(IfNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(IsNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;

  }

  @Override
  public ExpressionNode visit(LetNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(ForNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(ListNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;

  }

  @Override
  public ExpressionNode visit(LessThanNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(LessThanOrEqualNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(GreaterThanNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(GreaterThanOrEqualNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(NotNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(EqualNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(NotEqualNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(ValueAndTypeEqualsNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(NotValueAndTypeEqualsNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }


  @Override
  public ExpressionNode visit(PlusNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(MultNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(DivNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(IntDivNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(ModNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(PowNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(MinusNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(NegateNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(StringConcatNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(ListConcatNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(DictMergeNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(LongNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(AndNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(OrNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(DoubleNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(DictNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public DictEntryNode visit(DictEntryNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(NilNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(ReferenceNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(StringNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(ContainerAccessNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(ThrowNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(TryCatchNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(MatchNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseNotNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseAndNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseOrNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseXorNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseShiftLeftNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseZeroShiftRightNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(BitwisePreservingShiftRightNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionNode visit(DebugNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ForHeadElementNode visit(ForHeadElementNode node) {
    return (ForHeadElementNode) node.accept(this);
  }

  @Override
  public ForHead visit(ForHead node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public GeneratorNode visit(GeneratorNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public MatchLines visit(MatchLines node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public MatchLineNode visit(MatchLineNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public MatchPatternNode visit(MatchPatternNode node) {
    return (MatchPatternNode) node.accept(this);
  }

  @Override
  public CapturePatternNode visit(CapturePatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ExpressionPatternNode visit(ExpressionPatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ListPatternNode visit(ListPatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public HeadTailListPatternNode visit(HeadTailListPatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public InitLastListPatternNode visit(InitLastListPatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public MidListPatternNode visit(MidListPatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public DictPatternNode visit(DictPatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public OpenDictPatternNode visit(OpenDictPatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public DataTypePatternNode visit(DataTypePatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public DefaultPatternNode visit(DefaultPatternNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public Arguments visit(Arguments node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ArgumentNode visit(ArgumentNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ArgumentNode visit(NamedArgumentNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ArgumentNode visit(SplatArgumentNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ArgumentNode visit(PositionalArgumentNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public ParameterNode visit(ParameterNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public PartialArguments visit(PartialArguments node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public PartialArgumentNode visit(PartialArgumentNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public BindingsNode visit(BindingsNode node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }

  @Override
  public Parameters visit(Parameters node) {
    node.getChildren().forEach((m) -> m.accept(this));
    return node;
  }
}
