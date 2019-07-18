import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.slice as slice;

library spec {
  spec:
    describe("data.slice", [

      it("empty_slice_of_empty", () -> 
        expect(slice([], 0, 0), to.be([]))
      ),
    
      it("empty_slice_of_some", () -> 
        expect(slice([1, 2], 0, 0), to.be([]))
      ),
    
      it("empty_slice_of_some_more", () -> 
        expect(slice([1, 2], 1, 0), to.be([]))
      ),
    
      it("whole_slice", () -> 
        expect(slice([1, 2, 3], 0), to.be([1, 2, 3]))
      ),
    
      it("head_slice", () -> 
        expect(slice([1, 2, 3], 0, 1), to.be([1]))
      ),
    
      it("init_slice", () -> 
        expect(slice([1, 2, 3], 0, 2), to.be([1, 2]))
      ),
    
      it("mid_slice", () -> 
        expect(slice([1, 2, 3, 4], 1, 3), to.be([2, 3]))
      ),
    
      it("mid_empty_slice", () -> 
        expect(slice([1, 2, 3], 1, 1), to.be([]))
      ),
    
      it("tail_slice", () -> 
        expect(slice([1, 2, 3], 1, 3), to.be([2, 3]))
      ),
    
      it("last_slice", () -> 
        expect(slice([1, 2, 3], 2, nil), to.be([3]))
      ),
    
      it("overextended_slice", () -> 
        expect(slice([1, 2, 3], 1, 100), to.be([2, 3]))
      ),
    
      it("end_before_start_slice", () -> 
        expect(slice([1, 2, 3], 2, 1), to.be([]))
      ),
    
      it("start_post_size_slice", () -> 
        expect(slice([1, 2, 3], 4), to.be([]))
      ),
    
      it("of_default", () -> 
        expect(slice(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(slice(nil, nil, nil), to.be_nil())
      ),
    
      it("of_nil_end", () -> 
        expect(slice(nil, 0, nil), to.be_nil())
      ),
    
      it("of_nil_start", () -> 
        expect_error(
          () -> slice([], nil),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("of_neg_start", () -> 
        expect_error(
          () -> slice([], -1),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),

  ]);
}