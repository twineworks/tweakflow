import core from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias core.id as id;

library spec {
  spec:
    describe("core.id", [

      it("is_function", () ->
        assert(id is function)
      ),

      it("evaluates_to_input_str", () ->
        assert(id("foo") === "foo")
      ),

      it("evaluates_to_input_list", () ->
        assert(id([]) === [])
      ),

      it("evaluates_to_input_nil", () ->
        assert(id(nil) === nil)
      ),

      it("evaluates_to_default_nil", () ->
        assert(id() === nil)
      ),

  ]);
}