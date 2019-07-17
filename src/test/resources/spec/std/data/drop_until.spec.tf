import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.drop_until as drop_until;


library spec {
  spec:
    describe("data.drop_until", [

      it("drop_everything_from_some", () ->
        expect(drop_until((_) -> true, [1, 2, 3]), to.be([1, 2, 3]))
      ),

      it("drop_nothing_from_some", () ->
        expect(drop_until((_) -> false, [1, 2, 3]), to.be([]))
      ),

      it("drop_one_from_some", () ->
        expect(drop_until((x) -> x > 1, [1, 2, 3]), to.be([2, 3]))
      ),

      it("drop_some_from_some", () ->
        expect(drop_until((x) -> x > 2, [1, 2, 3]), to.be([3]))
      ),

      it("drop_some_from_some_with_cast", () ->
        expect(drop_until((x) -> if x > 2 then "yay" else "", [1, 2, 3]), to.be([3]))
      ),

      it("drop_indexed_one_from_some", () ->
        expect(drop_until((_, i) -> i > 0, [1, 2, 3]), to.be([2, 3]))
      ),

      it("drop_indexed_some_from_some", () ->
        expect(drop_until((_, i) -> i > 1, [1, 2, 3]), to.be([3]))
      ),

      it("drop_indexed_some_from_some_with_cast", () ->
        expect(drop_until((_, i) -> if i > 1 then "yay" else nil, [1, 2, 3]), to.be([3]))
      ),

      it("of_default", () ->
        expect(drop_until(nil, nil), to.be_nil())
      ),

      it("from_nil", () ->
        expect(drop_until((_) -> true, nil), to.be_nil())
      ),

      it("nil_predicate", () ->
        expect_error(
          () -> drop_until(nil, ["foo"]),
          to.have_code("NIL_ERROR")
        )
      ),

      it("bad_predicate", () ->
        expect_error(
          () -> drop_until(() -> true, ["foo"]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}