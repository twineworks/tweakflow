import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.values as values;


library values_spec {

  empty_list:
    expect(values([]), to.be([]));

  simple_list:
    expect(values([1,nil,3]), to.be([1, nil, 3]));

  empty_dict:
    expect(values({}), to.be([]));

  simple_dict:
    expect(values({:a 1, :b 2}), to.be_permutation_of([1, 2]));

  medium_dict:
    expect(
      values({:alpha 1, :beta 2, :gamma 3, :delta 4, :epsilon 5, :eta 6, :theta 7}),
      to.be_permutation_of([1, 2, 3, 4, 5, 6, 7])
    );

  of_nil:
    expect(values(nil), to.be_nil());

  invalid_type:
    expect_error(
      () -> values("foo"),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}