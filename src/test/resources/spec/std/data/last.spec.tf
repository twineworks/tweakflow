import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.last as last;

library spec {
  spec:
    describe("data.last", [

      it("singleton", () ->
        expect(last([1]), to.be(1))
      ),

      it("simple_list", () ->
        expect(last([1, 2, 3]), to.be(3))
      ),

      it("of_nil", () ->
        expect(last(nil), to.be_nil())
      ),

      it("empty", () ->
        expect_error(
          () -> last([]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}