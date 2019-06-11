import bin, math from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias bin.double_at as double_at;

library double_at_spec {

  of_nil_nil:
    expect(double_at(nil, nil), to.be_nil());

  of_empty_0:
    expect(double_at(0b, 0), to.be_nil());

  of_empty_10:
    expect(double_at(0b, 10), to.be_nil());

  of_empty_neg_1:
    expect(double_at(0b, -1), to.be_nil());

  of_0b00_0:
    expect(double_at(0b00, 0), to.be_nil());

  of_0bFF_0:
    expect(double_at(0bFF, 0), to.be_nil());

  of_0b0000000000000000_0:
    expect(double_at(0b0000000000000000, 0), to.be(0.0));

  of_0b0000000000000000_0_big_endian:
    expect(double_at(0b0000000000000000, 0, true), to.be(0.0));

  of_0b000000000000f03f_0:
    expect(double_at(0b000000000000f03f, 0), to.be(1.0));

  of_0bc000000000000000_0_big_endian:
    expect(double_at(0bc000000000000000, 0, true), to.be(-2.0));

  of_0b000000000000f07f_0:
    expect(double_at(0b000000000000f07f, 0), to.be(Infinity));

  of_0b000000000000f0ff_0:
    expect(double_at(0b000000000000f0ff, 0), to.be(-Infinity));

  of_0b010000000000f07f_0:
    expect(double_at(0b010000000000f07f, 0), to.be_NaN());

  of_0b010000000000f87f_0:
    expect(double_at(0b010000000000f87f, 0), to.be_NaN());

  of_0b400921fb54442d18_0_big_endian:
    expect(double_at(0b400921fb54442d18, 0, true), to.be_close_to(math.pi));

}