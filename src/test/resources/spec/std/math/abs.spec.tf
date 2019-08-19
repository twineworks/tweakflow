import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.abs as abs;

library spec {
  spec:
    describe("math.abs", [

      it("of_default", () ->
        expect(abs(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(abs(nil), to.be_nil())
      ),

      it("pos_long", () ->
        expect(abs(12345), to.be(12345))
      ),

      it("pos_decimal", () ->
        expect(abs(12345.56d), to.be(12345.56d))
      ),

      it("of_NaN", () ->
        expect(abs(NaN), to.be_NaN())
      ),

      it("of_pos_infinity", () ->
        expect(abs(Infinity), to.be(Infinity))
      ),

      it("of_neg_infinity", () ->
        expect(abs(-Infinity), to.be(Infinity))
      ),

      it("pos_double", () ->
        expect(abs(12345.6789), to.be(12345.6789))
      ),

      it("neg_long", () ->
        expect(abs(-12345), to.be(12345))
      ),

      it("neg_double", () ->
        expect(abs(-12345.6789), to.be(12345.6789))
      ),

      it("neg_decimal", () ->
        expect(abs(-12345.6789d), to.be(12345.6789d))
      ),

      it("of_min_long", () ->
        expect_error(
          () -> abs(m.min_long),
          to.have_code("NUMBER_OUT_OF_BOUNDS")
        )
      ),

      it("of_non_numeric", () ->
        expect_error(
          () -> abs("foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

    ]);
}