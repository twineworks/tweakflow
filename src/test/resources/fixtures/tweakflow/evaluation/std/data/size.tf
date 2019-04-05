import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.size as size;


library size_spec {

  empty_list:
    expect(size([]), to.be(0));

  simple_list:
    expect(size([1,2,3]), to.be(3));

  empty_dict:
    expect(size({}), to.be(0));

  simple_dict:
    expect(size({:a 1, :b 2}), to.be(2));

  of_nil:
    expect(size(nil), to.be_nil());

  invalid_type:
    expect_error(
      () -> size("foo"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}