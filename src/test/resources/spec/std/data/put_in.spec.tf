import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.put_in as put_in;


library spec {
  spec:
    describe("data.put_in", [

      it("empty_list", () -> 
        expect(put_in([], [0], "a"), to.be(["a"]))
      ),
    
      it("nested_empty_list", () -> 
        expect(put_in([[]], [0, 0], "a"), to.be([["a"]]))
      ),
    
      it("append_list", () -> 
        expect(put_in([1,2,3], [3], "a"), to.be([1, 2, 3, "a"]))
      ),
    
      it("nested_append_list", () -> 
        expect(put_in([[1,2,3]], [0, 3], "a"), to.be([[1, 2, 3, "a"]]))
      ),
    
      it("overwrite_list", () -> 
        expect(put_in([1,2,3], [0], "a"), to.be(["a", 2, 3]))
      ),
    
      it("nested_overwrite_list", () -> 
        expect(put_in({:a [1,2,3]}, [:a, 0], "a"), to.be({:a ["a", 2, 3]}))
      ),
    
      it("extend_list", () -> 
        expect(put_in([1,2,3], [5], "a"), to.be([1, 2, 3, nil, nil, "a"]))
      ),
    
      it("nest_list", () -> 
        expect(put_in([1,2,3], [5, 2], "a"), to.be([1, 2, 3, nil, nil, [nil, nil, "a"]]))
      ),
    
      it("nested_extend_list", () -> 
        expect(put_in([[1,2,3]], [0, 5], "a"), to.be([[1, 2, 3, nil, nil, "a"]]))
      ),
    
      it("multi_nested_extend_list", () -> 
        expect(put_in([], [1, 1, 1], "a"), to.be([nil, [nil, [nil, "a"]]]))
      ),
    
      it("empty_dict", () -> 
        expect(put_in({}, ["a"], "foo"), to.be({:a "foo"}))
      ),
    
      it("nested_empty_dict", () -> 
        expect(put_in({:a {}}, [:a, :a], "foo"), to.be({:a {:a "foo"}}))
      ),
    
      it("overwrite_dict", () -> 
        expect(put_in({:a "foo", :b "bar"}, [:a], "changed"), to.be({:a "changed", :b "bar"}))
      ),
    
      it("nested_overwrite_dict", () -> 
        expect(put_in({:a {:a "foo", :b "bar"}}, [:a, :a], "changed"), to.be({:a {:a "changed", :b "bar"}}))
      ),
    
      it("extend_dict", () -> 
        expect(put_in({:a "foo", :b "bar"}, [:c], "baz"), to.be({:a "foo", :b "bar", :c "baz"}))
      ),
    
      it("nest_dict", () -> 
        expect(put_in({:a "foo", :b "bar"}, [:c, :d], "baz"), to.be({:a "foo", :b "bar", :c {:d "baz"}}))
      ),
    
      it("nested_extend_dict", () -> 
        expect(
          put_in({:a {:a "foo", :b "bar"}}, [:a, :c], "baz"),
          to.be({:a {:a "foo", :b "bar", :c "baz"}})
        )
      ),
    
      it("of_nil", () -> 
        expect(put_in(nil, [0], "a"), to.be_nil())
      ),
    
      it("invalid_collection", () -> 
        expect_error(
          () -> put_in("foo", [1], 0),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("nested_invalid_collection", () -> 
        expect_error(
          () -> put_in(["foo"], [0, 1], 0),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("nil_key", () -> 
        expect_error(
          () -> put_in({}, [nil], 0),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("bad_key_for_dict", () -> 
        expect_error(
          () -> put_in({}, [[]], 0),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("nested_bad_key_for_dict", () -> 
        expect_error(
          () -> put_in({:a {}}, [:a, []], 0),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("bad_key_for_list", () -> 
        expect_error(
          () -> put_in([], [2019-01-01T], 0),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("nested_bad_key_for_list", () -> 
        expect_error(
          () -> put_in([[]], [0, 2019-01-01T], 0),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("neg_key_for_list", () -> 
        expect_error(
          () -> put_in([], [-1], 0),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),
    
      it("nested_neg_key_for_list", () -> 
        expect_error(
          () -> put_in([[]], [0, -1], 0),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),
    ]);
}