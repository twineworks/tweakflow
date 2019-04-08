import time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.with_month as with_month;

library with_month_spec {

  of_default:
    expect(with_month(), to.be_nil());

  of_x_nil:
    expect(with_month(nil, 12), to.be_nil());

  of_month_nil:
    expect(with_month(t.epoch, nil), to.be_nil());

  with_12:
    expect(with_month(t.epoch, 12), to.be(1970-12-01T));

  with_03:
    expect(with_month(t.epoch, 3), to.be(1970-03-01T));

  with_change_from_longer_month:
    expect(with_month(2016-03-31T, 2), to.be(2016-02-29T));

  out_of_range:
    expect_error(
      () -> with_month(t.epoch, 13),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}