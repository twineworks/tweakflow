import time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.with_minute as with_minute;

library with_minute_spec {

  of_default:
    expect(with_minute(), to.be_nil());

  of_x_nil:
    expect(with_minute(nil, 1), to.be_nil());

  of_minute_nil:
    expect(with_minute(t.epoch, nil), to.be_nil());

  with_01:
    expect(with_minute(t.epoch, 1), to.be(1970-01-01T00:01:00));

  with_59:
    expect(with_minute(t.epoch, 59), to.be(1970-01-01T00:59:00));

  out_of_range:
    expect_error(
      () -> with_minute(t.epoch, 60),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}