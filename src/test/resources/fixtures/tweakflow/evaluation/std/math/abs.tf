import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.abs as abs;

library abs_spec {

  of_default:
    expect(abs(), to.be_nil());

  of_nil:
    expect(abs(nil), to.be_nil());

  pos_long:
    expect(abs(12345), to.be(12345));

  of_NaN:
    expect(abs(NaN), to.be_NaN());

  of_pos_infinity:
    expect(abs(Infinity), to.be(Infinity));

  of_neg_infinity:
    expect(abs(-Infinity), to.be(Infinity));

  pos_double:
    expect(abs(12345.6789), to.be(12345.6789));

  neg_long:
    expect(abs(-12345), to.be(12345));

  neg_double:
    expect(abs(-12345.6789), to.be(12345.6789));

  of_min_long:
    expect_error(
      () -> abs(m.min_long),
      to.have_code("NUMBER_OUT_OF_BOUNDS")
    );

  of_non_numeric:
    expect_error(
      () -> abs("foo"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}