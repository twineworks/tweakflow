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

public class TimeTest {

  @TestFactory
  public Collection<DynamicTest> epoch() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/epoch.tf");
  }

  @TestFactory
  public Collection<DynamicTest> seconds_between() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/seconds_between.tf");
  }

  @TestFactory
  public Collection<DynamicTest> minutes_between() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/minutes_between.tf");
  }

  @TestFactory
  public Collection<DynamicTest> hours_between() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/hours_between.tf");
  }

  @TestFactory
  public Collection<DynamicTest> days_between() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/days_between.tf");
  }

  @TestFactory
  public Collection<DynamicTest> months_between() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/months_between.tf");
  }

  @TestFactory
  public Collection<DynamicTest> years_between() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/years_between.tf");
  }

  @TestFactory
  public Collection<DynamicTest> period_between() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/period_between.tf");
  }

  @TestFactory
  public Collection<DynamicTest> duration_between() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/duration_between.tf");
  }

  @TestFactory
  public Collection<DynamicTest> add_period() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/add_period.tf");
  }

  @TestFactory
  public Collection<DynamicTest> add_duration() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/add_duration.tf");
  }

  @TestFactory
  public Collection<DynamicTest> year() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/year.tf");
  }

  @TestFactory
  public Collection<DynamicTest> month() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/month.tf");
  }

  @TestFactory
  public Collection<DynamicTest> day_of_month() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/day_of_month.tf");
  }

  @TestFactory
  public Collection<DynamicTest> day_of_year() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/day_of_year.tf");
  }

  @TestFactory
  public Collection<DynamicTest> day_of_week() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/day_of_week.tf");
  }

  @TestFactory
  public Collection<DynamicTest> hour() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/hour.tf");
  }

  @TestFactory
  public Collection<DynamicTest> minute() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/minute.tf");
  }

  @TestFactory
  public Collection<DynamicTest> second() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/second.tf");
  }

  @TestFactory
  public Collection<DynamicTest> nano_of_second() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/nano_of_second.tf");
  }

  @TestFactory
  public Collection<DynamicTest> week_of_year() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/week_of_year.tf");
  }

  @TestFactory
  public Collection<DynamicTest> offset_seconds() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/offset_seconds.tf");
  }

  @TestFactory
  public Collection<DynamicTest> zone() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/zone.tf");
  }

  @TestFactory
  public Collection<DynamicTest> with_year() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/with_year.tf");
  }

  @TestFactory
  public Collection<DynamicTest> with_month() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/with_month.tf");
  }

  @TestFactory
  public Collection<DynamicTest> with_day_of_month() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/with_day_of_month.tf");
  }

  @TestFactory
  public Collection<DynamicTest> with_hour() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/with_hour.tf");
  }

  @TestFactory
  public Collection<DynamicTest> with_minute() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/with_minute.tf");
  }

  @TestFactory
  public Collection<DynamicTest> with_second() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/with_second.tf");
  }

  @TestFactory
  public Collection<DynamicTest> with_nano_of_second() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/with_nano_of_second.tf");
  }

  @TestFactory
  public Collection<DynamicTest> with_zone() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/with_zone.tf");
  }

  @TestFactory
  public Collection<DynamicTest> same_instant_at_zone() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/same_instant_at_zone.tf");
  }

  @TestFactory
  public Collection<DynamicTest> unix_timestamp() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/unix_timestamp.tf");
  }

  @TestFactory
  public Collection<DynamicTest> unix_timestamp_ms() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/unix_timestamp_ms.tf");
  }

  @TestFactory
  public Collection<DynamicTest> compare() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/compare.tf");
  }

  @TestFactory
  public Collection<DynamicTest> formatter() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/formatter.tf");
  }

  @TestFactory
  public Collection<DynamicTest> parser() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/parser.tf");
  }

  @TestFactory
  public Collection<DynamicTest> zones() throws Exception {
    return TestHelper.dynamicTestsSpecModule("fixtures/tweakflow/evaluation/std/time/zones.tf");
  }

}