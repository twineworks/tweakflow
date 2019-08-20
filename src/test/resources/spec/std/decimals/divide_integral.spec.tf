import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.divide_integral as f;

library spec {
  spec:
    describe("decimals.divide_integral", [

      it("of_default", () ->
        expect(f(), to.be_nil())
      ),

      it("of x nil", () ->
        expect(f(nil, 1d), to.be_nil())
      ),

      it("of y nil", () ->
        expect(f(1d, nil), to.be_nil())
      ),

      it("of 5d / 1d -> 5d", () ->
        expect(f(5d, 1d), to.be(5d))
      ),

      it("of 5d / -1d -> -5d", () ->
        expect(f(5d, -1d), to.be(-5d))
      ),

      it("of -5d / -1d -> 5d", () ->
        expect(f(-5d, -1d), to.be(5d))
      ),

      it("of 5d / 2d -> 2d", () ->
        expect(f(5d, 2d), to.be(2d))
      ),

      it("of 5d / 0.2d -> 25d", () ->
        expect(f(5d, 0.2d), to.be(25d))
      ),

      it("of 10.55d / 0.5d -> 21d", () ->
        expect(f(10.5d, 0.5d), to.be(21d))
      ),

      it("throws DIVISION_BY_ZERO on 1d / 0d", () ->
        expect_error(
          () -> f(1d, 0d),
          to.have_code("DIVISION_BY_ZERO")
        )
      ),

    ]);
}