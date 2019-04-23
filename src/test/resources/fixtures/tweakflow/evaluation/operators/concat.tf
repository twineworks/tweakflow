import expect, expect_error, to from "std/assert.tf";

library lib {
  string a: "alice";
  string b: "bob";
}

library concat_spec {

  simple:
    expect("a".."b", to.be("ab"));

  refs:
    expect(lib.a.." and "..lib.b, to.be("alice and bob"));

  cast:
   expect(1..2..3..4..5..6..7..8..9, to.be("123456789"));

  mixed:
    expect(lib.a..2..3..4..5..6..7..8..lib.b, to.be("alice2345678bob"));

  err:
    expect_error(
      () -> lib.a..(1//0)..lib.b
      to.have_code("DIVISION_BY_ZERO")
    );

}