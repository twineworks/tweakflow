import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.cos as cos;

library spec {
  spec:
    describe("cos", [


  of_default:
    expect(cos(), to.be_nil());

  of_nil:
    expect(cos(nil), to.be_nil());

  of_zero:
    expect(cos(0.0), to.be(1.0));

  of_NaN:
    expect(cos(NaN), to.be_NaN());

  of_Infinity:
    expect(cos(Infinity), to.be_NaN());

  of_neg_Infinity:
    expect(cos(-Infinity), to.be_NaN());

  of_one:
    expect(cos(1.0), to.be_close_to(0.5403023058681398));

  of_neg_one:
    expect(cos(-1.0), to.be_close_to(0.5403023058681398));

  of_half_pi:
    expect(cos(m.pi/2), to.be_close_to(0.0));

  of_neg_half_pi:
    expect(cos(-m.pi/2), to.be_close_to(0.0));

  of_pi:
    expect(cos(m.pi), to.be_close_to(-1.0));

  of_neg_pi:
    expect(cos(-m.pi), to.be_close_to(-1.0));

  of_two_pi:
    expect(cos(2*m.pi), to.be_close_to(1.0));

  of_neg_two_pi:
    expect(cos(-2*m.pi), to.be_close_to(1.0));

  ]);
}