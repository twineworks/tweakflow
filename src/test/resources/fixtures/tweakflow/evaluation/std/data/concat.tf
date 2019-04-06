import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.concat as concat;

library concat_spec {

  empty:
    expect(concat([]), to.be([]));

  single:
    expect(concat([[1,2,3]]), to.be([1,2,3]));

  pair:
    expect(concat([[1,2,3], [4,5,6]]), to.be([1,2,3,4,5,6]));

  triple:
    expect(concat([[1,2,3], [4,5,6], [7,8,9]]), to.be([1,2,3,4,5,6,7,8,9]));

  of_nil:
    expect(concat(nil), to.be_nil());

  of_nil_element:
    expect(concat([[1, 2], nil]), to.be_nil());

  invalid_element_type:
    expect_error(
      () -> concat([{}]),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}