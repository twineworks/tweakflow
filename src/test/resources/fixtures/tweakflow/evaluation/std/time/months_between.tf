import time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.months_between as months_between;

library months_between_spec {

  of_default:
    expect(months_between(), to.be_nil());

  of_start_nil:
    expect(months_between(nil, t.epoch), to.be_nil());

  of_end_nil:
    expect(months_between(t.epoch, nil), to.be_nil());

  same:
    expect(
      months_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
      to.be(0)
    );
 
  one_sec:
    expect(
      months_between(1970-01-01T00:00:00, 1970-01-01T00:00:01),
      to.be(0)
    );

  one_min:
    expect(
      months_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
      to.be(0)
    );

  one_hour:
    expect(
      months_between(1970-01-01T00:00:00, 1970-01-01T01:00:00),
      to.be(0)
    );

  one_day:
    expect(
      months_between(1970-01-01T00:00:00, 1970-01-02T00:00:00),
      to.be(0)
    );

  one_month:
    expect(
      months_between(1970-01-01T00:00:00, 1970-02-01T00:00:00),
      to.be(1)
    );

  five_months:
    expect(
      months_between(1970-01-01T00:00:00, 1970-06-01T00:00:00),
      to.be(5)
    );

  five_months_seven_days:
    expect(
      months_between(1970-01-01T00:00:00, 1970-06-08T00:00:00),
      to.be(5)
    );

  one_year:
    expect(
      months_between(1970-01-01T00:00:00, 1971-01-01T00:00:00),
      to.be(12)
    );

  one_year_inverse:
    expect(
      months_between(1971-01-01T00:00:00, 1970-01-01T00:00:00),
      to.be(-12)
    );

}