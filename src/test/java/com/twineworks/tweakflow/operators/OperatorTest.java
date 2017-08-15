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

package com.twineworks.tweakflow.operators;

import com.twineworks.tweakflow.LibraryTestHelper;
import org.junit.Test;

public class OperatorTest {

  @Test
  public void match() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/match.tf");
  }

  @Test
  public void for_list_comprehension() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/for.tf");
  }

  @Test
  public void def() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/default.tf");
  }

  @Test
  public void is() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/is.tf");
  }

  @Test
  public void typeof() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/typeof.tf");
  }

  @Test
  public void and() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/and.tf");
  }

  @Test
  public void or() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/or.tf");
  }

  @Test
  public void bitwise_xor() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_xor.tf");
  }

  @Test
  public void bitwise_or() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_or.tf");
  }

  @Test
  public void bitwise_shift_left() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_shift_left.tf");
  }

  @Test
  public void bitwise_preserving_shift_right() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_preserving_shift_right.tf");
  }

  @Test
  public void bitwise_zero_shift_right() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_zero_shift_right.tf");
  }

  @Test
  public void bitwise_not() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/bitwise_not.tf");
  }

  @Test
  public void not() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/not.tf");
  }

  @Test
  public void equal() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/equal.tf");
  }

  @Test
  public void not_equal() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/not_equal.tf");
  }

  @Test
  public void equal_and_type() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/equal_and_type.tf");
  }

  @Test
  public void not_equal_and_type() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/not_equal_and_type.tf");
  }

  @Test
  public void mult() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/mult.tf");
  }

  @Test
  public void pow() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/pow.tf");
  }

  @Test
  public void plus() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/plus.tf");
  }

  @Test
  public void minus() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/minus.tf");
  }

  @Test
  public void div() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/div.tf");
  }

  @Test
  public void mod() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/mod.tf");
  }

  @Test
  public void int_div() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/int_div.tf");
  }

  @Test
  public void less_than() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/less_than.tf");
  }

  @Test
  public void less_than_or_equal() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/less_than_or_equal.tf");
  }

  @Test
  public void greater_than() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/greater_than.tf");
  }

  @Test
  public void greater_than_or_equal() throws Exception {
    LibraryTestHelper.assertSpecModule("fixtures/tweakflow/evaluation/operators/greater_than_or_equal.tf");
  }

}