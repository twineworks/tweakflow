import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.dec as dec;

library spec {
  spec:
    describe("math.dec", [

      it("of_default", () -> 
        expect(dec(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(dec(nil), to.be_nil())
      ),
    
      it("of_zero_long", () -> 
        expect(dec(0), to.be(-1))
      ),
    
      it("of_NaN", () -> 
        expect(dec(NaN), to.be_NaN())
      ),
    
      it("of_one_long", () -> 
        expect(dec(1), to.be(0))
      ),
    
      it("of_neg_one_long", () -> 
        expect(dec(-1), to.be(-2))
      ),
    
      it("of_zero_double", () -> 
        expect(dec(0.0), to.be(-1.0))
      ),
    
      it("of_one_double", () -> 
        expect(dec(1.0), to.be(0.0))
      ),
    
      it("of_neg_one_double", () -> 
        expect(dec(-1.0), to.be(-2.0))
      ),
    
      it("of_min_long", () -> 
        expect(dec(m.min_long), to.be(m.max_long))
      ),
    
      it("of_min_long_as_double", () -> 
        expect(dec(m.min_long as double), to.be(m.min_long as double - 1.0))
      ),
    
      it("of_non_numeric", () -> 
        expect_error(
          () -> dec("foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}