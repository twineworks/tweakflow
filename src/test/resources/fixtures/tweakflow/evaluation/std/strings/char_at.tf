import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.char_at as char_at;

library char_at_spec {

  of_default:
    expect(char_at(), to.be_nil());

  of_nil:
    expect(char_at(nil, nil), to.be_nil());

  of_x_nil:
    expect(char_at(nil, 0), to.be_nil());

  of_i_nil:
    expect(char_at("foo", nil), to.be_nil());

  simple:
    expect(char_at("foo", -1), to.be_nil()) &&
    expect(char_at("foo", 0), to.be("f")) &&
    expect(char_at("foo", 1), to.be("o")) &&
    expect(char_at("foo", 2), to.be("o")) &&
    expect(char_at("foo", 3), to.be_nil());

  code_points:
    expect(char_at("你好", -1), to.be_nil()) &&
    expect(char_at("你好", 0), to.be("你")) &&
    expect(char_at("你好", 1), to.be("好")) &&
    expect(char_at("你好", 2), to.be_nil());

  code_points_beyond_bmp:
    let {
      d: "foo 𝄞 你好 𝄞";
    }
    expect(char_at(d, -1), to.be_nil()) &&
    expect(char_at(d, 0), to.be("f")) &&
    expect(char_at(d, 1), to.be("o")) &&
    expect(char_at(d, 2), to.be("o")) &&
    expect(char_at(d, 3), to.be(" ")) &&
    expect(char_at(d, 4), to.be("𝄞")) &&
    expect(char_at(d, 5), to.be(" ")) &&
    expect(char_at(d, 6), to.be("你")) &&
    expect(char_at(d, 7), to.be("好")) &&
    expect(char_at(d, 8), to.be(" ")) &&
    expect(char_at(d, 9), to.be("𝄞")) &&
    expect(char_at(d, 10), to.be_nil());

}