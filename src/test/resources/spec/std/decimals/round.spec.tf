import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.round as round;

library spec {
  spec:
    describe("decimals.round", [

      it("of_default", () ->
        expect(round(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(round(nil), to.be_nil())
      ),

      it("rounds 0.0001234d to 2 significant digits -> 0.00012d", () ->
        expect(round(0.0001234d, 2), to.be(0.00012d))
      ),

      it("rounds 0.0001234d to 2 significant digits rounding up -> 0.00013d", () ->
        expect(round(0.0001234d, 2, 'up'), to.be(0.00013d))
      ),

      it("rounds 0.01234d to 2 significant digits -> 0.012d", () ->
        expect(round(0.01234d, 2), to.be(0.012d))
      ),

      it("rounds 0.01234d to 2 significant digits rounding up -> 0.013d", () ->
        expect(round(0.01234d, 2, 'up'), to.be(0.013d))
      ),

      it("rounds 1.01234d to 2 significant digits -> 1.0d", () ->
        expect(round(1.01234d, 2), to.be(1.0d))
      ),

      it("rounds 1.01234d to 2 significant digits rounding up -> 1.1d", () ->
        expect(round(1.01234d, 2, 'up'), to.be(1.1d))
      ),

      it("rounds 1_000_001d to 1 significant digit -> 1_000_000d", () ->
        expect(round(1_000_001d, 1), to.be(1_000_000d))
      ),

      it("rounds 1.01234d to 0 significant digits -> noop", () ->
        expect(round(1.01234d, 0), to.be(1.01234d))
      ),

      it("throws on negative digits", () ->
        expect_error(
          () -> round(1.0, -1),
          to.have_code('ILLEGAL_ARGUMENT')
        )
      ),

      it("throws on unknown rounding mode", () ->
        expect_error(
          () -> round(1.0, 0, 'foo'),
          to.have_code('ILLEGAL_ARGUMENT')
        )
      ),

    ]);
}