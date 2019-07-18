import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.update_in as update_in;


library spec {
  spec:
    describe("data.update_in", [

      it("empty_list", () -> 
        expect(update_in([], [0], (x) -> if x == nil "a" else "wrong"), to.be(["a"]))
      ),
    
      it("nested_empty_list", () -> 
        expect(update_in([[]], [0, 0], (x) -> if x == nil "a" else "wrong"), to.be([["a"]]))
      ),
    
      it("append_list", () -> 
        expect(update_in([1,2,3], [3], (x) -> if x == nil "a" else "wrong"), to.be([1, 2, 3, "a"]))
      ),
    
      it("nested_append_list", () -> 
        expect(update_in([[1,2,3]], [0, 3], (x) -> if x == nil "a" else "wrong"), to.be([[1, 2, 3, "a"]]))
      ),
    
      it("overwrite_list", () -> 
        expect(update_in([1,2,3], [0], (x) -> if x == 1 "a" else "wrong"), to.be(["a", 2, 3]))
      ),
    
      it("nested_overwrite_list", () -> 
        expect(update_in({:a [1,2,3]}, [:a, 0], (x) -> if x == 1 "a" else "wrong"), to.be({:a ["a", 2, 3]}))
      ),
    
      it("extend_list", () -> 
        expect(update_in([1,2,3], [5], (x) -> if x == nil "a" else "wrong"), to.be([1, 2, 3, nil, nil, "a"]))
      ),
    
      it("nest_list", () -> 
        expect(update_in([1,2,3], [5, 2], (x) -> if x == nil "a" else "wrong"), to.be([1, 2, 3, nil, nil, [nil, nil, "a"]]))
      ),
    
      it("nested_extend_list", () -> 
        expect(update_in([[1,2,3]], [0, 5], (x) -> if x == nil "a" else "wrong"), to.be([[1, 2, 3, nil, nil, "a"]]))
      ),
    
      it("multi_nested_extend_list", () -> 
        expect(update_in([], [1, 1, 1], (x) -> if x == nil "a" else "wrong"), to.be([nil, [nil, [nil, "a"]]]))
      ),
    
      it("empty_dict", () -> 
        expect(update_in({}, [:a], (x) -> if x == nil "foo" else "wrong"), to.be({:a "foo"}))
      ),
    
      it("nested_empty_dict", () -> 
        expect(update_in({:a {}}, [:a, :a], (x) -> if x == nil "foo" else "wrong"), to.be({:a {:a "foo"}}))
      ),
    
      it("overwrite_dict", () -> 
        expect(update_in({:a "foo", :b "bar"}, [:a], (x) -> if x == "foo" "changed" else "wrong"), to.be({:a "changed", :b "bar"}))
      ),
    
      it("nested_overwrite_dict", () -> 
        expect(update_in({:a {:a "foo", :b "bar"}}, [:a, :a], (x) -> if x == "foo" "changed" else "wrong"), to.be({:a {:a "changed", :b "bar"}}))
      ),
    
      it("extend_dict", () -> 
        expect(update_in({:a "foo", :b "bar"}, [:c], (x) -> if x == nil "baz" else "wrong"), to.be({:a "foo", :b "bar", :c "baz"}))
      ),
    
      it("nest_dict", () -> 
        expect(update_in({:a "foo", :b "bar"}, [:c, :d], (x) -> if x == nil "baz" else "wrong"), to.be({:a "foo", :b "bar", :c {:d "baz"}}))
      ),
    
      it("nested_extend_dict", () -> 
        expect(
          update_in({:a {:a "foo", :b "bar"}}, [:a, :c], (x) -> if x == nil "baz" else "wrong"),
          to.be({:a {:a "foo", :b "bar", :c "baz"}})
        )
      ),
    
      it("of_nil", () -> 
        expect(update_in(nil, [0], (x) -> x), to.be_nil())
      ),
    
      it("invalid_collection", () -> 
        expect_error(
          () -> update_in("foo", [1], (x) -> x),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("nested_invalid_collection", () -> 
        expect_error(
          () -> update_in(["foo"], [0, 1], (x) -> x),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("nil_key", () -> 
        expect_error(
          () -> update_in({}, [nil], (x) -> x),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("bad_key_for_dict", () -> 
        expect_error(
          () -> update_in({}, [[]], (x) -> x),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("nested_bad_key_for_dict", () -> 
        expect_error(
          () -> update_in({:a {}}, [:a, []], (x) -> x),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("bad_key_for_list", () -> 
        expect_error(
          () -> update_in([], [2019-01-01T], (x) -> x),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("nested_bad_key_for_list", () -> 
        expect_error(
          () -> update_in([[]], [0, 2019-01-01T], (x) -> x),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("neg_key_for_list", () -> 
        expect_error(
          () -> update_in([], [-1], (x) -> x),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),
    
      it("nested_neg_key_for_list", () -> 
        expect_error(
          () -> update_in([[]], [0, -1], (x) -> x),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),
    ]);
}