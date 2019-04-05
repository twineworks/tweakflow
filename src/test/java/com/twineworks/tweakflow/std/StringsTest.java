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

package com.twineworks.tweakflow.std;

import com.twineworks.tweakflow.TestHelper;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;

public class StringsTest {

  @TestFactory
  public Collection<DynamicTest> length() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/length.tf");
  }

  @TestFactory
  public Collection<DynamicTest> concat() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/concat.tf");
  }

  @TestFactory
  public Collection<DynamicTest> substring() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/substring.tf");
  }

  @TestFactory
  public Collection<DynamicTest> replace() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/replace.tf");
  }

  @TestFactory
  public Collection<DynamicTest> join() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/join.tf");
  }

  @TestFactory
  public Collection<DynamicTest> trim() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/trim.tf");
  }

  @TestFactory
  public Collection<DynamicTest> lower_case() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/lower_case.tf");
  }

  @TestFactory
  public Collection<DynamicTest> upper_case() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/upper_case.tf");
  }

  @TestFactory
  public Collection<DynamicTest> chars() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/chars.tf");
  }

  @TestFactory
  public Collection<DynamicTest> split() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/split.tf");
  }

  @TestFactory
  public Collection<DynamicTest> comparator() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/comparator.tf");
  }

  @TestFactory
  public Collection<DynamicTest> starts_with() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/starts_with.tf");
  }

  @TestFactory
  public Collection<DynamicTest> ends_with() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/ends_with.tf");
  }

  @TestFactory
  public Collection<DynamicTest> index_of() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/index_of.tf");
  }

  @TestFactory
  public Collection<DynamicTest> last_index_of() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/last_index_of.tf");
  }

  @TestFactory
  public Collection<DynamicTest> char_at() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/char_at.tf");
  }

  @TestFactory
  public Collection<DynamicTest> code_point_at() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/code_point_at.tf");
  }

  @TestFactory
  public Collection<DynamicTest> code_points() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/code_points.tf");
  }

  @TestFactory
  public Collection<DynamicTest> of_code_points() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/strings/of_code_points.tf");
  }

}