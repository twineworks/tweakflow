import locale, data from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias locale.languages as languages;
alias data.has? as has?;

library languages_spec {

  contains_english:
    expect(has?(languages(), :en), to.be_true());

  contains_german:
    expect(has?(languages(), :de), to.be_true());

  localized_german:
    expect(languages('de')[:de], to.be("Deutsch"));

  fallback_to_english_labels_on_unknown:
    expect(languages('unknown tag')[:de], to.be("German"));

  fallback_to_english_labels_on_nil:
    expect(languages(nil)[:de], to.be("German"));

}