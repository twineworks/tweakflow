import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.from_hex as from_hex;

library spec {
  spec:
    describe("from_hex", [

      it("of_nil", () ->
        expect(from_hex(nil), to.be_nil())
      ),

      it("of_empty", () ->
        expect(from_hex(""), to.be(0b))
      ),

      it("of_00", () ->
        expect(from_hex("00"), to.be(0b00))
      ),

      it("of_0a", () ->
        expect(from_hex("0a"), to.be(0b0A))
      ),

      it("of_0A", () ->
        expect(from_hex("0A"), to.be(0b0A))
      ),

      it("of_000102030405060708090a0B0c0D0e0F", () ->
        expect(from_hex("000102030405060708090a0B0c0D0e0F"), to.be(0b000102030405060708090a0b0c0d0e0f))
      ),

      it("of_invalid_length", () ->
        expect_error(
          () -> from_hex("010"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}