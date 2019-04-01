import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.substring as substring;

library substring_spec {

  of_default:
    substring() === nil;

  of_empty:
    expect(substring(""), to.be(""));

  simple:
    expect(substring("hello world", 6), to.be("world"));

  all:
    expect(substring("hello world", 0), to.be("hello world"));

  beyond:
    expect(substring("hello world", 0, 99), to.be("hello world"));

  recess:
    expect(substring("hello world", 0, -1), to.be(""));

  excess:
    expect(substring("hello world", 10, 1), to.be(""));

  zero_len:
    expect(substring("hello world", 0, 0), to.be(""));

  zero_len_offset:
    expect(substring("hello world", 2, 2), to.be(""));

  zero_len_beyond:
    expect(substring("hello world", 99, 99), to.be(""));

  respects_code_point_1:
    expect(substring("你好", 0, 1), to.be("你"));

  respects_code_point_2:
    expect(substring("你好", 1, 2), to.be("好"));

  respects_code_point_beyond_bmp:
    expect(substring("你好 𝄞 你好", 1, 6), to.be("好 𝄞 你"));

  with_start_sub_zero:
    expect_error(
      () -> substring("foo", -1),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

  with_start_nil:
    expect_error(
      () -> substring("foo", nil),
      to.have_code("NIL_ERROR")
    );

}