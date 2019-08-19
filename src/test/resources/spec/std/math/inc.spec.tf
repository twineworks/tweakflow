import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.inc as inc;

library spec {
  spec:
    describe("math.inc", [

      it("of_default", () -> 
        expect(inc(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(inc(nil), to.be_nil())
      ),
    
      it("of_zero_long", () -> 
        expect(inc(0), to.be(1))
      ),

      it("of_zero_decimal", () ->
        expect(inc(0d), to.be(1d))
      ),
    
      it("of_NaN", () -> 
        expect(inc(NaN), to.be_NaN())
      ),
    
      it("of_one_long", () -> 
        expect(inc(1), to.be(2))
      ),
    
      it("of_neg_one_long", () -> 
        expect(inc(-1), to.be(0))
      ),

      it("of_one_decimal", () ->
        expect(inc(1d), to.be(2d))
      ),

      it("of_fraction_decimal", () ->
        expect(inc(1.234d), to.be(2.234d))
      ),

      it("of_neg_one_decimal", () ->
        expect(inc(-1d), to.be(0d))
      ),

      it("of_neg_fraction_decimal", () ->
        expect(inc(-1.234d), to.be(-0.234d))
      ),
    
      it("of_zero_double", () -> 
        expect(inc(0.0), to.be(1.0))
      ),
    
      it("of_one_double", () -> 
        expect(inc(1.0), to.be(2.0))
      ),
    
      it("of_neg_one_double", () -> 
        expect(inc(-1.0), to.be(0.0))
      ),
    
      it("of_max_long", () -> 
        expect(inc(m.max_long), to.be(m.min_long))
      ),
    
      it("of_max_long_as_double", () -> 
        expect(inc(m.max_long as double), to.be(m.max_long as double + 1.0))
      ),
    
      it("of_non_numeric", () -> 
        expect_error(
          () -> inc("foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}