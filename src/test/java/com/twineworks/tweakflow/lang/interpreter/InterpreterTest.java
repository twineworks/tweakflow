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

package com.twineworks.tweakflow.lang.interpreter;

import com.twineworks.collections.shapemap.ConstShapeMap;
import com.twineworks.tweakflow.TestHelper;
import com.twineworks.tweakflow.lang.TweakFlow;
import com.twineworks.tweakflow.lang.ast.structure.LibraryNode;
import com.twineworks.tweakflow.lang.ast.structure.ModuleNode;
import com.twineworks.tweakflow.lang.ast.structure.VarDefNode;
import com.twineworks.tweakflow.lang.errors.ErrorCode;
import com.twineworks.tweakflow.lang.errors.LangError;
import com.twineworks.tweakflow.lang.errors.LangException;
import com.twineworks.tweakflow.lang.interpreter.memory.Cell;
import com.twineworks.tweakflow.lang.interpreter.memory.GlobalMemorySpace;
import com.twineworks.tweakflow.lang.interpreter.memory.MemorySpace;
import com.twineworks.tweakflow.lang.load.loadpath.LoadPath;
import com.twineworks.tweakflow.lang.load.loadpath.ResourceLocation;
import com.twineworks.tweakflow.lang.runtime.Runtime;
import com.twineworks.tweakflow.lang.scope.ScopeType;
import com.twineworks.tweakflow.lang.types.Types;
import com.twineworks.tweakflow.lang.values.FunctionValue;
import com.twineworks.tweakflow.lang.values.Value;
import com.twineworks.tweakflow.lang.values.Values;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.twineworks.util.ShapeMapAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class InterpreterTest {

  private Runtime evaluate(String ... paths){

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();

    List<String> pathList = Arrays.asList(paths);

    Runtime runtime;
    try {
      runtime = TweakFlow.compile(loadPath, pathList);
      runtime.evaluate();
      return runtime;
    } catch (LangException e){
//      e.printDetails();
      throw e;
    }

  }

  private Runtime evaluateWithStd(String ... paths){

    LoadPath loadPath = new LoadPath.Builder()
        .add(new ResourceLocation.Builder().build())
        .build();

    List<String> pathList = new ArrayList<>(Arrays.asList(paths));
    pathList.add("com/twineworks/tweakflow/std/std.tf");

    Runtime runtime;
    try {
      runtime = TweakFlow.compile(loadPath, pathList);
      runtime.evaluate();
      return runtime;
    } catch (LangException e){
//      e.printDetails();
      throw e;
    }

  }

  @Test
  public void evaluates_empty_module() throws Exception {

  String path = "fixtures/tweakflow/evaluation/empty.tf";

    Runtime result = evaluate(path);
    RuntimeSet runtimeSet = result.getRuntimeSet();
    ModuleNode moduleNode = (ModuleNode) runtimeSet.getAnalysisSet().getUnits().get(path).getUnit();

    // no standard library loaded, globals are empty
    GlobalMemorySpace globals = runtimeSet.getGlobalMemorySpace();
    assertThat(globals.getCells()).isEmpty();

    // global space links to global scope
    assertThat(globals.getScope().getScopeType()).isSameAs(ScopeType.GLOBAL);

    // only one module loaded
    ConstShapeMap<Cell> modules = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells();
    assertThat(modules).containsOnlyStrKeys(path);

    // module space parent is the global space
    MemorySpace moduleSpace = modules.gets(path);
    assertThat(moduleSpace.getEnclosingSpace()).isSameAs(globals);

    // module's space links to module's scope
    assertThat(moduleSpace.getScope()).isSameAs(moduleNode.getUnitScope());

  }

  @Test
  public void evaluates_basic_library() throws Exception {

//    library lib
//    {
//      a: 1
//    }

    String path = "fixtures/tweakflow/evaluation/basic_library.tf";
    RuntimeSet runtimeSet = evaluate(path).getRuntimeSet();

    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);
    ModuleNode moduleNode = (ModuleNode) runtimeSet.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode libraryNode = (LibraryNode) moduleNode.getComponentsMap().get("lib");

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");

    // space references library symbol
    assertThat(lib.getSymbol()).isSameAs(libraryNode.getSymbol());

    // library scope is variable scope
    assertThat(lib.getScope()).isSameAs(libraryNode.getVars().getScope());

    // library contains variable
    assertThat(lib.getCells()).containsOnlyStrKeys("a");

    // variable symbol is referenced
    Cell a = lib.getCells().gets("a");
    assertThat(a.getSymbol()).isSameAs(libraryNode.getVars().getMap().get("a").getSymbol());

    // value of cell is evaluated expression value
    assertThat(a.getValue()).isEqualTo(Values.make(1L));

  }

  @Test
  public void evaluates_library_with_meta_data_vars() throws Exception {

//    library lib
//    {
//      doc "a doc string for `a`"
//      meta: {
//        :version "0.0.1"
//        :revision -1
//      }
//      a: 1
//    }

    String path = "fixtures/tweakflow/evaluation/meta.tf";
    RuntimeSet runtimeSet = evaluate(path).getRuntimeSet();

    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);
    ModuleNode moduleNode = (ModuleNode) runtimeSet.getAnalysisSet().getUnits().get(path).getUnit();
    LibraryNode libraryNode = (LibraryNode) moduleNode.getComponentsMap().get("lib");

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");

    // variable symbol is referenced
    Cell a = lib.getCells().gets("a");

    // value of cell is evaluated expression value
    assertThat(a.getValue()).isEqualTo(Values.make(1L));

    VarDefNode varDefNode = (VarDefNode) a.getSymbol().getNode();


  }

  @Test
  public void evaluates_literals() throws Exception {

    String path = "fixtures/tweakflow/evaluation/literals.tf";
    Runtime evaluationResult = evaluate(path);

    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();
    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();

    // nothing: nil
    Cell nothing = vars.gets("nothing");
    assertThat(nothing.getValue()).isSameAs(Values.NIL);

    // str: "string value"
    Cell str = vars.gets("str");
    assertThat(str.getValue()).isEqualTo(Values.make("string value"));

    // long_1: 1
    Cell long_1 = vars.gets("long_1");
    assertThat(long_1.getValue()).isEqualTo(Values.make(1L));

    // hex_1:    0x01;
    Cell hex_1 = vars.gets("hex_1");
    assertThat(hex_1.getValue()).isEqualTo(Values.make(1L));

    // empty_list: [];
    Cell empty_list = vars.gets("empty_list");
    assertThat(empty_list.getValue()).isEqualTo(Values.makeList());

    // simple_list: [1, 2, 3];
    Cell simple_list = vars.gets("simple_list");
    assertThat(simple_list.getValue()).isEqualTo(Values.makeList(1L, 2L, 3L));

    // nested_list: [1, "a", ["x","y"]]
    Cell nested_list = vars.gets("nested_list");
    assertThat(nested_list.getValue()).isEqualTo(Values.makeList(1L, "a", Values.makeList("x", "y")));

    // empty_dict: {}
    Cell empty_dict = vars.gets("empty_dict");
    assertThat(empty_dict.getValue()).isEqualTo(Values.makeDict());

    // basic_dict: {:key "value"}
    Cell basic_dict = vars.gets("basic_dict");
    assertThat(basic_dict.getValue()).isEqualTo(Values.makeDict("key", "value"));

    // simple_dict: {:key1 "value1" :key2 "value2"}
    Cell simple_dict = vars.gets("simple_dict");
    assertThat(simple_dict.getValue()).isEqualTo(Values.makeDict("key1", "value1", "key2", "value2"));

    // nested_dict: {"k" "v", "sub" {:key "value"}}
    Cell nested_dict = vars.gets("nested_dict");
    assertThat(nested_dict.getValue()).isEqualTo(Values.makeDict("k", "v", "sub", Values.makeDict("key", "value")));

    // string_escape: "-\n-"
    Cell newline_escape = vars.gets("newline_escape");
    assertThat(newline_escape.getValue()).isEqualTo(Values.make("-\n-"));

    // backslash_escape: "-\\n-"
    Cell backslash_escape = vars.gets("backslash_escape");
    assertThat(backslash_escape.getValue()).isEqualTo(Values.make("-\\n-"));

    // hash_alone: "-#-"
    Cell hash_alone = vars.gets("hash_alone");
    assertThat(hash_alone.getValue()).isEqualTo(Values.make("-#-"));

    // hash_interpolation: "-#-"
    Cell hash_interpolation = vars.gets("hash_interpolation");
    assertThat(hash_interpolation.getValue()).isEqualTo(Values.make("-bar-"));

    // hash_escaped: "-\#{foo}-";
    Cell hash_escaped = vars.gets("hash_escaped");
    assertThat(hash_escaped.getValue()).isEqualTo(Values.make("-#{foo}-"));

    // bool_t: true
    Cell bool_t = vars.gets("bool_t");
    assertThat(bool_t.getValue()).isSameAs(Values.TRUE);

    // bool_f: false
    Cell bool_f = vars.gets("bool_f");
    assertThat(bool_f.getValue()).isSameAs(Values.FALSE);

    // f_const: () -> true # constant function returning true
    Cell f_const = vars.gets("f_const");
    assertThat(f_const.getValue().type()).isSameAs(Types.FUNCTION);

    // f_args: (long x = 0, long y = 0) -> list [x y]
    Cell f_args = vars.gets("f_args");
    assertThat(f_args.getValue().type()).isSameAs(Types.FUNCTION);
    assertThat(f_args.getValue().value()).isNotNull();
    FunctionValue f_args_body = (FunctionValue) f_args.getValue().value();
    assertThat(f_args_body.getSignature().getReturnType()).isSameAs(Types.LIST);

    // dbl_1: 1.0
    Cell dbl_1 = vars.gets("dbl_1");
    assertThat(dbl_1.getValue().type()).isSameAs(Types.DOUBLE);
    assertThat(dbl_1.getValue()).isEqualTo(Values.make(1.0d));

    // dbl_20: 2e1;
    Cell dbl_20 = vars.gets("dbl_20");
    assertThat(dbl_20.getValue().type()).isSameAs(Types.DOUBLE);
    assertThat(dbl_20.getValue()).isEqualTo(Values.make(20.0d));

    // epoch: 1970-01-01T00:00:00Z@UTC;
    Cell epoch = vars.gets("epoch");
    assertThat(epoch.getValue().type()).isSameAs(Types.DATETIME);
    assertThat(epoch.getValue()).isEqualTo(Values.EPOCH);

  }

  @Test
  public void evaluates_imports() throws Exception {

    String path = "fixtures/tweakflow/evaluation/imports/main.tf";
    Runtime evaluationResult = evaluateWithStd(path);
    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();
    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // module space check

    // import * as module_a from "./libs/module_a.tf"
    // import lib_a as l_a from "./libs/module_a.tf"
    // import lib_b from "./libs/module_b.tf"

    assertThat(moduleSpace.getCells()).containsStrKeys("main", "module_a", "l_a", "lib_b");
    Cell lib = moduleSpace.getCells().gets("main");
    ConstShapeMap<Cell> vars = lib.getCells();

    // e0: module_a.lib_a.a
    Cell e0 = vars.gets("e0");
    assertThat(e0.getValue()).isEqualTo(Values.make("a"));

    // e1: l_a.a
    Cell e1 = vars.gets("e1");
    assertThat(e1.getValue()).isEqualTo(Values.make("a"));

    // e2: lib_b.b
    Cell e2 = vars.gets("e2");
    assertThat(e2.getValue()).isEqualTo(Values.make("b"));

  }

  @Test
  public void evaluates_import_simple_export() throws Exception {

    String path = "fixtures/tweakflow/evaluation/exports/import_simple_export.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_import_module_reexports() throws Exception {

    String path = "fixtures/tweakflow/evaluation/exports/import_module_reexports.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_import_reexports() throws Exception {

    String path = "fixtures/tweakflow/evaluation/exports/import_reexports.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_mutual_imports() throws Exception {

    String path = "fixtures/tweakflow/evaluation/exports/mutual/main.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_import_values() throws Exception {

    String path = "fixtures/tweakflow/evaluation/exports/import_values.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_function_calls() throws Exception {
    String path = "fixtures/tweakflow/evaluation/function_calls.tf";
    TestHelper.assertSpecModule(path);
  }

  @TestFactory
  public Collection<DynamicTest> evaluates_function_curry() throws Exception {
    String path = "fixtures/tweakflow/evaluation/function_curry.tf";
    return TestHelper.dynamicTestsSpecModule(path);
  }

  @Test
  public void evaluates_recursion() throws Exception {

    String path = "fixtures/tweakflow/evaluation/closures/recursion.tf";

    Runtime evaluationResult = evaluateWithStd(path);

    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();
    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();

    //  fib_0: fib(0)
    Cell fib_0 = vars.gets("fib_0");
    assertThat(fib_0.getValue().longNum()).isEqualTo(0L);

    // fib_1:  fib(1)
    Cell fib_1 = vars.gets("fib_1");
    assertThat(fib_1.getValue().longNum()).isEqualTo(1L);

    // fib_2:  fib(2)
    Cell fib_2 = vars.gets("fib_2");
    assertThat(fib_2.getValue().longNum()).isEqualTo(1L);

    // fib_3:  fib(3)
    Cell fib_3 = vars.gets("fib_3");
    assertThat(fib_3.getValue().longNum()).isEqualTo(2L);

    // fib_10: fib(10)
    Cell fib_10 = vars.gets("fib_10");
    assertThat(fib_10.getValue().longNum()).isEqualTo(55L);

  }

  @Test
  public void evaluates_cross_recursion() throws Exception {

    String path = "fixtures/tweakflow/evaluation/cross_recursion.tf";

    Runtime evaluationResult = evaluateWithStd(path);

    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();
    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();


    /*
      function f: (long x) -> long
                if x == 0
                  0
                else
                  g(x-1)

      function g: (long x) -> long
                if x == 0
                  1
                else
                  f(x-1)
     */


    // f_10: f(10)
    Cell f_10 = vars.gets("f_10");
    assertThat(f_10.getValue().longNum()).isEqualTo(0L);

    // g_10: g(10)
    Cell g_10 = vars.gets("g_10");
    assertThat(g_10.getValue().longNum()).isEqualTo(1L);

  }

  @Test
  public void evaluates_recursion_variants() throws Exception {

    String path = "fixtures/tweakflow/evaluation/closures/recursion_variants.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_try_catch_trace() throws Exception {

    String path = "fixtures/tweakflow/evaluation/throwing/try_catch_trace.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_try_catch_nested() throws Exception {

    String path = "fixtures/tweakflow/evaluation/throwing/try_catch_trace_nested.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_throw() throws Exception {

    String path = "fixtures/tweakflow/evaluation/throwing/throw.tf";
    try {
      Runtime evaluationResult = evaluateWithStd(path);
    } catch (LangException exception){
      ErrorCode code = exception.getCode();
      assertThat(code).isSameAs(LangError.CUSTOM_ERROR);

      Value errorValue = (Value) exception.get("value");
      assertThat(errorValue).isEqualTo(Values.make("error"));
      return;
    }

    fail("Expected LangException");

  }

  @Test
  public void evaluates_try_catch() throws Exception {

    String path = "fixtures/tweakflow/evaluation/throwing/try_catch.tf";
    Runtime evaluationResult = evaluateWithStd(path);

    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();
    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();

    // catch_error:
    //  try
    //    throw "error"
    //  catch e
    //    "caught: " .. e
    Cell catch_error = vars.gets("catch_error");
    assertThat(catch_error.getValue()).isEqualTo(Values.make("caught: error"));

    // catch_let:
    // try
    //   throw {:message "error"}
    // catch e
    //   let {
    //     message: e[:message]
    //   }
    //   "caught: ${message}"
    Cell catch_let = vars.gets("catch_let");
    assertThat(catch_let.getValue()).isEqualTo(Values.make("caught: another error"));

    // let_catch:  let {
    //               message: "yet another error"
    //             }
    //             try
    //               throw message
    //             catch e
    //               "caught: #{e}"

    Cell let_catch = vars.gets("let_catch");
    assertThat(let_catch.getValue()).isEqualTo(Values.make("caught: yet another error"));

    // catch_trace:
    // try
    //   throw "error"
    // catch e, trace
    //   let {
    //     t0: trace[0][:at]
    //   }
    //   "caught: #{e} trace: #{t0}"
    Cell catch_trace = vars.gets("catch_trace");
    String prefix = "caught: error trace: ";
    // starts correctly
    assertThat(catch_trace.getValue().string()).startsWith(prefix);
    // and is longer than the prefix alone
    assertThat(catch_trace.getValue().string().length()).isGreaterThan(prefix.length());
  }

  @Test
  public void evaluates_bindings() throws Exception {

    String path = "fixtures/tweakflow/evaluation/bindings.tf";
    Runtime evaluationResult = evaluateWithStd(path);
    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();

    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();

    //  a: let {long x: 1} x
    Cell a = vars.gets("a");
    assertThat(a.getValue()).isEqualTo(Values.make(1L));

    // f: (long x) -> let {
    //   x_squared: x**2
    // }
    // x_squared + x_squared
    //
    // g: f(3) # 9+9 = 18

    Cell g = vars.gets("g");
    assertThat(g.getValue()).isEqualTo(Values.make(18L));

    //  make: (string x) ->
    //      let {
    //        f: if x == "inverse"
    //           (x) -> -x
    //         else
    //           (x) -> x
    //
    //        }
    //        f
    //
    //  id:   make()
    //  inv:  make("inverse")
    //
    //  pos_one: id(1)
    //  neg_one: inv(1)

    Cell pos_one = vars.gets("pos_one");
    assertThat(pos_one.getValue()).isEqualTo(Values.make(1L));

    Cell neg_one = vars.gets("neg_one");
    assertThat(neg_one.getValue()).isEqualTo(Values.make(-1L));

  }

  @Test
  public void evaluates_closures() throws Exception {

    String path = "fixtures/tweakflow/evaluation/closures/closures.tf";
    Runtime evaluationResult = evaluateWithStd(path);
    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();
    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();

    /*

      make_adder: (long a) -> function
                    (x) -> x + a

      add_0: make_adder(0)
      add_1: make_adder(1)
      add_10: make_adder(10)

     */


    // n10: add_0(10)
    Cell n10 = vars.gets("n10");
    assertThat(n10.getValue()).isEqualTo(Values.make(10L));

    // n11: add_1(10)
    Cell n11 = vars.gets("n11");
    assertThat(n11.getValue()).isEqualTo(Values.make(11L));

    // n20: add_10(10)
    Cell n20 = vars.gets("n20");
    assertThat(n20.getValue()).isEqualTo(Values.make(20L));

  }

  @Test
  public void evaluates_cross_lib_closures() throws Exception {
    String path = "fixtures/tweakflow/evaluation/closures/cross_lib_closures.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_cross_module_closures() throws Exception {
    String path = "fixtures/tweakflow/evaluation/closures/cross_module_closures.tf";
    TestHelper.assertSpecModule(path);
  }

  @Test
  public void evaluates_std() throws Exception {

    String path = "com/twineworks/tweakflow/std/std.tf";
    RuntimeSet runtimeSet = evaluate(path).getRuntimeSet();

    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // libraries are present
    assertThat(moduleSpace.getCells()).containsStrKeys("strings", "core", "data");

  }

  @Test
  public void evaluates_operators() throws Exception {

    String path = "fixtures/tweakflow/evaluation/operators.tf";
    Runtime evaluationResult = evaluateWithStd(path);

    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();

    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();

    //   concat: "foo" .. "bar"
    Cell concat = vars.gets("concat");
    assertThat(concat.getValue()).isEqualTo(Values.make("foobar"));

    // equal:      "foo" == "foo"
    Cell equal = vars.gets("equal");
    assertThat(equal.getValue()).isSameAs(Values.TRUE);

    // not_equal:  "foo" == "bar"
    Cell not_equal = vars.gets("not_equal");
    assertThat(not_equal.getValue()).isSameAs(Values.FALSE);

    // identical:        1 === 1
    Cell identical = vars.gets("identical");
    assertThat(identical.getValue()).isSameAs(Values.TRUE);

    // identical_false:  1 === 1.0
    Cell identical_false = vars.gets("identical_false");
    assertThat(identical_false.getValue()).isSameAs(Values.FALSE);

    // not_identical:    1 !== 1.0
    Cell not_identical = vars.gets("not_identical");
    assertThat(not_identical.getValue()).isSameAs(Values.TRUE);

    // not_identical_false: 1 !== 1
    Cell not_identical_false = vars.gets("not_identical_false");
    assertThat(not_identical_false.getValue()).isSameAs(Values.FALSE);

    // not_false:        !false
    Cell not_false = vars.gets("not_false");
    assertThat(not_false.getValue()).isSameAs(Values.TRUE);

    // not_true:         !true
    Cell not_true = vars.gets("not_true");
    assertThat(not_true.getValue()).isSameAs(Values.FALSE);

    // not_nil:          !nil
    Cell not_nil = vars.gets("not_nil");
    assertThat(not_nil.getValue()).isSameAs(Values.TRUE);

    // inequality_true:  1 != 2
    Cell inequality_true = vars.gets("inequality_true");
    assertThat(inequality_true.getValue()).isSameAs(Values.TRUE);

    // inequality_false: 1 != 1
    Cell inequality_false = vars.gets("inequality_false");
    assertThat(inequality_false.getValue()).isSameAs(Values.FALSE);

    // and_true          1 && 1
    Cell and_true = vars.gets("and_true");
    assertThat(and_true.getValue()).isSameAs(Values.TRUE);

    // and_false         true && false
    Cell and_false = vars.gets("and_false");
    assertThat(and_false.getValue()).isSameAs(Values.FALSE);

    // or_true           true || false
    Cell or_true = vars.gets("or_true");
    assertThat(or_true.getValue()).isSameAs(Values.TRUE);

    // or_false          nil || false
    Cell or_false = vars.gets("or_false");
    assertThat(or_false.getValue()).isSameAs(Values.FALSE);

    // lt_true:          1 < 2
    Cell lt_true = vars.gets("lt_true");
    assertThat(lt_true.getValue()).isSameAs(Values.TRUE);

    // lt_false:         2 < 1
    Cell lt_false = vars.gets("lt_false");
    assertThat(lt_false.getValue()).isSameAs(Values.FALSE);

    // lte_true:         2 <= 2
    Cell lte_true = vars.gets("lte_true");
    assertThat(lte_true.getValue()).isSameAs(Values.TRUE);

    // lte_false:        "b" <= "a"
    Cell lte_false = vars.gets("lte_false");
    assertThat(lte_false.getValue()).isSameAs(Values.FALSE);

    // gt_true:          2 > 1
    Cell gt_true = vars.gets("gt_true");
    assertThat(gt_true.getValue()).isSameAs(Values.TRUE);

    // gt_false:         "a" > "b"
    Cell gt_false = vars.gets("gt_false");
    assertThat(gt_false.getValue()).isSameAs(Values.FALSE);

    // gte_true:         2 >= 2
    Cell gte_true = vars.gets("gte_true");
    assertThat(gte_true.getValue()).isSameAs(Values.TRUE);

    // gte_false:        "a" >= "b"
    Cell gte_false = vars.gets("gte_false");
    assertThat(gte_false.getValue()).isSameAs(Values.FALSE);

    // negate:           -2
    Cell negate = vars.gets("negate");
    assertThat(negate.getValue()).isEqualTo(Values.make(-2L));

    // sum:           1+2
    Cell sum = vars.gets("sum");
    assertThat(sum.getValue()).isEqualTo(Values.make(3L));

    // minus:           2-1
    Cell minus = vars.gets("minus");
    assertThat(minus.getValue()).isEqualTo(Values.make(1L));

    // product:          1*2*3
    Cell product = vars.gets("product");
    assertThat(product.getValue()).isEqualTo(Values.make(6L));

    // power:            2**10
    Cell power = vars.gets("power");
    assertThat(power.getValue()).isEqualTo(Values.make(1024.0));

    // divide:           16/2
    Cell divide = vars.gets("divide");
    assertThat(divide.getValue()).isEqualTo(Values.make(8.0));

    // modulo:           16 % 3 # 16=3*5+1 -> 1
    Cell modulo = vars.gets("modulo");
    assertThat(modulo.getValue()).isEqualTo(Values.make(1L));

    // unary_plus:       +2;
    Cell unary_plus = vars.gets("unary_plus");
    assertThat(unary_plus.getValue()).isEqualTo(Values.make(2L));

    // unary_sum:        +1++2;
    Cell unary_sum = vars.gets("unary_sum");
    assertThat(unary_sum.getValue()).isEqualTo(Values.make(3L));

    // unary_mult:       +3*+3;
    Cell unary_mult = vars.gets("unary_mult");
    assertThat(unary_mult.getValue()).isEqualTo(Values.make(9L));

    // unary_minus:       +3-+3;
    Cell unary_minus = vars.gets("unary_minus");
    assertThat(unary_minus.getValue()).isEqualTo(Values.make(0L));

    // get_in:           {:a ["x" "y" "z"] :b nil}[:a 1] # "y"
    Cell get_in = vars.gets("get_in");
    assertThat(get_in.getValue()).isEqualTo(Values.make("y"));

    // get_in_nil:           [1 2 3][100]
    Cell get_in_nil = vars.gets("get_in_nil");
    assertThat(get_in_nil.getValue()).isSameAs(Values.NIL);

    // list_concat:           [...[1 2 3] ...[4 5 6]]          # [1 2 3 4 5 6]
    Cell list_concat = vars.gets("list_concat");
    assertThat(list_concat.getValue()).isEqualTo(Values.makeList(1L, 2L, 3L, 4L, 5L, 6L));

    // map_merge:        {...{:a 1 :b 2} ...{:a 2 :c 2}}  # {:a 2 :b 2 :c 2}
    Cell map_merge = vars.gets("map_merge");
    assertThat(map_merge.getValue()).isEqualTo(Values.makeDict("a", 2L, "b", 2L, "c", 2L));

    // list_comp:        for x <- [1,2], y <- [3,4], x*y  # [3, 4, 6, 8]
    Cell list_comp = vars.gets("list_comp");
    assertThat(list_comp.getValue()).isEqualTo(Values.makeList(3L, 4L, 6L, 8L));

    // if_true: if true then "yes" else "no"
    Cell if_true = vars.gets("if_true");
    assertThat(if_true.getValue().type()).isSameAs(Types.STRING);
    assertThat(if_true.getValue().value()).isEqualTo("yes");

    // if_false: if false then "yes" else "no"
    Cell if_false = vars.gets("if_false");
    assertThat(if_false.getValue().type()).isSameAs(Types.STRING);
    assertThat(if_false.getValue().value()).isEqualTo("no");

    // type_check: "foo" is string
    Cell type_check = vars.gets("type_check");
    assertThat(type_check.getValue().type()).isSameAs(Types.BOOLEAN);
    assertThat(type_check.getValue()).isSameAs(Values.TRUE);

  }

  @Test
  public void evaluates_casts() throws Exception {

    String path = "fixtures/tweakflow/evaluation/casts.tf";
    RuntimeSet runtimeSet = evaluate(path).getRuntimeSet();

    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();

    //   boolean_to_long: true as long
    Cell boolean_to_long = vars.gets("boolean_to_long");
    assertThat(boolean_to_long.getValue()).isEqualTo(Values.make(1L));

    // boolean_to_double: true as double
    Cell boolean_to_double = vars.gets("boolean_to_double");
    assertThat(boolean_to_double.getValue()).isEqualTo(Values.make(1.0d));

    // boolean_to_string: true as string
    Cell boolean_to_string = vars.gets("boolean_to_string");
    assertThat(boolean_to_string.getValue()).isEqualTo(Values.make("true"));

    // string_to_boolean: "foo" as boolean
    Cell string_to_boolean = vars.gets("string_to_boolean");
    assertThat(string_to_boolean.getValue()).isSameAs(Values.TRUE);

    // string_to_long: "123" as long
    Cell string_to_long = vars.gets("string_to_long");
    assertThat(string_to_long.getValue()).isEqualTo(Values.make(123L));

    // string_to_double: "1.2" as double
    Cell string_to_double = vars.gets("string_to_double");
    assertThat(string_to_double.getValue()).isEqualTo(Values.make(1.2d));

    // string_to_list: "123" as list
    Cell string_to_list = vars.gets("string_to_list");
    assertThat(string_to_list.getValue()).isEqualTo(Values.makeList("1","2","3"));

    // long_to_boolean: 1 as boolean
    Cell long_to_boolean = vars.gets("long_to_boolean");
    assertThat(long_to_boolean.getValue()).isSameAs(Values.TRUE);

    // long_to_double: 123 as double
    Cell long_to_double = vars.gets("long_to_double");
    assertThat(long_to_double.getValue()).isEqualTo(Values.make(123.0d));

    // long_to_string: 123 as string
    Cell long_to_string = vars.gets("long_to_string");
    assertThat(long_to_string.getValue()).isEqualTo(Values.make("123"));

    // double_to_boolean: 1.0 as boolean
    Cell double_to_boolean = vars.gets("double_to_boolean");
    assertThat(double_to_boolean.getValue()).isEqualTo(Values.TRUE);

    // double_to_long: 1.2 as long
    Cell double_to_long = vars.gets("double_to_long");
    assertThat(double_to_long.getValue()).isEqualTo(Values.make(1L));

    // double_to_string: 1.2 as string
    Cell double_to_string = vars.gets("double_to_string");
    assertThat(double_to_string.getValue()).isEqualTo(Values.make("1.2"));

    // list_to_boolean: [1 2 3] as boolean
    Cell list_to_boolean = vars.gets("list_to_boolean");
    assertThat(list_to_boolean.getValue()).isSameAs(Values.TRUE);

    // list_to_map: ["key" "value"] as map
    Cell list_to_map = vars.gets("list_to_map");
    assertThat(list_to_map.getValue()).isEqualTo(Values.makeDict("key", "value"));

    // map_to_boolean: {} as boolean
    Cell map_to_boolean = vars.gets("map_to_boolean");
    assertThat(map_to_boolean.getValue()).isSameAs(Values.FALSE);

    // map_to_list: {} as list
    Cell map_to_list = vars.gets("map_to_list");
    assertThat(map_to_list.getValue()).isEqualTo(Values.makeList());

    // nil_to_boolean: nil as boolean
    Cell nil_to_boolean = vars.gets("nil_to_boolean");
    assertThat(nil_to_boolean.getValue()).isSameAs(Values.NIL);

    // function_to_boolean: (() -> nil) as boolean
    Cell function_to_boolean = vars.gets("function_to_boolean");
    assertThat(function_to_boolean.getValue()).isSameAs(Values.TRUE);

  }

  @Test
  public void evaluates_parameter_casts() throws Exception {

    String path = "fixtures/tweakflow/evaluation/parameter_casts.tf";

    Runtime evaluationResult = evaluateWithStd(path);

    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();
    MemorySpace moduleSpace = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells().gets(path);

    // library space is present
    assertThat(moduleSpace.getCells()).containsOnlyStrKeys("lib");
    Cell lib = moduleSpace.getCells().gets("lib");
    ConstShapeMap<Cell> vars = lib.getCells();

    // function f: (string x=1) -> x == "1"

    // param_default_value:  f()
    Cell param_default_value = vars.gets("param_default_value");
    assertThat(param_default_value.getValue()).isSameAs(Values.TRUE);

    // param_argument_value: f(1)
    Cell param_argument_value = vars.gets("param_argument_value");
    assertThat(param_argument_value.getValue()).isSameAs(Values.TRUE);

  }

  @Test
  public void evaluates_interactive_session() throws Exception {

    String module_path_a = "fixtures/tweakflow/evaluation/interactive/module_a.tf";
    String module_path_b = "fixtures/tweakflow/evaluation/interactive/module_b.tf";
    String interactivePath = "fixtures/tweakflow/evaluation/interactive/interactive.tf";

    Runtime evaluationResult = evaluateWithStd(module_path_a, module_path_b, interactivePath);

    RuntimeSet runtimeSet = evaluationResult.getRuntimeSet();
    ConstShapeMap<Cell> unitCells = runtimeSet.getGlobalMemorySpace().getUnitSpace().getCells();

    MemorySpace interactiveSpace = unitCells.gets(interactivePath);

    // interactive space is present, and contains in_scope section for module
    assertThat(interactiveSpace.getCells()).containsOnlyStrKeys(module_path_a, module_path_b);

    // interactive space in module_a
    MemorySpace in_scope_a = interactiveSpace.getCells().gets(module_path_a);
    assertThat(in_scope_a.getCells()).containsOnlyStrKeys("e0", "e1", "e2", "e3", "e4");

    Value a_e0 = in_scope_a.getCells().gets("e0").getValue();
    assertThat(a_e0).isEqualTo(Values.make(0L));

    Value a_e1 = in_scope_a.getCells().gets("e1").getValue();
    assertThat(a_e1).isEqualTo(Values.make(1L));

    Value a_e2 = in_scope_a.getCells().gets("e2").getValue();
    assertThat(a_e2).isEqualTo(Values.makeList(1L, 1L));

    Value a_e3 = in_scope_a.getCells().gets("e3").getValue();
    assertThat(a_e3.value()).isInstanceOf(FunctionValue.class);

    Value a_e4 = in_scope_a.getCells().gets("e4").getValue();
    assertThat(a_e4).isEqualTo(Values.make(1L));

    // interactive space in module_b
    MemorySpace in_scope_b = interactiveSpace.getCells().gets(module_path_b);
    assertThat(in_scope_b.getCells()).containsOnlyStrKeys("e0", "e1", "e2", "e3", "e4");

    Value b_e0 = in_scope_b.getCells().gets("e0").getValue();
    assertThat(b_e0).isEqualTo(Values.make(0L));

    Value b_e1 = in_scope_b.getCells().gets("e1").getValue();
    assertThat(b_e1).isEqualTo(Values.make(2L));

    Value b_e2 = in_scope_b.getCells().gets("e2").getValue();
    assertThat(b_e2).isEqualTo(Values.makeList(2L, 2L));

    Value b_e3 = in_scope_b.getCells().gets("e3").getValue();
    assertThat(b_e3.value()).isInstanceOf(FunctionValue.class);

    Value b_e4 = in_scope_b.getCells().gets("e4").getValue();
    assertThat(b_e4).isEqualTo(Values.make(2L));

  }

  @Test
  public void fails_on_extra_positional_args() throws Exception {

    String path = "fixtures/tweakflow/evaluation/errors/extra_positional_args.tf";
    try {
      evaluateWithStd(path);
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNEXPECTED_ARGUMENT);
      return;
    }

    fail("expected LangException");

  }

  @Test
  public void fails_on_extra_curry_args() throws Exception {

    String path = "fixtures/tweakflow/evaluation/errors/extra_curry_args.tf";
    try {
      evaluateWithStd(path);
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNEXPECTED_ARGUMENT);
      return;
    }

    fail("expected LangException");
  }

  @Test
  public void fails_on_non_function_curry() throws Exception {

    String path = "fixtures/tweakflow/evaluation/errors/non_function_curry.tf";
    try {
      evaluateWithStd(path);
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.CANNOT_CURRY);
      return;
    }

    fail("expected LangException");
  }

  @Test
  public void fails_on_non_function_call() throws Exception {

    String path = "fixtures/tweakflow/evaluation/errors/non_function_call.tf";
    try {
      evaluateWithStd(path);
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.CANNOT_CALL);
      return;
    }

    fail("expected LangException");
  }

  @Test
  public void fails_on_extra_named_args() throws Exception {

    String path = "fixtures/tweakflow/evaluation/errors/extra_named_args.tf";
    try {
      evaluateWithStd(path);
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNEXPECTED_ARGUMENT);
      return;
    }

    fail("expected LangException");
  }

  @Test
  public void fails_on_extra_splat_args() throws Exception {

    String path = "fixtures/tweakflow/evaluation/errors/extra_splat_args.tf";
    try {
      evaluateWithStd(path);
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNEXPECTED_ARGUMENT);
      return;
    }

    fail("expected LangException");

  }

  @Test
  public void fails_on_non_map_splat_args() throws Exception {

    String path = "fixtures/tweakflow/evaluation/errors/non_map_splat_args.tf";
    try {
      evaluateWithStd(path);
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNEXPECTED_ARGUMENT);
      return;
    }

    fail("expected LangException");

  }

  @Test
  public void fails_on_nil_splat_args() throws Exception {

    String path = "fixtures/tweakflow/evaluation/errors/nil_splat_args.tf";
    try {
      evaluateWithStd(path);
    }
    catch (LangException e){
      assertThat(e.getCode()).isSameAs(LangError.UNEXPECTED_ARGUMENT);
      return;
    }

    fail("expected LangException");

  }

}