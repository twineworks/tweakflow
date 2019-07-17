import locale, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias locale.languages as languages;
alias data.has? as has?;

library spec {
  spec:
    describe("locale.languages", [

      it("contains_english", () ->
        expect(has?(languages(), :en), to.be_true())
      ),

      it("contains_german", () ->
        expect(has?(languages(), :de), to.be_true())
      ),

      it("localized_german", () ->
        expect(languages('de')[:de], to.be("Deutsch"))
      ),

      it("fallback_to_english_labels_on_unknown", () ->
        expect(languages('unknown tag')[:de], to.be("German"))
      ),

      it("fallback_to_english_labels_on_nil", () ->
        expect(languages(nil)[:de], to.be("German"))
      ),

    ]);
}