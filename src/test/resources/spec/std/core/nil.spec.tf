import core from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias core.nil? as nil?;

library spec {
  spec:
    describe("core.nil?", [

      it("is_function", () ->
        assert(nil? is function)
      ),

      it("non_nil_false", () ->
        assert(nil?("") === false)
      ),

      it("nil_true", () ->
        assert(nil?(nil) === true)
      ),

      it("default_true", () ->
        assert(nil?() === true)
      ),

  ]);
}