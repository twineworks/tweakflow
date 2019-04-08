import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.asin as asin;

library asin_spec {

  of_default:
    expect(asin(), to.be_nil());

  of_nil:
    expect(asin(nil), to.be_nil());

  of_zero:
    expect(asin(0.0), to.be(0.0));

  of_NaN:
    expect(asin(NaN), to.be_NaN());

  of_Infinity:
    expect(asin(Infinity), to.be_NaN());

  of_neg_Infinity:
    expect(asin(-Infinity), to.be_NaN());

  of_one:
    expect(asin(1.0), to.be_close_to(m.pi/2));

  of_neg_one:
    expect(asin(-1.0), to.be_close_to(-m.pi/2));

}