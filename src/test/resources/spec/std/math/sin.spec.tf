import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.sin as sin;

library spec {
  spec:
    describe("math.sin", [
    
      it("of_default", () -> 
        expect(sin(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(sin(nil), to.be_nil())
      ),
    
      it("of_zero", () -> 
        expect(sin(0.0), to.be(0.0))
      ),
    
      it("of_neg_zero", () -> 
        expect(sin(-0.0), to.be(-0.0))
      ),
    
      it("of_NaN", () -> 
        expect(sin(NaN), to.be_NaN())
      ),
    
      it("of_Infinity", () -> 
        expect(sin(Infinity), to.be_NaN())
      ),
    
      it("of_neg_Infinity", () -> 
        expect(sin(-Infinity), to.be_NaN())
      ),
    
      it("of_one", () -> 
        expect(sin(1.0), to.be_close_to(0.8414709848078965))
      ),
    
      it("of_neg_one", () -> 
        expect(sin(-1.0), to.be_close_to(-0.8414709848078965))
      ),
    
      it("of_half_pi", () -> 
        expect(sin(m.pi/2), to.be_close_to(1.0))
      ),
    
      it("of_neg_half_pi", () -> 
        expect(sin(-m.pi/2), to.be_close_to(-1.0))
      ),
    
      it("of_pi", () -> 
        expect(sin(m.pi), to.be_close_to(0.0))
      ),
    
      it("of_neg_pi", () -> 
        expect(sin(-m.pi), to.be_close_to(0.0))
      ),
    
      it("of_two_pi", () -> 
        expect(sin(2*m.pi), to.be_close_to(0.0))
      ),
    
      it("of_neg_two_pi", () -> 
        expect(sin(-2*m.pi), to.be_close_to(0.0))
      ),

  ]);
}