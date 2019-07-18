import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.range as range;

library spec {
  spec:
    describe("data.range", [

      it("empty_range", () -> 
        expect(range(1, 0), to.be([]))
      ),
    
      it("singleton_range", () -> 
        expect(range(1, 1), to.be([1]))
      ),
    
      it("simple_range", () -> 
        expect(range(1, 3), to.be([1,2,3]))
      ),
    
      it("neg_pos_range", () -> 
        expect(range(-2, 2), to.be([-2, -1, 0, 1, 2]))
      ),
    
      it("of_default", () -> 
        expect(range(), to.be([0]))
      ),
    
      it("of_nil", () -> 
        expect(range(nil, nil), to.be_nil())
      ),
    
      it("of_nil_start", () -> 
        expect(range(nil, 0), to.be_nil())
      ),
    
      it("of_nil_end", () -> 
        expect(range(0, nil), to.be_nil())
      ),

  ]);
}