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
import org.junit.Test;

public class StringsTest {

  @Test
  public void length() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/length.tf");
  }

  @Test
  public void concat() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/concat.tf");
  }

  @Test
  public void substring() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/substring.tf");
  }

  @Test
  public void replace() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/replace.tf");
  }

  @Test
  public void join() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/join.tf");
  }

  @Test
  public void trim() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/trim.tf");
  }

  @Test
  public void lower_case() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/lower_case.tf");
  }

  @Test
  public void upper_case() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/upper_case.tf");
  }

  @Test
  public void chars() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/chars.tf");
  }

  @Test
  public void split() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/split.tf");
  }

  @Test
  public void comparator() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/comparator.tf");
  }

  @Test
  public void starts_with() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/starts_with.tf");
  }

  @Test
  public void ends_with() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/ends_with.tf");
  }

  @Test
  public void index_of() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/index_of.tf");
  }

  @Test
  public void last_index_of() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/last_index_of.tf");
  }

  @Test
  public void char_at() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/char_at.tf");
  }

  @Test
  public void code_point_at() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/code_point_at.tf");
  }

  @Test
  public void code_points() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/code_points.tf");
  }

  @Test
  public void of_code_points() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/strings/of_code_points.tf");
  }

}