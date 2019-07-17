import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.acos as acos;

library spec {
  spec:
    describe("math.acos", [

      it("of_default", () ->
        expect(acos(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(acos(nil), to.be_nil())
      ),

      it("of_zero", () ->
        expect(acos(0.0), to.be_close_to(m.pi/2))
      ),

      it("of_NaN", () ->
        expect(acos(NaN), to.be_NaN())
      ),

      it("of_Infinity", () ->
        expect(acos(Infinity), to.be_NaN())
      ),

      it("of_neg_Infinity", () ->
        expect(acos(-Infinity), to.be_NaN())
      ),

      it("of_one", () ->
        expect(acos(1.0), to.be_close_to(0))
      ),

      it("of_neg_one", () ->
        expect(acos(-1.0), to.be_close_to(m.pi))
      ),

    ]);
}