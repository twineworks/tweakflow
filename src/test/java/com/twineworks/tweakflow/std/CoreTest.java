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

public class CoreTest {

  @Test
  public void eval() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/core/eval.tf");
  }

  @Test
  public void id() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/core/id.tf");
  }

  @Test
  public void inspect() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/core/inspect.tf");
  }

  @Test
  public void nil() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/core/nil.tf");
  }

  @Test
  public void present() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/core/present.tf");
  }

  @Test
  public void hash() throws Exception {
    TestHelper.assertSpecModule("fixtures/tweakflow/evaluation/std/core/hash.tf");
  }

}