import store from "./../../data.tf";
import * as std from "std.tf";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias std.data.get_in as get_in;
alias store.inventory as inv;

library spec {
  spec:
    describe("data.get_in", [

      it("bicycle_color", () ->
        assert(get_in(inv, [:bicycle, :color]) == "red")
      ),

      it("lord_of_the_rings_price", () ->
        assert(get_in(inv, [:book, 3, :price]) == 22.99)
      ),

      it("moby_dick_isbn", () ->
        assert(get_in(inv, [:book, 2, :isbn]) == "0-553-21311-3")
      ),

      it("gets_nil", () ->
        assert(get_in(inv, [nil]) == nil)
      ),

      it("gets_from_nil", () ->
        assert(get_in(nil, [:book]) == nil)
      ),

      it("no_default_from_nil", () ->
        assert(get_in(nil, [:title], "d") == nil)
      ),

      it("no_default_from_nil_keys", () ->
        assert(get_in({}, nil, "d") == nil)
      ),

      it("default_from_empty_map", () ->
        assert(get_in({}, [:title], "d") == "d")
      ),

      it("default_from_missing_in_map", () ->
        assert(get_in({:a "foo"}, [:title], "d") == "d")
      ),

      it("default_from_empty_list", () ->
        assert(get_in([], [2], "d") == "d")
      ),

      it("default_from_missing_in_list", () ->
        assert(get_in(["foo"], [2], "d") == "d")
      ),

      it("default_from_invalid_in_list", () ->
        assert(get_in(["foo"], [-2], "d") == "d")
      ),

      it("default_from_high_in_list", () ->
        assert(get_in(["foo"], [999999999999999999], "d") == "d")
      ),

      it("nested_default_map", () ->
        assert(get_in(inv, ["book", 3, "miss"], "d") == "d")
      ),

      it("nested_default_list", () ->
         assert(get_in(inv, ["book", -1], "d") == "d")
      ),

      it("of_non_list_key", () ->
        expect_error(() -> get_in([1, 2, 3], 0), to.have_code("CAST_ERROR"))
      ),

      it("of_non_collection", () ->
        expect_error(() -> get_in("foo", [0]), to.have_code("ILLEGAL_ARGUMENT"))
      ),

      it("of_invalid_dict_key", () ->
        expect_error(() -> get_in({}, [[]]), to.have_code("CAST_ERROR"))
      ),

      it("of_invalid_list_key", () ->
        expect_error(() -> get_in([], [2019-01-01T]), to.have_code("CAST_ERROR"))
      ),

  ]);
}