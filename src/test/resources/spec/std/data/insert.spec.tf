import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.insert as insert;

library spec {
  spec:
    describe("data.insert", [

      it("empty_list_append", () -> 
        expect(insert([], 0, "a"), to.be(["a"]))
      ),
    
      it("empty_list_extend", () -> 
        expect(insert([], 2, "a"), to.be([nil, nil, "a"]))
      ),
    
      it("list_append", () -> 
        expect(insert([1, 2], 2, "a"), to.be([1, 2, "a"]))
      ),
    
      it("list_extend", () -> 
        expect(insert([1, 2], 4, "a"), to.be([1, 2, nil, nil, "a"]))
      ),
    
      it("list_prepend", () -> 
        expect(insert([1, 2], 0, "a"), to.be(["a", 1, 2]))
      ),
    
      it("list_shift", () -> 
        expect(insert([1, 2], 1, "a"), to.be([1, "a", 2]))
      ),
    
      it("of_nil", () -> 
        expect(insert(nil, 0, "a"), to.be_nil())
      ),
    
      it("of_nil_pos", () -> 
        expect_error(
          () -> insert([], nil, "a"),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("of_neg_pos", () -> 
        expect_error(
          () -> insert([], -1, "a"),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),
  ]);
}