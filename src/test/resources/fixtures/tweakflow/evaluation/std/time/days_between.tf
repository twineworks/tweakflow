import time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.days_between as days_between;

library days_between_spec {

  of_default:
    expect(days_between(), to.be_nil());

  of_start_nil:
    expect(days_between(nil, t.epoch), to.be_nil());

  of_end_nil:
    expect(days_between(t.epoch, nil), to.be_nil());

  same:
    expect(
      days_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
      to.be(0)
    );

  one_sec:
    expect(
      days_between(1970-01-01T00:00:00, 1970-01-01T00:00:01),
      to.be(0)
    );

  one_min:
    expect(
      days_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
      to.be(0)
    );

  one_hour:
    expect(
      days_between(1970-01-01T00:00:00, 1970-01-01T01:00:00),
      to.be(0)
    );

  one_day:
    expect(
      days_between(1970-01-01T00:00:00, 1970-01-02T00:00:00),
      to.be(1)
    );

  one_year:
    expect(
      days_between(1970-01-01T00:00:00, 1971-01-01T00:00:00),
      to.be(365)
    );

  one_year_inverse:
    expect(
      days_between(1971-01-01T00:00:00, 1970-01-01T00:00:00),
      to.be(-365)
    );

}