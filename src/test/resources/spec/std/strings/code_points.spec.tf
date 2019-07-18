import strings as s from 'std.tf';
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.code_points as code_points;

library spec {
  spec:
    describe("strings.code_points", [

      it("of_default", () -> 
        expect(code_points(), to.be_nil())
      ),
    
      it("of_nil", () -> 
        expect(code_points(nil), to.be_nil())
      ),
    
      it("simple", () -> 
        expect(code_points("foo"), to.be([102, 111, 111]))
      ),
    
      it("of_empty", () -> 
        expect(code_points(""), to.be([]))
      ),
    
      it("of_code_points", () -> 
        expect(code_points("你好"), to.be([0x4F60, 0x597D]))
      ),
    
      it("of_code_points_beyond_bmp", () -> 
        expect(code_points("𝄞你好𝄞"), to.be([0x01D11E, 0x4F60, 0x597D, 0x01D11E]))
      ),

  ]);
}