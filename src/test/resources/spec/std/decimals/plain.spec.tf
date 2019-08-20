import decimals as d from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias d.plain as plain;

library spec {
  spec:
    describe("decimals.plain", [

      it("of_default", () ->
        expect(plain(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(plain(nil), to.be_nil())
      ),

      it("of 1.00d -> '1.00'", () ->
        expect(plain(1.00d), to.be("1.00"))
      ),

      it("of 1d -> '1'", () ->
        expect(plain(1d), to.be("1"))
      ),

      it("of 1e+6d -> '100000'", () ->
        expect(plain(1e+6d), to.be("1000000"))
      ),

      it("of 1e-6d -> '0.000001'", () ->
        expect(plain(1e-6d), to.be("0.000001"))
      ),

    ]);
}