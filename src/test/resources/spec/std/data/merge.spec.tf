import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.merge as merge;

library spec {
  spec:
    describe("data.merge", [

      it("empty", () -> 
        expect(merge([]), to.be({}))
      ),
    
      it("single", () -> 
        expect(merge([{:a 1, :b 2}]), to.be({:a 1, :b 2}))
      ),
    
      it("pair", () -> 
        expect(merge([{:a 1, :b 2}, {:c 3, :d 4}]), to.be({:a 1, :b 2, :c 3, :d 4}))
      ),
    
      it("pair_replace", () -> 
        expect(merge([{:a 1, :b 2}, {:c 3, :b "r", :d 4}]), to.be({:a 1, :b "r", :c 3, :d 4}))
      ),
    
      it("triple", () -> 
        expect(merge([{:a 1, :b 2}, {:c 3, :d 4}, {:e 5, :f 6}]), to.be({:a 1, :b 2, :c 3, :d 4, :e 5, :f 6}))
      ),
    
      it("triple_replace", () -> 
        expect(merge([{:a 1, :b 2}, {:c 3, :b "r", :d 4}, {:e 5, :b "win", :f 6}]), to.be({:a 1, :b "win", :c 3, :d 4, :e 5, :f 6}))
      ),
    
      it("of_nil", () -> 
        expect(merge(nil), to.be_nil())
      ),
    
      it("of_nil_element", () -> 
        expect(merge([{:a 1, :b 2}, nil]), to.be_nil())
      ),
    
      it("invalid_element_type", () -> 
        expect_error(
          () -> merge(["foo"]),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),

  ]);
}