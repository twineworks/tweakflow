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

package com.twineworks.tweakflow.lang.interpreter;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.collections.shapemap.ShapeKey;
import com.twineworks.tweakflow.lang.interpreter.memory.*;
import com.twineworks.tweakflow.lang.interpreter.ops.ExpressionOp;
import com.twineworks.tweakflow.lang.analysis.ops.OpBuilderVisitor;
import com.twineworks.tweakflow.lang.ast.MetaDataNode;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.SymbolNode;
import com.twineworks.tweakflow.lang.ast.args.ArgumentNode;
import com.twineworks.tweakflow.lang.ast.args.Arguments;
import com.twineworks.tweakflow.lang.ast.args.NamedArgumentNode;
import com.twineworks.tweakflow.lang.ast.args.SplatArgumentNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.meta.ViaNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDecNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.scope.GlobalScope;
import com.twineworks.tweakflow.lang.types.Type;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.*;

import java.util.*;

public class Interpreter {

  public static void evaluateSpace(MemorySpace space, EvaluationContext context){

    ConstShapeMap<Cell> cells = space.getCells();
    for (Cell cell : cells.values()) {
      evaluateCell(cell, new Stack(), context);
    }

  }

  public static void evaluateSpace(MemorySpace space, Stack stack, EvaluationContext context){

    ConstShapeMap<Cell> cells = space.getCells();
    for (Cell cell : cells.values()) {
      evaluateCell(cell, stack, context);
    }

  }

  public static void evaluateCell(Cell cell, Stack stack, EvaluationContext context){

    if (!cell.isDirty()) return;

    // variable cell
    if (cell.isVar()){
      SymbolNode targetNode = cell.getSymbol().getTargetNode();
      if (targetNode instanceof VarDefNode){
        VarDefNode varDef = (VarDefNode) targetNode;
        ExpressionNode exp = varDef.getValueExpression();

        // need to push cell lexical space so transitively referenced items can be found
        // when referenced from other spaces
        stack.push(new StackEntry(targetNode, cell.getEnclosingSpace(), stack.peek().getClosures()));
        cell.setEvaluating(true);
        cell.setValue(evaluateExpression(exp, stack, context));
        cell.setEvaluating(false);
        closeDeferredClosures(cell, context);
        stack.pop();
      }
    }
    // module cell
    else if (cell.isModule()){
      stack.push(new StackEntry(cell.getSymbol().getTargetNode(), cell, Collections.emptyMap()));
      evaluateSpace(cell, stack, context);
      stack.pop();
    }
    // library cell
    else if (cell.isLibrary()){
      stack.push(new StackEntry(cell.getSymbol().getTargetNode(), cell, Collections.emptyMap()));
      evaluateSpace(cell, stack, context);
      stack.pop();
    }
    // interactive unit
    else if (cell.isInteractiveUnit()){
      stack.push(new StackEntry(cell.getSymbol().getTargetNode(), cell, Collections.emptyMap()));
      evaluateSpace(cell, stack, context);
      stack.pop();
    }
    // interactive section
    else if (cell.isInteractiveSection()){
      stack.push(new StackEntry(cell.getSymbol().getTargetNode(), cell, Collections.emptyMap()));
      evaluateSpace(cell, stack, context);
      stack.pop();
    }

  }

  private static void closeDeferredClosures(Cell cell, EvaluationContext context){

    Map<Cell, List<RecursiveDeferredClosure>> deferredClosures = context.getRecursiveDeferredClosures();
    List<RecursiveDeferredClosure> closureList = deferredClosures.get(cell);

    if (closureList != null){
      for (RecursiveDeferredClosure deferredClosureDef : closureList) {
        Map<ReferenceNode, ValueProvider> closures = deferredClosureDef.getClosures();
        ReferenceNode key = deferredClosureDef.getReferenceNode();
        closures.put(key, cell.getValue());
      }
      deferredClosures.remove(cell);
    }

  }

  public static Value evaluateUserFunctionCall(UserFunctionValue userFunction, Value[] args, Node at, Stack stack, EvaluationContext context) {

    Value[] callArgs = Interpreter.argumentsForPositionalUserCall(args, userFunction.getSignature());

    stack.push(new StackEntry(at, LocalMemorySpace.EMPTY, Collections.emptyMap()));
    CallContext userCallContext = new CallContext(stack, context);

    UserFunction f = userFunction.getUserFunction();
    Type retType = userFunction.getSignature().getReturnType();

    Value retValue;
    switch (callArgs.length){
      case 0:
        retValue = ((Arity0UserFunction) f).call(userCallContext).castTo(retType);
        break;
      case 1:
        retValue = ((Arity1UserFunction) f).call(userCallContext, callArgs[0]).castTo(retType);
        break;
      case 2:
        retValue = ((Arity2UserFunction) f).call(userCallContext, callArgs[0], callArgs[1]).castTo(retType);
        break;
      case 3:
        retValue = ((Arity3UserFunction) f).call(userCallContext, callArgs[0], callArgs[1], callArgs[2]).castTo(retType);
        break;
      case 4:
        retValue = ((Arity4UserFunction) f).call(userCallContext, callArgs[0], callArgs[1], callArgs[2], callArgs[3]).castTo(retType);
        break;
      default:
        retValue = ((ArityNUserFunction) f).callVariadic(userCallContext, callArgs).castTo(retType);
    }
    stack.pop();
    return retValue;

  }

  private static Value evaluateStandardFunctionCall(StandardFunctionValue standardFunction, ConstShapeMap<Cell> args, Node at, Stack stack, EvaluationContext context){

    LocalMemorySpace argSpace = new LocalMemorySpace(
        stack.peek().getSpace(),
        standardFunction.getBody().getScope(),
        MemorySpaceType.CALL_ARGUMENTS,
        args
    );

    // put all local closures into arg space
    stack.push(new StackEntry(at, argSpace, standardFunction.getClosures()));
    Value retValue = standardFunction.getBody().getOp().eval(stack, context);
    stack.pop();
    return retValue;
  }

  public static Value evaluateTryCatchNode(TryCatchNode tryCatchNode, Stack stack, EvaluationContext context){

    StackEntry entryStackFrame = stack.peek();
    ExpressionNode tryExpression = tryCatchNode.getTryExpression();
    Value ret;

    try {
      ret = evaluateExpression(tryExpression, stack, context);
    } catch (LangException e){
      // discard stack frames lost in unwinding
      while(stack.peek() != entryStackFrame) stack.pop();
      // process catch
      ExpressionNode catchExpression = tryCatchNode.getCatchExpression();

      if (tryCatchNode.getCaughtException() != null){
        VarDecNode caughtException = tryCatchNode.getCaughtException();

        Set<ShapeKey> keys;
        if (tryCatchNode.getCaughtTrace() != null){
          keys = ShapeKey.getAll(tryCatchNode.getCaughtException().getSymbolName(), tryCatchNode.getCaughtTrace().getSymbolName());
        }
        else{
          keys = ShapeKey.getAll(tryCatchNode.getCaughtException().getSymbolName());
        }

        LocalMemorySpace bindingsSpace = new LocalMemorySpace(stack.peek().getSpace(), catchExpression.getScope(), MemorySpaceType.LOCAL, new ConstShapeMap<>(keys));
        ConstShapeMap<Cell> bindingsCells = bindingsSpace.getCells();

        Cell errorCell = new Cell()
            .setSymbol(catchExpression.getScope().getSymbols().get(caughtException.getSymbolName()));

        errorCell.setValue(e.toErrorValue());

//        Object errorValue = e.get("value");
//        if (errorValue instanceof Value){
//          // user-thrown errors
//          errorCell.setValue((Value) errorValue);
//        }
//        else{
//          // built-in errors
//          errorCell.setValue(e.toValue());
//        }

        bindingsCells.puts(caughtException.getSymbolName(), errorCell);

        // add trace?
        if (tryCatchNode.getCaughtTrace() != null){
          VarDecNode caughtTrace = tryCatchNode.getCaughtTrace();
          Cell traceCell = new Cell()
              .setSymbol(catchExpression.getScope().getSymbols().get(caughtTrace.getSymbolName()));

          traceCell.setValue(e.toTraceValue());

          bindingsCells.puts(caughtTrace.getSymbolName(), traceCell);

        }

        stack.push(new StackEntry(catchExpression, bindingsSpace, stack.peek().getClosures()));
        ret = evaluateExpression(catchExpression, stack, context);
        stack.pop();
      }
      else{
        ret = evaluateExpression(catchExpression, stack, context);
      }

    }

    return ret;

  }

  public static Value evaluateThrowNode(ThrowNode throwNode, Stack stack, EvaluationContext context) {
    Value data = evaluateExpression(throwNode.getExceptionExpression(), stack, context);
    stack.push(new StackEntry(throwNode, stack.peek().getSpace(), stack.peek().getClosures()));
    throw new LangException(LangError.CUSTOM_ERROR, "CUSTOM_ERROR", stack, throwNode.getSourceInfo())
        .put("value", data);
  }

  public static Value evaluateInEmptyScope(ExpressionNode node){
    Stack stack = new Stack();
    stack.push(new StackEntry(
        node,
        new GlobalMemorySpace(new GlobalScope()),
        Collections.emptyMap())
    );
    return evaluateExpression(node, stack, new EvaluationContext());
  }

  public static Value evaluateInEmptyScope(ExpressionOp op){
    Stack stack = new Stack();
    stack.push(new StackEntry(
        new NilNode(),
        new GlobalMemorySpace(new GlobalScope()),
        Collections.emptyMap())
    );
    return op.eval(stack, new EvaluationContext());
  }

  public static Value evaluateMetaExpression(MetaDataNode node){
    if (!node.hasMeta()) return Values.NIL;
    ExpressionNode meta = node.getMeta().getExpression();
    if (meta.getOp() == null) new OpBuilderVisitor().visit(meta);
    // meta expressions evaluate in empty space as literals
    return evaluateInEmptyScope(meta);
  }

  public static Value evaluateDocExpression(MetaDataNode node){
    if (!node.hasDoc()) return Values.NIL;
    ExpressionNode doc = node.getDoc().getExpression();
    if (doc.getOp() == null) new OpBuilderVisitor().visit(doc);
    return evaluateInEmptyScope(doc);
  }

  public static Value evaluateViaExpression(ViaNode node){
    if (node.getExpression() == null) return Values.NIL;
    ExpressionNode exp = node.getExpression();
    if (exp.getOp() == null) new OpBuilderVisitor().visit(exp);
    // via expressions evaluate in empty space
    return evaluateInEmptyScope(exp);
  }

  private static Value evaluateExpression(ExpressionNode node, Stack stack, EvaluationContext context){

    try {
      return node.getOp().eval(stack, context);
    }
    catch(Throwable e){
      LangException ex = LangException.wrap(e);
      if (ex.getSourceInfo() == null){
        ex.setSourceInfo(node.getSourceInfo());
      }
      if (ex.getStack() == null){
        ex.setStack(stack);
      }
      throw ex;
    }

  }

  public static Value[] evalArguments(Arguments arguments, Stack stack, EvaluationContext context){
    Value[] args = new Value[arguments.getList().size()];
    List<ArgumentNode> list = arguments.getList();
    for (int i = 0, listSize = list.size(); i < listSize; i++) {
      ArgumentNode node = list.get(i);
      args[i] = evaluateExpression(node.getExpression(), stack, context);
    }
    return args;
  }

  public static Value castArgumentValue(ArgumentNode node, FunctionParameter param, Value rawValue){

    if (rawValue == Values.NIL) return rawValue;

    Type declaredType = param.getDeclaredType();
    if (declaredType == Types.ANY) return rawValue;
    if (declaredType == rawValue.type()) return rawValue;

    try {
      return rawValue.castTo(declaredType);
    } catch (LangException e){
      if (node != null){
        // "cannot cast " + rawValue.type().name() + " to " + param.getDeclaredType().name() + " for parameter " + param.getName(),
        e.setSourceInfo(node.getSourceInfo());
      }
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  public static ConstShapeMap<Cell> mapArgumentsIntoCellMap(Arguments arguments, Value[] argumentValues, FunctionSignature signature) {

    if (argumentValues.length <= 3 && arguments.allPositional()){
      switch (argumentValues.length){
        case 0: return mapArgumentsIntoCellMap(Collections.emptyList(), signature); // TODO: could be done with dedicated option
        case 1: return mapArguments1IntoCellMap(argumentValues[0], signature);
        case 2: return mapArguments2IntoCellMap(argumentValues[0], argumentValues[1], signature);
        case 3: return mapArguments3IntoCellMap(argumentValues[0], argumentValues[1], argumentValues[2], signature);
      }
    }

    ConstShapeMap<Cell> args = new ConstShapeMap<>(signature.getParameterShapeMap());
    Map<String, FunctionParameter> parameterMap = signature.getParameterMap();
    List<FunctionParameter> parameterList = signature.getParameterList();
    List<ArgumentNode> list = arguments.getList();

    // all arguments initialized with default values (could be done with a copy of on-demand map in function signature)
    for (FunctionParameter parameter : signature.getParameterList()) {
      args.seta(parameter.getShapeAccessor(), new Cell().setValue(parameter.getDefaultValue()));
    }

    int currentPositional = 0;
    boolean processedNamed = false;

    // given args overwrite them

    for (int i = 0, listSize = list.size(); i < listSize; i++) {
      ArgumentNode argumentNode = list.get(i);
      if (argumentNode.isPositional()) {

        if (processedNamed){
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "Positional argument cannot follow named arguments.",
              argumentNode.getSourceInfo()
          );
        }
        int index = currentPositional;
        if (index >= parameterList.size()){
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "Function has "+parameterList.size()+" arguments, but positional argument nr. "+(index+1)+" was supplied.",
              argumentNode.getSourceInfo()
          );
        }
        FunctionParameter parameter = parameterList.get(index);
        args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(argumentNode, parameter, argumentValues[i])));
        currentPositional += 1;
      }
      else if (argumentNode.isNamed()){

        processedNamed = true;
        NamedArgumentNode namedArgumentNode = (NamedArgumentNode) argumentNode;
        FunctionParameter parameter = parameterMap.get(namedArgumentNode.getName());

        if (parameter == null){
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "Function does not have parameter named: "+namedArgumentNode.getName(),
              null,
              namedArgumentNode.getSourceInfo()
          );
        }

        args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(argumentNode, parameter, argumentValues[i])));
      }
      else if (argumentNode.isSplat()){
        SplatArgumentNode splatArgumentNode = (SplatArgumentNode) argumentNode;
        Value value = argumentValues[i];

        if (value.isDict()){

          processedNamed = true;
          DictValue splatMap = value.dict();

          for (String s : splatMap.keys()) {
            FunctionParameter parameter = parameterMap.get(s);

            if (parameter == null){
              throw new LangException(
                  LangError.UNEXPECTED_ARGUMENT,
                  "Function does not have parameter named: "+s,
                  null,
                  splatArgumentNode.getSourceInfo()
              );
            }

            args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(argumentNode, parameter, splatMap.get(s))));
          }

        }
        else if (value.isList()){

          if (processedNamed){
            throw new LangException(
                LangError.UNEXPECTED_ARGUMENT,
                "List splat provides positional arguments and cannot follow named arguments.",
                null,
                splatArgumentNode.getSourceInfo()
            );
          }

          ListValue listValue = value.list();

          for (int j=0, size=listValue.size(); j<size; j++){
            int index = currentPositional;
            if (index < 0 || index >= parameterList.size()){
              throw new LangException(
                  LangError.UNEXPECTED_ARGUMENT,
                  "Function has "+parameterList.size()+" arguments, but argument nr. "+(index+1)+" was supplied through splat list.",
                  null,
                  argumentNode.getSourceInfo()
              );
            }
            FunctionParameter parameter = parameterList.get(index);
            args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(argumentNode, parameter, listValue.get(j))));
            currentPositional += 1;

          }
        }
        else if (value.isNil()){
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "splat argument cannot be nil",
              null,
              splatArgumentNode.getSourceInfo()
          );
        }
        else {
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "splat argument has unsupported type: "+value.type().name(),
              null,
              splatArgumentNode.getSourceInfo()
          );

        }

      }

      else {
        throw new AssertionError("Unknown argument node type: "+argumentNode.getClass().getName());
      }
    }

    return args;
  }

  @SuppressWarnings("unchecked")
  public static ConstShapeMap<Value> mapArgumentsIntoValueMap(Arguments arguments, Value[] argumentValues, FunctionSignature signature) {

    if (argumentValues.length <= 3 && arguments.allPositional()){
      switch (argumentValues.length){
        case 0: return mapArgumentsIntoValueMap(Collections.emptyList(), signature); // TODO: could be done with dedicated option
        case 1: return mapArguments1IntoValueMap(argumentValues[0], signature);
        case 2: return mapArguments2IntoValueMap(argumentValues[0], argumentValues[1], signature);
        case 3: return mapArguments3IntoValueMap(argumentValues[0], argumentValues[1], argumentValues[2], signature);
      }
    }

    ConstShapeMap<Value> args = new ConstShapeMap<>(signature.getParameterShapeMap());
    Map<String, FunctionParameter> parameterMap = signature.getParameterMap();
    List<FunctionParameter> parameterList = signature.getParameterList();
    List<ArgumentNode> list = arguments.getList();

    // simple case of positional argument call to unary function?
    if (list.size() == 1 && parameterList.size() == 1){
      ArgumentNode argumentNode = list.get(0);
      if (argumentNode.isPositional()){
        FunctionParameter parameter = parameterList.get(0);
        args.seta(parameter.getShapeAccessor(), castArgumentValue(argumentNode, parameter, argumentValues[0]));
        return args;
      }
    }

    // all arguments initialized with default values (could be done with a copy of on-demand map in function signature)
    for (FunctionParameter parameter : signature.getParameterList()) {
      args.seta(parameter.getShapeAccessor(), parameter.getDefaultValue());
    }

    int currentPositional = 0;
    boolean processedNamed = false;

    // given args overwrite them

    for (int i = 0, listSize = list.size(); i < listSize; i++) {
      ArgumentNode argumentNode = list.get(i);
      if (argumentNode.isPositional()) {

        if (processedNamed){
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "Positional argument cannot follow named arguments.",
              argumentNode.getSourceInfo()
          );
        }
        int index = currentPositional;
        if (index >= parameterList.size()){
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "Function has "+parameterList.size()+" arguments, but positional argument nr. "+(index+1)+" was supplied.",
              argumentNode.getSourceInfo()
          );
        }
        FunctionParameter parameter = parameterList.get(index);
        args.seta(parameter.getShapeAccessor(), castArgumentValue(argumentNode, parameter, argumentValues[i]));
        currentPositional += 1;
      }
      else if (argumentNode.isNamed()){

        processedNamed = true;
        NamedArgumentNode namedArgumentNode = (NamedArgumentNode) argumentNode;
        FunctionParameter parameter = parameterMap.get(namedArgumentNode.getName());

        if (parameter == null){
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "Function does not have parameter named: "+namedArgumentNode.getName(),
              null,
              namedArgumentNode.getSourceInfo()
          );
        }

        args.seta(parameter.getShapeAccessor(), castArgumentValue(argumentNode, parameter, argumentValues[i]));
      }
      else if (argumentNode.isSplat()){
        SplatArgumentNode splatArgumentNode = (SplatArgumentNode) argumentNode;
        Value value = argumentValues[i];

        if (value.isDict()){

          processedNamed = true;
          DictValue splatMap = value.dict();

          for (String s : splatMap.keys()) {
            FunctionParameter parameter = parameterMap.get(s);

            if (parameter == null){
              throw new LangException(
                  LangError.UNEXPECTED_ARGUMENT,
                  "Function does not have parameter named: "+s,
                  null,
                  splatArgumentNode.getSourceInfo()
              );
            }

            args.seta(parameter.getShapeAccessor(), castArgumentValue(argumentNode, parameter, splatMap.get(s)));
          }

        }
        else if (value.isList()){

          if (processedNamed){
            throw new LangException(
                LangError.UNEXPECTED_ARGUMENT,
                "List splat provides positional arguments and cannot follow named arguments.",
                null,
                splatArgumentNode.getSourceInfo()
            );
          }

          ListValue listValue = value.list();

          for (int j=0, size=listValue.size(); j<size; j++){
            int index = currentPositional;
            if (index < 0 || index >= parameterList.size()){
              throw new LangException(
                  LangError.UNEXPECTED_ARGUMENT,
                  "Function has "+parameterList.size()+" arguments, but argument nr. "+(index+1)+" was supplied through splat list.",
                  null,
                  argumentNode.getSourceInfo()
              );
            }
            FunctionParameter parameter = parameterList.get(index);
            args.seta(parameter.getShapeAccessor(), castArgumentValue(argumentNode, parameter, listValue.get(j)));
            currentPositional += 1;

          }
        }
        else if (value.isNil()){
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "splat argument cannot be nil",
              null,
              splatArgumentNode.getSourceInfo()
          );
        }
        else {
          throw new LangException(
              LangError.UNEXPECTED_ARGUMENT,
              "splat argument has unsupported type: "+value.type().name(),
              null,
              splatArgumentNode.getSourceInfo()
          );

        }

      }

      else {
        throw new AssertionError("Unknown argument node type: "+argumentNode.getClass().getName());
      }
    }

    return args;
  }

  @SuppressWarnings({"unchecked"})
  public static Value[] argumentsForUserCall(Arguments arguments, Value[] argumentValues, FunctionSignature signature) {

    FunctionParameter[] parameters = signature.getParameterArray();

    if (arguments.allPositional() && argumentValues.length == parameters.length){
      List<ArgumentNode> argumentNodes = arguments.getList();
      // can simply cast arguments in place
      for (int i=0; i<argumentValues.length; i++){
        argumentValues[i] = argumentValues[i].castTo(parameters[i].getDeclaredType());
      }
      return argumentValues;
    }
    else {
      ConstShapeMap<Value> shapeMap = mapArgumentsIntoValueMap(arguments, argumentValues, signature);
      Value[] args = new Value[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
        args[i] = (Value) parameters[i].getShapeAccessor().get(shapeMap);
      }

      return args;
    }

  }

  @SuppressWarnings({"unchecked"})
  public static Value[] argumentsForPositionalUserCall(Value[] argumentValues, FunctionSignature signature) {

    FunctionParameter[] parameters = signature.getParameterArray();

    if (argumentValues.length == parameters.length){
      // can simply cast arguments in place
      for (int i=0; i< argumentValues.length; i++){
        argumentValues[i] = argumentValues[i].castTo(parameters[i].getDeclaredType());
      }
      return argumentValues;
    }
    else {
      ConstShapeMap<Value> shapeMap = mapArgumentsIntoValueMap(Arrays.asList(argumentValues), signature);
      Value[] args = new Value[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
        args[i] = (Value) parameters[i].getShapeAccessor().get(shapeMap);
      }

      return args;
    }

  }


  @SuppressWarnings("unchecked")
  private static ConstShapeMap<Value> mapArgumentsIntoValueMap(List<Value> arguments, FunctionSignature parameters) {

    ConstShapeMap<Value> args = new ConstShapeMap<>(parameters.getParameterShapeMap());
    List<FunctionParameter> parameterList = parameters.getParameterList();

    int argumentsSize = arguments.size();
    int paramsSize = parameterList.size();

    // short circuit full argument lists
    if (argumentsSize == paramsSize){

      // short circuit single argument calls
      if (argumentsSize == 1){
        Value argument = arguments.get(0);
        FunctionParameter parameter = parameterList.get(0);
        args.seta(parameter.getShapeAccessor(), castArgumentValue(null, parameter, argument));
        return args;
      }

      // short circuit two argument calls
      if (argumentsSize == 2){
        Value a1 = arguments.get(0);
        FunctionParameter p1 = parameterList.get(0);
        args.seta(p1.getShapeAccessor(), castArgumentValue(null, p1, a1));

        Value a2 = arguments.get(1);
        FunctionParameter p2 = parameterList.get(1);
        args.seta(p2.getShapeAccessor(), castArgumentValue(null, p2, a2));
        return args;
      }

      // short circuit three argument calls
      if (argumentsSize == 3){
        Value a1 = arguments.get(0);
        FunctionParameter p1 = parameterList.get(0);
        args.seta(p1.getShapeAccessor(), castArgumentValue(null, p1, a1));

        Value a2 = arguments.get(1);
        FunctionParameter p2 = parameterList.get(1);
        args.seta(p2.getShapeAccessor(), castArgumentValue(null, p2, a2));

        Value a3 = arguments.get(2);
        FunctionParameter p3 = parameterList.get(2);
        args.seta(p3.getShapeAccessor(), castArgumentValue(null, p3, a3));
        return args;
      }

    }


    if (argumentsSize > parameterList.size()) {
      throw new LangException(
          LangError.UNEXPECTED_ARGUMENT,
          "Function has " + paramsSize + " arguments, but positional argument nr. " + (paramsSize+ 1) + " was supplied."
      );
    }

    // args not given are initialized with default ones
    if (argumentsSize < paramsSize){

      for (int a = argumentsSize; a < paramsSize; a++) {
        FunctionParameter param = parameterList.get(a);
        args.seta(param.getShapeAccessor(), param.getDefaultValue());
      }

    }

    // args given are cast
    for (int index = 0; index < argumentsSize; index++) {
      Value argument = arguments.get(index);
      FunctionParameter parameter = parameterList.get(index);
      args.seta(parameter.getShapeAccessor(), castArgumentValue(null, parameter, argument));
    }

    return args;
  }

  @SuppressWarnings("unchecked")
  private static ConstShapeMap<Cell> mapArgumentsIntoCellMap(List<Value> arguments, FunctionSignature parameters) {

    ConstShapeMap<Cell> args = new ConstShapeMap<>(parameters.getParameterShapeMap());
    List<FunctionParameter> parameterList = parameters.getParameterList();

    int argumentsSize = arguments.size();
    int paramsSize = parameterList.size();

    // short circuit full argument lists
    if (argumentsSize == paramsSize){

      // short circuit single argument calls
      if (argumentsSize == 1){
        Value argument = arguments.get(0);
        FunctionParameter parameter = parameterList.get(0);
        args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, parameter, argument)));
        return args;
      }

      // short circuit two argument calls
      if (argumentsSize == 2){
        Value a1 = arguments.get(0);
        FunctionParameter p1 = parameterList.get(0);
        args.seta(p1.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p1, a1)));

        Value a2 = arguments.get(1);
        FunctionParameter p2 = parameterList.get(1);
        args.seta(p2.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p2, a2)));
        return args;
      }

      // short circuit three argument calls
      if (argumentsSize == 3){
        Value a1 = arguments.get(0);
        FunctionParameter p1 = parameterList.get(0);
        args.seta(p1.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p1, a1)));

        Value a2 = arguments.get(1);
        FunctionParameter p2 = parameterList.get(1);
        args.seta(p2.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p2, a2)));

        Value a3 = arguments.get(2);
        FunctionParameter p3 = parameterList.get(2);
        args.seta(p3.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p3, a3)));
        return args;
      }

    }


    if (argumentsSize > parameterList.size()) {
      throw new LangException(
          LangError.UNEXPECTED_ARGUMENT,
          "Function has " + paramsSize + " arguments, but positional argument nr. " + (paramsSize+ 1) + " was supplied."
      );
    }

    // args not given are initialized with default ones
    if (argumentsSize < paramsSize){

      for (int a = argumentsSize; a < paramsSize; a++) {
        FunctionParameter param = parameterList.get(a);
        args.seta(param.getShapeAccessor(), new Cell().setValue(param.getDefaultValue()));
      }

    }

    // args given are cast
    for (int index = 0; index < argumentsSize; index++) {
      Value argument = arguments.get(index);
      FunctionParameter parameter = parameterList.get(index);
      args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, parameter, argument)));
    }

    return args;
  }

  @SuppressWarnings("unchecked")
  private static ConstShapeMap<Value> mapArguments1IntoValueMap(Value arg1, FunctionSignature parameters) {

    ConstShapeMap<Value> args = new ConstShapeMap<>(parameters.getParameterShapeMap());
    FunctionParameter[] parameterArray = parameters.getParameterArray();

    int paramsSize = parameterArray.length;

    // short circuit full argument list
    if (paramsSize == 1){
      FunctionParameter parameter = parameterArray[0];
      args.seta(parameter.getShapeAccessor(), castArgumentValue(null, parameter, arg1));
      return args;
    }


    if (paramsSize < 1) {
      throw new LangException(
          LangError.UNEXPECTED_ARGUMENT,
          "Function has " + paramsSize + " arguments, but positional argument nr. " + (paramsSize+ 1) + " was supplied."
      );
    }

    // args not given are initialized with default ones

    for (int a = 1; a < paramsSize; a++) {
      FunctionParameter param = parameterArray[a];
      args.seta(param.getShapeAccessor(), param.getDefaultValue());
    }


    // arg given is cast
    FunctionParameter parameter = parameterArray[0];
    args.seta(parameter.getShapeAccessor(), castArgumentValue(null, parameter, arg1));

    return args;
  }

  @SuppressWarnings("unchecked")
  private static ConstShapeMap<Cell> mapArguments1IntoCellMap(Value arg1, FunctionSignature signature) {

    ConstShapeMap<Cell> args = new ConstShapeMap<>(signature.getParameterShapeMap());
    FunctionParameter[] parameterArray = signature.getParameterArray();

    int paramsSize = parameterArray.length;

    // short circuit full argument list
    if (paramsSize == 1){
      FunctionParameter parameter = parameterArray[0];
      args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, parameter, arg1)));
      return args;
    }


    if (paramsSize < 1) {
      throw new LangException(
          LangError.UNEXPECTED_ARGUMENT,
          "Function has " + paramsSize + " arguments, but positional argument nr. " + (paramsSize+ 1) + " was supplied."
      );
    }

    // args not given are initialized with default ones

    for (int a = 1; a < paramsSize; a++) {
      FunctionParameter param = parameterArray[a];
      args.seta(param.getShapeAccessor(), new Cell().setValue(param.getDefaultValue()));
    }


    // arg given is cast
    FunctionParameter parameter = parameterArray[0];
    args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, parameter, arg1)));

    return args;
  }

  @SuppressWarnings("unchecked")
  private static ConstShapeMap<Value> mapArguments2IntoValueMap(Value arg1, Value arg2, FunctionSignature parameters) {

    ConstShapeMap<Value> args = new ConstShapeMap<>(parameters.getParameterShapeMap());
    List<FunctionParameter> parameterList = parameters.getParameterList();

    int paramsSize = parameterList.size();

    // short circuit full argument list
    if (paramsSize == 2){
      FunctionParameter p1 = parameterList.get(0);
      args.seta(p1.getShapeAccessor(), castArgumentValue(null, p1, arg1));

      FunctionParameter p2 = parameterList.get(1);
      args.seta(p2.getShapeAccessor(), castArgumentValue(null, p2, arg2));
      return args;
    }


    if (paramsSize < 2) {
      throw new LangException(
          LangError.UNEXPECTED_ARGUMENT,
          "Function has " + paramsSize + " arguments, but positional argument nr. " + (paramsSize+ 1) + " was supplied."
      );
    }

    // args not given are initialized with default ones

    for (int a = 2; a < paramsSize; a++) {
      FunctionParameter param = parameterList.get(a);
      args.seta(param.getShapeAccessor(), param.getDefaultValue());
    }


    // args given are cast
    FunctionParameter parameter = parameterList.get(0);
    args.seta(parameter.getShapeAccessor(), castArgumentValue(null, parameter, arg1));

    FunctionParameter p1 = parameterList.get(0);
    args.seta(p1.getShapeAccessor(), castArgumentValue(null, p1, arg1));

    FunctionParameter p2 = parameterList.get(1);
    args.seta(p2.getShapeAccessor(), castArgumentValue(null, p2, arg2));

    return args;
  }

  @SuppressWarnings("unchecked")
  private static ConstShapeMap<Cell> mapArguments2IntoCellMap(Value arg1, Value arg2, FunctionSignature parameters) {

    ConstShapeMap<Cell> args = new ConstShapeMap<>(parameters.getParameterShapeMap());
    List<FunctionParameter> parameterList = parameters.getParameterList();

    int paramsSize = parameterList.size();

    // short circuit full argument list
    if (paramsSize == 2){
      FunctionParameter p1 = parameterList.get(0);
      args.seta(p1.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p1, arg1)));

      FunctionParameter p2 = parameterList.get(1);
      args.seta(p2.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p2, arg2)));
      return args;
    }


    if (paramsSize < 2) {
      throw new LangException(
          LangError.UNEXPECTED_ARGUMENT,
          "Function has " + paramsSize + " arguments, but positional argument nr. " + (paramsSize+ 1) + " was supplied."
      );
    }

    // args not given are initialized with default ones

    for (int a = 2; a < paramsSize; a++) {
      FunctionParameter param = parameterList.get(a);
      args.seta(param.getShapeAccessor(), new Cell().setValue(param.getDefaultValue()));
    }


    // args given are cast
    FunctionParameter parameter = parameterList.get(0);
    args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, parameter, arg1)));

    FunctionParameter p1 = parameterList.get(0);
    args.seta(p1.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p1, arg1)));

    FunctionParameter p2 = parameterList.get(1);
    args.seta(p2.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p2, arg2)));

    return args;
  }

  @SuppressWarnings("unchecked")
  private static ConstShapeMap<Value> mapArguments3IntoValueMap(Value arg1, Value arg2, Value arg3, FunctionSignature parameters) {

    ConstShapeMap<Value> args = new ConstShapeMap<>(parameters.getParameterShapeMap());
    List<FunctionParameter> parameterList = parameters.getParameterList();

    int paramsSize = parameterList.size();

    // short circuit full argument list
    if (paramsSize == 3){
      FunctionParameter p1 = parameterList.get(0);
      args.seta(p1.getShapeAccessor(), castArgumentValue(null, p1, arg1));

      FunctionParameter p2 = parameterList.get(1);
      args.seta(p2.getShapeAccessor(), castArgumentValue(null, p2, arg2));

      FunctionParameter p3 = parameterList.get(2);
      args.seta(p3.getShapeAccessor(), castArgumentValue(null, p3, arg3));
      return args;
    }


    if (paramsSize < 3) {
      throw new LangException(
          LangError.UNEXPECTED_ARGUMENT,
          "Function has " + paramsSize + " arguments, but positional argument nr. " + (paramsSize+ 1) + " was supplied."
      );
    }

    // args not given are initialized with default ones

    for (int a = 3; a < paramsSize; a++) {
      FunctionParameter param = parameterList.get(a);
      args.seta(param.getShapeAccessor(), param.getDefaultValue());
    }


    // args given are cast
    FunctionParameter parameter = parameterList.get(0);
    args.seta(parameter.getShapeAccessor(), castArgumentValue(null, parameter, arg1));

    FunctionParameter p1 = parameterList.get(0);
    args.seta(p1.getShapeAccessor(), castArgumentValue(null, p1, arg1));

    FunctionParameter p2 = parameterList.get(1);
    args.seta(p2.getShapeAccessor(), castArgumentValue(null, p2, arg2));

    FunctionParameter p3 = parameterList.get(2);
    args.seta(p3.getShapeAccessor(), castArgumentValue(null, p3, arg3));

    return args;
  }

  @SuppressWarnings("unchecked")
  private static ConstShapeMap<Cell> mapArguments3IntoCellMap(Value arg1, Value arg2, Value arg3, FunctionSignature parameters) {

    ConstShapeMap<Cell> args = new ConstShapeMap<>(parameters.getParameterShapeMap());
    List<FunctionParameter> parameterList = parameters.getParameterList();

    int paramsSize = parameterList.size();

    // short circuit full argument list
    if (paramsSize == 3){
      FunctionParameter p1 = parameterList.get(0);
      args.seta(p1.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p1, arg1)));

      FunctionParameter p2 = parameterList.get(1);
      args.seta(p2.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p2, arg2)));

      FunctionParameter p3 = parameterList.get(2);
      args.seta(p3.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p3, arg3)));
      return args;
    }


    if (paramsSize < 3) {
      throw new LangException(
          LangError.UNEXPECTED_ARGUMENT,
          "Function has " + paramsSize + " arguments, but positional argument nr. " + (paramsSize+ 1) + " was supplied."
      );
    }

    // args not given are initialized with default ones

    for (int a = 3; a < paramsSize; a++) {
      FunctionParameter param = parameterList.get(a);
      args.seta(param.getShapeAccessor(), new Cell().setValue(param.getDefaultValue()));
    }


    // args given are cast
    FunctionParameter parameter = parameterList.get(0);
    args.seta(parameter.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, parameter, arg1)));

    FunctionParameter p1 = parameterList.get(0);
    args.seta(p1.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p1, arg1)));

    FunctionParameter p2 = parameterList.get(1);
    args.seta(p2.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p2, arg2)));

    FunctionParameter p3 = parameterList.get(2);
    args.seta(p3.getShapeAccessor(), new Cell().setValue(castArgumentValue(null, p3, arg3)));

    return args;
  }

  static Value performUserCall(Value callableValue, Value[] args, Stack stack, EvaluationContext context){

    if (callableValue.type() != Types.FUNCTION){
      throw new LangException(
          LangError.CANNOT_CALL,
          "Cannot call "+callableValue.toString()+". Not a function.",
          stack
      );
    }
    FunctionValue function = callableValue.function();
    if (function.isStandard()){
      return evaluateStandardFunctionCall(
          (StandardFunctionValue) function,
          mapArgumentsIntoCellMap(Arrays.asList(args), function.getSignature()),
          stack.peek().getNode(),
          stack,
          context
      );
    }
    else{
      return evaluateUserFunctionCall(
          (UserFunctionValue) function,
          Interpreter.argumentsForPositionalUserCall(args, function.getSignature()),
          stack.peek().getNode(),
          stack,
          context
      );
    }

  }

}
