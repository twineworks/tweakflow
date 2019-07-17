import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.delete as delete;

library spec {
  spec:
    describe("data.delete", [

      it("delete_nil_from_list", () ->
        expect(delete([1, 2, 3], nil), to.be([1, 2, 3]))
      ),

      it("delete_from_empty_list", () ->
        expect(delete([], 2), to.be([]))
      ),

      it("delete_existing_in_list", () ->
        expect(delete([1, 2, 3], 0), to.be([2, 3]))
      ),

      it("delete_missing_in_list", () ->
        expect(delete([1, 2, 3], 10), to.be([1, 2, 3]))
      ),

      it("delete_neg_index_in_list", () ->
        expect(delete([1, 2, 3], -1), to.be([1, 2, 3]))
      ),

      it("delete_nil_from_dict", () ->
        expect(delete({:a 1, :b 2}, nil), to.be({:a 1, :b 2}))
      ),

      it("delete_from_empty_dict", () ->
        expect(delete({}, :a), to.be({}))
      ),

      it("delete_existing_in_dict", () ->
        expect(delete({:a 1, :b 2}, :a), to.be({:b 2}))
      ),

      it("delete_missing_in_dict", () ->
        expect(delete({:a 1, :b 2}, :c), to.be({:a 1, :b 2}))
      ),

      it("of_nil", () ->
        expect(delete(nil), to.be_nil())
      ),

      it("of_bad_key_in_dict", () ->
        expect_error(
          () -> delete({:a "foo"}, []),
          to.have_code("CAST_ERROR")
        )
      ),

      it("of_bad_key_in_list", () ->
        expect_error(
          () -> delete([1, 2, 3], "foo"),
          to.have_code("CAST_ERROR")
        )
      ),
    ]);
}