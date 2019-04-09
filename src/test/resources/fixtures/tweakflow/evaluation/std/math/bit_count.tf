import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.bit_count as bit_count;

library bit_count_spec {

  of_default:
    expect(bit_count(), to.be_nil());

  of_nil:
    expect(bit_count(nil), to.be_nil());

  of_zero:
    expect(bit_count(0), to.be(0));

  of_1:
    expect(bit_count(1), to.be(1));

  of_neg_1:
    expect(bit_count(-1), to.be(64));

  of_2:
    expect(bit_count(2), to.be(1));

  of_3:
    expect(bit_count(3), to.be(2));

  of_4:
    expect(bit_count(4), to.be(1));

  of_7:
    expect(bit_count(7), to.be(3));

  of_15:
    expect(bit_count(15), to.be(4));

  of_1024:
    expect(bit_count(1024), to.be(1));

}