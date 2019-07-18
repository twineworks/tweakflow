import regex from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

library p {
  hello?: regex.matching("hello .*");
  empty?: regex.matching("");
  digits?: regex.matching('\d+');
}

library spec {
  spec:
    describe("regex.matching", [

      it("hello_world", () ->
        assert(p.hello?("hello world"))
      ),

      it("helloween", () ->
        assert(p.hello?("helloween") === false)
      ),

      it("empty", () ->
        assert(p.empty?(""))
      ),

      it("a", () ->
        assert(p.empty?("a") === false)
      ),

      it("digits", () ->
        assert(p.digits?("12345"))
      ),

      it("non_digits", () ->
        assert(p.digits?("123-45") === false)
      ),

      it("of_nil", () ->
        assert(p.digits?(nil) === nil)
      ),

      it("nil_pattern", () ->
        expect_error(
          () -> regex.matching(nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("invalid_pattern", () ->
        expect_error(
          () -> regex.matching("[a"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}