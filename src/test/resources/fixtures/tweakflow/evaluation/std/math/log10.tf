import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.log10 as log10;

library log10_spec {

  of_default:
    expect(log10(), to.be_nil());

  of_nil:
    expect(log10(nil), to.be_nil());

  of_zero:
    expect(log10(0.0), to.be(-Infinity));

  of_NaN:
    expect(log10(NaN), to.be_NaN());

  of_neg:
    expect(log10(-1.0), to.be_NaN());

  of_Infinity:
    expect(log10(Infinity), to.be(Infinity));

  of_one:
    expect(log10(1), to.be_close_to(0));

  of_10_pow_2:
    expect(log10(10**2), to.be_close_to(2.0));

  of_10_pow_10:
    expect(log10(10**10), to.be_close_to(10.0));

}