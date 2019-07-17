import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.init as init;

library spec {
  spec:
    describe("data.init", [

      it("singleton", () ->
        expect(init([1]), to.be([]))
      ),

      it("simple_list", () ->
        expect(init([1, 2, 3]), to.be([1, 2]))
      ),

      it("of_nil", () ->
        expect(init(nil), to.be_nil())
      ),

      it("empty", () ->
        expect_error(
          () -> init([]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    ]);
}