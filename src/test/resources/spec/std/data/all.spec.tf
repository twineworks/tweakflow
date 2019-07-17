import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.all? as all?;

library spec {
  spec:
    describe("data.all?", [

      it("empty", () ->
        expect(all?([], (_) -> false), to.be_true())
      ),

      it("none", () ->
        expect(all?([1,2,3], (x) -> x > 10), to.be_false())
      ),

      it("none_with_cast", () ->
        expect(all?([1,2,3], (_) -> nil), to.be_false())
      ),

      it("some", () ->
        expect(all?([1,2,3], (x) -> x >= 2), to.be_false())
      ),

      it("some_with_cast", () ->
        expect(all?([1,2,3], (x) -> if x >= 2 then "yeah" else ""), to.be_false())
      ),

      it("all", () ->
        expect(all?([1,2,3], (x) -> x >= 1), to.be_true())
      ),

      it("all_with_cast", () ->
        expect(all?([1,2,3], (x) -> if x >= 1 then "yeah" else ""), to.be_true())
      ),

      it("all_with_index", () ->
        expect(all?([1,2,3], (_, i) -> i >= 0), to.be_true())
      ),

      it("none_with_index", () ->
        expect(all?([1,2,3], (_, i) -> i < 0), to.be_false())
      ),

      it("some_with_index", () ->
        expect(all?([1,2,3], (_, i) -> i == 0), to.be_false())
      ),

      it("of_nil", () ->
        expect(all?(nil), to.be_nil())
      ),

      it("of_nil_p_nil", () ->
        expect(all?(nil, nil), to.be_nil())
      ),

      it("of_only_p_nil", () ->
        expect_error(
          () -> all?([], nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("of_invalid_p_too_few_args", () ->
        expect_error(
          () -> all?([], () -> true), # p should accept 1 or 2 args
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}