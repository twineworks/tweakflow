import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.max as max;

library spec {
  spec:
    describe("math.max", [

      it("of_default", () -> 
        expect(max(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(max(nil), to.be_nil())
      ),
    
      it("of_empty", () -> 
        expect(max([]), to.be_nil())
      ),
    
      it("of_only_NaN", () -> 
        expect(max([NaN]), to.be_nil())
      ),
    
      it("of_only_NaNs", () -> 
        expect(max([NaN, NaN]), to.be_nil())
      ),
    
      it("of_NaN_element", () -> 
        expect(max([1.0, NaN, 3.0d]), to.be_nil()) &&
        expect(max([1, NaN, 3d]), to.be_nil()) &&
        expect(max([1, 2.0, NaN, 3d]), to.be_nil()) &&
        expect(max([2.0, 1d, NaN, 3.0]), to.be_nil())
      ),
    
      it("of_nil_element", () -> 
        expect(max([0, 1d, 2, nil]), to.be_nil())
      ),
    
      it("of_longs", () -> 
        expect(max([1,2,3]), to.be(3)) &&
        expect(max([3,-2,1]), to.be(3)) &&
        expect(max([1,2,-3]), to.be(2))
      ),
    
      it("of_double", () -> 
        expect(max([1.0, 2.0, 3.0]), to.be(3.0)) &&
        expect(max([1.0, -2.0, 3.0]), to.be(3.0)) &&
        expect(max([1.0, 2.0, -3.0]), to.be(2.0))
      ),

      it("of_decimal", () ->
        expect(max([1d, 2d, 3d]), to.be(3d)) &&
        expect(max([1d, -2d, 3d]), to.be(3d)) &&
        expect(max([1d, 2d, -3d]), to.be(2d))
      ),
    
      it("of_mixed", () -> 
        expect(max([1, 2.0, 3d]), to.be(3d)) &&
        expect(max([1d, -2.0, 3]), to.be(3)) &&
        expect(max([1, 2.0, -3d]), to.be(2.0))
      ),

      it("of_infinities", () ->
        expect(max([1, 2, -Infinity, Infinity]), to.be(Infinity)) &&
        expect(max([1.0, 2.0, -Infinity, Infinity]), to.be(Infinity)) &&
        expect(max([1d, 2d, -Infinity, Infinity]), to.be(Infinity)) &&
        expect(max([-Infinity, Infinity, 1, 2]), to.be(Infinity)) &&
        expect(max([-Infinity, Infinity, 1.0, 2.0]), to.be(Infinity)) &&
        expect(max([-Infinity, Infinity, 1d, 2d]), to.be(Infinity))
      ),
    
      it("of_non_numeric", () -> 
        expect_error(
          () -> max([1, 2, "foo"]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}