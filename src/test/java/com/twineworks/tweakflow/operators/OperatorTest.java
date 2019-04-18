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

package com.twineworks.tweakflow.operators;

import com.twineworks.tweakflow.TestHelper;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;

public class OperatorTest {

  @TestFactory
  public Collection<DynamicTest> match() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/match.tf");
  }

  @TestFactory
  public Collection<DynamicTest> for_list_comprehension() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/for.tf");
  }

  @TestFactory
  public Collection<DynamicTest> def() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/default.tf");
  }

  @TestFactory
  public Collection<DynamicTest> is() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/is.tf");
  }

  @TestFactory
  public Collection<DynamicTest> typeof() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/typeof.tf");
  }

  @TestFactory
  public Collection<DynamicTest> and() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/and.tf");
  }

  @TestFactory
  public Collection<DynamicTest> or() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/or.tf");
  }

  @TestFactory
  public Collection<DynamicTest> bitwise_xor() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_xor.tf");
  }

  @TestFactory
  public Collection<DynamicTest> bitwise_or() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_or.tf");
  }

  @TestFactory
  public Collection<DynamicTest> bitwise_shift_left() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_shift_left.tf");
  }

  @TestFactory
  public Collection<DynamicTest> bitwise_preserving_shift_right() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_preserving_shift_right.tf");
  }

  @TestFactory
  public Collection<DynamicTest> bitwise_zero_shift_right() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_zero_shift_right.tf");
  }

  @TestFactory
  public Collection<DynamicTest> bitwise_not() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_not.tf");
  }

  @TestFactory
  public Collection<DynamicTest> not() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/not.tf");
  }

  @TestFactory
  public Collection<DynamicTest> equal() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/equal.tf");
  }

  @TestFactory
  public Collection<DynamicTest> not_equal() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/not_equal.tf");
  }

  @TestFactory
  public Collection<DynamicTest> equal_and_type() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/equal_and_type.tf");
  }

  @TestFactory
  public Collection<DynamicTest> not_equal_and_type() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/not_equal_and_type.tf");
  }

  @TestFactory
  public Collection<DynamicTest> mult() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/mult.tf");
  }

  @TestFactory
  public Collection<DynamicTest> pow() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/pow.tf");
  }

  @TestFactory
  public Collection<DynamicTest> concat() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/concat.tf");
  }

  @TestFactory
  public Collection<DynamicTest> plus() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/plus.tf");
  }

  @TestFactory
  public Collection<DynamicTest> minus() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/minus.tf");
  }

  @TestFactory
  public Collection<DynamicTest> div() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/div.tf");
  }

  @TestFactory
  public Collection<DynamicTest> mod() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/mod.tf");
  }

  @TestFactory
  public Collection<DynamicTest> int_div() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/int_div.tf");
  }

  @TestFactory
  public Collection<DynamicTest> less_than() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/less_than.tf");
  }

  @TestFactory
  public Collection<DynamicTest> less_than_or_equal() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/less_than_or_equal.tf");
  }

  @TestFactory
  public Collection<DynamicTest> greater_than() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/greater_than.tf");
  }

  @TestFactory
  public Collection<DynamicTest> greater_than_or_equal() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/operators/greater_than_or_equal.tf");
  }

}