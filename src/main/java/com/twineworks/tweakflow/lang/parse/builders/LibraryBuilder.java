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

import com.twineworks.tweakflow.lang.ast.Node;
import com.twineworks.tweakflow.lang.ast.structure.LibraryNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseVisitor;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.parse.SourceInfo;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;
import java.util.Map;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.identifier;
import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class LibraryBuilder extends TweakFlowParserBaseVisitor<Node>{

  private final ParseUnit parseUnit;
  private final boolean recovery;
  private final List<LangException> recoveryErrors;

  public LibraryBuilder(ParseUnit parseUnit, boolean recovery, List<LangException> recoveryErrors) {
    this.parseUnit = parseUnit;
    this.recovery = recovery;
    this.recoveryErrors = recoveryErrors;
  }

  @Override
  public LibraryNode visitLibrary(TweakFlowParser.LibraryContext ctx) {

    String name = identifier(ctx.identifier().getText());
    SourceInfo srcInfo = srcOf(parseUnit, ctx);
    LibraryNode library = new LibraryNode()
        .setSourceInfo(srcInfo)
        .setSymbolName(name)
        .setExport(isExport(ctx));

    TweakFlowParser.DocContext docContext = getDocContext(ctx);
    if (docContext != null){
      library.setDoc(new DocBuilder(parseUnit, recovery, recoveryErrors).visitDoc(docContext));
    }

    TweakFlowParser.MetaContext metaContext = getMetaContext(ctx);
    if (metaContext != null){
      library.setMeta(new MetaBuilder(parseUnit, recovery, recoveryErrors).visitMeta(metaContext));
    }

    // add vars
    Map<String, VarDefNode> varDefs = library.getVars().getMap();
    for (TweakFlowParser.LibVarContext libVarContext : ctx.libVar()) {
      VarDefNode varDef = new VarDefBuilder(parseUnit, recovery, recoveryErrors).visitLibVar(libVarContext);
      if (varDefs.containsKey(varDef.getSymbolName())){
        LangException e = new LangException(LangError.ALREADY_DEFINED, varDef.getSymbolName()+" defined more than once in "+name, varDef.getSourceInfo());
        if (recovery){
          recoveryErrors.add(e);
        }
        else{
          throw e;
        }
      }
      varDefs.put(varDef.getSymbolName(), varDef);
    }
    library.getVars().cook();

    return library;
  }

  private boolean isExport(TweakFlowParser.LibraryContext ctx){
    ParseTree token = ctx.getChild(1);
    return token.getText().equals("export");
  }

  /**
   * Helper for null safe extraction of DocContext from a LibraryContext.
   * @param ctx the LibraryContext to extract DocContext from
   * @return DocContext of given LibraryContext or null
   */

  private TweakFlowParser.DocContext getDocContext(TweakFlowParser.LibraryContext ctx){

    TweakFlowParser.MetaDefContext metaDef = ctx.metaDef();
    if (metaDef == null) return null;

    return metaDef.doc();
  }


  /**
   * Helper for null safe extraction of MetaContext from a LibraryContext.
   * @param ctx the LibraryContext to extract MetaContext from
   * @return MetaContext of given LibraryContext or null
   */

  private TweakFlowParser.MetaContext getMetaContext(TweakFlowParser.LibraryContext ctx){

    TweakFlowParser.MetaDefContext metaDef = ctx.metaDef();
    if (metaDef == null) return null;

    return metaDef.meta();
  }

}
