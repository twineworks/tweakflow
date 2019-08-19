import data, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.compare as compare;
alias data.sort as sort;

library spec {
  spec:
    describe("math.compare", [

      it("of_default", () ->
        expect(compare(), to.be(0))
      ),

      it("of_same_long_long", () ->
        expect(compare(12345, 12345), to.be(0))
      ),

      it("of_same_double_double", () ->
        expect(compare(12345.0, 12345.0), to.be(0))
      ),

      it("of_same_decimal_decimal", () ->
        expect(compare(12345.1d, 12345.1d), to.be(0))
      ),

      it("of_same_double_long", () ->
        expect(compare(12345.0, 12345), to.be(0))
      ),

      it("of_same_double_decimal", () ->
        expect(compare(12345.10, 12345.10d), to.be(0))
      ),

      it("of_same_long_double", () ->
        expect(compare(12345, 12345.0), to.be(0))
      ),

      it("of_same_long_decimal", () ->
        expect(compare(12345, 12345.0d), to.be(0))
      ),

      it("of_same_decimal_long", () ->
        expect(compare(12345d, 12345), to.be(0))
      ),

      it("of_same_decimal_double", () ->
        expect(compare(12345d, 12345.0), to.be(0))
      ),

      it("of_long_lt_long", () ->
        expect(compare(1, 2), to.be(-1))
      ),

      it("of_long_lt_double", () ->
        expect(compare(1, 1.1), to.be(-1))
      ),

      it("of_long_lt_decimal", () ->
        expect(compare(1, 1.1d), to.be(-1))
      ),

      it("of_double_lt_double", () ->
        expect(compare(1.0, 2.0), to.be(-1))
      ),

      it("of_double_lt_long", () ->
        expect(compare(1.0, 2), to.be(-1))
      ),

      it("of_double_lt_decimal", () ->
        expect(compare(1.0, 2d), to.be(-1))
      ),

      it("of_decimal_lt_decimal", () ->
        expect(compare(1d, 2d), to.be(-1))
      ),

      it("of_decimal_lt_long", () ->
        expect(compare(1d, 2), to.be(-1))
      ),

      it("of_decimal_lt_double", () ->
        expect(compare(1d, 2.0), to.be(-1))
      ),

      it("of_long_gt_long", () ->
        expect(compare(2, 1), to.be(1))
      ),

      it("of_double_gt_double", () ->
        expect(compare(2.0, 1.0), to.be(1))
      ),

      it("of_decimal_gt_decimal", () ->
        expect(compare(2d, 1d), to.be(1))
      ),

      it("of_long_gt_double", () ->
        expect(compare(2, 1.0), to.be(1))
      ),

      it("of_long_gt_decimal", () ->
        expect(compare(2, 1d), to.be(1))
      ),

      it("of_double_gt_long", () ->
        expect(compare(2.0, 1), to.be(1))
      ),

      it("of_double_gt_decimal", () ->
        expect(compare(2.0, 1d), to.be(1))
      ),

      it("of_decimal_gt_long", () ->
        expect(compare(2d, 1), to.be(1))
      ),

      it("of_decimal_gt_double", () ->
        expect(compare(2d, 1.0), to.be(1))
      ),

      it("of_NaN_double", () ->
        expect(compare(NaN, 1.0), to.be(-1))
      ),

      it("of_NaN_long", () ->
        expect(compare(NaN, 1), to.be(-1))
      ),

      it("of_NaN_decimal", () ->
        expect(compare(NaN, 1d), to.be(-1))
      ),

      it("of_double_NaN", () ->
        expect(compare(1.0, NaN), to.be(1))
      ),

      it("of_long_NaN", () ->
        expect(compare(1, NaN), to.be(1))
      ),

      it("of_decimal_NaN", () ->
        expect(compare(1d, NaN), to.be(1))
      ),

      it("of_nil_double", () ->
        expect(compare(nil, 1.0), to.be(-1))
      ),

      it("of_nil_long", () ->
        expect(compare(nil, 1), to.be(-1))
      ),

      it("of_nil_decimal", () ->
        expect(compare(nil, 1d), to.be(-1))
      ),

      it("of_double_nil", () ->
        expect(compare(1.0, nil), to.be(1))
      ),

      it("of_long_nil", () ->
        expect(compare(1, nil), to.be(1))
      ),

      it("of_decimal_nil", () ->
        expect(compare(1d, nil), to.be(1))
      ),

      it("of_NaN_nil", () ->
        expect(compare(NaN, nil), to.be(1))
      ),

      it("of_nil_NaN", () ->
        expect(compare(nil, NaN), to.be(-1))
      ),

      it("of_neg_infinity_NaN", () ->
        expect(compare(-Infinity, NaN), to.be(1))
      ),

      it("of_infinity_NaN", () ->
        expect(compare(Infinity, NaN), to.be(1))
      ),

      it("of_NaN_neg_infinity", () ->
        expect(compare(NaN, -Infinity), to.be(-1))
      ),

      it("of_NaN_infinity", () ->
        expect(compare(NaN, Infinity), to.be(-1))
      ),

      it("of_decimal_infinity", () ->
        expect(compare(1d, Infinity), to.be(-1))
      ),

      it("of_decimal_neg_infinity", () ->
        expect(compare(1d, -Infinity), to.be(1))
      ),

      it("of_infinity_decimal", () ->
        expect(compare(Infinity, 1d), to.be(1))
      ),

      it("of_neg_infinity_decimal", () ->
        expect(compare(-Infinity, 1d), to.be(-1))
      ),

      it("of_long_infinity", () ->
        expect(compare(1, Infinity), to.be(-1))
      ),

      it("of_long_neg_infinity", () ->
        expect(compare(1, -Infinity), to.be(1))
      ),

      it("of_infinity_long", () ->
        expect(compare(Infinity, 1), to.be(1))
      ),

      it("of_neg_infinity_long", () ->
        expect(compare(-Infinity, 1), to.be(-1))
      ),

      it("non_finite_sequencing", () ->
        let {
          sorted: sort([Infinity, 2.5d, 1, 0.2, -Infinity, NaN, nil], compare);
        }
        expect(sorted[0], to.be_nil()) &&
        expect(sorted[1], to.be_NaN()) &&
        expect(sorted[2], to.be(-Infinity)) &&
        expect(sorted[3], to.be(0.2)) &&
        expect(sorted[4], to.be(1)) &&
        expect(sorted[5], to.be(2.5d)) &&
        expect(sorted[6], to.be(Infinity))
      ),

      it("of_non_numeric_a", () ->
        expect_error(
          () -> compare("2.0", 1.0),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("of_non_numeric_b", () ->
        expect_error(
          () -> compare(2.0, "1.0"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}
