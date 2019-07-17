import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.round as round;

library spec {
  spec:
    describe("round", [


  of_default:
    expect(round(), to.be_nil());

  of_nil:
    expect(round(nil), to.be_nil());

  of_NaN:
    expect(round(NaN), to.be(0));

  of_min_long:
    expect(round(m.min_long), to.be(m.min_long));

  of_max_long:
    expect(round(m.max_long), to.be(m.max_long));

  of_infinity:
    expect(round(Infinity), to.be(m.max_long));

  of_neg_infinity:
    expect(round(-Infinity), to.be(m.min_long));

  of_pos_down:
    expect(round(2.3), to.be(2));

  of_pos_mid_up:
    expect(round(2.5), to.be(3));

  of_pos_up:
    expect(round(2.7), to.be(3));

  of_neg_up:
    expect(round(-2.3), to.be(-2));

  of_neg_mid_up:
    expect(round(-2.5), to.be(-2));

  of_neg_down:
    expect(round(-2.7), to.be(-3));

  ]);
}