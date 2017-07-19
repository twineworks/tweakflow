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
import com.twineworks.tweakflow.lang.ast.exports.ExportNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.grammar.TweakFlowParser;
import com.twineworks.tweakflow.grammar.TweakFlowParserBaseVisitor;
import com.twineworks.tweakflow.lang.parse.units.ParseUnit;

import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.identifier;
import static com.twineworks.tweakflow.lang.parse.util.CodeParseHelper.srcOf;

public class ExportBuilder extends TweakFlowParserBaseVisitor<Node> {

  private final ParseUnit parseUnit;

  public ExportBuilder(ParseUnit parseUnit) {
    this.parseUnit = parseUnit;
  }

  @Override
  public ExportNode visitExportDef(TweakFlowParser.ExportDefContext ctx) {

    ExpressionBuilder b = new ExpressionBuilder(parseUnit);
    ReferenceNode source = (ReferenceNode) b.visit(ctx.reference());

    String name;
    // implicit name for export
    // export something.using.original_name
    //                        ^ the name of the export
    if (ctx.exportName() == null){
      name = source.getElements().get(source.getElements().size()-1);
    }
    // explicitly named export
    // export something.or.other as foo
    //                              ^ the name of the export
    else {
      name = identifier(ctx.exportName().getText());
    }

    return new ExportNode()
        .setSourceInfo(srcOf(parseUnit, ctx))
        .setSymbolName(name)
        .setSource(source);

  }

}
