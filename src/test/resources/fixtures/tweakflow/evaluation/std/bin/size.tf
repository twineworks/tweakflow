import bin from 'std.tf';
import expect, expect_error, to from "std/assert.tf";

alias bin.size as size;

library size_spec {

  of_nil:
    expect(size(nil), to.be_nil());

  of_empty:
    expect(size(0b), to.be(0));

  of_0b00:
    expect(size(0b00), to.be(1));

  of_0b000102030405060708090a0b0c0d0e0f:
    expect(size(0b000102030405060708090a0b0c0d0e0f), to.be(16));

  invalid_type:
    expect_error(
      () -> size("foo"),
      to.have_code("CAST_ERROR")
    );
}