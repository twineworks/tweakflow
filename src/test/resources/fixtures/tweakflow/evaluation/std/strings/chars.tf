import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.chars as chars;

library chars_spec {

  of_default:
    expect(chars(), to.be_nil());

  of_nil:
    expect(chars(nil), to.be_nil());

  simple:
    expect(chars("foo"), to.be(["f", "o", "o"]));

  of_empty:
    expect(chars(""), to.be([]));

  of_code_points:
    expect(chars("你好"), to.be(["你", "好"]));

  of_code_points_beyond_bmp:
    expect(chars("𝄞你好𝄞"), to.be(["𝄞", "你", "好", "𝄞"]));

}