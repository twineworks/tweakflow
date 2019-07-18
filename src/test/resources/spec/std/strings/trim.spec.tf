import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.trim as trim;

library spec {
  spec:
    describe("strings.trim", [

      it("of_default", () ->
        expect(trim(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(trim(nil), to.be_nil())
      ),

      it("of_only_whitespace", () ->
        expect(trim("\u3000\n\r\n  \n\u3000"), to.be(""))
      ),

      it("simple", () ->
        expect(trim(" foo bar "), to.be("foo bar"))
      ),

      it("with_newlines", () ->
        expect(trim("\nfoo bar\r\n"), to.be("foo bar"))
      ),

      it("with_unicode_whitespace", () ->
        expect(trim("\u3000\nfoo bar\u3000\r"), to.be("foo bar"))
      ),

  ]);
}