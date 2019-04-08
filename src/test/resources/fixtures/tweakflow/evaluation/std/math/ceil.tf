import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.ceil as ceil;

library ceil_spec {

  of_default:
    expect(ceil(), to.be_nil());

  of_nil:
    expect(ceil(nil), to.be_nil());

  of_NaN:
    expect(ceil(NaN), to.be_NaN());

  of_zero:
    expect(ceil(0.0), to.be(0.0));

  of_neg_zero:
    expect(ceil(-0.0), to.be(-0.0));

  of_neg_small:
    expect(ceil(-0.2), to.be(-0.0));

  of_neg_one:
    expect(ceil(-1.0), to.be(-1.0));

  of_neg:
    expect(ceil(-2.6), to.be(-2.0));

  of_infinity:
    expect(ceil(Infinity), to.be(Infinity));

  of_neg_infinity:
    expect(ceil(-Infinity), to.be(-Infinity));

  of_pos:
    expect(ceil(2.3), to.be(3.0));

  of_pos_one:
    expect(ceil(1.0), to.be(1.0));


}