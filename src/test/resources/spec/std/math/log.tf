import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.log as log;

library spec {
  spec:
    describe("log", [


  of_default:
    expect(log(), to.be_nil());

  of_nil:
    expect(log(nil), to.be_nil());

  of_zero:
    expect(log(0.0), to.be(-Infinity));

  of_NaN:
    expect(log(NaN), to.be_NaN());

  of_neg:
    expect(log(-1.0), to.be_NaN());

  of_Infinity:
    expect(log(Infinity), to.be(Infinity));

  of_one:
    expect(log(1), to.be_close_to(0));

  of_e_pow_2:
    expect(log(m.e**2), to.be_close_to(2.0));

  of_e_pow_10:
    expect(log(m.e**10), to.be_close_to(10.0));

  ]);
}