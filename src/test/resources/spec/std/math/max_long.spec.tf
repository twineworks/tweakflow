import data, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.max_long as max_long;

library spec {
  spec:
    describe("math.max_long", [

    it("is_pos", () ->
      expect(max_long, to.be_greater_than(0))
    ),

    it("wrap", () ->
      expect(max_long+1, to.be_less_than(0))
    ),

  ]);
}

