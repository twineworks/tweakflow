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

package com.twineworks.tweakflow.lang.parse.builders;

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.imports.NameImportNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportMemberNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.imports.ModuleImportNode;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseVisitor;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.util.ArrayList;
import java.util.List;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.identifier;
import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class ImportBuilder extends TweakFlowParserBaseVisitor<Node> {

  private final ParseUnit parseUnit;

  public ImportBuilder(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  @Override
  public ImportNode visitImportDef(TweakFlowParser.ImportDefContext ctx) {

    // module path
    ExpressionNode pathNode = new ExpressionBuilder(parseUnit).visit(ctx.modulePath());

    // members
    List<ImportMemberNode> members = new ArrayList<>();

    for (TweakFlowParser.ImportMemberContext membersContext : ctx.importMember()) {
      if (membersContext.moduleImport() != null){
        members.add(visitModuleImport(membersContext.moduleImport()));
      }
      else if (membersContext.componentImport() != null){
        members.add(visitComponentImport(membersContext.componentImport()));
      }
    }

    return new ImportNode()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setModulePath(pathNode)
        .setMembers(members);
  }

  @Override
  public ModuleImportNode visitModuleImport(TweakFlowParser.ModuleImportContext ctx) {

    return new ModuleImportNode()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setImportName(identifier(ctx.importModuleName().getText()));
  }

  @Override
  public NameImportNode visitComponentImport(TweakFlowParser.ComponentImportContext ctx) {
    String exportName = identifier(ctx.exportComponentName().getText());
    String importName = exportName;
    if (ctx.importComponentName() != null){
      importName = identifier(ctx.importComponentName().getText());
    }
    return new NameImportNode()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setImportName(importName)
        .setExportName(exportName);
  }
}
