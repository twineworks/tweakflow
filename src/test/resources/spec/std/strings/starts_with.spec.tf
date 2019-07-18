import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.starts_with? as starts_with?;

library spec {
  spec:
    describe("strings.starts_with", [

      it("of_default", () -> 
        expect(starts_with?(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(starts_with?(nil, nil), to.be_nil())
      ),
    
      it("of_init_nil", () -> 
        expect(starts_with?("foo", nil), to.be_nil())
      ),
    
      it("of_x_nil", () -> 
        expect(starts_with?(nil, ""), to.be_nil())
      ),
    
      it("of_substring", () -> 
        expect(starts_with?("foo", "o"), to.be_false())
      ),
    
      it("of_init", () -> 
        expect(starts_with?("foo", "fo"), to.be_true())
      ),
    
      it("of_same", () -> 
        expect(starts_with?("foo", "foo"), to.be_true())
      ),
    
      it("of_empty_init", () -> 
        expect(starts_with?("foo", ""), to.be_true())
      ),
    
      it("of_empty_x_empty_init", () -> 
        expect(starts_with?("", ""), to.be_true())
      ),
    
      it("of_empty_x_nonempty_init", () -> 
        expect(starts_with?("", "x"), to.be_false())
      ),
    
      it("of_bmp", () -> 
        expect(starts_with?("你好", "你"), to.be_true())
      ),
    
      it("of_beyond_bmp", () -> 
        expect(starts_with?("𝄞 music", "𝄞"), to.be_true())
      ),
  ]);
}