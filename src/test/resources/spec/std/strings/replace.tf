import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.replace as replace;

library spec {
  spec:
    describe("replace", [


  of_default:
    expect(replace(), to.be_nil());

  simple:
    expect(replace("hello", "h", "b"), to.be("bello"));

  left_to_right:
    expect(replace('ooo', 'oo', 'g'), to.be("go"));

  empty:
    expect(replace('grab', 'gr', ''), to.be("ab"));

  empty_search:
    expect(replace('grab', '', '|'), to.be("|g|r|a|b|"));

  of_code_points:
    expect(replace("foo 𝄞 你好 𝄞", "𝄞", "𝒜"), to.be("foo 𝒜 你好 𝒜"));

  not_found:
    expect(replace('hello world', 'not found', 'something'), to.be("hello world"));
  ]);
}