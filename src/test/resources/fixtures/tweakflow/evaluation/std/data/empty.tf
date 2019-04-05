import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.empty? as empty?;


library size_spec {

  empty_list:
    expect(empty?([]), to.be_true());

  simple_list:
    expect(empty?([1,2,3]), to.be_false());

  empty_dict:
    expect(empty?({}), to.be_true());

  simple_dict:
    expect(empty?({:a 1, :b 2}), to.be_false());

  of_nil:
    expect(empty?(nil), to.be_nil());

  invalid_type:
    expect_error(
      () -> empty?("foo"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}