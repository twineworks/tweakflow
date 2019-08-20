import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.divide as divide;

library spec {
  spec:
    describe("decimals.divide", [

      it("of_default", () ->
        expect(divide(), to.be_nil())
      ),

      it("of x nil", () ->
        expect(divide(nil, 1d), to.be_nil())
      ),

      it("of y nil", () ->
        expect(divide(1d, nil), to.be_nil())
      ),

      it("of 5d / 1d -> 5d", () ->
        expect(divide(5d, 1d), to.be(5d))
      ),

      it("of 1d / 3d -> 0d", () ->
        expect(divide(1d, 3d), to.be(0d))
      ),

      it("of 1d / 3d (2 digits) -> 0.33d", () ->
        expect(divide(1d, 3d, 2), to.be(0.33d))
      ),

      it("of 1d / 3d (2 digits, round up) -> 0.34d", () ->
        expect(divide(1d, 3d, 2, 'up'), to.be(0.34d))
      ),

      it("of 1000.0d / 3d -> 333.3d", () ->
        expect(divide(1000.0d, 3d), to.be(333.3d))
      ),

      it("of 1000.0d / 3d (4 digits) -> 333.3333d", () ->
        expect(divide(1000.0d, 3d, 4), to.be(333.3333d))
      ),

      it("of 1000.0d / 3d (4 digits, round up) -> 333.3334d", () ->
        expect(divide(1000.0d, 3d, 4, 'up'), to.be(333.3334d))
      ),

      it("of 1000.0d / 3d (-2 digits, round up) -> 4e+2d", () ->
        expect(divide(1000.0d, 3d, -2, 'up'), to.be(4e+2d))
      ),

      it("throws DIVISION_BY_ZERO on 1d / 0d", () ->
        expect_error(
          () -> divide(1d, 0d),
          to.have_code("DIVISION_BY_ZERO")
        )
      ),

      it("throws on unknown rounding mode", () ->
        expect_error(
          () -> divide(1d, 1d, 1, 'foo'),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("throws on nil rounding mode", () ->
        expect_error(
          () -> divide(1d, 1d, 1, nil),
          to.have_code("NIL_ERROR")
        )
      ),

    ]);
}