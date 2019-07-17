import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.size as size;

library spec {

  spec:
    describe("bin.size", [

      it("of_nil", () ->
        expect(size(nil), to.be_nil())
      ),

      it("of_empty", () ->
        expect(size(0b), to.be(0))
      ),

      it("of_0b00", () ->
        expect(size(0b00), to.be(1))
      ),

      it("of_0b000102030405060708090a0b0c0d0e0f", () ->
        expect(size(0b000102030405060708090a0b0c0d0e0f), to.be(16))
      ),

      it("invalid_type", () ->
        expect_error(
          () -> size("foo"),
          to.have_code("CAST_ERROR")
        )
      ),
    ]);
}