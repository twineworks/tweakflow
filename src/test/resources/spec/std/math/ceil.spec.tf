import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.ceil as ceil;

library spec {
  spec:
    describe("math.ceil", [

      it("of_default", () -> 
        expect(ceil(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(ceil(nil), to.be_nil())
      ),
    
      it("of_NaN", () -> 
        expect(ceil(NaN), to.be_NaN())
      ),
    
      it("of_zero", () -> 
        expect(ceil(0.0), to.be(0.0))
      ),
    
      it("of_neg_zero", () -> 
        expect(ceil(-0.0), to.be(-0.0))
      ),
    
      it("of_neg_small", () -> 
        expect(ceil(-0.2), to.be(-0.0))
      ),
    
      it("of_neg_one", () -> 
        expect(ceil(-1.0), to.be(-1.0))
      ),
    
      it("of_neg", () -> 
        expect(ceil(-2.6), to.be(-2.0))
      ),
    
      it("of_infinity", () -> 
        expect(ceil(Infinity), to.be(Infinity))
      ),
    
      it("of_neg_infinity", () -> 
        expect(ceil(-Infinity), to.be(-Infinity))
      ),
    
      it("of_pos", () -> 
        expect(ceil(2.3), to.be(3.0))
      ),
    
      it("of_pos_one", () -> 
        expect(ceil(1.0), to.be(1.0))
      ),

    ]);
}