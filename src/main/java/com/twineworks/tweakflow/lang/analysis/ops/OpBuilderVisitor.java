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

import com.twineworks.tweakflow.lang.interpreter.Interpreter;
import com.twineworks.tweakflow.lang.interpreter.ops.*;
import com.twineworks.tweakflow.lang.analysis.visitors.AExpressionDescendingVisitor;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.args.ParameterNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.ast.structure.match.*;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPathLocation;
import com.twineworks.tweakflow.lang.load.user.UserObjectFactory;
import com.twineworks.tweakflow.lang.values.*;

import java.util.ArrayList;

public class OpBuilderVisitor extends AExpressionDescendingVisitor implements Visitor {

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
    return node.setOp(new CallOp(node));
  }

  @Override
  public ExpressionNode visit(CurryNode node) {
    super.visit(node);
    return node.setOp(new CurryOp(node));
  }

  @Override
  public ExpressionNode visit(LessThanNode node) {
    super.visit(node);
    return node.setOp(new LessThanOp(node));
  }

  @Override
  public ExpressionNode visit(LessThanOrEqualNode node) {
    super.visit(node);
    return node.setOp(new LessThanOrEqualOp(node));
  }

  @Override
  public ExpressionNode visit(GreaterThanNode node) {
    super.visit(node);
    return node.setOp(new GreaterThanOp(node));
  }

  @Override
  public ExpressionNode visit(GreaterThanOrEqualNode node) {
    super.visit(node);
    return node.setOp(new GreaterThanOrEqualOp(node));
  }

  @Override
  public ExpressionNode visit(EqualNode node) {
    super.visit(node);
    return node.setOp(new EqualOp(node));
  }

  @Override
  public ExpressionNode visit(NotNode node) {
    super.visit(node);
    return node.setOp(new NotOp(node));
  }

  @Override
  public ExpressionNode visit(NotEqualNode node) {
    super.visit(node);
    return node.setOp(new NotEqualOp(node));
  }

  @Override
  public ExpressionNode visit(NegateNode node) {
    super.visit(node);
    return node.setOp(new NegateOp(node));
  }

  @Override
  public ExpressionNode visit(AndNode node) {
    super.visit(node);
    return node.setOp(new AndOp(node));
  }

  @Override
  public ExpressionNode visit(OrNode node) {
    super.visit(node);
    return node.setOp(new OrOp(node));
  }

  @Override
  public ExpressionNode visit(PlusNode node) {
    super.visit(node);
    return node.setOp(new PlusOp(node));
  }

  @Override
  public ExpressionNode visit(MultNode node) {
    super.visit(node);
    return node.setOp(new MultOp(node));
  }

  @Override
  public ExpressionNode visit(StringConcatNode node) {
    super.visit(node);
    return node.setOp(new StringConcatOp(node));
  }

  @Override
  public ExpressionNode visit(ListConcatNode node) {
    super.visit(node);
    return node.setOp(new ListConcatOp(node));
  }

  @Override
  public ExpressionNode visit(DictMergeNode node) {
    super.visit(node);
    return node.setOp(new DictMergeOp(node));
  }

  @Override
  public ExpressionNode visit(PowNode node) {
    super.visit(node);
    return node.setOp(new PowOp(node));
  }

  @Override
  public ExpressionNode visit(DivNode node) {
    super.visit(node);
    return node.setOp(new DivOp(node));
  }

  @Override
  public ExpressionNode visit(IntDivNode node) {
    super.visit(node);
    return node.setOp(new IntDivOp(node));
  }

  @Override
  public ExpressionNode visit(ModNode node) {
    super.visit(node);
    node.setOp(new ModOp(node));
    return node;
  }

  @Override
  public ExpressionNode visit(MinusNode node) {
    super.visit(node);
    return node.setOp(new MinusOp(node));
  }


  @Override
  public ExpressionNode visit(CastNode node) {
    super.visit(node);
    // if the cast is not necessary, just use the underlying expression directly
    if (node.getTargetType() == node.getExpression().getValueType()){
      return node.setOp(node.getExpression().getOp());
    }

    return node.setOp(new CastOp(node));
  }

  @Override
  public ExpressionNode visit(FunctionNode node) {
    super.visit(node);

    // construct signature for all nodes, no matter if they end up being compile time constants or not
    ArrayList<FunctionParameter> params = new ArrayList<>();

    for (ParameterNode parameterNode : node.getParameters().getMap().values()) {

      if (!parameterNode.getDefaultValue().getOp().isConstant()){
        throw new LangException(LangError.LITERAL_VALUE_REQUIRED, "parameter "+parameterNode.getSymbolName()+" default value must be a literal", parameterNode.getSourceInfo());
      }

      Value rawDefaultValue = Interpreter.evaluateInEmptyScope(parameterNode.getDefaultValue());
      Value typedDefaultValue;
      try {
        typedDefaultValue = rawDefaultValue.castTo(parameterNode.getDeclaredType());
      }
      catch(LangException e){
        e.setSourceInfo(node.getSourceInfo());
        throw e;
      }
      params.add(new FunctionParameter(
          parameterNode.getIndex(),
          parameterNode.getSymbolName(),
          parameterNode.getDeclaredType(),
          typedDefaultValue));
    }
    FunctionSignature functionSignature = new FunctionSignature(params, node.getDeclaredReturnType());
    node.setSignature(functionSignature);

    if (node.getVia() != null){

      // user functions are always compile time constants
      LoadPathLocation location = node.getSourceInfo().getParseUnit().getLocation();
      if (location.allowsNativeFunctions()){
        UserObjectFactory userObjectFactory = new UserObjectFactory();

        FunctionValue userFunction = userObjectFactory.createUserFunction(functionSignature, Interpreter.evaluateInEmptyScope(node.getVia().getExpression()));
        node.setFunctionValue(Values.make(userFunction));
        return node.setOp(new ConstantOp(node.getFunctionValue()));
      }
      else {
        throw new LangException(LangError.NATIVE_CODE_RESTRICTED, "code in location "+node.getSourceInfo().getParseUnit().getPath()+" cannot define native functions", node.getSourceInfo());
      }

    }

    return node.setOp(new FunctionOp(node));
  }

  @Override
  public ExpressionNode visit(IfNode node) {

    super.visit(node);
    return node.setOp(new IfOp(node));
  }

  @Override
  public ExpressionNode visit(IsNode node) {
    super.visit(node);
    return node.setOp(new IsOp(node));
  }

  @Override
  public ExpressionNode visit(DefaultNode node) {
    super.visit(node);
    return node.setOp(new DefaultOp(node));
  }

  @Override
  public ExpressionNode visit(ForNode node) {
    super.visit(node);
    return node.setOp(new ForOp(node));
  }


  @Override
  public ExpressionNode visit(MatchNode node) {
    super.visit(node);
    return node.setOp(new MatchOp(node));
  }

  @Override
  public ExpressionPatternNode visit(ExpressionPatternNode node) {
    super.visit(node);
    node.setPatternOp(new ExpressionPatternOp(node));
    return node;
  }

  @Override
  public CapturePatternNode visit(CapturePatternNode node) {
    super.visit(node);
    node.setPatternOp(new CapturePatternOp(node));
    return node;
  }

  @Override
  public DataTypePatternNode visit(DataTypePatternNode node) {
    super.visit(node);
    node.setPatternOp(new DataTypePatternOp(node));
    return node;
  }


  @Override
  public ListPatternNode visit(ListPatternNode node) {
    super.visit(node);
    node.setPatternOp(new ListPatternOp(node));
    return node;
  }

  @Override
  public HeadTailListPatternNode visit(HeadTailListPatternNode node) {
    super.visit(node);
    node.setPatternOp(new HeadTailListPatternOp(node));
    return node;
  }

  @Override
  public InitLastListPatternNode visit(InitLastListPatternNode node) {
    super.visit(node);
    node.setPatternOp(new InitLastListPatternOp(node));
    return node;
  }

  @Override
  public MidListPatternNode visit(MidListPatternNode node) {
    super.visit(node);
    node.setPatternOp(new MidListPatternOp(node));
    return node;
  }

  @Override
  public DictPatternNode visit(DictPatternNode node) {
    super.visit(node);
    node.setPatternOp(new DictPatternOp(node));
    return node;
  }

  @Override
  public OpenDictPatternNode visit(OpenDictPatternNode node) {
    super.visit(node);
    node.setPatternOp(new OpenDictPatternOp(node));
    return node;
  }

  @Override
  public DefaultPatternNode visit(DefaultPatternNode node) {
    super.visit(node);
    node.setPatternOp(new DefaultPatternOp());
    return node;
  }

  @Override
  public ExpressionNode visit(TypeOfNode node) {

    super.visit(node);
    return node.setOp(new TypeOfOp(node));
  }

  @Override
  public ExpressionNode visit(LetNode node) {
    super.visit(node);
    return node.setOp(new LetOp(node));
  }

  @Override
  public ExpressionNode visit(ListNode node) {
    super.visit(node);
    return node.setOp(new ListOp(node));
  }

  @Override
  public ExpressionNode visit(DictNode node) {
    super.visit(node);
    return node.setOp(new DictOp(node));
  }

  @Override
  public ExpressionNode visit(ThrowNode node) {
    super.visit(node);
    return node.setOp(new ThrowOp(node));
  }

  @Override
  public ExpressionNode visit(TryCatchNode node) {

    super.visit(node);
    return node.setOp(new TryCatchOp(node));
  }

  @Override
  public ExpressionNode visit(ReferenceNode node) {

    if (node.isClosure()){
      return node.setOp(new ClosureReferenceOp(node));
    }
    if (node.isSimpleLocal()){
      return node.setOp(new SimpleLocalReferenceOp(node.getSimpleName()));
    }
    if (node.isSimpleParent()){
      return node.setOp(new SimpleParentSpaceReferenceOp(node.getSimpleName()));
    }
    return node.setOp(new ReferenceOp(node));
  }

  @Override
  public ExpressionNode visit(ContainerAccessNode node) {
    super.visit(node);
    return node.setOp(new ContainerAccessOp(node));
  }

  @Override
  public BooleanNode visit(BooleanNode node) {
    node.setOp(new ConstantOp(node.getBoolVal() ? Values.TRUE : Values.FALSE));
    return node;
  }

  @Override
  public LongNode visit(LongNode node) {
    node.setOp(new ConstantOp(Values.make(node.getLongNum())));
    return node;
  }

  @Override
  public DoubleNode visit(DoubleNode node) {
    node.setOp(new ConstantOp(Values.make(node.getDoubleNum())));
    return node;
  }

  @Override
  public NilNode visit(NilNode node) {
    node.setOp(new ConstantOp(Values.NIL));
    return node;
  }

  @Override
  public StringNode visit(StringNode node) {
    node.setOp(new ConstantOp(Values.make(node.getStringVal())));
    return node;
  }

  @Override
  public DateTimeNode visit(DateTimeNode node) {
    node.setOp(new ConstantOp(Values.make(node.getDateTime())));
    return node;
  }

  @Override
  public ExpressionNode visit(ValueAndTypeEqualsNode node) {
    super.visit(node);
    node.setOp(new ValueAndTypeEqualsOp(node));
    return node;
  }

  @Override
  public ExpressionNode visit(NotValueAndTypeEqualsNode node) {
    super.visit(node);
    node.setOp(new NotValueAndTypeEqualsOp(node));
    return node;
  }

  @Override
  public ExpressionNode visit(DebugNode node) {
    super.visit(node);
    return node.setOp(new DebugOp(node));
  }

  @Override
  public ExpressionNode visit(BitwiseNotNode node) {
    super.visit(node);
    return node.setOp(new BitwiseNotOp(node));
  }

  @Override
  public ExpressionNode visit(BitwiseAndNode node) {
    super.visit(node);
    return node.setOp(new BitwiseAndOp(node));
  }

  @Override
  public ExpressionNode visit(BitwiseOrNode node) {
    super.visit(node);
    return node.setOp(new BitwiseOrOp(node));
  }

  @Override
  public ExpressionNode visit(BitwiseXorNode node) {
    super.visit(node);
    return node.setOp(new BitwiseXorOp(node));
  }

  @Override
  public ExpressionNode visit(BitwiseShiftLeftNode node) {
    super.visit(node);
    return node.setOp(new BitwiseShiftLeftOp(node));
  }

  @Override
  public ExpressionNode visit(BitwisePreservingShiftRightNode node) {
    super.visit(node);
    return node.setOp(new BitwisePreservingShiftRightOp(node));
  }

  @Override
  public ExpressionNode visit(BitwiseZeroShiftRightNode node) {
    super.visit(node);
    return node.setOp(new BitwiseZeroShiftRightOp(node));
  }
}
