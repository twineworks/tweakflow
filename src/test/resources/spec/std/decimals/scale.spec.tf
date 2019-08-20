import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.scale as scale;

library spec {
  spec:
    describe("decimals.scale", [

      it("of_default", () ->
        expect(scale(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(scale(nil), to.be_nil())
      ),

      it("of 0d -> 0", () ->
        expect(scale(0d), to.be(0))
      ),

      it("of 0.000d -> 3", () ->
        expect(scale(0.000d), to.be(3))
      ),

      it("of 1d -> 0", () ->
        expect(scale(1d), to.be(0))
      ),

      it("of 0.1d -> 1", () ->
        expect(scale(0.1d), to.be(1))
      ),

      it("of 0.01d -> 2", () ->
        expect(scale(0.01d), to.be(2))
      ),

      it("of 1e+0d -> 0", () ->
        expect(scale(1e+0d), to.be(0))
      ),

      it("of 1e+1d -> -1", () ->
        expect(scale(1e+1d), to.be(-1))
      ),

      it("of 1e+3d -> -3", () ->
        expect(scale(1e+3d), to.be(-3))
      ),

      it("of 1.0e+3d -> -2", () ->
        expect(scale(1.0e+3d), to.be(-2))
      ),

      it("of 1e-10d -> 10", () ->
        expect(scale(1e-10d), to.be(10))
      ),

      it("of 1e-1d -> 1", () ->
        expect(scale(1e-1d), to.be(1))
      ),

      it("of 1.1e-1d -> 2", () ->
        expect(scale(1.1e-1d), to.be(2))
      ),

      it("of 1.0e-10d -> 11", () ->
        expect(scale(1.0e-10d), to.be(11))
      ),


    ]);
}