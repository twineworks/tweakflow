import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.factorial as f;

library spec {
  spec:
    describe("math.factorial", [

      it("of_default", () -> 
        expect(f(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(f(nil), to.be_nil())
      ),
    
      it("of_zero", () ->
        expect(f(0), to.be(1d))
      ),

      # NaN casts to 0
      it("of_NaN", () ->
        expect(f(NaN), to.be(1d))
      ),

      it("of_one", () ->
        expect(f(1), to.be(1d))
      ),

      it("of_two", () ->
        expect(f(2), to.be(2d))
      ),

      it("of_ten", () ->
        expect(f(10), to.be(3628800d))
      ),

      it("of_twenty_five", () ->
        expect(f(25), to.be(15511210043330985984000000d))
      ),

      it("of_one_hundred", () ->
        expect(f(100), to.be(93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000d))
      ),

      it("of_negative_one", () ->
        expect_error(
          () -> f(-1),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      )

  ]);
}