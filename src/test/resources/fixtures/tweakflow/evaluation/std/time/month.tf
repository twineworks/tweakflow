import time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.month as month;

library month_spec {

  of_default:
    expect(month(), to.be_nil());

  of_x_nil:
    expect(month(nil), to.be_nil());

  of_epoch:
    expect(month(t.epoch), to.be(1));

  of_1:
    expect(month(0001-01-01T), to.be(1));

  of_12:
    expect(month(2019-12-01T), to.be(12));

}