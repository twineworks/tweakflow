import locale, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias locale.decimal_symbols as decimal_symbols;
alias data.has? as has?;

library spec {
  spec:
    describe("locale.decimal_symbols", [

      it("of_default", () ->
        expect(
          decimal_symbols(),
          to.be_superset_of(
            {
              :infinity "∞",
              :grouping_separator ",",
              :minus_sign "-",
              :exponent_separator "E",
              :zero_digit "0",
              :decimal_separator "."
            }
          )
        )
      ),

      it("of_de", () ->
        expect(
          decimal_symbols('de'),
          to.be_superset_of(
            {
              :infinity "∞",
              :grouping_separator ".",
              :minus_sign "-",
              :exponent_separator "E",
              :zero_digit "0",
              :decimal_separator ","
            }
          )
        )
      ),

  ]);
}