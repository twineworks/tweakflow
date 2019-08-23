import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.NaN? as f;

library spec {
  spec:
    describe("math.NaN?", [

      it("of_default", () -> 
        expect(f(), to.be_false())
      ),
    
      it("of_nil", () -> 
        expect(f(nil), to.be_false())
      ),
    
      it("of_zero_long", () -> 
        expect(f(0), to.be_false())
      ),

      it("of_zero_decimal", () ->
        expect(f(0d), to.be_false())
      ),
    
      it("of_NaN", () -> 
        expect(f(NaN), to.be_true())
      ),


      it("of_zero_double", () -> 
        expect(f(0.0), to.be_false())
      ),

      it("of_max_long", () -> 
        expect(f(m.max_long), to.be_false())
      ),

      it("of_non_numeric", () ->
        expect(f("foo"), to.be_false())
      ),

  ]);
}