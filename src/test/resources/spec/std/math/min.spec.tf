import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.min as min;

library spec {
  spec:
    describe("math.min", [

      it("of_default", () -> 
        expect(min(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(min(nil), to.be_nil())
      ),
    
      it("of_empty", () -> 
        expect(min([]), to.be_nil())
      ),
    
      it("of_only_NaN", () -> 
        expect(min([NaN]), to.be_nil())
      ),
    
      it("of_only_NaNs", () -> 
        expect(min([NaN, NaN]), to.be_nil())
      ),
    
      it("of_NaN_element", () -> 
        expect(min([1.0, NaN, 3.0]), to.be_nil()) &&
        expect(min([1, NaN, 3d]), to.be_nil()) &&
        expect(min([1, 2.0, NaN, 3d]), to.be_nil()) &&
        expect(min([2.0, 1d, NaN, 3.0]), to.be_nil())
      ),
    
      it("of_nil_element", () -> 
        expect(min([0, 1, 2, nil]), to.be_nil())
      ),
    
      it("of_longs", () -> 
        expect(min([1,2,3]), to.be(1)) &&
        expect(min([1,-2,3]), to.be(-2)) &&
        expect(min([1,2,-3]), to.be(-3))
      ),
    
      it("of_double", () -> 
        expect(min([1.0, 2.0, 3.0]), to.be(1.0)) &&
        expect(min([1.0, -2.0, 3.0]), to.be(-2.0)) &&
        expect(min([1.0, 2.0, -3.0]), to.be(-3.0))
      ),

      it("of_decimal", () ->
        expect(min([1d, 2d, 3d]), to.be(1d)) &&
        expect(min([1d, -2d, 3d]), to.be(-2d)) &&
        expect(min([1d, 2d, -3d]), to.be(-3d))
      ),
    
      it("of_mixed", () -> 
        expect(min([1, 2.0, 3d]), to.be(1)) &&
        expect(min([1.0, -2d, 3]), to.be(-2d)) &&
        expect(min([1d, 2.0, -3.0]), to.be(-3.0))
      ),

      it("of_infinities", () ->
        expect(min([1, 2, -Infinity, Infinity]), to.be(-Infinity)) &&
        expect(min([1.0, 2.0, -Infinity, Infinity]), to.be(-Infinity)) &&
        expect(min([1d, 2d, -Infinity, Infinity]), to.be(-Infinity)) &&
        expect(min([-Infinity, Infinity, 1, 2]), to.be(-Infinity)) &&
        expect(min([-Infinity, Infinity, 1.0, 2.0]), to.be(-Infinity)) &&
        expect(min([-Infinity, Infinity, 1d, 2d]), to.be(-Infinity))
      ),
    
      it("of_non_numeric", () -> 
        expect_error(
          () -> min([1, 2, "foo"]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}