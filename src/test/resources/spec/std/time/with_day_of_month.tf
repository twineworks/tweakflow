import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.with_day_of_month as with_day_of_month;

library spec {
  spec:
    describe("with_day_of_month", [


  of_default:
    expect(with_day_of_month(), to.be_nil());

  of_x_nil:
    expect(with_day_of_month(nil, 12), to.be_nil());

  of_day_nil:
    expect(with_day_of_month(t.epoch, nil), to.be_nil());

  with_12:
    expect(with_day_of_month(t.epoch, 12), to.be(1970-01-12T));

  with_leap:
    expect(with_day_of_month(2016-02-01T, 29), to.be(2016-02-29T));

  out_of_range_in_non_leap:
    expect_error(
      () -> with_day_of_month(2019-02-20T, 29),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  out_of_range_in_leap:
    expect_error(
      () -> with_day_of_month(2016-02-29T, 30),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  out_of_range:
    expect_error(
      () -> with_day_of_month(t.epoch, 32),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
  ]);
}