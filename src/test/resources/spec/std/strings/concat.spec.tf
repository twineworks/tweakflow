import strings as s from 'std';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.concat as concat;

library spec {
  spec:
    describe("strings.concat", [

      it("empty", () ->
        assert(concat([]) === "")
      ),

      it("simple", () ->
        assert(concat(["a", "b", "c"]) === "abc")
      ),

      it("with_nil", () ->
        assert(concat(["a", nil, "b"]) === "anilb")
      ),

      it("of_nil", () ->
        assert(concat(nil) === nil)
      ),

      it("of_non_list", () ->
        expect_error(
          () -> concat(1),
          to.have_code("CAST_ERROR")
        )
      ),

      # equivalent of concat("hello" as list), which is concat(["h", "e", "l", "l", "o"])
      it("of_string", () ->
        assert(concat("hello") === "hello")
      ),

      it("with_castable", () ->
        assert(concat([1,2,3]) === "123")
      ),

      it("with_non_castable", () ->
        expect_error(
          () -> concat(["a", {}]),
          to.have_code("CAST_ERROR")
        )
      ),
  ]);
}