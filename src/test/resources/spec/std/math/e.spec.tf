import data, math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.e as e;
alias m.log as log;

library spec {
  spec:
    describe("math.e", [

    it("base_e", () ->
      expect(log(e), to.be_close_to(1.0))
    ),

  ]);
}

