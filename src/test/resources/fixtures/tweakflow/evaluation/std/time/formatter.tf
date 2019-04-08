import time as t, math from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.formatter as formatter;

library formatter_spec {

  of_default:
    let {
      f: formatter();
    }
    expect(f, to.be_function()) &&
    expect(f(nil), to.be_nil()) &&
    expect(f(t.epoch), to.be("1970-01-01T00:00:00Z@`UTC`"));

  of_pattern:
    let {
      f: formatter("dd/MM/uuuu");
    }
    expect(f(t.epoch), to.be("01/01/1970"));

  of_localized_pattern:
    let {
      f: formatter("cccc, MMMM d uuuu");
    }
    expect(f(t.epoch), to.be("Thursday, January 1 1970"));

  of_localized_pattern_de:
    let {
      f: formatter("cccc, d. MMMM uuuu", "de");
    }
    expect(f(t.epoch), to.be("Donnerstag, 1. Januar 1970"));


  of_bad_pattern:
    expect_error(
      () -> formatter("invalid pattern here"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_nil_pattern:
    expect_error(
      () -> formatter(nil, "en"),
      to.have_code("NIL_ERROR")
    );

  of_nil_lang:
    expect_error(
      () -> formatter("uuuu-MM-dd", nil),
      to.have_code("NIL_ERROR")
    );

}