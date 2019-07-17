import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.flatten as flatten;


library spec {
  spec:
    describe("data.flatten", [

      it("empty", () -> 
        expect(flatten([]), to.be([]))
      ),
    
      it("simple_list", () -> 
        expect(flatten([1,2,3,nil]), to.be([1,2,3,nil]))
      ),
    
      it("nested_lists", () -> 
        expect(flatten([[1],["foo"],[nil],[1,{},[]]]), to.be([1,"foo",nil,1,{},[]]))
      ),
    
      it("nested_empty_lists", () -> 
        expect(flatten([[],[],[],[]]), to.be([]))
      ),
    
      it("of_default", () -> 
        expect(flatten(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(flatten(nil), to.be_nil())
      ),

  ]);
}