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

public class MathTest {

  @TestFactory
  public Collection<DynamicTest> abs() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/abs.tf");
  }

  @TestFactory
  public Collection<DynamicTest> rand() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/rand.tf");
  }

  @TestFactory
  public Collection<DynamicTest> inc() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/inc.tf");
  }

  @TestFactory
  public Collection<DynamicTest> dec() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/dec.tf");
  }

  @TestFactory
  public Collection<DynamicTest> compare() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/compare.tf");
  }

  @TestFactory
  public Collection<DynamicTest> min() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/min.tf");
  }

  @TestFactory
  public Collection<DynamicTest> max() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/max.tf");
  }

  @TestFactory
  public Collection<DynamicTest> round() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/round.tf");
  }

  @TestFactory
  public Collection<DynamicTest> ceil() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/ceil.tf");
  }

  @TestFactory
  public Collection<DynamicTest> floor() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/floor.tf");
  }

  @TestFactory
  public Collection<DynamicTest> sqrt() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/sqrt.tf");
  }

  @TestFactory
  public Collection<DynamicTest> sin() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/sin.tf");
  }

  @TestFactory
  public Collection<DynamicTest> cos() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/cos.tf");
  }

}