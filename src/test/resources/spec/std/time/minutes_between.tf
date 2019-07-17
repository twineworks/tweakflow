import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.minutes_between as minutes_between;

library spec {
  spec:
    describe("minutes_between", [


  of_default:
    expect(minutes_between(), to.be_nil());

  of_start_nil:
    expect(minutes_between(nil, t.epoch), to.be_nil());

  of_end_nil:
    expect(minutes_between(t.epoch, nil), to.be_nil());

  same:
    expect(
      minutes_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
      to.be(0)
    );

  one_sec:
    expect(
      minutes_between(1970-01-01T00:00:00, 1970-01-01T00:00:01),
      to.be(0)
    );

  cross_zones:
    expect(
      minutes_between(1970-01-01T00:00:00+04:00, 1970-01-01T00:00:00+03:00),
      to.be(60)
    );

  one_min:
    expect(
      minutes_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
      to.be(1)
    );

  one_sec_inverse:
    expect(
      minutes_between(1970-01-01T00:00:01, 1970-01-01T00:00:00),
      to.be(0)
    );

  one_min_inverse:
    expect(
      minutes_between(1970-01-01T00:01:00, 1970-01-01T00:00:00),
      to.be(-1)
    );

  one_hour:
    expect(
      minutes_between(1970-01-01T00:00:00, 1970-01-01T01:00:00),
      to.be(60)
    );

  one_hour_inverse:
    expect(
      minutes_between(1970-01-01T01:00:00, 1970-01-01T00:00:00),
      to.be(-60)
    );

  ]);
}