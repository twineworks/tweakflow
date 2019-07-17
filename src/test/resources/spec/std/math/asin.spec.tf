import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.asin as asin;

library spec {
  spec:
    describe("math.asin", [

      it("of_default", () ->
        expect(asin(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(asin(nil), to.be_nil())
      ),

      it("of_zero", () ->
        expect(asin(0.0), to.be(0.0))
      ),

      it("of_NaN", () ->
        expect(asin(NaN), to.be_NaN())
      ),

      it("of_Infinity", () ->
        expect(asin(Infinity), to.be_NaN())
      ),

      it("of_neg_Infinity", () ->
        expect(asin(-Infinity), to.be_NaN())
      ),

      it("of_one", () ->
        expect(asin(1.0), to.be_close_to(m.pi/2))
      ),

      it("of_neg_one", () ->
        expect(asin(-1.0), to.be_close_to(-m.pi/2))
      ),

    ]);
}