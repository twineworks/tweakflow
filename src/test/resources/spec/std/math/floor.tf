import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.floor as floor;

library spec {
  spec:
    describe("floor", [


  of_default:
    expect(floor(), to.be_nil());

  of_nil:
    expect(floor(nil), to.be_nil());

  of_NaN:
    expect(floor(NaN), to.be_NaN());

  of_zero:
    expect(floor(0.0), to.be(0.0));

  of_neg_zero:
    expect(floor(-0.0), to.be(-0.0));

  of_neg_small:
    expect(floor(-0.2), to.be(-1.0));

  of_neg_one:
    expect(floor(-1.0), to.be(-1.0));

  of_neg:
    expect(floor(-2.6), to.be(-3.0));

  of_infinity:
    expect(floor(Infinity), to.be(Infinity));

  of_neg_infinity:
    expect(floor(-Infinity), to.be(-Infinity));

  of_pos:
    expect(floor(2.3), to.be(2.0));

  of_pos_one:
    expect(floor(1.0), to.be(1.0));


  ]);
}