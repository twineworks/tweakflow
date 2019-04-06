import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.filter as filter;

library filter_spec {

  empty_list:
    expect(filter([], (_) -> true), to.be([]));

  keep_all_list:
    expect(filter([1,2,3], (_) -> true), to.be([1,2,3]));

  keep_some_list:
    expect(filter([1,2,3], (x) -> x % 2 == 1), to.be([1,3]));

  keep_some_via_index_list:
    expect(filter([1,2,3], (_, i) -> i % 2 == 1), to.be([2]));

  drop_all_list:
    expect(filter([1,2,3], (_) -> false), to.be([]));

  keep_all_list_with_cast:
    expect(filter([1,2,3], (_) -> "foo"), to.be([1,2,3]));

  drop_all_list_with_cast:
    expect(filter([1,2,3], (_) -> ""), to.be([]));

  keep_some_list_with_cast:
    expect(filter([1,2,3], (x) -> if x % 2 == 1 then "yo" else ""), to.be([1,3]));


  empty_dict:
    expect(filter({}, (_) -> true), to.be({}));

  keep_all_dict:
    expect(filter({:a 1, :b 2}, (_) -> true), to.be({:a 1, :b 2}));

  drop_all_dict:
    expect(filter({:a 1, :b 2}, (_) -> false), to.be({}));

  keep_some_dict:
    expect(filter({:a 1, :b 2}, (x) -> x == 2), to.be({:b 2}));

  keep_some_via_index_dict:
    expect(filter({:a 1, :b 2}, (_, k) -> k == :b), to.be({:b 2}));

  keep_some_dict_with_cast:
    expect(filter({:a 1, :b 2}, (x) -> if x == 2 then "keep" else []), to.be({:b 2}));


  of_nil:
    expect(filter(nil), to.be_nil());

  of_nil_p_nil:
    expect(filter(nil, nil), to.be_nil());

  of_only_p_nil:
    expect_error(
      () -> filter([], nil),
      to.have_code("NIL_ERROR")
    );

  of_invalid_p_too_few_args:
    expect_error(
      () -> filter([], () -> true), # p should accept 1 or 2 args
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_non_collection:
    expect_error(
      () -> filter("foo", (_) -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}