import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.atan as atan;

library spec {
  spec:
    describe("math.atan", [

      it("of_default", () ->
        expect(atan(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(atan(nil), to.be_nil())
      ),

      it("of_zero", () ->
        expect(atan(0.0), to.be(0.0))
      ),

      it("of_NaN", () ->
        expect(atan(NaN), to.be_NaN())
      ),

      it("of_Infinity", () ->
        expect(atan(Infinity), to.be_close_to(m.pi/2))
      ),

      it("of_neg_Infinity", () ->
        expect(atan(-Infinity), to.be_close_to(-m.pi/2))
      ),

      it("of_mid_pos", () ->
        expect(atan(1.5574077246549023), to.be_close_to(1.0))
      ),

      it("of_mid_neg", () ->
        expect(atan(-1.5574077246549023), to.be_close_to(-1.0))
      ),

    ]);
}