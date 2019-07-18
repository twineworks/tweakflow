import fun, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias fun.times as times;

library spec {
  spec:
    describe("fun.times", [
    
      it("of_n_nil", () -> 
        expect(times(nil, 'foo', (x) -> "bar"), to.be_nil())
      ),
    
      it("of_n_zero", () -> 
        expect(times(0, 'foo', (x) -> "bar"), to.be("foo"))
      ),
    
      it("of_n_1", () -> 
        expect(times(1, 0, (x) -> x+1), to.be(1))
      ),
    
      it("of_n_100", () -> 
        expect(times(100, 0, (x) -> x+1), to.be(100))
      ),
    
      it("of_f_nil", () -> 
        expect_error(
          () -> times(1, 0, nil),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("of_neg_n", () -> 
        expect_error(
          () -> times(-1, 0, (x) -> true),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_bad_f", () -> 
        expect_error(
          () -> times(1, 0, () -> true),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_all_nil", () -> 
        expect_error(
          () -> times(),
          to.have_code("NIL_ERROR")
        )
      ),

  ]);
}