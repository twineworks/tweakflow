import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.log10 as log10;

library spec {
  spec:
    describe("math.log10", [

      it("of_default", () -> 
        expect(log10(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(log10(nil), to.be_nil())
      ),
    
      it("of_zero", () -> 
        expect(log10(0.0), to.be(-Infinity))
      ),
    
      it("of_NaN", () -> 
        expect(log10(NaN), to.be_NaN())
      ),
    
      it("of_neg", () -> 
        expect(log10(-1.0), to.be_NaN())
      ),
    
      it("of_Infinity", () -> 
        expect(log10(Infinity), to.be(Infinity))
      ),
    
      it("of_one", () -> 
        expect(log10(1), to.be_close_to(0))
      ),
    
      it("of_10_pow_2", () -> 
        expect(log10(10**2), to.be_close_to(2.0))
      ),
    
      it("of_10_pow_10", () -> 
        expect(log10(10**10), to.be_close_to(10.0))
      ),

  ]);
}