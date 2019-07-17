import time as t from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias t.with_second as with_second;

library spec {
  spec:
    describe("with_second", [


  of_default:
    expect(with_second(), to.be_nil());

  of_x_nil:
    expect(with_second(nil, 1), to.be_nil());

  of_second_nil:
    expect(with_second(t.epoch, nil), to.be_nil());

  with_01:
    expect(with_second(t.epoch, 1), to.be(1970-01-01T00:00:01));

  with_59:
    expect(with_second(t.epoch, 59), to.be(1970-01-01T00:00:59));

  out_of_range:
    expect_error(
      () -> with_second(t.epoch, 60),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}