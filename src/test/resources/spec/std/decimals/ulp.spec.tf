import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.ulp as ulp;

library spec {
  spec:
    describe("decimals.ulp", [

      it("of_default", () ->
        expect(ulp(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(ulp(nil), to.be_nil())
      ),

      it("of 0d -> 1d", () ->
        expect(ulp(0d), to.be(1d))
      ),

      it("of 100d -> 1d", () ->
        expect(ulp(100d), to.be(1d))
      ),

      it("of -100d -> 1d", () ->
        expect(ulp(-100d), to.be(1d))
      ),

      it("of 100.0d -> 0.1d", () ->
        expect(ulp(100.0d), to.be(0.1d))
      ),

      it("of -100.0d -> 0.1d", () ->
        expect(ulp(-100.0d), to.be(0.1d))
      ),

      it("of 100.9d -> 0.1d", () ->
        expect(ulp(100.9d), to.be(0.1d))
      ),

      it("of -100.9d -> 0.1d", () ->
        expect(ulp(-100.9d), to.be(0.1d))
      ),

      it("of 100.00d -> 0.01d", () ->
        expect(ulp(100.00d), to.be(0.01d))
      ),

      it("of -100.00d -> 0.01d", () ->
        expect(ulp(-100.00d), to.be(0.01d))
      ),

      it("of 100.99d -> 0.01d", () ->
        expect(ulp(100.99d), to.be(0.01d))
      ),

      it("of -100.99d -> 0.01d", () ->
        expect(ulp(-100.99d), to.be(0.01d))
      ),

      it("of 9.99d -> 0.01d", () ->
        expect(ulp(9.99d), to.be(0.01d))
      ),

      it("of -9.99d -> 0.01d", () ->
        expect(ulp(-9.99d), to.be(0.01d))
      ),

    ]);
}