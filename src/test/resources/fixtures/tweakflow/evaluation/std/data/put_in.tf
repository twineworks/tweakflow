import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.put_in as put_in;


library put_in_spec {

  empty_list:
    expect(put_in([], [0], "a"), to.be(["a"]));

  nested_empty_list:
    expect(put_in([[]], [0, 0], "a"), to.be([["a"]]));

  append_list:
    expect(put_in([1,2,3], [3], "a"), to.be([1, 2, 3, "a"]));

  nested_append_list:
    expect(put_in([[1,2,3]], [0, 3], "a"), to.be([[1, 2, 3, "a"]]));

  overwrite_list:
    expect(put_in([1,2,3], [0], "a"), to.be(["a", 2, 3]));

  nested_overwrite_list:
    expect(put_in({:a [1,2,3]}, [:a, 0], "a"), to.be({:a ["a", 2, 3]}));

  extend_list:
    expect(put_in([1,2,3], [5], "a"), to.be([1, 2, 3, nil, nil, "a"]));

  nested_extend_list:
    expect(put_in([[1,2,3]], [0, 5], "a"), to.be([[1, 2, 3, nil, nil, "a"]]));

  multi_nested_extend_list:
    expect(put_in([], [1, 1, 1], "a"), to.be([nil, [nil, [nil, "a"]]]));

  empty_dict:
    expect(put_in({}, ["a"], "foo"), to.be({:a "foo"}));

  nested_empty_dict:
    expect(put_in({:a {}}, [:a, :a], "foo"), to.be({:a {:a "foo"}}));

  overwrite_dict:
    expect(put_in({:a "foo", :b "bar"}, [:a], "changed"), to.be({:a "changed", :b "bar"}));

  nested_overwrite_dict:
    expect(put_in({:a {:a "foo", :b "bar"}}, [:a, :a], "changed"), to.be({:a {:a "changed", :b "bar"}}));

  extend_dict:
    expect(put_in({:a "foo", :b "bar"}, [:c], "baz"), to.be({:a "foo", :b "bar", :c "baz"}));

  nested_extend_dict:
    expect(
      put_in({:a {:a "foo", :b "bar"}}, [:a, :c], "baz"),
      to.be({:a {:a "foo", :b "bar", :c "baz"}})
    );

  of_nil:
    expect(put_in(nil, [0], "a"), to.be_nil());

  invalid_collection:
    expect_error(
      () -> put_in("foo", [1], 0),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  nested_invalid_collection:
    expect_error(
      () -> put_in(["foo"], [0, 1], 0),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  nil_key:
    expect_error(
      () -> put_in({}, [nil], 0),
      to.have_code("NIL_ERROR")
    );

  bad_key_for_dict:
    expect_error(
      () -> put_in({}, [[]], 0),
      to.have_code("CAST_ERROR")
    );

  nested_bad_key_for_dict:
    expect_error(
      () -> put_in({:a {}}, [:a, []], 0),
      to.have_code("CAST_ERROR")
    );

  bad_key_for_list:
    expect_error(
      () -> put_in([], [2019-01-01T], 0),
      to.have_code("CAST_ERROR")
    );

  nested_bad_key_for_list:
    expect_error(
      () -> put_in([[]], [0, 2019-01-01T], 0),
      to.have_code("CAST_ERROR")
    );

  neg_key_for_list:
    expect_error(
      () -> put_in([], [-1], 0),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

  nested_neg_key_for_list:
    expect_error(
      () -> put_in([[]], [0, -1], 0),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

  }