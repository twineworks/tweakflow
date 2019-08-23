import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.from_double_exact as f;

library spec {
  spec:
    describe("decimals.from_double_exact", [

      it("of_default", () ->
        expect(f(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(f(nil), to.be_nil())
      ),

      it("of 0.0 -> 0d", () ->
        expect(f(0.0), to.be(0d))
      ),

      it("of 0.1 -> 0.1000000000000000055511151231257827021181583404541015625d", () ->
        expect(f(0.1), to.be(0.1000000000000000055511151231257827021181583404541015625d))
      ),

      it("of -0.1 -> -0.1000000000000000055511151231257827021181583404541015625d", () ->
        expect(f(-0.1), to.be(-0.1000000000000000055511151231257827021181583404541015625d))
      ),

      it("of 2.5 -> 2.5d", () ->
        expect(f(2.5), to.be(2.5d))
      ),

      it("of -2.5 -> -2.5d", () ->
        expect(f(-2.5), to.be(-2.5d))
      ),

      it("throws on NaN", () ->
        expect_error(
          ()->f(NaN),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("throws on -Infinity", () ->
        expect_error(
          ()->f(-Infinity),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("throws on Infinity", () ->
        expect_error(
          ()->f(Infinity),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

    ]);
}