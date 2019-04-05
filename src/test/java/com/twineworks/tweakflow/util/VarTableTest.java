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

package com.twineworks.tweakflow.util;

import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.fail;

public class VarTableTest {

  @Test
  public void empty() throws Exception {

    VarTable table = new VarTable.Builder().build();
    Runtime runtime = table.compile();
    assertThat(runtime.getModules().get(table.getModulePath())).isNotNull();
  }

  @Test
  public void simple_variables() throws Exception {

    VarTable table = new VarTable.Builder()
        .addVar("a", "1")
        .addVar("b", "2")
        .addVar("c", "3")
        .build();

    assertThat(table.hasParseErrors()).isFalse();

    // verify var lines

/*
library var_table {
a:
1
;

b:
2
;

c:
3
;

}
*/

    assertThat(table.varExpressionLine("a")).isEqualTo(3);
    assertThat(table.varExpressionLine("b")).isEqualTo(7);
    assertThat(table.varExpressionLine("c")).isEqualTo(11);

    Runtime runtime = table.compile();
    runtime.evaluate();

    Runtime.Module module = runtime.getModules().get(table.getModulePath());
    Runtime.Library lib = module.getLibrary(table.getVarLibraryName());

    // verify var values
    assertThat(lib.getVar("a").getValue()).isEqualTo(Values.make(1L));
    assertThat(lib.getVar("b").getValue()).isEqualTo(Values.make(2L));
    assertThat(lib.getVar("c").getValue()).isEqualTo(Values.make(3L));

  }

  @Test
  public void custom_module_path_and_library_name() throws Exception {

    VarTable table = new VarTable.Builder()
        .setModulePath("user_vars")
        .setVarLibraryName("alphabet")
        .addVar("a", "1")
        .addVar("b", "2")
        .addVar("c", "3")
        .build();

    assertThat(table.hasParseErrors()).isFalse();

    assertThat(table.getModulePath()).isEqualTo("user_vars");
    assertThat(table.getVarLibraryName()).isEqualTo("alphabet");

    Runtime runtime = table.compile();
    runtime.evaluate();

    Runtime.Module module = runtime.getModules().get(table.getModulePath());
    Runtime.Library lib = module.getLibrary(table.getVarLibraryName());

    // verify var values
    assertThat(lib.getVar("a").getValue()).isEqualTo(Values.make(1L));
    assertThat(lib.getVar("b").getValue()).isEqualTo(Values.make(2L));
    assertThat(lib.getVar("c").getValue()).isEqualTo(Values.make(3L));

  }

  @Test
  public void prologue_and_variables() throws Exception {

    VarTable table = new VarTable.Builder()
        .setPrologue("import core, data, strings from 'std'")
        .addVar("a", "strings.length('foo')")
        .addVar("b", "let {\n" +
            "  x: 3\n" +
            "}\n" +
            "a+x")
        .addVar("c", "a+b")
        .build();

    assertThat(table.hasParseErrors()).isFalse();

    Runtime runtime = table.compile();
    runtime.evaluate();

    Runtime.Module module = runtime.getModules().get(table.getModulePath());
    Runtime.Library lib = module.getLibrary(table.getVarLibraryName());

    // verify var lines
/*
import core, data, strings from 'std'
library var_table {
a:
strings.length('foo')
;

b:
let {
  x: 3
}
a+x
;

c:
a+b
;

}
*/

    assertThat(table.varExpressionLine("a")).isEqualTo(4);
    assertThat(table.varExpressionLine("b")).isEqualTo(8);
    assertThat(table.varExpressionLine("c")).isEqualTo(15);

    // verify var values

    assertThat(lib.getVar("a").getValue()).isEqualTo(Values.make(3L));
    assertThat(lib.getVar("b").getValue()).isEqualTo(Values.make(6L));
    assertThat(lib.getVar("c").getValue()).isEqualTo(Values.make(9L));

  }

  @Test
  public void reports_parse_errors_in_variables() throws Exception {

    VarTable table = new VarTable.Builder()
        .addVar("a", "1.E?") // parse error
        .addVar("b", "90873.34")
        .addVar("c", "n\\009'") // parse error
        .build();

    assertThat(table.hasParseErrors()).isTrue();

    LinkedHashMap<String, LangException> varParseErrors = table.getVarParseErrors();
    assertThat(varParseErrors).hasSize(2);
    assertThat(varParseErrors.keySet()).containsExactly("a", "c");

    assertThat(varParseErrors.get("a")).isNotNull();
    assertThat(varParseErrors.get("c")).isNotNull();

    assertThat(table.getPrologueParseError()).isNull();
  }

  @Test
  public void reports_parse_errors_in_prologue() throws Exception {

    VarTable table = new VarTable.Builder()
        .setPrologue("import foo-bar") // parse error
        .addVar("a", "1")
        .addVar("b", "2")
        .addVar("c", "3")
        .build();

    assertThat(table.hasParseErrors()).isTrue();

    assertThat(table.getPrologueParseError()).isNotNull();
    assertThat(table.getVarParseErrors()).isEmpty();
  }

  @Test
  public void reports_compilation_error_in_first_variable() throws Exception {

    VarTable table = new VarTable.Builder()
        .setPrologue("import core, data from 'std'")
        .addVar("a", "core.ids")  // core.ids -> unresolved reference
        .addVar("b", "1")
        .addVar("c", "2")
        .build();

    assertThat(table.hasParseErrors()).isFalse();

    try {
      Runtime runtime = table.compile();
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);
      // verify the problem is in our module
      assertThat(e.getSourceInfo().getParseUnit()).isSameAs(table.getModuleParseUnit());

      String varName = table.varNameFor(e.getSourceInfo());
      assertThat(varName).isEqualTo("a");
      return;
    }

    fail("expected to throw, catch, and return");
  }

  @Test
  public void reports_compilation_error_in_mid_variable() throws Exception {

    VarTable table = new VarTable.Builder()
        .setPrologue("import core, data from 'std'")
        .addVar("a", "1")
        .addVar("b", "core.ids") // core.ids -> unresolved reference
        .addVar("c", "2")
        .build();

    assertThat(table.hasParseErrors()).isFalse();

    try {
      Runtime runtime = table.compile();
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);
      // verify the problem is in our module
      assertThat(e.getSourceInfo().getParseUnit()).isSameAs(table.getModuleParseUnit());

      String varName = table.varNameFor(e.getSourceInfo());
      assertThat(varName).isEqualTo("b");
      return;
    }

    fail("expected to throw, catch, and return");
  }

  @Test
  public void reports_compilation_error_in_last_variable() throws Exception {

    VarTable table = new VarTable.Builder()
        .setPrologue("import core, data from 'std'")
        .addVar("a", "1")
        .addVar("b", "2")
        .addVar("c", "core.ids") // core.ids -> unresolved reference
        .build();

    assertThat(table.hasParseErrors()).isFalse();

    try {
      Runtime runtime = table.compile();
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNRESOLVED_REFERENCE);
      // verify the problem is in our module
      assertThat(e.getSourceInfo().getParseUnit()).isSameAs(table.getModuleParseUnit());

      String varName = table.varNameFor(e.getSourceInfo());
      assertThat(varName).isEqualTo("c");
      return;
    }

    fail("expected to throw, catch, and return");
  }

  @Test
  public void reports_compilation_error_in_prologue() throws Exception {

    VarTable table = new VarTable.Builder()
        .setPrologue("import missing from 'std'")
        .addVar("a", "1")
        .addVar("b", "2")
        .build();

    assertThat(table.hasParseErrors()).isFalse();

    try {
      Runtime runtime = table.compile();
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.CANNOT_FIND_EXPORT);
      // verify the problem is in our module
      assertThat(e.getSourceInfo().getParseUnit()).isSameAs(table.getModuleParseUnit());

      // but not in any variable
      String varName = table.varNameFor(e.getSourceInfo());
      assertThat(varName).isNull();
      return;
    }

    fail("expected to throw, catch, and return");
  }

}