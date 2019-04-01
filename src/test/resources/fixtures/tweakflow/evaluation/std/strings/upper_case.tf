import strings as s from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias s.upper_case as upper_case;

library upper_case_spec {

  of_default:
    expect(upper_case(), to.be_nil());

  of_nil:
    expect(upper_case(nil), to.be_nil());

  of_nil_lang:
    expect(upper_case("foo", nil), to.be_nil());

  empty:
    expect(upper_case(""), to.be(""));

  simple:
    expect(upper_case("foo"), to.be("FOO"));

  localized:
    # dot-enriched i in turkish language
    expect(upper_case("title", "tr"), to.be("TİTLE")) &&
    # upper case transformation of ß to SS in german
    expect(upper_case("straße", "de"), to.be("STRASSE"));

}