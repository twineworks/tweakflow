import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.day_of_month as day_of_month;

library spec {
  spec:
    describe("day_of_month", [


  of_default:
    expect(day_of_month(), to.be_nil());

  of_x_nil:
    expect(day_of_month(nil), to.be_nil());

  of_epoch:
    expect(day_of_month(t.epoch), to.be(1));

  of_1:
    expect(day_of_month(0001-01-01T), to.be(1));

  of_31:
    expect(day_of_month(2019-12-31T), to.be(31));

  ]);
}