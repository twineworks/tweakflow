import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.day_of_week as day_of_week;

library spec {
  spec:
    describe("day_of_week", [


  of_default:
    expect(day_of_week(), to.be_nil());

  of_x_nil:
    expect(day_of_week(nil), to.be_nil());

  of_epoch:
    expect(day_of_week(t.epoch), to.be(4));

  of_mon_2019_04_08:
    expect(day_of_week(2019-04-08T), to.be(1));

  of_tue_2019_04_09:
    expect(day_of_week(2019-04-09T), to.be(2));

  of_wed_2019_04_10:
    expect(day_of_week(2019-04-10T), to.be(3));

  of_thu_2019_04_11:
    expect(day_of_week(2019-04-11T), to.be(4));

  of_fri_2019_04_12:
    expect(day_of_week(2019-04-12T), to.be(5));

  of_sat_2019_04_13:
    expect(day_of_week(2019-04-13T), to.be(6));

  of_sun_2019_04_14:
    expect(day_of_week(2019-04-14T), to.be(7));

  of_sun_1770_12_16:
    expect(day_of_week(1770-12-16T), to.be(7));

  ]);
}