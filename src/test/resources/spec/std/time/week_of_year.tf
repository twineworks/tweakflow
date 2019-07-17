import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.week_of_year as week_of_year;

library spec {
  spec:
    describe("week_of_year", [


  of_default:
    expect(week_of_year(), to.be_nil());

  of_x_nil:
    expect(week_of_year(nil), to.be_nil());

  of_epoch:
    expect(week_of_year(t.epoch), to.be(1));

  of_52nd_week:
    expect(week_of_year(2011-01-01T), to.be(52));

  of_53rd_week:
    expect(week_of_year(2010-01-01T), to.be(53));

  of_1st_week:
    expect(week_of_year(2013-01-01T), to.be(1));

  of_2nd_week:
    expect(week_of_year(2013-01-08T), to.be(2));

  of_8th_week:
    expect(week_of_year(2017-02-21T), to.be(8));

  ]);
}