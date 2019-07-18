import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.reverse as reverse;

library spec {
  spec:
    describe("data.reverse", [

      it("empty_list", () ->
        expect(reverse([]), to.be([]))
      ),

      it("simple_list", () ->
        expect(reverse([1,2,3]), to.be([3,2,1]))
      ),

      it("of_nil", () ->
        expect(reverse(nil), to.be_nil())
      ),

  ]);
}