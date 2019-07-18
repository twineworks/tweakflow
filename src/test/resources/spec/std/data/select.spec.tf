import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.select as select;

library spec {
  spec:
    describe("data.select", [

      it("select_nil_from_list", () ->
        expect(select([1, 2, 3], nil), to.be_nil())
      ),

      it("select_from_empty_list", () ->
        expect(select([], [2]), to.be([nil]))
      ),

      it("select_default_from_empty_list", () ->
        expect(select([], [2], "a"), to.be(["a"]))
      ),

      it("select_existing_in_list", () ->
        expect(select([1, 2, 3], [1, 1]), to.be([2, 2]))
      ),

      it("select_existing_and_default_in_list", () ->
        expect(select([1, 2, 3], [1, 10, 1], "a"), to.be([2, "a", 2]))
      ),

      it("select_nil_from_dict", () ->
        expect(select({:a 1, :b 2}, nil), to.be_nil())
      ),

      it("select_from_empty_dict", () ->
        expect(select({:a 1, :b 2}, [:foo]), to.be({:foo nil}))
      ),

      it("select_default_from_empty_dict", () ->
        expect(select({}, [:foo], "a"), to.be({:foo "a"}))
      ),

      it("select_existing_in_dict", () ->
        expect(select({:a 1, :b 2}, [:b, :a]), to.be({:b 2, :a 1}))
      ),

      it("select_existing_and_default_in_dict", () ->
        expect(select({:a 1, :b 2}, [:a, :foo, :b], "def"), to.be({:a 1, :foo "def", :b 2}))
      ),

      it("of_nil", () ->
        expect(select(nil), to.be_nil())
      ),

      it("of_bad_key_in_dict", () ->
        expect_error(
          () -> select({:a "foo"}, [[]]),
          to.have_code("CAST_ERROR")
        )
      ),

      it("of_bad_key_in_list", () ->
        expect_error(
          () -> select([1, 2, 3], ["foo"]),
          to.have_code("CAST_ERROR")
        )
      ),
  ]);
}