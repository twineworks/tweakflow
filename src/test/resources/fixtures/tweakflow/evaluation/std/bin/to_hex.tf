import bin from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias bin.to_hex as to_hex;

library to_hex_spec {

  of_nil:
    expect(to_hex(nil), to.be_nil());

  of_empty:
    expect(to_hex(0b), to.be(""));

  of_0b00:
    expect(to_hex(0b00), to.be("00"));

  of_0b0A:
    expect(to_hex(0b0A), to.be("0a"));

  of_0b000102030405060708090a0b0c0d0e0f:
    expect(to_hex(0b000102030405060708090a0b0c0d0e0f), to.be("000102030405060708090a0b0c0d0e0f"));

}