import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.starts_with? as starts_with?;

library starts_with_spec {

  of_default:
    expect(starts_with?(), to.be_nil());

  of_nil:
    expect(starts_with?(nil, nil), to.be_nil());

  of_init_nil:
    expect(starts_with?("foo", nil), to.be_nil());

  of_x_nil:
    expect(starts_with?(nil, ""), to.be_nil());

  of_substring:
    expect(starts_with?("foo", "o"), to.be_false());

  of_init:
    expect(starts_with?("foo", "fo"), to.be_true());

  of_same:
    expect(starts_with?("foo", "foo"), to.be_true());

  of_empty_init:
    expect(starts_with?("foo", ""), to.be_true());

  of_empty_x_empty_init:
    expect(starts_with?("", ""), to.be_true());

  of_empty_x_nonempty_init:
    expect(starts_with?("", "x"), to.be_false());

  of_bmp:
    expect(starts_with?("你好", "你"), to.be_true());

  of_beyond_bmp:
    expect(starts_with?("𝄞 music", "𝄞"), to.be_true());
}