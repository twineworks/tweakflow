import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.strip_trailing_zeros as f;

library spec {
  spec:
    describe("decimals.strip_trailing_zeros", [

      it("of_default", () ->
        expect(f(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(f(nil), to.be_nil())
      ),

      it("of 0.0000d -> 0d", () ->
        expect(f(0.0000d) as string, to.be(0d as string))
      ),

      it("of 1.00d -> 1d", () ->
        expect(f(1.00d) as string, to.be(1d as string))
      ),

      it("of 1.10d -> 1.1d", () ->
        expect(f(1.10d) as string, to.be(1.1d as string))
      ),

      it("of 100d -> 1E+2d", () ->
        expect(f(100d) as string, to.be(1E+2d as string))
      ),

      it("of 110d -> 1.1E+2d", () ->
        expect(f(110d) as string, to.be(1.1E+2d as string))
      ),

    ]);
}