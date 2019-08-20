import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.with_scale as with_scale;
alias d.scale as scale;

library spec {
  spec:
    describe("decimals.with_scale", [

      it("of_default", () ->
        expect(with_scale(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(with_scale(nil), to.be_nil())
      ),

      it("scales 0d to 0.00000d", () ->
        expect(with_scale(0d, 5) as string, to.be(0.00000d as string))
      ),

      it("scales 1d to 1.00000d", () ->
        expect(with_scale(1d, 5) as string, to.be(1.00000d as string))
      ),

      it("scales 1.000d to 1d rounding unnecessary", () ->
        expect(with_scale(1.000d, 0, 'unnecessary') as string, to.be(1d as string))
      ),

      it("throws scaling 1.1d to 1d rounding unnecessary", () ->
        expect_error(
          () -> with_scale(1.1d, 0, 'unnecessary'),
          to.have_code("ROUNDING_NECESSARY")
        )
      ),

      it("scales 1000d to 1e+3d rounding unnecessary", () ->
        expect(with_scale(1000d, -3, 'unnecessary') as string, to.be(1e+3d as string))
      ),

      it("throws scaling 1001d to 1e+3d rounding unnecessary", () ->
        expect_error(
          () -> with_scale(1001d, -3, 'unnecessary'),
          to.have_code("ROUNDING_NECESSARY")
        )
      ),

      it("throws scaling with unknown rounding mode", () ->
        expect_error(
          () -> with_scale(1.1234d, 1, 'foo'),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      describe("scaling 0.125d to 2 decimal places", [

        it("scales 0.125d to 0.12d rounding down", () ->
          expect(with_scale(0.125d, 2, 'down') as string, to.be(0.12d as string))
        ),

        it("scales 0.125d to 0.12d rounding half_down", () ->
          expect(with_scale(0.125d, 2, 'half_down') as string, to.be(0.12d as string))
        ),

        it("scales 0.125d to 0.13d rounding ceiling", () ->
          expect(with_scale(0.125d, 2, 'ceiling') as string, to.be(0.13d as string))
        ),

        it("scales 0.125d to 0.12d rounding floor", () ->
          expect(with_scale(0.125d, 2, 'floor') as string, to.be(0.12d as string))
        ),

        it("scales 0.125d to 0.13d rounding up", () ->
          expect(with_scale(0.125d, 2, 'up') as string, to.be(0.13d as string))
        ),

        it("scales 0.125d to 0.13d rounding half_up", () ->
          expect(with_scale(0.125d, 2, 'half_up') as string, to.be(0.13d as string))
        ),

        it("scales 0.125d to 0.12d rounding half_even", () ->
          expect(with_scale(0.125d, 2, 'half_even') as string, to.be(0.12d as string))
        ),

      ]),

      describe("scaling -0.125d to 2 decimal places", [

        it("scales -0.125d to -0.12d rounding down", () ->
          expect(with_scale(-0.125d, 2, 'down') as string, to.be(-0.12d as string))
        ),

        it("scales -0.125d to -0.12d rounding half_down", () ->
          expect(with_scale(-0.125d, 2, 'half_down') as string, to.be(-0.12d as string))
        ),

        it("scales -0.125d to -0.12d rounding ceiling", () ->
          expect(with_scale(-0.125d, 2, 'ceiling') as string, to.be(-0.12d as string))
        ),

        it("scales -0.125d to -0.13d rounding floor", () ->
          expect(with_scale(-0.125d, 2, 'floor') as string, to.be(-0.13d as string))
        ),

        it("scales -0.125d to -0.13d rounding up", () ->
          expect(with_scale(-0.125d, 2, 'up') as string, to.be(-0.13d as string))
        ),

        it("scales -0.125d to -0.13d rounding half_up", () ->
          expect(with_scale(-0.125d, 2, 'half_up') as string, to.be(-0.13d as string))
        ),

        it("scales -0.125d to -0.12d rounding half_even", () ->
          expect(with_scale(-0.125d, 2, 'half_even') as string, to.be(-0.12d as string))
        ),

      ]),

      describe("scaling 150d to -2 decimal places (1.5x100)", [

        it("scales 150d to 1e+2d rounding down", () ->
          expect(with_scale(150d, -2, 'down') as string, to.be(1e+2d as string))
        ),

        it("scales 150d to 1e+2d rounding half_down", () ->
          expect(with_scale(150d, -2, 'half_down') as string, to.be(1e+2d as string))
        ),

        it("scales 150d to 2e+2d rounding ceiling", () ->
          expect(with_scale(150d, -2, 'ceiling') as string, to.be(2e+2d as string))
        ),

        it("scales 150d to 1e+2d rounding floor", () ->
          expect(with_scale(150d, -2, 'floor') as string, to.be(1e+2d as string))
        ),

        it("scales 150d to 2e+2d rounding up", () ->
          expect(with_scale(150d, -2, 'up') as string, to.be(2e+2d as string))
        ),

        it("scales 150d to 2e+2d rounding half_up", () ->
          expect(with_scale(150d, -2, 'half_up') as string, to.be(2e+2d as string))
        ),

        it("scales 150d to 2e+2d rounding half_even", () ->
          expect(with_scale(150d, -2, 'half_even') as string, to.be(2e+2d as string))
        ),

      ]),

      describe("scaling -150d to -2 decimal places (-1.5x100)", [

        it("scales -150d to -1e+2d rounding down", () ->
          expect(with_scale(-150d, -2, 'down') as string, to.be(-1e+2d as string))
        ),

        it("scales -150d to -1e+2d rounding half_down", () ->
          expect(with_scale(-150d, -2, 'half_down') as string, to.be(-1e+2d as string))
        ),

        it("scales -150d to -1e+2d rounding ceiling", () ->
          expect(with_scale(-150d, -2, 'ceiling') as string, to.be(-1e+2d as string))
        ),

        it("scales -150d to -2e+2d rounding floor", () ->
          expect(with_scale(-150d, -2, 'floor') as string, to.be(-2e+2d as string))
        ),

        it("scales -150d to -2e+2d rounding up", () ->
          expect(with_scale(-150d, -2, 'up') as string, to.be(-2e+2d as string))
        ),

        it("scales -150d to -2e+2d rounding half_up", () ->
          expect(with_scale(-150d, -2, 'half_up') as string, to.be(-2e+2d as string))
        ),

        it("scales -150d to -2e+2d rounding half_even", () ->
          expect(with_scale(-150d, -2, 'half_even') as string, to.be(-2e+2d as string))
        ),

      ])

    ]);
}