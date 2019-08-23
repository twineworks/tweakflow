import math as m from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias m.finite? as f;

library spec {
  spec:
    describe("math.finite?", [

      it("default -> false", () ->
        expect(f(), to.be_false())
      ),
    
      it("nil -> false", () ->
        expect(f(nil), to.be_false())
      ),
    
      it("zero_long -> true", () ->
        expect(f(0), to.be_true())
      ),

      it("zero_decimal -> true", () ->
        expect(f(0d), to.be_true())
      ),

      it("zero_double", () ->
        expect(f(0.0), to.be_true())
      ),
    
      it("NaN -> false", () ->
        expect(f(NaN), to.be_false())
      ),

      it("Infinity -> false", () ->
        expect(f(Infinity), to.be_false())
      ),

      it("-Infinity -> false", () ->
        expect(f(Infinity), to.be_false())
      ),

      it("max_long -> true", () ->
        expect(f(m.max_long), to.be_true())
      ),

      it("non_numeric -> error", () ->
        expect_error(
          () -> f("foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}