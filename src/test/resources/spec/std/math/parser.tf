import locale, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.parser as parser;

library spec {
  spec:
    describe("parser", [


  of_default:
    let {
      f: parser();
    }
    expect(f, to.be_function()) &&
    expect(f(), to.be_nil()) &&
    expect(f("1"), to.be(1));

  of_two_digit_decimals:
    let {
      f: parser("0.00");
    }
    expect(f("1.00") as double, to.be(1.0)) &&
    expect(f("-2.20"), to.be(-2.2));

  of_two_digit_decimals_strict:
    let {
      f: parser("0.00", lenient: false);
    }
    expect_error(
      () -> f("1.00kg"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_two_digit_decimals_lenient:
    let {
      f: parser("0.00", lenient: true);
    }
    expect(f("1.00kg") as double, to.be(1.0));

  of_localized_symbols:
    let {
      s: locale.decimal_symbols('de');
      f: parser("0.00", s);
    }
    expect(f("1,00") as double, to.be(1.0)) &&
    expect(f("-2,20"), to.be(-2.2));

  of_bad_pattern:
    expect_error(
      () -> parser("bad pattern 0#0"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_bad_decimal_symbols:
    expect_error(
      () -> parser(decimal_symbols: {}),
      to.have_code("NIL_ERROR")
    );

  ]);
}