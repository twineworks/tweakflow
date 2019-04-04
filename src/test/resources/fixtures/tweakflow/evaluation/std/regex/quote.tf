import regex from "std";
import expect, expect_error, to from "std/assert.tf";

alias regex.quote as quote;
alias regex.splitting as splitting;

library p {
  unquoted_split: splitting('\b');        # splits on word boundary
  quoted_split: splitting(quote('\b'));   # splits on string '\b'
}

library scanning_spec {

  quoted:
    expect(
      p.quoted_split('a\bb\bc'),
      to.be(["a", "b", "c"])
    );

  unquoted:
    expect(
      p.unquoted_split('a\bb\bc'),
      to.be(['a', '\', 'bb', '\', 'bc'])
    );

  of_nil:
    expect(
      quote(nil),
      to.be(nil)
    );

}