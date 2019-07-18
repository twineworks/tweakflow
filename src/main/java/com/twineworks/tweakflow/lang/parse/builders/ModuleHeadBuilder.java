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

package com.twineworks.tweakflow.lang.parse.builders;

import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseVisitor;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleHeadNode;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.util.ArrayList;
import java.util.List;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.identifier;
import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class ModuleHeadBuilder extends TweakFlowParserBaseVisitor<Node>{

  private final ParseUnit parseUnit;

  public ModuleHeadBuilder(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  @Override
  public Node visitModuleHead(TweakFlowParser.ModuleHeadContext ctx) {

    if (ctx.getStop() == null){
      // not a proper head node
      ModuleHeadNode headNode = new ModuleHeadNode()
          .setSourceInfo(srcOf(parseUnit, ctx.getStart()));
      return headNode;
    }

    ModuleHeadNode headNode = new ModuleHeadNode()
        .setSourceInfo(srcOf(parseUnit, ctx));

    // global declaration?
    TweakFlowParser.NameDecContext globalDecContext = ctx.nameDec();
    if (globalDecContext != null && globalDecContext.identifier() != null){
      headNode.setGlobalName(identifier(globalDecContext.identifier().getText()));
    }

    // imports
    List<ImportNode> imports = new ArrayList<>();

    for (TweakFlowParser.ImportDefContext importDefContext : ctx.importDef()) {
      imports.add(new ImportBuilder(parseUnit).visitImportDef(importDefContext));
    }

    headNode.setImports(imports);

    // aliases
    List<AliasNode> aliases = new ArrayList<>();
    for (TweakFlowParser.AliasDefContext aliasDefContext : ctx.aliasDef()) {
      aliases.add(new AliasBuilder(parseUnit).visitAliasDef(aliasDefContext));
    }

    headNode.setAliases(aliases);

    // exports
    List<ExportNode> exports = new ArrayList<>();
    for (TweakFlowParser.ExportDefContext exportDefContext : ctx.exportDef()) {
      exports.add(new ExportBuilder(parseUnit).visitExportDef(exportDefContext));
    }

    headNode.setExports(exports);

    return headNode;
  }


  private TweakFlowParser.DocContext getDocContext(TweakFlowParser.ModuleHeadContext headContext){

    TweakFlowParser.NameDecContext nameDecContext = headContext.nameDec();
    if (nameDecContext == null) return null;

    TweakFlowParser.MetaDefContext metaDef = nameDecContext.metaDef();

    if (metaDef == null) return null;

    return metaDef.doc();
  }


  /**
   * Helper for null safe extraction of MetaContext from a ModuleContext.
   * @param ctx the ModuleContext to extract MetaContext from
   * @return MetaContext of given ModuleContext or null
   */

  private TweakFlowParser.MetaContext getMetaContext(TweakFlowParser.ModuleHeadContext headContext){

    TweakFlowParser.NameDecContext nameDecContext = headContext.nameDec();
    if (nameDecContext == null) return null;

    TweakFlowParser.MetaDefContext metaDef = nameDecContext.metaDef();
    if (metaDef == null) return null;

    return metaDef.meta();
  }


}
