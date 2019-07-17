import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.seconds_between as seconds_between;

library spec {
  spec:
    describe("seconds_between", [


  of_default:
    expect(seconds_between(), to.be_nil());

  of_start_nil:
    expect(seconds_between(nil, t.epoch), to.be_nil());

  of_end_nil:
    expect(seconds_between(t.epoch, nil), to.be_nil());

  same:
    expect(
      seconds_between(1970-01-01T00:00:00, 1970-01-01T00:00:00),
      to.be(0)
    );

  one_sec:
    expect(
      seconds_between(1970-01-01T00:00:00, 1970-01-01T00:00:01),
      to.be(1)
    );

  cross_zones:
    expect(
      seconds_between(1970-01-01T04:00:00+04:00, 1970-01-01T00:00:00),
      to.be(0)
    );

  one_min:
    expect(
      seconds_between(1970-01-01T00:00:00, 1970-01-01T00:01:00),
      to.be(60)
    );

  one_sec_inverse:
    expect(
      seconds_between(1970-01-01T00:00:01, 1970-01-01T00:00:00),
      to.be(-1)
    );

  one_min_inverse:
    expect(
      seconds_between(1970-01-01T00:01:00, 1970-01-01T00:00:00),
      to.be(-60)
    );

  ]);
}