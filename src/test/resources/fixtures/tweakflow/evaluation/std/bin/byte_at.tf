import bin from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias bin.byte_at as byte_at;

library byte_at_spec {

  of_nil_nil:
    expect(byte_at(nil, nil), to.be_nil());

  of_empty_0:
    expect(byte_at(0b, 0), to.be_nil());

  of_empty_10:
    expect(byte_at(0b, 10), to.be_nil());

  of_empty_neg_1:
    expect(byte_at(0b, -1), to.be_nil());

  of_0b00_0:
    expect(byte_at(0b00, 0), to.be(0));

  of_0bFF_0:
    expect(byte_at(0bFF, 0), to.be(255));

  of_0bFF_1:
    expect(byte_at(0bFF, 1), to.be_nil());

  of_0b000102030405060708090a0b0c0d0e0f_0:
    expect(byte_at(0b000102030405060708090a0b0c0d0e0f, 0), to.be(0));

  of_0b000102030405060708090a0b0c0d0e0f_1:
    expect(byte_at(0b000102030405060708090a0b0c0d0e0f, 1), to.be(1));

  of_0b000102030405060708090a0b0c0d0e0f_15:
    expect(byte_at(0b000102030405060708090a0b0c0d0e0f, 15), to.be(15));

}