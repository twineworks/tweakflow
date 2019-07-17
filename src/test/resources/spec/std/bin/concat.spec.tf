import bin from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.concat as concat;

library spec {

  spec:
    describe("bin.concat", [
      it("of_nil", () ->
        expect(concat(nil), to.be_nil())
      ),

      it("of_nil_nil", () ->
        expect(concat([nil, nil]), to.be_nil())
      ),

      it("of_none", () ->
        expect(concat([]), to.be(0b))
      ),

      it("of_empty_nil", () ->
        expect(concat([0b, nil]), to.be_nil())
      ),

      it("of_nil_empty", () ->
        expect(concat([nil, 0b]), to.be_nil())
      ),

      it("of_empty_empty", () ->
        expect(concat([0b, 0b]), to.be(0b))
      ),

      it("of_0b00_0b01", () ->
        expect(concat([0b00, 0b01]), to.be(0b0001))
      ),

      it("of_0b00_0b", () ->
        expect(concat([0b00, 0b]), to.be(0b00))
      ),

      it("of_0b_0b00", () ->
        expect(concat([0b, 0b00]), to.be(0b00))
      ),

      it("of_0b0001020304_0b0506070809", () ->
        expect(concat([0b0001020304, 0b0506070809]), to.be(0b00010203040506070809))
      ),

      it("of_0b0001020304_0b0506070809_0b0a0b0c0d0e0f", () ->
        expect(concat([0b0001020304, 0b0506070809, 0b0a0b0c0d0e0f]), to.be(0b000102030405060708090a0b0c0d0e0f))
      ),

      it("invalid_element_type", () ->
        expect_error(
          () -> concat(["foo"]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

    ]);

}