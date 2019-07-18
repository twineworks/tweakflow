import fun, data from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias fun.while as while;

library spec {
  spec:
    describe("fun.while", [

      it("of_0_times", () ->
        let {
          p: (_) -> false;
          x: 0;
          f: (x) -> x+1;
        }
        expect(while(p, x, f), to.be(0))
      ),
    
      it("of_1_times", () ->
        let {
          p: (x) -> x < 1;
          x: 0;
          f: (x) -> x+1;
        }
        expect(while(p, x, f), to.be(1))
      ),
    
      it("of_1000_times_with_cast", () ->
        let {
          p: (x) -> if x < 1000 then true else nil;
          x: 0;
          f: (x) -> x+1;
        }
        expect(while(p, x, f), to.be(1000))
      ),
    
      it("of_f_nil", () -> 
        expect_error(
          () -> while((_) -> false, 0, nil),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("of_bad_f", () -> 
        expect_error(
          () -> while((_) -> false, 0, () -> true),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    
      it("of_p_nil", () -> 
        expect_error(
          () -> while(nil, 0, (_) -> true),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("of_bad_p", () -> 
        expect_error(
          () -> while(() -> false, 0, (_) -> true),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}