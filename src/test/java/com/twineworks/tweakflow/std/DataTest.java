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

public class DataTest {

  @Test
  public void size() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/size.tf");
  }

  @Test
  public void empty() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/empty.tf");
  }

  @Test
  public void get_in() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/get_in.tf");
  }

  @Test
  public void get() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/get.tf");
  }

  @Test
  public void put() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/put.tf");
  }

  @Test
  public void filter() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/filter.tf");
  }

  @Test
  public void map() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/map.tf");
  }

  @Test
  public void has() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/has.tf");
  }

  @Test
  public void sort() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/sort.tf");
  }

  @Test
  public void reduce() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/reduce.tf");
  }

  @Test
  public void inspect() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/data/inspect.tf");
  }


}