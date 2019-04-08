import math as m from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias m.dec as dec;

library dec_spec {

  of_default:
    expect(dec(), to.be_nil());

  of_nil:
    expect(dec(nil), to.be_nil());

  of_zero_long:
    expect(dec(0), to.be(-1));

  of_NaN:
    expect(dec(NaN), to.be_NaN());

  of_one_long:
    expect(dec(1), to.be(0));

  of_neg_one_long:
    expect(dec(-1), to.be(-2));

  of_zero_double:
    expect(dec(0.0), to.be(-1.0));

  of_one_double:
    expect(dec(1.0), to.be(0.0));

  of_neg_one_double:
    expect(dec(-1.0), to.be(-2.0));

  of_min_long:
    expect(dec(m.min_long), to.be(m.max_long));

  of_min_long_as_double:
    expect(dec(m.min_long as double), to.be(m.min_long as double - 1.0));

  of_non_numeric:
    expect_error(
      () -> dec("foo"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}