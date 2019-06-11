import bin, math from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias bin.float_at as float_at;

library float_at_spec {

  of_nil_nil:
    expect(float_at(nil, nil), to.be_nil());

  of_empty_0:
    expect(float_at(0b, 0), to.be_nil());

  of_empty_10:
    expect(float_at(0b, 10), to.be_nil());

  of_empty_neg_1:
    expect(float_at(0b, -1), to.be_nil());

  of_0b00_0:
    expect(float_at(0b00, 0), to.be_nil());

  of_0bFF_0:
    expect(float_at(0bFF, 0), to.be_nil());

  of_0b00000000_0:
    expect(float_at(0b00000000, 0), to.be(0.0));

  of_0b00000000_0_big_endian:
    expect(float_at(0b00000000, 0, true), to.be(0.0));

  of_0b0000803f_0:
    expect(float_at(0b0000803f, 0), to.be(1.0));

  of_0bc0000000_0_big_endian:
    expect(float_at(0bc0000000, 0, true), to.be(-2.0));

  of_0b0000807f_0:
    expect(float_at(0b0000807f, 0), to.be(Infinity));

  of_0b000080ff_0:
    expect(float_at(0b000080ff, 0), to.be(-Infinity));

  of_0b0100C0ff_0:
    expect(float_at(0b0100C0ff, 0), to.be_NaN());

  of_0b010080ff_0:
    expect(float_at(0b010080ff, 0), to.be_NaN());

  of_0b40490fdb_0_big_endian:
    expect(float_at(0b40490fdb, 0, true), to.be_close_to(math.pi, 1e-7));

}