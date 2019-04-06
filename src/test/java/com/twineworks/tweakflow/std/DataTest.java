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

public class DataTest {

  @TestFactory
  public Collection<DynamicTest> size() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/size.tf");
  }

  @TestFactory
  public Collection<DynamicTest> select() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/select.tf");
  }

  @TestFactory
  public Collection<DynamicTest> insert() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/insert.tf");
  }

  @TestFactory
  public Collection<DynamicTest> delete() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/delete.tf");
  }

  @TestFactory
  public Collection<DynamicTest> find() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/find.tf");
  }

  @TestFactory
  public Collection<DynamicTest> find_index() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/find_index.tf");
  }

  @TestFactory
  public Collection<DynamicTest> prepend() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/prepend.tf");
  }

  @TestFactory
  public Collection<DynamicTest> append() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/append.tf");
  }

  @TestFactory
  public Collection<DynamicTest> init() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/init.tf");
  }

  @TestFactory
  public Collection<DynamicTest> tail() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/tail.tf");
  }

  @TestFactory
  public Collection<DynamicTest> head() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/head.tf");
  }

  @TestFactory
  public Collection<DynamicTest> last() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/last.tf");
  }

  @TestFactory
  public Collection<DynamicTest> slice() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/slice.tf");
  }

  @TestFactory
  public Collection<DynamicTest> slices() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/slices.tf");
  }

  @TestFactory
  public Collection<DynamicTest> reverse() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/reverse.tf");
  }

  @TestFactory
  public Collection<DynamicTest> empty() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/empty.tf");
  }

  @TestFactory
  public Collection<DynamicTest> get_in() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/get_in.tf");
  }

  @TestFactory
  public Collection<DynamicTest> get() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/get.tf");
  }

  @TestFactory
  public Collection<DynamicTest> put() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/put.tf");
  }

  @TestFactory
  public Collection<DynamicTest> put_in() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/put_in.tf");
  }

  @TestFactory
  public Collection<DynamicTest> update() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/update.tf");
  }

  @TestFactory
  public Collection<DynamicTest> update_in() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/update_in.tf");
  }

  @TestFactory
  public Collection<DynamicTest> keys() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/keys.tf");
  }

  @TestFactory
  public Collection<DynamicTest> values() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/values.tf");
  }

  @TestFactory
  public Collection<DynamicTest> entries() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/entries.tf");
  }

  @TestFactory
  public Collection<DynamicTest> has() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/has.tf");
  }

  @TestFactory
  public Collection<DynamicTest> any() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/any.tf");
  }

  @TestFactory
  public Collection<DynamicTest> all() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/all.tf");
  }

  @TestFactory
  public Collection<DynamicTest> none() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/none.tf");
  }

  @TestFactory
  public Collection<DynamicTest> filter() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/filter.tf");
  }

  @TestFactory
  public Collection<DynamicTest> shuffle() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/shuffle.tf");
  }

  @TestFactory
  public Collection<DynamicTest> unique() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/unique.tf");
  }

  @TestFactory
  public Collection<DynamicTest> range() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/range.tf");
  }

  @TestFactory
  public Collection<DynamicTest> map() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/map.tf");
  }

  @TestFactory
  public Collection<DynamicTest> sort() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/sort.tf");
  }

  @TestFactory
  public Collection<DynamicTest> repeat() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/repeat.tf");
  }

  @TestFactory
  public Collection<DynamicTest> concat() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/concat.tf");
  }

  @TestFactory
  public Collection<DynamicTest> merge() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/merge.tf");
  }

  @TestFactory
  public Collection<DynamicTest> take() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/take.tf");
  }

  @TestFactory
  public Collection<DynamicTest> take_while() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/take_while.tf");
  }

  @TestFactory
  public Collection<DynamicTest> take_until() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/take_until.tf");
  }

  @TestFactory
  public Collection<DynamicTest> drop() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/drop.tf");
  }

  @TestFactory
  public Collection<DynamicTest> drop_while() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/drop_while.tf");
  }

  @TestFactory
  public Collection<DynamicTest> drop_until() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/drop_until.tf");
  }

  @TestFactory
  public Collection<DynamicTest> reduce() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/reduce.tf");
  }

  @TestFactory
  public Collection<DynamicTest> inspect() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/inspect.tf");
  }

  @TestFactory
  public Collection<DynamicTest> contains() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/contains.tf");
  }

  @TestFactory
  public Collection<DynamicTest> index_of() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/index_of.tf");
  }

  @TestFactory
  public Collection<DynamicTest> last_index_of() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/last_index_of.tf");
  }

  @TestFactory
  public Collection<DynamicTest> key_of() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/key_of.tf");
  }

  @TestFactory
  public Collection<DynamicTest> flatten() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/flatten.tf");
  }

  @TestFactory
  public Collection<DynamicTest> flatmap() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/flatmap.tf");
  }

  @TestFactory
  public Collection<DynamicTest> mapcat() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/mapcat.tf");
  }

  @TestFactory
  public Collection<DynamicTest> zip() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/zip.tf");
  }

  @TestFactory
  public Collection<DynamicTest> zip_dict() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/zip_dict.tf");
  }

  @TestFactory
  public Collection<DynamicTest> interpose() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/interpose.tf");
  }

  @TestFactory
  public Collection<DynamicTest> reduce_until() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/reduce_until.tf");
  }

  @TestFactory
  public Collection<DynamicTest> reduce_while() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/data/reduce_while.tf");
  }

}