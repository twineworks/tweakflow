import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.any? as any?;

library spec {
  spec:
    describe("data.any?", [

      it("empty_list", () ->
        expect(any?([], (_) -> true), to.be_false())
      ),

      it("not_found", () ->
        expect(any?([1,2,3], (x) -> x > 10), to.be_false())
      ),

      it("not_found_with_cast", () ->
        expect(any?([1,2,3], (x) -> if x > 10 then "yes" else nil), to.be_false())
      ),

      it("not_found_with_index", () ->
        expect(any?([1,2,3], (x, i) -> i > 10), to.be_false())
      ),

      it("not_found_with_default_3rd_param", () ->
        expect(any?([1,2,3], (x, i, a="foo") -> i > 10), to.be_false())
      ),

      it("found", () ->
        expect(any?([1,2,3], (x) -> x == 2), to.be_true())
      ),

      it("found_with_cast", () ->
        expect(any?([1,2,3], (x) -> if x == 2 "foo" else ""), to.be_true())
      ),

      it("found_with_index", () ->
        expect(any?([1,2,3], (x, i) -> i == 2), to.be_true())
      ),

      it("found_with_default_3rd_param", () ->
        expect(any?([1,2,3], (x, i, a="foo") -> a == "foo" && i == 2), to.be_true())
      ),

      it("found_first", () ->
        expect(any?([1,2,3], (x) -> x <= 2), to.be_true())
      ),

      it("found_last", () ->
        expect(any?([1,2,3], (x) -> x == 3), to.be_true())
      ),

      it("of_nil", () ->
        expect(any?(nil), to.be_nil())
      ),

      it("of_nil_p_nil", () ->
        expect(any?(nil, nil), to.be_nil())
      ),

      it("of_only_p_nil", () ->
        expect_error(
          () -> any?([], nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("of_invalid_p_too_few_args", () ->
        expect_error(
          () -> any?([], () -> true), # p should accept 1 or 2 args
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}