import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.repeat as repeat;


library spec {
  spec:
    describe("data.repeat", [
    
      it("empty", () -> 
        expect(repeat(0, "a"), to.be([]))
      ),
    
      it("singleton", () -> 
        expect(repeat(1, "a"), to.be(["a"]))
      ),
    
      it("some", () -> 
        expect(repeat(5, "a"), to.be(["a", "a", "a", "a", "a"]))
      ),
    
      it("of_default", () -> 
        expect(repeat(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(repeat(nil, "a"), to.be_nil())
      ),
    
      it("invalid_type", () -> 
        expect_error(
          () -> repeat(-1, "foo"),
          to.have_code("INDEX_OUT_OF_BOUNDS")
        )
      ),
  ]);
}