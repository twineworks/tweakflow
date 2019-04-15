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

package com.twineworks.tweakflow.lang.parse;

import com.twineworks.tweakflow.lang.ast.expressions.ExpressionNode;
import com.twineworks.tweakflow.lang.ast.expressions.ReferenceNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.parse.units.ResourceParseUnit;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserReferenceTest {

  private HashMap<String, Map<String, VarDefNode>> moduleCache = new HashMap<>();

  private synchronized Map<String, VarDefNode> getVars(String ofModule) {
    if (!moduleCache.containsKey(ofModule)){
      Parser p = new Parser(
          new ResourceParseUnit(new ResourceLocation.Builder().build(), ofModule)
      );
      ParseResult result = p.parseUnit();

      if (result.isError()){
        result.getException().printDigestMessageAndStackTrace();
      }
      // parse is successful
      assertThat(result.isSuccess()).isTrue();

      // get the variable map
      Map<String, VarDefNode> varMap = ((ModuleNode) result.getNode()).getLibraries().get(0).getVars().getMap();
      moduleCache.put(ofModule, varMap);
    }
    return moduleCache.get(ofModule);

  }

  @Test
  public void parses_reference() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/reference.tf");

    // reference: import_name.lib.x
    ExpressionNode reference_node = varDefMap.get("reference").getValueExpression();
    assertThat(reference_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode reference = (ReferenceNode) reference_node;
    assertThat(reference.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> elements = reference.getElements();
    assertThat(elements).hasSize(3);
    assertThat(elements).containsExactly("import_name", "lib", "x");

  }

  @Test
  public void parses_library_reference() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/reference.tf");

    // lib_ref: library::e0            # library reference
    ExpressionNode lib_ref_node = varDefMap.get("lib_ref").getValueExpression();
    assertThat(lib_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode lib_ref = (ReferenceNode) lib_ref_node;
    assertThat(lib_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.LIBRARY);
    List<String> lib_ref_elements = lib_ref.getElements();
    assertThat(lib_ref_elements).hasSize(1);
    assertThat(lib_ref_elements).containsExactly("e0");

  }


  @Test
  public void parses_module_shorthand_reference() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/reference.tf");

    // mod_short_ref: ::lib.e0               # module reference
    ExpressionNode mod_short_ref_node = varDefMap.get("mod_short_ref").getValueExpression();
    assertThat(mod_short_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode mod_short_ref = (ReferenceNode) mod_short_ref_node;
    assertThat(mod_short_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.MODULE);
    List<String> mod_short_ref_elements = mod_short_ref.getElements();
    assertThat(mod_short_ref_elements).hasSize(2);
    assertThat(mod_short_ref_elements).containsExactly("lib", "e0");

  }

  @Test
  public void parses_module_reference() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/reference.tf");

    // mod_ref: module::lib.e0         # module reference
    ExpressionNode mod_ref_node = varDefMap.get("mod_ref").getValueExpression();
    assertThat(mod_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode mod_ref = (ReferenceNode) mod_ref_node;
    assertThat(mod_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.MODULE);
    List<String> mod_ref_elements = mod_ref.getElements();
    assertThat(mod_ref_elements).hasSize(2);
    assertThat(mod_ref_elements).containsExactly("lib", "e0");

  }

  @Test
  public void parses_global_shorthand_reference() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/reference.tf");

//    global_short_ref: $global_var            # global reference
    ExpressionNode global_short_ref_node = varDefMap.get("global_short_ref").getValueExpression();
    assertThat(global_short_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode global_short_ref = (ReferenceNode) global_short_ref_node;
    assertThat(global_short_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.GLOBAL);
    List<String> global_short_ref_elements = global_short_ref.getElements();
    assertThat(global_short_ref_elements).hasSize(1);
    assertThat(global_short_ref_elements).containsExactly("global_var");

  }

  @Test
  public void parses_global_reference() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/reference.tf");

//    global_ref: global::global_var     # global reference
    ExpressionNode global_ref_node = varDefMap.get("global_ref").getValueExpression();
    assertThat(global_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode global_ref = (ReferenceNode) global_ref_node;
    assertThat(global_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.GLOBAL);
    List<String> global_ref_elements = global_ref.getElements();
    assertThat(global_ref_elements).hasSize(1);
    assertThat(global_ref_elements).containsExactly("global_var");

  }

  @Test
  public void parses_local_reference() throws Exception {

    Map<String, VarDefNode> varDefMap = getVars("fixtures/tweakflow/analysis/parsing/semantic/reference.tf");

//    local_ref: e0                     # local reference
    ExpressionNode local_ref_node = varDefMap.get("local_ref").getValueExpression();
    assertThat(local_ref_node).isInstanceOf(ReferenceNode.class);
    ReferenceNode local_ref = (ReferenceNode) local_ref_node;
    assertThat(local_ref.getAnchor()).isSameAs(ReferenceNode.Anchor.LOCAL);
    List<String> local_ref_elements = local_ref.getElements();
    assertThat(local_ref_elements).hasSize(1);
    assertThat(local_ref_elements).containsExactly("e0");
  }

}