import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.none? as none?;

library spec {
  spec:
    describe("data.none", [
    
      it("empty_list", () -> 
        expect(none?([], (_) -> true), to.be_true())
      ),
    
      it("not_found", () -> 
        expect(none?([1,2,3], (x) -> x > 10), to.be_true())
      ),
    
      it("not_found_with_cast", () -> 
        expect(none?([1,2,3], (x) -> if x > 10 then "yes" else nil), to.be_true())
      ),
    
      it("not_found_with_index", () -> 
        expect(none?([1,2,3], (x, i) -> i > 10), to.be_true())
      ),
    
      it("not_found_with_default_3rd_param", () -> 
        expect(none?([1,2,3], (x, i, a="foo") -> i > 10), to.be_true())
      ),
    
      it("found", () -> 
        expect(none?([1,2,3], (x) -> x == 2), to.be_false())
      ),
    
      it("found_with_cast", () -> 
        expect(none?([1,2,3], (x) -> if x == 2 "foo" else ""), to.be_false())
      ),
    
      it("found_with_index", () -> 
        expect(none?([1,2,3], (x, i) -> i == 2), to.be_false())
      ),
    
      it("found_with_default_3rd_param", () -> 
        expect(none?([1,2,3], (x, i, a="foo") -> a == "foo" && i == 2), to.be_false())
      ),
    
      it("found_first", () -> 
        expect(none?([1,2,3], (x) -> x <= 2), to.be_false())
      ),
    
      it("found_last", () -> 
        expect(none?([1,2,3], (x) -> x == 3), to.be_false())
      ),
    
      it("of_nil", () -> 
        expect(none?(nil), to.be_nil())
      ),
    
      it("of_nil_p_nil", () -> 
        expect(none?(nil, nil), to.be_nil())
      ),
    
      it("of_only_p_nil", () -> 
        expect_error(
          () -> none?([], nil),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("of_invalid_p_too_few_args", () -> 
        expect_error(
          () -> none?([], () -> true), # p should accept 1 or 2 args
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}