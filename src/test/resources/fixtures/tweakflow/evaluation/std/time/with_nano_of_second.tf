import time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.with_nano_of_second as with_nano_of_second;

library with_nano_of_second_spec {

  of_default:
    expect(with_nano_of_second(), to.be_nil());

  of_x_nil:
    expect(with_nano_of_second(nil, 1), to.be_nil());

  of_nano_nil:
    expect(with_nano_of_second(t.epoch, nil), to.be_nil());

  with_1:
    expect(with_nano_of_second(t.epoch, 1), to.be(1970-01-01T00:00:00.000000001));

  with_one_tenth_of_second:
    expect(with_nano_of_second(t.epoch, 100000000), to.be(1970-01-01T00:00:00.1));

  with_half_of_second:
    expect(with_nano_of_second(t.epoch, 500000000), to.be(1970-01-01T00:00:00.5));

  out_of_range:
    expect_error(
      () -> with_nano_of_second(t.epoch, 1000000000),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}