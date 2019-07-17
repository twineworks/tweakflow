import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.bit_count as bit_count;

library spec {
  spec:
    describe("math.bit_count", [

      it("of_default", () ->
        expect(bit_count(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(bit_count(nil), to.be_nil())
      ),

      it("of_zero", () ->
        expect(bit_count(0), to.be(0))
      ),

      it("of_1", () ->
        expect(bit_count(1), to.be(1))
      ),

      it("of_neg_1", () ->
        expect(bit_count(-1), to.be(64))
      ),

      it("of_2", () ->
        expect(bit_count(2), to.be(1))
      ),

      it("of_3", () ->
        expect(bit_count(3), to.be(2))
      ),

      it("of_4", () ->
        expect(bit_count(4), to.be(1))
      ),

      it("of_7", () ->
        expect(bit_count(7), to.be(3))
      ),

      it("of_15", () ->
        expect(bit_count(15), to.be(4))
      ),

      it("of_1024", () ->
        expect(bit_count(1024), to.be(1))
      ),

    ]);
}