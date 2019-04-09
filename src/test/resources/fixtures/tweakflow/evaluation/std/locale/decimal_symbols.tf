import locale, data from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias locale.decimal_symbols as decimal_symbols;
alias data.has? as has?;

library decimal_symbols_spec {

  of_default:
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
    );

  of_de:
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
    );

}