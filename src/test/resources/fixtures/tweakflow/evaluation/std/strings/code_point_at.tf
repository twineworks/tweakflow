import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.code_point_at as code_point_at;

library code_point_at_spec {

  of_default:
    expect(code_point_at(), to.be_nil());

  of_nil:
    expect(code_point_at(nil, nil), to.be_nil());

  of_x_nil:
    expect(code_point_at(nil, 0), to.be_nil());

  of_i_nil:
    expect(code_point_at("foo", nil), to.be_nil());

  simple:
    expect(code_point_at("foo", -1), to.be_nil()) &&
    expect(code_point_at("foo", 0), to.be(102)) &&
    expect(code_point_at("foo", 1), to.be(111)) &&
    expect(code_point_at("foo", 2), to.be(111)) &&
    expect(code_point_at("foo", 3), to.be_nil());

  code_points:
    expect(code_point_at("你好", -1), to.be_nil()) &&
    expect(code_point_at("你好", 0), to.be(0x4F60)) &&
    expect(code_point_at("你好", 1), to.be(0x597D)) &&
    expect(code_point_at("你好", 2), to.be_nil());

  code_points_beyond_bmp:
    let {
      d: "foo 𝄞 你好 𝄞";
    }
    expect(code_point_at(d, -1), to.be_nil()) &&
    expect(code_point_at(d, 0), to.be(0x66)) &&
    expect(code_point_at(d, 1), to.be(0x6F)) &&
    expect(code_point_at(d, 2), to.be(0x6F)) &&
    expect(code_point_at(d, 3), to.be(0x20)) &&
    expect(code_point_at(d, 4), to.be(0x01D11E)) && # clef
    expect(code_point_at(d, 5), to.be(0x20)) &&
    expect(code_point_at(d, 6), to.be(0x4F60)) &&
    expect(code_point_at(d, 7), to.be(0x597D)) &&
    expect(code_point_at(d, 8), to.be(0x20)) &&
    expect(code_point_at(d, 9), to.be(0x01D11E)) && # clef
    expect(code_point_at(d, 10), to.be_nil());

}