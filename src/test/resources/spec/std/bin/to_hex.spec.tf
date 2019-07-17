import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.to_hex as to_hex;

library spec {

  spec:
    describe("bin.to_hex", [

      it("of_nil", () ->
        expect(to_hex(nil), to.be_nil())
      ),

      it("of_empty", () ->
        expect(to_hex(0b), to.be(""))
      ),

      it("of_0b00", () ->
        expect(to_hex(0b00), to.be("00"))
      ),

      it("of_0b0A", () ->
        expect(to_hex(0b0A), to.be("0a"))
      ),

      it("of_0b000102030405060708090a0b0c0d0e0f", () ->
        expect(to_hex(0b000102030405060708090a0b0c0d0e0f), to.be("000102030405060708090a0b0c0d0e0f"))
      ),
    ]);
}