import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.atan as atan;

library atan_spec {

  of_default:
    expect(atan(), to.be_nil());

  of_nil:
    expect(atan(nil), to.be_nil());

  of_zero:
    expect(atan(0.0), to.be(0.0));

  of_NaN:
    expect(atan(NaN), to.be_NaN());

  of_Infinity:
    expect(atan(Infinity), to.be_close_to(m.pi/2));

  of_neg_Infinity:
    expect(atan(-Infinity), to.be_close_to(-m.pi/2));

  of_mid_pos:
    expect(atan(1.5574077246549023), to.be_close_to(1.0));

  of_mid_neg:
    expect(atan(-1.5574077246549023), to.be_close_to(-1.0));




}