import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.index_by as index_by;

library spec {
  spec:
    describe("data.index_by", [

      it("indexes list", () ->
        expect(
          index_by([{:id "a1", :v 1}, {:id "a2", :v 2}], (x) -> x[:id]),
          to.be({:a1 {:id "a1", :v 1}, :a2 {:id "a2", :v 2}})
        )
      ),

      it("indexes list with i", () ->
        expect(
          index_by([{:id "a1", :v 1}, {:id "a2", :v 2}], (x, i) -> "i"..i),
          to.be({:i0 {:id "a1", :v 1}, :i1 {:id "a2", :v 2}})
        )
      ),

      it("indexes dict", () ->
        expect(
          index_by({:a1 1, :a2 2}, (x) -> "b"..x),
          to.be({:b1 1, :b2 2})
        )
      ),

      it("indexes dict with key", () ->
        expect(
          index_by({:a1 1, :a2 2}, (x, k) -> k..x),
          to.be({:a11 1, :a22 2})
        )
      ),

      it("nil_f", () ->
        expect_error(
          () -> index_by([0,1], nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("zero_arg_f", () ->
        expect_error(
          () -> index_by([0,1], () -> true),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("non_collection_xs", () ->
        expect_error(
          () -> index_by("foo", (x) -> x),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}