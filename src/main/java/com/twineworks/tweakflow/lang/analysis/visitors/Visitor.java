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

public interface Visitor {

  // structure
  UnitNode visit(UnitNode node);
  InteractiveNode visit(InteractiveNode node);
  InteractiveSectionNode visit(InteractiveSectionNode node);

  ModuleNode visit(ModuleNode node);
  LibraryNode visit(LibraryNode node);
  ComponentNode visit(ComponentNode node);
  VarDefNode visit(VarDefNode node);
  VarDecNode visit(VarDecNode node);
  BindingsNode visit(BindingsNode node);
  VarDefs visit(VarDefs node);
  VarDecs visit(VarDecs node);

  // imports
  ImportNode visit(ImportNode node);
  ImportMemberNode visit(ImportMemberNode node);
  NameImportNode visit(NameImportNode node);
  ModuleImportNode visit(ModuleImportNode node);

  // aliases
  AliasNode visit(AliasNode node);

  // exports
  ExportNode visit(ExportNode node);

  // meta
  MetaNode visit(MetaNode node);
  DocNode visit(DocNode node);
  ViaNode visit(ViaNode node);

  // expressions
  ExpressionNode visit(ExpressionNode node);
  ExpressionNode visit(BooleanNode node);
  ExpressionNode visit(CallNode node);
  ExpressionNode visit(CastNode node);
  ExpressionNode visit(FunctionNode node);
  ExpressionNode visit(DateTimeNode node);
  ExpressionNode visit(DefaultNode node);
  ExpressionNode visit(IfNode node);
  ExpressionNode visit(IsNode node);
  ExpressionNode visit(TypeOfNode node);
  ExpressionNode visit(LetNode node);
  ExpressionNode visit(ForNode node);
  ExpressionNode visit(ListNode node);
  ExpressionNode visit(LessThanNode node);
  ExpressionNode visit(LessThanOrEqualNode node);
  ExpressionNode visit(GreaterThanNode node);
  ExpressionNode visit(GreaterThanOrEqualNode node);
  ExpressionNode visit(NotNode node);
  ExpressionNode visit(EqualNode node);
  ExpressionNode visit(NotEqualNode node);
  ExpressionNode visit(ValueAndTypeEqualsNode node);
  ExpressionNode visit(NotValueAndTypeEqualsNode node);
  ExpressionNode visit(PlusNode node);
  ExpressionNode visit(MultNode node);
  ExpressionNode visit(DivNode node);
  ExpressionNode visit(IntDivNode node);
  ExpressionNode visit(ModNode node);
  ExpressionNode visit(PowNode node);
  ExpressionNode visit(MinusNode node);
  ExpressionNode visit(NegateNode node);
  ExpressionNode visit(StringConcatNode node);
  ExpressionNode visit(ListConcatNode node);
  ExpressionNode visit(DictMergeNode node);
  ExpressionNode visit(LongNode node);
  ExpressionNode visit(AndNode node);
  ExpressionNode visit(OrNode node);
  ExpressionNode visit(DoubleNode node);
  ExpressionNode visit(DictNode node);
  DictEntryNode visit(DictEntryNode node);
  ExpressionNode visit(NilNode node);
  ExpressionNode visit(ReferenceNode node);
  ExpressionNode visit(StringNode node);
  ExpressionNode visit(ContainerAccessNode node);
  ExpressionNode visit(ThrowNode node);
  ExpressionNode visit(TryCatchNode node);
  ExpressionNode visit(MatchNode node);
  ExpressionNode visit(BitwiseNotNode node);
  ExpressionNode visit(BitwiseAndNode node);
  ExpressionNode visit(BitwiseOrNode node);
  ExpressionNode visit(BitwiseXorNode node);
  ExpressionNode visit(BitwiseShiftLeftNode node);
  ExpressionNode visit(BitwiseZeroShiftRightNode node);
  ExpressionNode visit(BitwisePreservingShiftRightNode node);
  ExpressionNode visit(DebugNode node);

  // list comprehension support
  ForHeadElementNode visit(ForHeadElementNode node);
  ForHead visit(ForHead node);
  GeneratorNode visit(GeneratorNode node);

  // pattern matching support
  MatchLines visit(MatchLines node);
  MatchLineNode visit(MatchLineNode node);
  MatchPatternNode visit(MatchPatternNode node);
  CapturePatternNode visit(CapturePatternNode node);
  ExpressionPatternNode visit(ExpressionPatternNode node);
  ListPatternNode visit(ListPatternNode node);
  HeadTailListPatternNode visit(HeadTailListPatternNode node);
  InitLastListPatternNode visit(InitLastListPatternNode node);
  MidListPatternNode visit(MidListPatternNode node);
  DictPatternNode visit(DictPatternNode node);
  OpenDictPatternNode visit(OpenDictPatternNode node);
  DataTypePatternNode visit(DataTypePatternNode node);
  DefaultPatternNode visit(DefaultPatternNode node);

  // args
  Arguments visit(Arguments libraries);
  ArgumentNode visit(ArgumentNode node);
  ArgumentNode visit(NamedArgumentNode node);
  ArgumentNode visit(SplatArgumentNode node);
  ArgumentNode visit(PositionalArgumentNode node);

  Parameters visit(Parameters node);
  ParameterNode visit(ParameterNode node);

}
