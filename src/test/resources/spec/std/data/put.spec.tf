import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.put as put;


library spec {
  spec:
    describe("data.put", [

      it("empty_list", () -> 
        expect(put([], 0, "a"), to.be(["a"]))
      ),
    
      it("append_list", () -> 
        expect(put([1,2,3], 3, "a"), to.be([1, 2, 3, "a"]))
      ),
    
      it("overwrite_list", () -> 
        expect(put([1,2,3], 0, "a"), to.be(["a", 2, 3]))
      ),
    
      it("extend_list", () -> 
        expect(put([1,2,3], 5, "a"), to.be([1, 2, 3, nil, nil, "a"]))
      ),
    
      it("empty_dict", () -> 
        expect(put({}, "a", "foo"), to.be({:a "foo"}))
      ),
    
      it("overwrite_dict", () -> 
        expect(put({:a "foo", :b "bar"}, :a, "changed"), to.be({:a "changed", :b "bar"}))
      ),
    
      it("extend_dict", () -> 
        expect(put({:a "foo", :b "bar"}, :c, "baz"), to.be({:a "foo", :b "bar", :c "baz"}))
      ),
    
      it("of_nil", () -> 
        expect(put(nil, 0, "a"), to.be_nil())
      ),
    
      it("invalid_collection", () -> 
        expect_error(
          () -> put("foo", 1, 0),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("nil_key", () -> 
        expect_error(
          () -> put({}, nil, 0),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("bad_key_for_dict", () -> 
        expect_error(
          () -> put({}, [], 0),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("bad_key_for_list", () -> 
        expect_error(
          () -> put([], 2019-01-01T, 0),
          to.have_code("CAST_ERROR")
        )
      ),
    
      it("neg_key_for_list", () -> 
        expect_error(
          () -> put([], -1, 0),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),
    ]);
}