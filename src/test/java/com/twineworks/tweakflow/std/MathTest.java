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

  @TestFactory
  public Collection<DynamicTest> tan() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/tan.tf");
  }

  @TestFactory
  public Collection<DynamicTest> asin() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/asin.tf");
  }

  @TestFactory
  public Collection<DynamicTest> acos() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/acos.tf");
  }

  @TestFactory
  public Collection<DynamicTest> atan() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/atan.tf");
  }

  @TestFactory
  public Collection<DynamicTest> log() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/log.tf");
  }

  @TestFactory
  public Collection<DynamicTest> log10() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/log10.tf");
  }

  @TestFactory
  public Collection<DynamicTest> bit_count() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/bit_count.tf");
  }

  @TestFactory
  public Collection<DynamicTest> formatter() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/formatter.tf");
  }

  @TestFactory
  public Collection<DynamicTest> parser() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/parser.tf");
  }

  @TestFactory
  public Collection<DynamicTest> e() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/e.tf");
  }

  @TestFactory
  public Collection<DynamicTest> pi() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/pi.tf");
  }

  @TestFactory
  public Collection<DynamicTest> min_long() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/min_long.tf");
  }

  @TestFactory
  public Collection<DynamicTest> max_long() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/math/max_long.tf");
  }

}