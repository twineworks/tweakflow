import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.find_index as find_index;

library find_index_spec {

  empty_list:
    expect(find_index([], (_) -> true), to.be_nil());

  not_found:
    expect(find_index([1,2,3], (x) -> x > 10), to.be_nil());

  not_found_with_cast:
    expect(find_index([1,2,3], (x) -> if x > 10 then "yes" else nil), to.be_nil());

  not_found_with_index:
    expect(find_index([1,2,3], (x, i) -> i > 10), to.be_nil());

  not_found_with_default_3rd_param:
    expect(find_index([1,2,3], (x, i, a="foo") -> i > 10), to.be_nil());

  found:
    expect(find_index([1,2,3], (x) -> x == 2), to.be(1));

  found_with_cast:
    expect(find_index([1,2,3], (x) -> if x == 2 "foo" else ""), to.be(1));

  found_with_index:
    expect(find_index([1,2,3], (x, i) -> i == 2), to.be(2));

  found_with_default_3rd_param:
    expect(find_index([1,2,3], (x, i, a="foo") -> a == "foo" && i == 2), to.be(2));

  found_first:
    expect(find_index([1,2,3], (x) -> x <= 2), to.be(0));

  found_last:
    expect(find_index([1,2,3], (x) -> x == 3), to.be(2));

  of_nil:
    expect(find_index(nil), to.be_nil());

  of_nil_p_nil:
    expect(find_index(nil, nil), to.be_nil());

  of_only_p_nil:
    expect_error(
      () -> find_index([], nil),
      to.have_code("NIL_ERROR")
    );

  of_invalid_p_too_few_args:
    expect_error(
      () -> find_index([], () -> true), # p should accept 1 or 2 args
      to.have_code("ILLEGAL_ARGUMENT")
    );

}