import time as t from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.nano_of_second as nano_of_second;

library nano_of_second_spec {

  of_default:
    expect(nano_of_second(), to.be_nil());

  of_x_nil:
    expect(nano_of_second(nil), to.be_nil());

  of_epoch:
    expect(nano_of_second(t.epoch), to.be(0));

  of_0:
    expect(nano_of_second(2019-03-12T12:33:00.0), to.be(0));

  of_1:
    expect(nano_of_second(2019-04-23T12:23:01.000000001), to.be(1));

  of_half_second:
    expect(nano_of_second(2019-12-31T13:33:23.5), to.be(500000000));

  of_tenth_second:
    expect(nano_of_second(2019-12-31T12:23:59.1), to.be(100000000));

}