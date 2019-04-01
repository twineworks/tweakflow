import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.lower_case as lower_case;

library lower_case_spec {

  of_default:
    expect(lower_case(), to.be_nil());

  of_nil:
    expect(lower_case(nil), to.be_nil());

  of_nil_lang:
    expect(lower_case("foo", nil), to.be_nil());

  empty:
    expect(lower_case(""), to.be(""));

  simple:
    expect(lower_case("FOO"), to.be("foo"));

  localized:
    expect(lower_case("TITLE", "tr"), to.be("tıtle")); # dot-less i in turkish language

}