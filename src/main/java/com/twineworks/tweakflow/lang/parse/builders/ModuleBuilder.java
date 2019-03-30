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

import com.twineworks.tweakflow.lang.ast.ComponentNode;
import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.aliases.AliasNode;
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.imports.ImportNode;
import com.twineworks.tweakflow.lang.ast.structure.LibraryNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseVisitor;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.identifier;
import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class ModuleBuilder extends TweakFlowParserBaseVisitor<Node>{

  private final ParseUnit parseUnit;

  public ModuleBuilder(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  @Override
  public ModuleNode visitModule(TweakFlowParser.ModuleContext ctx) {

    ModuleNode module = new ModuleNode()
        .setSourceInfo(srcOf(parseUnit, ctx));


    // global declaration?
    TweakFlowParser.NameDecContext globalDecContext = getNameDecContext(ctx);
    if (globalDecContext != null && globalDecContext.identifier() != null){
      module.setGlobalName(identifier(globalDecContext.identifier().getText()));
    }

    // imports
    List<ImportNode> imports = new ArrayList<>();

    for (TweakFlowParser.ImportDefContext importDefContext : getImportDefs(ctx)) {
      imports.add(new ImportBuilder(parseUnit).visitImportDef(importDefContext));
    }

    module.setImports(imports);

    // aliases
    List<AliasNode> aliases = new ArrayList<>();
    for (TweakFlowParser.AliasDefContext aliasDefContext : getAliasDefs(ctx)) {
      aliases.add(new AliasBuilder(parseUnit).visitAliasDef(aliasDefContext));
    }

    module.setAliases(aliases);

    // exports
    List<ExportNode> exports = new ArrayList<>();
    for (TweakFlowParser.ExportDefContext exportDefContext : getExportDefs(ctx)) {
      exports.add(new ExportBuilder(parseUnit).visitExportDef(exportDefContext));
    }

    module.setExports(exports);

    // doc
    TweakFlowParser.DocContext docContext = getDocContext(ctx);
    if (docContext != null){
      module.setDoc(new DocBuilder(parseUnit).visitDoc(docContext));
    }

    // meta
    TweakFlowParser.MetaContext metaContext = getMetaContext(ctx);
    if (metaContext != null){
      module.setMeta(new MetaBuilder(parseUnit).visitMeta(metaContext));
    }

    // components

    List<ComponentNode> components = new ArrayList<>();
    for (TweakFlowParser.ModuleComponentContext comp : ctx.moduleComponent()) {

      // library
      if (comp instanceof TweakFlowParser.LibraryComponentContext){
        TweakFlowParser.LibraryComponentContext libraryCtx = (TweakFlowParser.LibraryComponentContext) comp;
        LibraryNode library = new LibraryBuilder(parseUnit).visitLibrary(libraryCtx.library());
        components.add(library);
      }

    }

    module.setComponents(components);

    return module;
  }

  /**
   * Helper for null safe extraction of ExportDefs from a Module context.
   * @param ctx
   * @return List of export definitions. Returns empty list if no exports defined.
   */
  private List<TweakFlowParser.ExportDefContext> getExportDefs(TweakFlowParser.ModuleContext ctx){
    TweakFlowParser.ModuleHeadContext headContext = ctx.moduleHead();
    if (headContext == null) return Collections.emptyList();
    if (headContext.exportDef() == null) return Collections.emptyList();
    return headContext.exportDef();
  }

  /**
   * Helper for null safe extraction of ImportDefs from a Module context.
   * @param ctx
   * @return List of import definitions. Returns empty list if no imports defined.
   */
  private List<TweakFlowParser.ImportDefContext> getImportDefs(TweakFlowParser.ModuleContext ctx){
    TweakFlowParser.ModuleHeadContext headContext = ctx.moduleHead();
    if (headContext == null) return Collections.emptyList();
    if (headContext.importDef() == null) return Collections.emptyList();
    return headContext.importDef();
  }

  /**
   * Helper for null safe extraction of AliasDefs from a Module context.
   * @param ctx
   * @return List of alias definitions. Returns empty list if no aliases defined.
   */
  private List<TweakFlowParser.AliasDefContext> getAliasDefs(TweakFlowParser.ModuleContext ctx){
    TweakFlowParser.ModuleHeadContext headContext = ctx.moduleHead();
    if (headContext == null) return Collections.emptyList();
    if (headContext.aliasDef() == null) return Collections.emptyList();
    return headContext.aliasDef();
  }

  /**
   * Helper for null safe extraction of DocContext from a ModuleContext.
   * @param ctx the ModuleContext to extract DocContext from
   * @return DocContext of given ModuleContext or null
   */

  private TweakFlowParser.DocContext getDocContext(TweakFlowParser.ModuleContext ctx){

    TweakFlowParser.ModuleHeadContext headContext = ctx.moduleHead();
    if (headContext == null) return null;

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

  private TweakFlowParser.MetaContext getMetaContext(TweakFlowParser.ModuleContext ctx){

    TweakFlowParser.ModuleHeadContext headContext = ctx.moduleHead();
    if (headContext == null) return null;

    TweakFlowParser.NameDecContext nameDecContext = headContext.nameDec();
    if (nameDecContext == null) return null;

    TweakFlowParser.MetaDefContext metaDef = nameDecContext.metaDef();
    if (metaDef == null) return null;

    return metaDef.meta();
  }

  private TweakFlowParser.NameDecContext getNameDecContext(TweakFlowParser.ModuleContext ctx){

    TweakFlowParser.ModuleHeadContext headContext = ctx.moduleHead();
    if (headContext == null) return null;

    return headContext.nameDec();
  }

}
