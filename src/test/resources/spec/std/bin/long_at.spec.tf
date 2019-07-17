import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.long_at as long_at;

library spec {

  spec:
    describe("bin.long_at", [

      it("of_nil_nil", () ->
        expect(long_at(nil, nil), to.be_nil())
      ),

      it("of_empty_0", () ->
        expect(long_at(0b, 0), to.be_nil())
      ),

      it("of_empty_10", () ->
        expect(long_at(0b, 10), to.be_nil())
      ),

      it("of_empty_neg_1", () ->
        expect(long_at(0b, -1), to.be_nil())
      ),

      it("of_0b00_0", () ->
        expect(long_at(0b00, 0), to.be_nil())
      ),

      it("of_0bFF_0", () ->
        expect(long_at(0bFF, 0), to.be_nil())
      ),

      it("of_0b0000000000000000_0", () ->
        expect(long_at(0b0000000000000000, 0), to.be(0))
      ),

      it("of_0b0000000000000000_0_big_endian", () ->
        expect(long_at(0b0000000000000000, 0, true), to.be(0))
      ),

      it("of_0bFF00000000000000_0", () ->
        expect(long_at(0bFF00000000000000, 0), to.be(255))
      ),

      it("of_0bFF00000000000000_0_big_endian", () ->
        expect(long_at(0bFF00000000000000, 0, true), to.be(0xFF00000000000000))
      ),

      it("of_0bFFFFFFFFFFFFFFFF_0", () ->
        expect(long_at(0bFFFFFFFFFFFFFFFF, 0), to.be(-1))
      ),

      it("of_0bFFFFFFFFFFFFFFFF_0_big_endian", () ->
        expect(long_at(0bFFFFFFFFFFFFFFFF, 0, true), to.be(-1))
      ),

      it("of_0b000102030405060708090a0b0c0d0e0f_0", () ->
        expect(long_at(0b000102030405060708090a0b0c0d0e0f, 0), to.be(0x0706050403020100))
      ),

      it("of_0b000102030405060708090a0b0c0d0e0f_0_big_endian", () ->
        expect(long_at(0b000102030405060708090a0b0c0d0e0f, 0, true), to.be(0x0001020304050607))
      ),

      it("of_0b000102030405060708090a0b0c0d0e0f_8", () ->
        expect(long_at(0b000102030405060708090a0b0c0d0e0f, 8), to.be(0x0f0e0d0c0b0a0908))
      ),

      it("of_0b000102030405060708090a0b0c0d0e0f_8_big_endian", () ->
        expect(long_at(0b000102030405060708090a0b0c0d0e0f, 8, true), to.be(0x08090a0b0c0d0e0f))
      ),
    ]);
}