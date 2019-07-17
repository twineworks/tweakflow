import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.years_between as years_between;

library spec {
  spec:
    describe("years_between", [


  of_default:
    expect(years_between(), to.be_nil());

  of_start_nil:
    expect(years_between(nil, t.epoch), to.be_nil());

  of_end_nil:
    expect(years_between(t.epoch, nil), to.be_nil());

  same:
    expect(
      years_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
      to.be(0)
    );
 
  one_sec:
    expect(
      years_between(1970-01-01T00:00:00, 1970-01-01T00:00:01),
      to.be(0)
    );

  one_min:
    expect(
      years_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
      to.be(0)
    );

  one_hour:
    expect(
      years_between(1970-01-01T00:00:00, 1970-01-01T01:00:00),
      to.be(0)
    );

  one_day:
    expect(
      years_between(1970-01-01T00:00:00, 1970-01-02T00:00:00),
      to.be(0)
    );

  one_month:
    expect(
      years_between(1970-01-01T00:00:00, 1970-02-01T00:00:00),
      to.be(0)
    );

  one_year:
    expect(
      years_between(1970-01-01T00:00:00, 1971-01-01T00:00:00),
      to.be(1)
    );

  five_years:
    expect(
      years_between(1970-01-01T00:00:00, 1975-11-12T00:00:00),
      to.be(5)
    );

  one_year_inverse:
    expect(
      years_between(1971-01-01T00:00:00, 1970-01-01T00:00:00),
      to.be(-1)
    );

  five_years_inverse:
    expect(
      years_between(1975-11-12T00:00:00, 1970-01-01T00:00:00),
      to.be(-5)
    );

  ]);
}