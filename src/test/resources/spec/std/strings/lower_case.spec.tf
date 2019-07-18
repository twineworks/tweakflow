import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.lower_case as lower_case;

library spec {
  spec:
    describe("strings.lower_case", [

      it("of_default", () ->
        expect(lower_case(), to.be_nil())
      ),

      it("of_nil", () ->
        expect(lower_case(nil), to.be_nil())
      ),

      it("of_nil_lang", () ->
        expect(lower_case("foo", nil), to.be_nil())
      ),

      it("empty", () ->
        expect(lower_case(""), to.be(""))
      ),

      it("simple", () ->
        expect(lower_case("FOO"), to.be("foo"))
      ),

      it("localized", () ->
        expect(lower_case("TITLE", "tr"), to.be("tıtle"))
      ), # dot-less i in turkish language

  ]);
}