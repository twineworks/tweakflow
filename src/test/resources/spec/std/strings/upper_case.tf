import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.upper_case as upper_case;

library spec {
  spec:
    describe("upper_case", [


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

  ]);
}