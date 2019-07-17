import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.contains? as contains?;

library spec {
  spec:
    describe("data.contains?", [

      it("empty_list", () ->
        expect(contains?([], 1), to.be_false())
      ),

      it("non_comparable_nan_list", () ->
        expect(contains?([NaN], NaN), to.be_false())
      ),

      it("non_comparable_function_list", () ->
        let {
          f: (x) -> x;
        }
        expect(contains?([f], f), to.be_false())
      ),

      it("not_found_list", () ->
        expect(contains?([1,2,3], 4), to.be_false())
      ),

      it("found_first_list", () ->
        expect(contains?([1,2,3], 1), to.be_true())
      ),

      it("found_mid_list", () ->
        expect(contains?([1,2,3], 2), to.be_true())
      ),

      it("found_last_list", () ->
        expect(contains?([1,2,3], 3), to.be_true())
      ),

      it("found_nil_in_list", () ->
        expect(contains?([1,nil,3], nil), to.be_true())
      ),

      it("empty_dict", () ->
        expect(contains?({}, 1), to.be_false())
      ),

      it("non_comparable_nan_dict", () ->
        expect(contains?({:a NaN}, NaN), to.be_false())
      ),

      it("non_comparable_function_dict", () ->
        let {
          f: (x) -> x;
        }
        expect(contains?({:a f}, f), to.be_false())
      ),

      it("not_found_dict", () ->
        expect(contains?({:a 1, :b 2, :c 3}, 4), to.be_false())
      ),

      it("found_first_dict", () ->
        expect(contains?({:a 1, :b 2, :c 3}, 1), to.be_true())
      ),

      it("found_mid_dict", () ->
        expect(contains?({:a 1, :b 2, :c 3}, 2), to.be_true())
      ),

      it("found_last_dict", () ->
        expect(contains?({:a 1, :b 2, :c 3}, 3), to.be_true())
      ),

      it("found_nil_in_dict", () ->
        expect(contains?({:a 1, :b nil, :c 3}, nil), to.be_true())
      ),

      it("of_nil", () ->
        expect(contains?(nil, 1), to.be_nil())
      ),

      it("of_default", () ->
        expect(contains?(), to.be_nil())
      ),

      it("of_non_collection", () ->
        expect_error(
          () -> contains?("foo", "o"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

    ]);
}