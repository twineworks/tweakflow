import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.join as join;

library spec {
  spec:
    describe("join", [


  of_default:
    expect(join(), to.be_nil());

  of_nil:
    expect(join(nil), to.be_nil());

  simple:
    expect(join(["foo", "bar"]), to.be("foobar"));

  with_nil:
    expect(join(["foo", nil, "bar"], "-"), to.be("foo-nil-bar"));

  with_cast:
    expect(join(["foo", true, "bar"], "-"), to.be("foo-true-bar"));

  with_bad_cast:
    expect_error(
      () -> join(["foo", {}, "bar"], "-"),
      to.have_code("CAST_ERROR")
    );

  single:
    expect(join(["foo"], "-"), to.be("foo"));

  empty:
    expect(join([], "-"), to.be("")) &&
    expect(join([], ""), to.be(""));

  separator_nil:
    expect(join(["foo"], nil), to.be_nil());

  with_separator:
    expect(join(["foo", "bar"], ", "), to.be("foo, bar")) &&
    expect(join([1, 2, 3], "-"), to.be("1-2-3"));

  ]);
}