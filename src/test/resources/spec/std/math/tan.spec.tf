import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.tan as tan;

library spec {
  spec:
    describe("math.tan", [
    
      it("of_default", () -> 
        expect(tan(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(tan(nil), to.be_nil())
      ),
    
      it("of_zero", () -> 
        expect(tan(0.0), to.be(0.0))
      ),
    
      it("of_NaN", () -> 
        expect(tan(NaN), to.be_NaN())
      ),
    
      it("of_Infinity", () -> 
        expect(tan(Infinity), to.be_NaN())
      ),
    
      it("of_neg_Infinity", () -> 
        expect(tan(-Infinity), to.be_NaN())
      ),
    
      it("of_one", () -> 
        expect(tan(1.0), to.be_close_to(1.5574077246549023))
      ),
    
      it("of_neg_one", () -> 
        expect(tan(-1.0), to.be_close_to(-1.5574077246549023))
      ),
    
      it("of_half_pi", () -> 
        expect(tan(m.pi/2), to.be_greater_than(1E16))
      ),
    
      it("of_neg_half_pi", () -> 
        expect(tan(-m.pi/2), to.be_less_than(-1E16))
      ),
    
      it("of_pi", () -> 
        expect(tan(m.pi), to.be_close_to(0.0))
      ),
    
      it("of_neg_pi", () -> 
        expect(tan(-m.pi), to.be_close_to(0.0))
      ),


  ]);
}