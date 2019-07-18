import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.take as take;

library spec {
  spec:
    describe("data.take", [

      it("take_0_of_empty", () -> 
        expect(take(0, []), to.be([]))
      ),
    
      it("take_1_of_empty", () -> 
        expect(take(1, []), to.be([]))
      ),
    
      it("take_2_of_empty", () -> 
        expect(take(2, []), to.be([]))
      ),
    
      it("take_neg_1_of_empty", () -> 
        expect(take(-1, []), to.be([]))
      ),
    
      it("take_0_of_one", () -> 
        expect(take(0, [1]), to.be([]))
      ),
    
      it("take_1_of_one", () -> 
        expect(take(1, [1]), to.be([1]))
      ),
    
      it("take_2_of_one", () -> 
        expect(take(2, [1]), to.be([1]))
      ),
    
      it("take_neg_1_of_one", () -> 
        expect(take(-1, [1]), to.be([]))
      ),
    
      it("take_0_of_some", () -> 
        expect(take(0, [1, 2, 3]), to.be([]))
      ),
    
      it("take_1_of_some", () -> 
        expect(take(1, [1, 2, 3]), to.be([1]))
      ),
    
      it("take_2_of_some", () -> 
        expect(take(2, [1, 2, 3]), to.be([1, 2]))
      ),
    
      it("take_neg_1_of_some", () -> 
        expect(take(-1, [1, 2, 3]), to.be([]))
      ),
    
      it("of_default", () -> 
        expect(take(), to.be_nil())
      ),
    
      it("nil_items", () -> 
        expect(take(nil, []), to.be_nil())
      ),
    
      it("from_nil", () -> 
        expect(take(0, nil), to.be_nil())
      ),

  ]);
}