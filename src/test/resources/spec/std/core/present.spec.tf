import core from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias core.present? as present?;

library spec {
  spec:
    describe("core.present?", [

      it("is_function", () ->
        assert(present? is function)
      ),

      it("non_nil_true", () ->
        assert(present?("") === true)
      ),

      it("nil_false", () ->
        assert(present?(nil) === false)
      ),

      it("default_false", () ->
        assert(present?() === false)
      ),

  ]);
}