import store from "./../data.tf";
import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.get as get;
alias store.inventory as inv;

library spec {
  spec:
    describe("data.get", [

      it("bicycle_color", () ->
        assert(get(get(inv, :bicycle), :color) == "red")
      ),

      it("lord_of_the_rings_price", () ->
        assert(get(get(get(inv, :book), 3), :price) == 22.99)
      ),

      it("moby_dick_isbn", () ->
        assert(get(get(get(inv, :book), 2), :isbn) == "0-553-21311-3")
      ),

      it("gets_nil", () ->
        assert(get(inv, nil) == nil)
      ),

      it("gets_from_nil", () ->
        assert(get(nil, :book) == nil)
      ),

      it("no_default_from_nil", () ->
        assert(get(nil, :title, "d") == nil)
      ),

      it("no_default_from_nil_key", () ->
        assert(get({}, nil, "d") == nil)
      ),

      it("default_from_empty_map", () ->
        assert(get({}, :title, "d") == "d")
      ),

      it("default_from_missing_in_map", () ->
        assert(get({:a "foo"}, :title, "d") == "d")
      ),

      it("default_from_empty_list", () ->
        assert(get([], 2, "d") == "d")
      ),

      it("default_from_missing_in_list", () ->
        assert(get(["foo"], 2, "d") == "d")
      ),

      it("default_from_invalid_in_list", () ->    
        assert(get(["foo"], -2, "d") == "d")
      ),
      
      it("default_from_high_in_list", () -> 
        assert(get(["foo"], 999999999999999999, "d") == "d")
      ),
        
      it("of_non_collection", () -> 
        expect_error(() -> get("foo", 0), to.have_code("ILLEGAL_ARGUMENT"))
      ),
    
      it("of_invalid_dict_key", () -> 
        expect_error(() -> get({}, []), to.have_code("CAST_ERROR"))
      ),
    
      it("of_invalid_list_key", () -> 
        expect_error(() -> get([], 2019-01-01T), to.have_code("CAST_ERROR"))
      ),

    ]);
}