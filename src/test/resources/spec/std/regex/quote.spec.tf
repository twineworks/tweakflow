import regex from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias regex.quote as quote;
alias regex.splitting as splitting;

library p {
  unquoted_split: splitting('\b');        # splits on word boundary
  quoted_split: splitting(quote('\b'));   # splits on string '\b'
}

library spec {
  spec:
    describe("regex.scanning", [

      it("quoted", () -> 
        expect(
          p.quoted_split('a\bb\bc'),
          to.be(["a", "b", "c"])
        )
      ),
    
      it("unquoted", () -> 
        expect(
          p.unquoted_split('a\bb\bc'),
          to.be(['a', '\', 'bb', '\', 'bc'])
        )
      ),
    
      it("of_nil", () -> 
        expect(
          quote(nil),
          to.be(nil)
        )
      ),

  ]);
}