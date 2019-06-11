import bin from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias bin.from_hex as from_hex;

library from_hex_spec {

  of_nil:
    expect(from_hex(nil), to.be_nil());

  of_empty:
    expect(from_hex(""), to.be(0b));

  of_00:
    expect(from_hex("00"), to.be(0b00));

  of_0a:
    expect(from_hex("0a"), to.be(0b0A));

  of_0A:
    expect(from_hex("0A"), to.be(0b0A));

  of_000102030405060708090a0B0c0D0e0F:
    expect(from_hex("000102030405060708090a0B0c0D0e0F"), to.be(0b000102030405060708090a0b0c0d0e0f));

  of_invalid_length:
    expect_error(
      () -> from_hex("010"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}