import time as t, math from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias t.parser as parser;

library parser_spec {

  of_default:
    let {
      f: parser();
    }
    expect(f, to.be_function()) &&
    expect(f(nil), to.be_nil()) &&
    expect(f("1970-01-01T00:00:00Z@`UTC`"), to.be(t.epoch));

  of_pattern:
    let {
      f: parser("dd/MM/uuuu");
    }
    expect(f("01/01/1970"), to.be(t.epoch));

  of_localized_pattern:
    let {
      f: parser("cccc, MMMM d uuuu");
    }
    expect(f("Thursday, January 1 1970"), to.be(t.epoch));

  of_localized_pattern_de:
    let {
      f: parser("cccc, d. MMMM uuuu", lang: "de");
    }
    expect(f("Donnerstag, 1. Januar 1970"), to.be(t.epoch));

  of_unparseable_date:
    let {
      f: parser("uuuu-MM-dd");
    }
    expect_error(
      () -> f("this does not parst"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  strict_of_bad_date:
    let {
      f: parser("uuuu-MM-dd", lenient: false);
    }
    expect_error(
      () -> f("2019-03-32"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  lenient_of_bad_date:
    let {
      f: parser("uuuu-MM-dd", lenient: true);
    }
    expect(
      f("2019-03-32"),
      to.be(2019-04-01T)
    );

  uses_default_tz:
    let {
      f: parser('uuuu-MM-dd[ HH:mm:ss]', default_tz: 'America/Chicago');
    }
    expect(
      f("2017-06-22 12:34:11"),
      to.be(2017-06-22T12:34:11-05:00@`America/Chicago`)
    ) &&
    expect(
      f("2017-06-22"),
      to.be(2017-06-22T00:00:00-05:00@`America/Chicago`)
    );

  of_bad_pattern:
    expect_error(
      () -> parser("invalid pattern here"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_nil_pattern:
    expect_error(
      () -> parser(nil),
      to.have_code("NIL_ERROR")
    );

  of_nil_lenient:
    expect_error(
      () -> parser(lenient: nil),
      to.have_code("NIL_ERROR")
    );

  of_nil_lang:
    expect_error(
      () -> parser(lang: nil),
      to.have_code("NIL_ERROR")
    );

  of_nil_default_tz:
    expect_error(
      () -> parser(default_tz: nil),
      to.have_code("NIL_ERROR")
    );

}