import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.entries as entries;


library spec {
  spec:
    describe("data.entries", [

      it("empty_dict", () -> 
        expect(entries({}), to.be([]))
      ),
    
      it("simple_dict", () -> 
        expect(entries({:a 1, :b 2}), to.be_permutation_of([{:key "a", :value 1}, {:key "b", :value 2}]))
      ),
    
      it("of_nil", () -> 
        expect(entries(nil), to.be_nil())
      ),

    ]);
}