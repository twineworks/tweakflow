import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.sqrt as sqrt;

library spec {
  spec:
    describe("sqrt", [


  of_default:
    expect(sqrt(), to.be_nil());

  of_nil:
    expect(sqrt(nil), to.be_nil());

  of_zero:
    expect(sqrt(0.0), to.be(0.0));

  of_neg_zero:
    expect(sqrt(-0.0), to.be(-0.0));

  of_NaN:
    expect(sqrt(NaN), to.be_NaN());

  of_Infinity:
    expect(sqrt(Infinity), to.be(Infinity));

  of_neg_Infinity:
    expect(sqrt(-Infinity), to.be_NaN());

  of_one:
    expect(sqrt(1.0), to.be(1.0));

  of_neg_one:
    expect(sqrt(-1.0), to.be_NaN());

  of_four:
    expect(sqrt(4.0), to.be(2.0));

  of_two:
    expect(sqrt(2.0), to.be_close_to(1.4142135623730951));


  ]);
}