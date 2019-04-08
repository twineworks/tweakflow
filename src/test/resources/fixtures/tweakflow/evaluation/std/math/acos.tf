import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.acos as acos;

library acos_spec {

  of_default:
    expect(acos(), to.be_nil());

  of_nil:
    expect(acos(nil), to.be_nil());

  of_zero:
    expect(acos(0.0), to.be_close_to(m.pi/2));

  of_NaN:
    expect(acos(NaN), to.be_NaN());

  of_Infinity:
    expect(acos(Infinity), to.be_NaN());

  of_neg_Infinity:
    expect(acos(-Infinity), to.be_NaN());

  of_one:
    expect(acos(1.0), to.be_close_to(0));

  of_neg_one:
    expect(acos(-1.0), to.be_close_to(m.pi));


}