import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.group_by as group_by;

library spec {
  spec:
    describe("data.group_by", [

      it("groups nil", () ->
        expect(
          group_by(nil, (x) -> "foo"),
          to.be(nil)
        )
      ),

      it("groups empty list", () ->
        expect(
          group_by([], (x) -> x[:v]),
          to.be({})
        )
      ),

      it("groups list to empty", () ->
        expect(
          group_by([1, 2, 3], (x) -> nil),
          to.be({})
        )
      ),

      it("groups list to constant", () ->
        expect(
          group_by([1, 2, 3], (x) -> "a"),
          to.be({:a [1, 2, 3]})
        )
      ),

      it("groups list of dicts", () ->
        expect(
          group_by([{:id "a1", :v 1}, {:id "a2", :v 2}, {:id "a3", :v 1}], (x) -> x[:v]),
          to.be({:1 [{:id "a1", :v 1}, {:id "a3", :v 1}], :2 [{:id "a2", :v 2}]})
        )
      ),

      it("groups list of numbers", () ->
        expect(
          group_by([1, 2, 3, 4, 5], (x) -> if x % 2 == 0 then "even" else "odd"),
          to.be(
            {
              :even [2, 4],
              :odd [1, 3, 5]
            }
          )
        )
      ),

      it("groups list omitting nil keys", () ->
        expect(
          group_by(["one", "two", "three"], (x) -> if x != "two" then x else nil),
          to.be({:one ["one"], :three ["three"]})
        )
      ),

      it("groups list with i", () ->
        expect(
          group_by([{:id "a1", :v 1}, {:id "a2", :v 2}], (x, i) -> "i"..i),
          to.be({:i0 [{:id "a1", :v 1}], :i1 [{:id "a2", :v 2}]})
        )
      ),

      it("groups list with i omitting nil keys", () ->
        expect(
          group_by(["one", "two", "three"], (x, i) -> if i!=1 then x else nil),
          to.be({:one ["one"], :three ["three"]})
        )
      ),

      it("groups dict", () ->
        expect(
          group_by({:a1 1, :a2 2, :a3 2}, (x) -> "b"..x),
          to.be({:b1 [1], :b2 [2, 2]})
        )
      ),

      it("groups dict omitting nil keys", () ->
        expect(
          group_by({:a1 1, :a2 2}, (x) -> if x!=1 "b"..x else nil),
          to.be({:b2 [2]})
        )
      ),

      it("groups dict with key", () ->
        expect(
          group_by({:a1 1, :a2 2}, (x, k) -> k..x),
          to.be({:a11 [1], :a22 [2]})
        )
      ),

      it("groups dict with key omitting nil keys", () ->
        expect(
          group_by({:a1 1, :a2 2}, (x, k) -> if k!=:a1 "b"..x else nil),
          to.be({:b2 [2]})
        )
      ),

      it("nil_f", () ->
        expect_error(
          () -> group_by([0,1], nil),
          to.have_code("NIL_ERROR")
        )
      ),

      it("zero_arg_f", () ->
        expect_error(
          () -> group_by([0,1], () -> true),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

      it("non_string_returning_f", () ->
        expect_error(
          () -> group_by([0,1], (x) -> [1, 2, 3]),
          to.have_code("CAST_ERROR")
        )
      ),

      it("non_collection_xs", () ->
        expect_error(
          () -> group_by("foo", (x) -> x),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}