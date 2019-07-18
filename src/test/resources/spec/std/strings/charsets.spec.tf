import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.charsets as charsets;

library spec {
  spec:
    describe("strings.charsets", [

      it("of_default", () ->
        expect(charsets(), to.contain_all(["UTF-8", "ISO-8859-1", "windows-1252"]))
      ),

  ]);
}