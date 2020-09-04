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

package com.twineworks.tweakflow.lang.analysis.scope;

import com.twineworks.tweakflow.lang.analysis.visitors.AExpressionDescendingVisitor;
import com.twineworks.tweakflow.lang.analysis.visitors.Visitor;
import com.twineworks.tweakflow.lang.ast.ForHeadElementNode;
import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.args.*;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.expressions.*;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.imports.ModuleImportNode;
import com.twineworks.tweakflow.lang.ast.imports.NameImportNode;
import com.twineworks.tweakflow.lang.ast.meta.DocNode;
import com.twineworks.tweakflow.lang.ast.meta.MetaNode;
import com.twineworks.tweakflow.lang.ast.meta.ViaNode;
import com.twineworks.tweakflow.lang.ast.partial.PartialArgumentNode;
import com.twineworks.tweakflow.lang.ast.partial.PartialArguments;
import com.twineworks.tweakflow.lang.ast.structure.*;
import com.twineworks.tweakflow.lang.ast.structure.match.*;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.scope.*;
import com.twineworks.tweakflow.lang.types.Types;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/*
  SymbolInterface scopes and local scopes are created for every node.
  No reference checks or reference resolution is done here.
  Merely scope construction and population.

  Global scope is given during construction.
*/

public class ScopeBuilderVisitor extends AExpressionDescendingVisitor implements Visitor {

  private final ArrayDeque<Scope> scopes = new ArrayDeque<>();
  private final ArrayDeque<Scope> matchLineScopes = new ArrayDeque<>();

  private final boolean recovery;
  private final List<LangException> recoveryErrors;

  public ScopeBuilderVisitor(Scope topLevelScope, boolean recovery, List<LangException> recoveryErrors) {
    this.recovery = recovery;
    this.recoveryErrors = recoveryErrors;
    scopes.push(topLevelScope);
  }

  public ScopeBuilderVisitor(Scope topLevelScope) {
      this(topLevelScope, false, null);
  }

  @Override
  public InteractiveNode visit(InteractiveNode node) {
    if (node.getScope() != null) return node;

    GlobalScope globalScope = (GlobalScope) scopes.peek();
    LocalScope unitScope = globalScope.getUnitScope();

    Symbol unitSymbol = new Symbol()
        .setScope(unitScope)
        .setEnclosingScope(globalScope)
        .setName(node.getSourceInfo().getParseUnit().getPath())
        .setType(SymbolType.LOCAL)
        .setNode(node)
        .setTarget(SymbolTarget.INTERACTIVE);

    // already present?
    if (unitScope.getSymbols().containsKey(unitSymbol.getName())) {
      LangException e = new LangException(LangError.ALREADY_DEFINED, unitSymbol.getName() + " already loaded");
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }
    }
    unitScope.getSymbols().put(unitSymbol.getName(), unitSymbol);

    node.setScope(unitScope);
    node.setUnitSymbol(unitSymbol);
    scopes.push(unitSymbol);

    node.getSections().forEach(this::visit);

    scopes.pop();
    return node;
  }

  @Override
  public ModuleNode visit(ModuleNode node) {

    if (node.getScope() != null) return node;

    if (node.hasDoc())
      visit(node.getDoc());

    if (node.hasMeta())
      visit(node.getMeta());

    GlobalScope globalScope = (GlobalScope) scopes.peek();
    LocalScope unitScope = globalScope.getUnitScope();

    Symbol unitSymbol = new Symbol()
        .setScope(unitScope)
        .setEnclosingScope(globalScope)
        .setName(node.getSourceInfo().getParseUnit().getPath())
        .setType(SymbolType.LOCAL)
        .setNode(node)
        .setTarget(SymbolTarget.MODULE);

    // already present?
    if (unitScope.getSymbols().containsKey(unitSymbol.getName())) {
      LangException e = new LangException(LangError.ALREADY_DEFINED, unitSymbol.getName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }

    }
    unitScope.getSymbols().put(unitSymbol.getName(), unitSymbol);

    // if the module declares itself global, an import symbol references it in global scope

    if (node.isGlobal()) {
      Symbol globalModuleSymbol = new Symbol()
          .setScope(globalScope)
          .setEnclosingScope(globalScope)
          .setName(node.getGlobalName())
          .setNode(node)
          .setType(SymbolType.MODULE_IMPORT)
          .setExport(true)
          .setRef(unitSymbol)
          .setTarget(SymbolTarget.MODULE);

      // already present?
      if (globalScope.getSymbols().containsKey(globalModuleSymbol.getName())) {
        LangException e = new LangException(LangError.ALREADY_DEFINED, "global " + globalModuleSymbol.getName() + " already defined", node.getSourceInfo());
        if (recovery) {
          recoveryErrors.add(e);
        } else {
          throw e;
        }
      }

      globalScope.getSymbols().put(globalModuleSymbol.getName(), globalModuleSymbol);
    }

    node.setScope(unitScope);
    node.setUnitSymbol(unitSymbol);

    scopes.push(unitSymbol);

    node.getImports().forEach(this::visit);
    node.getAliases().forEach(this::visit);
    node.getComponents().forEach(this::visit);
    node.getExports().forEach(this::visit);

    scopes.pop();

    return node;
  }

  @Override
  public ExportNode visit(ExportNode node) {

    if (node.getScope() != null) return node;

    Scope scope = scopes.peek();

    Symbol exportSymbol = new Symbol()
        .setName(node.getSymbolName())
        .setNode(node)
        .setScope(scope)
        .setEnclosingScope(scope)
        .setType(SymbolType.EXPORT)
        .setExport(true)
        .setRefNode(node.getSource())
        .setTarget(SymbolTarget.UNKNOWN);

    // cannot clash with anything already exported
    if (scope.getPublicScope().getSymbols().containsKey(node.getSymbolName())) {
      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }

    }

    scope.getPublicScope().getSymbols().put(node.getSymbolName(), exportSymbol);
    node.setScope(scope);

    visit(node.getSource());

    return node;

  }

  @Override
  public VarDefs visit(VarDefs node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public VarDecs visit(VarDecs node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public LibraryNode visit(LibraryNode node) {

    if (node.getScope() != null) return node;

    if (node.hasDoc())
      visit(node.getDoc());

    if (node.hasMeta())
      visit(node.getMeta());

    Scope scope = scopes.peek();

    Symbol librarySymbol = new Symbol()
        .setName(node.getSymbolName())
        .setNode(node)
        .setScope(scope)
        .setEnclosingScope(scope)
        .setType(SymbolType.LOCAL)
        .setExport(node.isExport())
        .setTarget(SymbolTarget.LIBRARY);

    if (scope.getSymbols().containsKey(node.getSymbolName())) {

      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }
    }

    scope.getSymbols().put(node.getSymbolName(), librarySymbol);
    node.setScope(scope);

    // if it's exported, it cannot clash with existing exports
    if (node.isExport()) {
      if (scope.getPublicScope().getSymbols().containsKey(node.getSymbolName())) {
        LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " export already defined", node.getSourceInfo());
        if (recovery) {
          recoveryErrors.add(e);
        } else {
          throw e;
        }
      }

      scope.getPublicScope().getSymbols().put(node.getSymbolName(), librarySymbol);
    }

    scopes.push(librarySymbol);
    visit(node.getVars());
    scopes.pop();

    return node;
  }

  @Override
  public VarDecNode visit(VarDecNode node) {

    if (node.getScope() != null) return node;

    if (node.hasDoc())
      visit(node.getDoc());

    if (node.hasMeta())
      visit(node.getMeta());

    Scope scope = scopes.peek();
    node.setScope(scope);

    if (scope.getSymbols().containsKey(node.getSymbolName())) {
      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }
    }

    scope.getSymbols().put(node.getSymbolName(), new Symbol()
        .setName(node.getSymbolName())
        .setTarget(SymbolTarget.VAR)
        .setType(SymbolType.LOCAL)
        .setVarType(node.getDeclaredType())
        .setNode(node)
        .setScope(scope));

    return node;
  }

  @Override
  public VarDefNode visit(VarDefNode node) {

    if (node.getScope() != null) return node;

    if (node.hasDoc())
      visit(node.getDoc());

    if (node.hasMeta())
      visit(node.getMeta());

    Scope scope = scopes.peek();
    node.setScope(scope);

    if (scope.getSymbols().containsKey(node.getSymbolName())) {
      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }
    }

    scope.getSymbols().put(node.getSymbolName(), new Symbol()
        .setName(node.getSymbolName())
        .setTarget(SymbolTarget.VAR)
        .setType(SymbolType.LOCAL)
        .setVarType(node.getDeclaredType())
        .setNode(node)
        .setScope(scope));

    visit(node.getValueExpression());

    return node;
  }

  @Override
  public ExpressionNode visit(LetNode node) {

    if (node.getScope() != null) return node;

    Scope scope = scopes.peek();
    node.setScope(scope);

    LocalScope bindingsScope = new LocalScope(scope, ScopeType.LOCAL);
    scopes.push(bindingsScope);

    super.visit(node);

    scopes.pop();

    return node;
  }

  @Override
  public ExpressionNode visit(MatchNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public MatchLines visit(MatchLines node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public MatchLineNode visit(MatchLineNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);

    LocalScope bindingsScope = new LocalScope(scope, ScopeType.LOCAL);

    MatchPatternNode pattern = node.getPattern();

    if (pattern == null && recovery) {
      recoveryErrors.add(new LangException(LangError.PARSE_ERROR, "missing match pattern in: ", node.getSourceInfo()));
    } else {
      matchLineScopes.push(bindingsScope);
      visit(pattern);
      matchLineScopes.pop();
    }


    scopes.push(bindingsScope);

    if (node.getGuard() != null) {
      visit(node.getGuard());
    }
    visit(node.getExpression());

    // pop the bindings scope
    scopes.pop();

    return node;
  }

  @Override
  public ExpressionPatternNode visit(ExpressionPatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public DefaultPatternNode visit(DefaultPatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public ListPatternNode visit(ListPatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public HeadTailListPatternNode visit(HeadTailListPatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public InitLastListPatternNode visit(InitLastListPatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public MidListPatternNode visit(MidListPatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public DictPatternNode visit(DictPatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }


  @Override
  public OpenDictPatternNode visit(OpenDictPatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }


  @Override
  public DataTypePatternNode visit(DataTypePatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public CapturePatternNode visit(CapturePatternNode node) {
    if (node.getScope() != null) return node;
    Scope scope = matchLineScopes.peek();
    node.setScope(scope);
    super.visit(node);

    if (node.getSymbolName() != null) {

      if (scope.getSymbols().containsKey(node.getSymbolName())) {
        LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
        if (recovery) {
          recoveryErrors.add(e);
        } else {
          throw e;
        }
      }

      scope.getSymbols().put(node.getSymbolName(), new Symbol()
          .setName(node.getSymbolName())
          .setTarget(SymbolTarget.VAR)
          .setType(SymbolType.LOCAL)
          .setVarType(Types.ANY)
          .setNode(node)
          .setScope(scope));

    }

    return node;
  }

  @Override
  public ExpressionNode visit(ForNode node) {

    if (node.getScope() != null) return node;

    Scope scope = scopes.peek();
    node.setScope(scope);
    LocalScope bindingsScope = new LocalScope(scope, ScopeType.LOCAL, true);
    scopes.push(bindingsScope);
    super.visit(node);
    scopes.pop();
    return node;
  }

  @Override
  public ForHead visit(ForHead node) {

    if (node.getScope() != null) return node;

    Scope scope = scopes.peek();
    node.setScope(scope);

    // ensure order of elements is sane
    ArrayList<ForHeadElementNode> elements = node.getElements();
    for (ForHeadElementNode element : elements) {
      visit(element);
    }

    return node;
  }

  @Override
  public GeneratorNode visit(GeneratorNode node) {

    if (node.getScope() != null) return node;

    Scope scope = scopes.peek();
    node.setScope(scope);

    if (scope.getSymbols().containsKey(node.getSymbolName())) {
      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }

    }

    scope.getSymbols().put(node.getSymbolName(), new Symbol()
        .setName(node.getSymbolName())
        .setTarget(SymbolTarget.VAR)
        .setType(SymbolType.LOCAL)
        .setVarType(node.getDeclaredType())
        .setNode(node)
        .setScope(scope));

    visit(node.getValueExpression());

    return node;

  }

  @Override
  public DictEntryNode visit(DictEntryNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }


  @Override
  public ImportNode visit(ImportNode node) {
    if (node.getScope() != null) return node;

    node.setScope(scopes.peek());
    visit(node.getModulePath());
    node.getMembers().forEach(this::visit);
    return node;
  }


  @Override
  public NameImportNode visit(NameImportNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);

    // ensure module scope does not already define that name
    if (scope.getSymbols().containsKey(node.getSymbolName())) {

      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }

    }

    Symbol importSymbol = new Symbol()
        .setName(node.getSymbolName())
        .setNode(node)
        .setScope(scope)
        .setEnclosingScope(scope)
        .setType(SymbolType.NAME_IMPORT)
        .setRefName(node.getExportName())
        .setTarget(SymbolTarget.UNKNOWN);

    scope.getSymbols().put(node.getSymbolName(), importSymbol);
    return node;
  }

  @Override
  public ModuleImportNode visit(ModuleImportNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);

    // ensure module scope does not already define that name
    if (scope.getSymbols().containsKey(node.getSymbolName())) {

      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }

    }

    Symbol importSymbol = new Symbol()
        .setName(node.getSymbolName())
        .setNode(node)
        .setScope(scope)
        .setEnclosingScope(scope)
        .setType(SymbolType.MODULE_IMPORT)
        .setRefName("")
        .setTarget(SymbolTarget.UNKNOWN);

    scope.getSymbols().put(node.getSymbolName(), importSymbol);

    return node;
  }

  @Override
  public AliasNode visit(AliasNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);

    // ensure module scope does not already define that name
    if (scope.getSymbols().containsKey(node.getSymbolName())) {

      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }

    }

    Symbol aliasSymbol = new Symbol()
        .setName(node.getSymbolName())
        .setNode(node)
        .setScope(scope)
        .setEnclosingScope(scope)
        .setType(SymbolType.ALIAS)
        .setRefNode(node.getSource())
        .setTarget(SymbolTarget.UNKNOWN);

    scope.getSymbols().put(node.getSymbolName(), aliasSymbol);
    visit(node.getSource());
    return node;
  }


  @Override
  public ExpressionNode visit(ThrowNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }


  @Override
  public ExpressionNode visit(TryCatchNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);
    visit(node.getTryExpression());

    // open a scope for named exception catch
    LocalScope catchScope = new LocalScope(scope);

    VarDecNode caughtException = node.getCaughtException();
    if (caughtException != null) {
      Symbol exceptionSymbol = new Symbol()
          .setName(caughtException.getSymbolName())
          .setVarType(caughtException.getDeclaredType())
          .setType(SymbolType.LOCAL)
          .setScope(catchScope)
          .setNode(caughtException)
          .setTarget(SymbolTarget.VAR);
      catchScope.getSymbols().put(caughtException.getSymbolName(), exceptionSymbol);
      caughtException.setScope(catchScope);
    }

    VarDecNode caughtTrace = node.getCaughtTrace();
    if (caughtTrace != null) {
      Symbol traceSymbol = new Symbol()
          .setName(caughtTrace.getSymbolName())
          .setVarType(Types.LIST)
          .setType(SymbolType.LOCAL)
          .setScope(catchScope)
          .setNode(caughtTrace)
          .setTarget(SymbolTarget.VAR);
      catchScope.getSymbols().put(caughtTrace.getSymbolName(), traceSymbol);
      caughtTrace.setScope(catchScope);
    }

    scopes.push(catchScope);
    visit(node.getCatchExpression());
    scopes.pop();

    return node;
  }

  @Override
  public ExpressionNode visit(CastNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(PartialApplicationNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(CallNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public PartialArguments visit(PartialArguments node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public PartialArgumentNode visit(PartialArgumentNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public Arguments visit(Arguments node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ArgumentNode visit(NamedArgumentNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ArgumentNode visit(PositionalArgumentNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ArgumentNode visit(SplatArgumentNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(FunctionNode node) {
    if (node.getScope() != null) return node;
    Scope scope = scopes.peek();
    node.setScope(scope);

    if (node.getVia() != null)
      visit(node.getVia());

    LocalScope functionScope = new LocalScope(scope, ScopeType.FUNCTION);
    scopes.push(functionScope);

    visit(node.getParameters());

    // if the function is defined natively using 'via' the expression is null
    if (node.getExpression() != null) {
      visit(node.getExpression());
    }

    scopes.pop();

    return node;
  }

  @Override
  public Parameters visit(Parameters node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ParameterNode visit(ParameterNode node) {

    if (node.getScope() != null) return node;

    Scope scope = scopes.peek();
    node.setScope(scope);

    if (scope.getSymbols().containsKey(node.getSymbolName())) {
      LangException e = new LangException(LangError.ALREADY_DEFINED, node.getSymbolName() + " already defined", node.getSourceInfo());
      if (recovery) {
        recoveryErrors.add(e);
      } else {
        throw e;
      }

    }

    scope.getSymbols().put(node.getSymbolName(), new Symbol()
        .setName(node.getSymbolName())
        .setVarType(node.getDeclaredType())
        .setScope(scope)
        .setNode(node)
        .setType(SymbolType.LOCAL)
        .setTarget(SymbolTarget.VAR));

    visit(node.getDefaultValue());

    return node;
  }

  @Override
  public ExpressionNode visit(ListNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(DictNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ViaNode visit(ViaNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public BindingsNode visit(BindingsNode node) {
    Scope scope = scopes.peek();
    node.setScope(scope);
    super.visit(node);
    return node;
  }

  @Override
  public InteractiveSectionNode visit(InteractiveSectionNode node) {

    if (node.getScope() != null) return node;

    Scope scope = scopes.peek();
    node.setScope(scope);

    ReferenceNode inScope = node.getInScopeRef();
    String symbolName = inScope.getElements().get(0);
    Symbol symbol = new Symbol()
        .setName(symbolName)
        .setRefNode(inScope)
        .setTarget(SymbolTarget.INTERACTIVE_SECTION)
        .setNode(node);

    node.setSymbol(symbol);
    scope.getSymbols().put(symbolName, symbol);

    // interactive section reference resolves in unit scope, so it finds modules by path
    Scope unitScope = ((Symbol) scope).getScope();
    scopes.push(unitScope);
    visit(inScope);
    scopes.pop();

    // bindings live in scope attached to whatever the section links to
    // linker establishes connection later
    LocalScope bindingsScope = new LocalScope(null, ScopeType.LOCAL);
    scopes.push(bindingsScope);
    visit(node.getVars());
    scopes.pop();

    return node;
  }

  @Override
  public ExpressionNode visit(ContainerAccessNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(IfNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(IsNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }


  @Override
  public ExpressionNode visit(TypeOfNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(DefaultNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(ReferenceNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public ExpressionNode visit(BooleanNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public ExpressionNode visit(BinaryNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public ExpressionNode visit(DecimalNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public ExpressionNode visit(LongNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public ExpressionNode visit(DoubleNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public ExpressionNode visit(StringNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public ExpressionNode visit(DateTimeNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public ExpressionNode visit(NilNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public AndNode visit(AndNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public OrNode visit(OrNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(LessThanNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(LessThanOrEqualNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public GreaterThanNode visit(GreaterThanNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public GreaterThanOrEqualNode visit(GreaterThanOrEqualNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public NotNode visit(NotNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public NegateNode visit(NegateNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public EqualNode visit(EqualNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public NotEqualNode visit(NotEqualNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }


  @Override
  public ExpressionNode visit(ValueAndTypeEqualsNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(NotValueAndTypeEqualsNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public PlusNode visit(PlusNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public StringConcatNode visit(StringConcatNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ListConcatNode visit(ListConcatNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public DictMergeNode visit(DictMergeNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public MultNode visit(MultNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public PowNode visit(PowNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public DivNode visit(DivNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public IntDivNode visit(IntDivNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ModNode visit(ModNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public MinusNode visit(MinusNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }


  @Override
  public MetaNode visit(MetaNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public DocNode visit(DocNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    return node;
  }

  @Override
  public DebugNode visit(DebugNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public BitwiseNotNode visit(BitwiseNotNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseAndNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseOrNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseXorNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseShiftLeftNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwisePreservingShiftRightNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

  @Override
  public ExpressionNode visit(BitwiseZeroShiftRightNode node) {
    if (node.getScope() != null) return node;
    node.setScope(scopes.peek());
    super.visit(node);
    return node;
  }

}
