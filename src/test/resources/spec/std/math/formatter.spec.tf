import locale, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.formatter as formatter;

library spec {
  spec:
    describe("math.formatter", [

      it("of_default", () ->
        let {
          f: formatter();
        }
        expect(f, to.be_function()) &&
        expect(f(), to.be_nil()) &&
        expect(f(1), to.be("1"))
      ),

      it("of_two_digit_decimals", () ->
        let {
          f: formatter("0.00");
        }
        expect(f(1), to.be("1.00")) &&
        expect(f(-2.2), to.be("-2.20")) &&
        expect(f(-2.2d), to.be("-2.20"))
      ),

      it("of_localized_symbols", () ->
        let {
          s: locale.decimal_symbols('de');
          f: formatter("0.00", s);
        }
        expect(f(1), to.be("1,00")) &&
        expect(f(-2.2), to.be("-2,20")) &&
        expect(f(-2.2d), to.be("-2,20"))
      ),

      it("of_floor_rounding", () ->
        let {
          f: formatter("0.00", rounding_mode: 'floor');
        }
        expect(f(1.888), to.be("1.88")) &&
        expect(f(-2.222), to.be("-2.23")) &&
        expect(f(-2.222d), to.be("-2.23"))
      ),

      it("of_always_show_decimal_sep", () ->
        let {
          f: formatter("0.##", always_show_decimal_separator: true);
        }
        expect(f(1.0), to.be("1.")) &&
        expect(f(-20.0d), to.be("-20."))
      ),

      it("of_not_always_show_decimal_sep", () ->
        let {
          f: formatter("0.##", always_show_decimal_separator: false);
        }
        expect(f(1.0), to.be("1")) &&
        expect(f(-20.0d), to.be("-20"))
      ),

      it("of_bad_pattern", () ->
        expect_error(
          () -> formatter("bad pattern 0#0"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("of_nil_pattern", () ->
        expect_error(
          () -> formatter(nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("of_nil_rounding", () ->
        expect_error(
          () -> formatter("0", rounding_mode: nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("of_nil_always_show_decimal_separator", () ->
        expect_error(
          () -> formatter("0", always_show_decimal_separator: nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("of_non_numeric", () ->
        expect_error(
          () -> formatter()("hello"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("of_bad_decimal_symbols", () ->
        expect_error(
          () -> formatter(decimal_symbols: {}),
          to.have_code("NIL_ERROR")
        )
      ),

  ]);
}