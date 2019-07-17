import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.concat as concat;

library spec {
  spec:
    describe("data.concat", [

      it("empty", () ->
        expect(concat([]), to.be([]))
      ),

      it("single", () ->
        expect(concat([[1,2,3]]), to.be([1,2,3]))
      ),

      it("pair", () ->
        expect(concat([[1,2,3], [4,5,6]]), to.be([1,2,3,4,5,6]))
      ),

      it("triple", () ->
        expect(concat([[1,2,3], [4,5,6], [7,8,9]]), to.be([1,2,3,4,5,6,7,8,9]))
      ),

      it("of_nil", () ->
        expect(concat(nil), to.be_nil())
      ),

      it("of_nil_element", () ->
        expect(concat([[1, 2], nil]), to.be_nil())
      ),

      it("invalid_element_type", () ->
        expect_error(
          () -> concat([{}]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

   ]);
}