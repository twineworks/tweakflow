import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.values as values;


library spec {
  spec:
    describe("data.values", [

      it("empty_list", () -> 
        expect(values([]), to.be([]))
      ),
    
      it("simple_list", () -> 
        expect(values([1,nil,3]), to.be([1, nil, 3]))
      ),
    
      it("empty_dict", () -> 
        expect(values({}), to.be([]))
      ),
    
      it("simple_dict", () -> 
        expect(values({:a 1, :b 2}), to.be_permutation_of([1, 2]))
      ),
    
      it("medium_dict", () -> 
        expect(
          values({:alpha 1, :beta 2, :gamma 3, :delta 4, :epsilon 5, :eta 6, :theta 7}),
          to.be_permutation_of([1, 2, 3, 4, 5, 6, 7])
        )
      ),
    
      it("of_nil", () -> 
        expect(values(nil), to.be_nil())
      ),
    
      it("invalid_type", () -> 
        expect_error(
          () -> values("foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
  ]);
}