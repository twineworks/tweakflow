import strings as s from 'std.tf';

import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

library spec {
  spec:
    describe("length", [

      it("empty", () ->
        assert(s.length("") == 0)
      ),

      it("missing", () ->
        assert(s.length(nil) == nil)
      ),

      it("single", () ->
        assert(s.length("a") == 1)
      ),

      it("basic", () ->
        assert(s.length("foo") == 3)
      ),

      it("code_points", () ->
        assert(s.length("𐐂") == 1)
      ),

      it("bmp_code_points", () ->
        assert(s.length("你好") == 2)
      ),

      it("more_code_points", () ->
        assert(s.length("𝄞𝒜𝕍") == 3)
      ),
  ]);
}