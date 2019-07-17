import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.with_hour as with_hour;

library spec {
  spec:
    describe("with_hour", [


  of_default:
    expect(with_hour(), to.be_nil());

  of_x_nil:
    expect(with_hour(nil, 1), to.be_nil());

  of_hour_nil:
    expect(with_hour(t.epoch, nil), to.be_nil());

  with_01:
    expect(with_hour(t.epoch, 1), to.be(1970-01-01T01:00:00));

  with_23:
    expect(with_hour(t.epoch, 23), to.be(1970-01-01T23:00:00));

  with_change_to_dst_gap:
    expect(
      with_hour(2019-03-31T01:30:00+01:00@Europe/Berlin, 2),
      to.be(2019-03-31T03:30:00+02:00@Europe/Berlin)
    );

  out_of_range:
    expect_error(
      () -> with_hour(t.epoch, 1000000000),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}