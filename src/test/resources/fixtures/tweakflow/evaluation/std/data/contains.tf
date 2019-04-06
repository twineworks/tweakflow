import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.contains? as contains?;

library contains_spec {

  empty_list:
    expect(contains?([], 1), to.be_false());

  non_comparable_nan_list:
    expect(contains?([NaN], NaN), to.be_false());

  non_comparable_function_list:
    let {
      f: (x) -> x;
    }
    expect(contains?([f], f), to.be_false());

  not_found_list:
    expect(contains?([1,2,3], 4), to.be_false());

  found_first_list:
    expect(contains?([1,2,3], 1), to.be_true());

  found_mid_list:
    expect(contains?([1,2,3], 2), to.be_true());

  found_last_list:
    expect(contains?([1,2,3], 3), to.be_true());

  found_nil_in_list:
    expect(contains?([1,nil,3], nil), to.be_true());

  empty_dict:
    expect(contains?({}, 1), to.be_false());

  non_comparable_nan_dict:
    expect(contains?({:a NaN}, NaN), to.be_false());

  non_comparable_function_dict:
    let {
      f: (x) -> x;
    }
    expect(contains?({:a f}, f), to.be_false());

  not_found_dict:
    expect(contains?({:a 1, :b 2, :c 3}, 4), to.be_false());

  found_first_dict:
    expect(contains?({:a 1, :b 2, :c 3}, 1), to.be_true());

  found_mid_dict:
    expect(contains?({:a 1, :b 2, :c 3}, 2), to.be_true());

  found_last_dict:
    expect(contains?({:a 1, :b 2, :c 3}, 3), to.be_true());

  found_nil_in_dict:
    expect(contains?({:a 1, :b nil, :c 3}, nil), to.be_true());

  of_nil:
    expect(contains?(nil, 1), to.be_nil());

  of_default:
    expect(contains?(), to.be_nil());

  of_non_collection:
    expect_error(
      () -> contains?("foo", "o"),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}