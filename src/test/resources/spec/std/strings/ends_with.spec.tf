import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.ends_with? as ends_with?;

library spec {
  spec:
    describe("strings.ends_with", [
    
      it("of_default", () -> 
        expect(ends_with?(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(ends_with?(nil, nil), to.be_nil())
      ),
    
      it("of_tail_nil", () -> 
        expect(ends_with?("foo", nil), to.be_nil())
      ),
    
      it("of_x_nil", () -> 
        expect(ends_with?(nil, ""), to.be_nil())
      ),
    
      it("of_substring", () -> 
        expect(ends_with?("fgh", "g"), to.be_false())
      ),
    
      it("of_init", () -> 
        expect(ends_with?("foo", "fo"), to.be_false())
      ),
    
      it("of_tail", () -> 
        expect(ends_with?("foo", "oo"), to.be_true())
      ),
    
      it("of_same", () -> 
        expect(ends_with?("foo", "foo"), to.be_true())
      ),
    
      it("of_empty_tail", () -> 
        expect(ends_with?("foo", ""), to.be_true())
      ),
    
      it("of_empty_x_empty_tail", () -> 
        expect(ends_with?("", ""), to.be_true())
      ),
    
      it("of_empty_x_nonempty_tail", () -> 
        expect(ends_with?("", "x"), to.be_false())
      ),
    
      it("of_bmp", () -> 
        expect(ends_with?("你好", "好"), to.be_true())
      ),
    
      it("of_beyond_bmp", () -> 
        expect(ends_with?("foo 𝄞", "𝄞"), to.be_true())
      ),

  ]);
}