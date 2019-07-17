import bin from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias bin.word_at as word_at;

library spec {
  spec:
    describe("bin.word_at", [

    it("of_nil_nil", () ->
      expect(word_at(nil, nil), to.be_nil())
    ),

    it("of_empty_0", () ->
      expect(word_at(0b, 0), to.be_nil())
    ),

    it("of_empty_10", () ->
      expect(word_at(0b, 10), to.be_nil())
    ),

    it("of_empty_neg_1", () ->
      expect(word_at(0b, -1), to.be_nil())
    ),

    it("of_0b00_0", () ->
      expect(word_at(0b00, 0), to.be_nil())
    ),

    it("of_0bFF_0", () ->
      expect(word_at(0bFF, 0), to.be_nil())
    ),

    it("of_0b0000_0", () ->
      expect(word_at(0b0000, 0), to.be(0))
    ),

    it("of_0b0000_0_big_endian", () ->
      expect(word_at(0b0000, 0, true), to.be(0))
    ),

    it("of_0bFF00_0", () ->
      expect(word_at(0bFF00, 0), to.be(255))
    ),

    it("of_0bFF00_0_big_endian", () ->
      expect(word_at(0bFF00, 0, true), to.be(0xFF00))
    ),

    it("of_0bFFFF_0", () ->
      expect(word_at(0bFFFF, 0), to.be(65535))
    ),

    it("of_0bFFFF_0_big_endian", () ->
      expect(word_at(0bFFFF, 0), to.be(65535))
    ),

    it("of_0b000102030405060708090a0b0c0d0e0f_0", () ->
      expect(word_at(0b000102030405060708090a0b0c0d0e0f, 0), to.be(0x0100))
    ),

    it("of_0b000102030405060708090a0b0c0d0e0f_0_big_endian", () ->
      expect(word_at(0b000102030405060708090a0b0c0d0e0f, 0, true), to.be(0x0001))
    ),

    it("of_0b000102030405060708090a0b0c0d0e0f_8", () ->
      expect(word_at(0b000102030405060708090a0b0c0d0e0f, 8), to.be(0x0908))
    ),

    it("of_0b000102030405060708090a0b0c0d0e0f_8_big_endian", () ->
      expect(word_at(0b000102030405060708090a0b0c0d0e0f, 8, true), to.be(0x0809))
    ),

  ]);
}