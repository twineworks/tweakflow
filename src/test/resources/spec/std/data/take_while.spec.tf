import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.take_while as take_while;


library spec {
  spec:
    describe("data.take_while", [

      it("take_everything_from_some", () -> 
        expect(take_while((_) -> true, [1, 2, 3]), to.be([1, 2, 3]))
      ),
    
      it("take_nothing_from_some", () -> 
        expect(take_while((_) -> false, [1, 2, 3]), to.be([]))
      ),
    
      it("take_one_from_some", () -> 
        expect(take_while((x) -> x <= 1, [1, 2, 3]), to.be([1]))
      ),
    
      it("take_some_from_some", () -> 
        expect(take_while((x) -> x <= 2, [1, 2, 3]), to.be([1, 2]))
      ),
    
      it("take_some_from_some_with_cast", () -> 
        expect(take_while((x) -> if x <= 2 then "yay" else "", [1, 2, 3]), to.be([1, 2]))
      ),
    
      it("take_indexed_one_from_some", () -> 
        expect(take_while((_, i) -> i <= 0, [1, 2, 3]), to.be([1]))
      ),
    
      it("take_indexed_some_from_some", () -> 
        expect(take_while((_, i) -> i <= 1, [1, 2, 3]), to.be([1, 2]))
      ),
    
      it("take_indexed_some_from_some_with_cast", () -> 
        expect(take_while((_, i) -> if i <= 1 then "yay" else "", [1, 2, 3]), to.be([1, 2]))
      ),
    
      it("of_default", () -> 
        expect(take_while(nil, nil), to.be_nil())
      ),
    
      it("from_nil", () -> 
        expect(take_while((_) -> true, nil), to.be_nil())
      ),
    
      it("nil_predicate", () -> 
        expect_error(
          () -> take_while(nil, ["foo"]),
          to.have_code("NIL_ERROR")
        )
      ),
    
      it("bad_predicate", () -> 
        expect_error(
          () -> take_while(() -> true, ["foo"]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}