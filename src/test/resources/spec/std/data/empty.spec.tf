import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.empty? as empty?;


library spec {
  spec:
    describe("data.empty?", [
    
      it("empty_list", () -> 
        expect(empty?([]), to.be_true())
      ),
    
      it("simple_list", () -> 
        expect(empty?([1,2,3]), to.be_false())
      ),
    
      it("empty_dict", () -> 
        expect(empty?({}), to.be_true())
      ),
    
      it("simple_dict", () -> 
        expect(empty?({:a 1, :b 2}), to.be_false())
      ),
    
      it("of_nil", () -> 
        expect(empty?(nil), to.be_nil())
      ),
    
      it("invalid_type", () -> 
        expect_error(
          () -> empty?("foo"),
          to.have_code("ILLEGAL_ARGUMENT")
        )
      ),
    ]);
}