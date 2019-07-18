import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.size as size;

library spec {
  spec:
    describe("data.size", [

      it("empty_list", () ->
        expect(size([]), to.be(0))
      ),

      it("simple_list", () ->
        expect(size([1,2,3]), to.be(3))
      ),

      it("empty_dict", () ->
        expect(size({}), to.be(0))
      ),

      it("simple_dict", () ->
        expect(size({:a 1, :b 2}), to.be(2))
      ),

      it("of_nil", () ->
        expect(size(nil), to.be_nil())
      ),

      it("invalid_type", () ->
        expect_error(
          () -> size("foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}