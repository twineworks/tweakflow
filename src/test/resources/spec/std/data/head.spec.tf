import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.head as head;

library spec {
  spec:
    describe("data.head", [

      it("singleton", () ->
        expect(head([1]), to.be(1))
      ),

      it("simple_list", () ->
        expect(head([1, 2, 3]), to.be(1))
      ),

      it("of_nil", () ->
        expect(head(nil), to.be_nil())
      ),

      it("empty", () ->
        expect_error(
          () -> head([]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}