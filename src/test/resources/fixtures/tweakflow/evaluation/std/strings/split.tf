import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.split as split;

library split_spec {

  of_default:
    expect(split(), to.be_nil());

  of_nil:
    expect(split(nil), to.be_nil());

  of_nil_splitter:
    expect(split("foo bar", nil), to.be(nil));

  simple:
    expect(split("foo bar"), to.be(["foo", "bar"]));

  splitter:
    expect(split("foo,bar", ","), to.be(["foo", "bar"]));

  code_point_splitter:
    expect(split("好你好你好", "你"), to.be(["好", "好", "好"]));

  code_point_beyond_bmp_splitter:
    expect(split("好你𝄞好你𝄞好你", "𝄞"), to.be(["好你", "好你", "好你"]));

  multi_char_splitter:
    expect(split("foo, bar", ", "), to.be(["foo", "bar"]));

  non_regex_splitter:
    expect(split("foo.bar", "."), to.be(["foo", "bar"]));

  leading_splitter:
    expect(split(",foo,bar", ","), to.be(["", "foo", "bar"]));

  trailing_splitter:
    expect(split("foo,bar,", ","), to.be(["foo", "bar", ""]));

  consecutive_splitter:
    expect(split(",,foo,,,,bar,,", ","), to.be(["", "", "foo", "", "", "", "bar", "", ""]));

  of_single:
    expect(split("foo"), to.be(["foo"]));

  of_empty:
    expect(split(""), to.be([""]));

}