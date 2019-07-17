import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.max as max;

library spec {
  spec:
    describe("max", [


  of_default:
    expect(max(), to.be_nil());

  of_nil:
    expect(max(nil), to.be_nil());

  of_empty:
    expect(max([]), to.be_nil());

  of_only_NaN:
    expect(max([NaN]), to.be_nil());

  of_only_NaNs:
    expect(max([NaN, NaN]), to.be_nil());

  of_NaN_element:
    expect(max([1.0, NaN, 3.0]), to.be_nil()) &&
    expect(max([1, NaN, 3]), to.be_nil()) &&
    expect(max([1, 2.0, NaN, 3.0]), to.be_nil()) &&
    expect(max([2.0, 1, NaN, 3.0]), to.be_nil());

  of_nil_element:
    expect(max([0, 1, 2, nil]), to.be_nil());

  of_longs:
    expect(max([1,2,3]), to.be(3)) &&
    expect(max([1,-2,3]), to.be(3)) &&
    expect(max([1,2,-3]), to.be(2));

  of_double:
    expect(max([1.0, 2.0, 3.0]), to.be(3.0)) &&
    expect(max([1.0, -2.0, 3.0]), to.be(3.0)) &&
    expect(max([1.0, 2.0, -3.0]), to.be(2.0));

  of_mixed:
    expect(max([1, 2.0, 3.0]), to.be(3.0)) &&
    expect(max([1.0, -2.0, 3]), to.be(3)) &&
    expect(max([1, 2.0, -3.0]), to.be(2.0));

  of_non_numeric:
    expect_error(
      () -> max([1, 2, "foo"]),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}