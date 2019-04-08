import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.sin as sin;

library sin_spec {

  of_default:
    expect(sin(), to.be_nil());

  of_nil:
    expect(sin(nil), to.be_nil());

  of_zero:
    expect(sin(0.0), to.be(0.0));

  of_neg_zero:
    expect(sin(-0.0), to.be(-0.0));

  of_NaN:
    expect(sin(NaN), to.be_NaN());

  of_Infinity:
    expect(sin(Infinity), to.be_NaN());

  of_neg_Infinity:
    expect(sin(-Infinity), to.be_NaN());

  of_one:
    expect(sin(1.0), to.be_close_to(0.8414709848078965));

  of_neg_one:
    expect(sin(-1.0), to.be_close_to(-0.8414709848078965));

  of_half_pi:
    expect(sin(m.pi/2), to.be_close_to(1.0));

  of_neg_half_pi:
    expect(sin(-m.pi/2), to.be_close_to(-1.0));

  of_pi:
    expect(sin(m.pi), to.be_close_to(0.0));

  of_neg_pi:
    expect(sin(-m.pi), to.be_close_to(0.0));

  of_two_pi:
    expect(sin(2*m.pi), to.be_close_to(0.0));

  of_neg_two_pi:
    expect(sin(-2*m.pi), to.be_close_to(0.0));

}