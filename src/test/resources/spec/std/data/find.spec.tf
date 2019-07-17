import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.find as find;

library spec {
  spec:
    describe("data.find", [

      it("empty_list", () -> 
        expect(find([], (_) -> true), to.be_nil())
      ),
    
      it("not_found", () -> 
        expect(find([1,2,3], (x) -> x > 10), to.be_nil())
      ),
    
      it("not_found_with_cast", () -> 
        expect(find([1,2,3], (x) -> if x > 10 then "yes" else nil), to.be_nil())
      ),
    
      it("not_found_with_index", () -> 
        expect(find([1,2,3], (x, i) -> i > 10), to.be_nil())
      ),
    
      it("not_found_with_default_3rd_param", () -> 
        expect(find([1,2,3], (x, i, a="foo") -> i > 10), to.be_nil())
      ),
    
      it("found", () -> 
        expect(find([1,2,3], (x) -> x == 2), to.be(2))
      ),
    
      it("found_with_cast", () -> 
        expect(find([1,2,3], (x) -> if x == 2 "foo" else ""), to.be(2))
      ),
    
      it("found_with_index", () -> 
        expect(find([1,2,3], (x, i) -> i == 2), to.be(3))
      ),
    
      it("found_with_default_3rd_param", () -> 
        expect(find([1,2,3], (x, i, a="foo") -> a == "foo" && i == 2), to.be(3))
      ),
    
      it("found_first", () -> 
        expect(find([1,2,3], (x) -> x <= 2), to.be(1))
      ),
    
      it("found_last", () -> 
        expect(find([1,2,3], (x) -> x == 3), to.be(3))
      ),
    
      it("of_nil", () -> 
        expect(find(nil), to.be_nil())
      ),
    
      it("of_nil_p_nil", () -> 
        expect(find(nil, nil), to.be_nil())
      ),
    
      it("of_only_p_nil", () -> 
        expect_error(
          () -> find([], nil),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("of_invalid_p_too_few_args", () -> 
        expect_error(
          () -> find([], () -> true), # p should accept 1 or 2 args
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

    ]);
}