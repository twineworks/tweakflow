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

package com.twineworks.tweakflow.lang.analysis;

import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.twineworks.tweakflow.lang.ast.NodeStructureAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class RecoveryAnalysisTest {

  private AnalysisResult analyze(String... paths) {

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();

    List<String> filePaths = new ArrayList<>(paths.length);
    filePaths.addAll(Arrays.asList(paths));

    return Analysis.recoveryAnalysis(filePaths, loadPath, false);
  }

  @Test
  public void analyzes_empty_module() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/empty.tf");

    // parse is successful
    assertThat(result.isSuccess()).isTrue();

    // no recovery errors
    assertThat(result.getRecoveryErrors()).isEmpty();

    // module node is empty
    ModuleNode module = (ModuleNode) result.getAnalysisSet().getUnits().get("fixtures/tweakflow/analysis/empty.tf").getUnit();
    assertThat(module).isNotNull();
    assertThat(module.hasMeta()).isFalse();
    assertThat(module.hasDoc()).isFalse();

  }

  @Test
  public void accepts_non_literal_docs() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/meta/errors/non_literal_docs.tf");
    assertThat(result.isSuccess()).isTrue();

    // has a recovery error
    assertThat(result.getRecoveryErrors().size()).isEqualTo(1);

    // doc () -> "foo" # is invalid
    LangException e = result.getRecoveryErrors().get(0);
    assertThat(e.getCode()).isEqualTo(LangError.LITERAL_VALUE_REQUIRED);
    assertThat(e.getSourceInfo().getSourceCode()).isEqualTo("() -> \"foo\"");
  }


  @Test
  public void accepts_non_literal_meta_data() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/meta/errors/non_literal_metadata.tf");
    assertThat(result.isSuccess()).isTrue();

    // has a recovery error
    assertThat(result.getRecoveryErrors().size()).isEqualTo(1);

//    meta {
//      :a "foo",
//      :b 1+1
//    }
    LangException e = result.getRecoveryErrors().get(0);
    assertThat(e.getCode()).isEqualTo(LangError.LITERAL_VALUE_REQUIRED);
    assertThat(e.getSourceInfo().getSourceCode()).isEqualTo("1+1");

  }

  @Test
  public void accepts_non_constant_default_parameter() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/non_const_default_parameter.tf");
    assertThat(result.isSuccess()).isTrue();

    // has a recovery error
    assertThat(result.getRecoveryErrors().size()).isEqualTo(1);

    // f: (string x=a) -> x;
    LangException e = result.getRecoveryErrors().get(0);
    assertThat(e.getCode()).isEqualTo(LangError.LITERAL_VALUE_REQUIRED);
    assertThat(e.getSourceInfo().getSourceCode()).isEqualTo("string x=a");

  }

  @Test
  public void accepts_missing_import() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/imports/errors/missing_import.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_unresolved_variable_reference() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/unresolved_var_reference.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_variable_referencing_a_non_var() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/invalid_var_reference.tf");
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void accepts_for_unordered_scope() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/for_unordered_scope.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_for_shadowed_variable() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/for_shadowing.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_multiple_default_match_patterns() throws Exception {
    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/match_multiple_default_patterns.tf");
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void accepts_default_match_pattern_not_last() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/match_default_pattern_not_last.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_match_pattern_referencing_binding() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/match_pattern_referencing_binding.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_match_pattern_invalid_open_dict_capture() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/match_pattern_invalid_open_dict_capture.tf");
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void accepts_call_referencing_a_non_function() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/invalid_call_reference.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_unresolved_variable_reference_in_import() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/unresolved_var_reference_in_import.tf");
    assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void accepts_unresolved_multi_element_variable_reference() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/unresolved_multi_element_var_reference.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_redefined_library_variable() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/libraries/errors/already_defined_lib_var.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_redefined_function_parameter() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/already_defined_function_parameter.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_redefined_function_partial_arg() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/already_defined_function_partial_arg.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_redefined_import() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/imports/errors/already_defined_import.tf");
    assertThat(result.isSuccess()).isTrue();

  }


  @Test
  public void accepts_explicit_cast_of_boolean_to_list() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/casts/explicit_cast_boolean_to_list.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_implicit_cast_of_boolean_to_list() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/casts/implicit_cast_boolean_to_list.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_invalid_long() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/values/invalid_long.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_cyclic_dependency_in_expression() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/cyclic_dependency_in_expression.tf");

    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_cyclic_dependency_in_expression_across_modules() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/imports/errors/cycle/cycle_expression_module_a.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_cyclic_dependency_in_let_definition() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/cyclic_dependency_in_let_definition.tf");
    assertThat(result.isSuccess()).isTrue();

  }


  @Test
  public void accepts_cyclic_dependency_in_bindings_definition() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/cyclic_dependency_in_bindings_definition.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_cyclic_dependency_in_sub_expression() throws Exception {

    AnalysisResult result = analyze("fixtures/tweakflow/analysis/expressions/errors/cyclic_dependency_in_sub_expression.tf");
    assertThat(result.isSuccess()).isTrue();

  }

  @Test
  public void accepts_cyclic_dependency_in_interactive() throws Exception {

    AnalysisResult result = analyze(
        "fixtures/tweakflow/analysis/interactive/module_a.tf",
        "fixtures/tweakflow/analysis/interactive/errors/cyclic_dependency_in_interactive.tf"
    );

    assertThat(result.isSuccess()).isTrue();

  }

}