import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.update_in as update_in;


library update_in_spec {

  empty_list:
    expect(update_in([], [0], (x) -> if x == nil "a" else "wrong"), to.be(["a"]));

  nested_empty_list:
    expect(update_in([[]], [0, 0], (x) -> if x == nil "a" else "wrong"), to.be([["a"]]));

  append_list:
    expect(update_in([1,2,3], [3], (x) -> if x == nil "a" else "wrong"), to.be([1, 2, 3, "a"]));

  nested_append_list:
    expect(update_in([[1,2,3]], [0, 3], (x) -> if x == nil "a" else "wrong"), to.be([[1, 2, 3, "a"]]));

  overwrite_list:
    expect(update_in([1,2,3], [0], (x) -> if x == 1 "a" else "wrong"), to.be(["a", 2, 3]));

  nested_overwrite_list:
    expect(update_in({:a [1,2,3]}, [:a, 0], (x) -> if x == 1 "a" else "wrong"), to.be({:a ["a", 2, 3]}));

  extend_list:
    expect(update_in([1,2,3], [5], (x) -> if x == nil "a" else "wrong"), to.be([1, 2, 3, nil, nil, "a"]));

  nest_list:
    expect(update_in([1,2,3], [5, 2], (x) -> if x == nil "a" else "wrong"), to.be([1, 2, 3, nil, nil, [nil, nil, "a"]]));

  nested_extend_list:
    expect(update_in([[1,2,3]], [0, 5], (x) -> if x == nil "a" else "wrong"), to.be([[1, 2, 3, nil, nil, "a"]]));

  multi_nested_extend_list:
    expect(update_in([], [1, 1, 1], (x) -> if x == nil "a" else "wrong"), to.be([nil, [nil, [nil, "a"]]]));

  empty_dict:
    expect(update_in({}, [:a], (x) -> if x == nil "foo" else "wrong"), to.be({:a "foo"}));

  nested_empty_dict:
    expect(update_in({:a {}}, [:a, :a], (x) -> if x == nil "foo" else "wrong"), to.be({:a {:a "foo"}}));

  overwrite_dict:
    expect(update_in({:a "foo", :b "bar"}, [:a], (x) -> if x == "foo" "changed" else "wrong"), to.be({:a "changed", :b "bar"}));

  nested_overwrite_dict:
    expect(update_in({:a {:a "foo", :b "bar"}}, [:a, :a], (x) -> if x == "foo" "changed" else "wrong"), to.be({:a {:a "changed", :b "bar"}}));

  extend_dict:
    expect(update_in({:a "foo", :b "bar"}, [:c], (x) -> if x == nil "baz" else "wrong"), to.be({:a "foo", :b "bar", :c "baz"}));

  nest_dict:
    expect(update_in({:a "foo", :b "bar"}, [:c, :d], (x) -> if x == nil "baz" else "wrong"), to.be({:a "foo", :b "bar", :c {:d "baz"}}));

  nested_extend_dict:
    expect(
      update_in({:a {:a "foo", :b "bar"}}, [:a, :c], (x) -> if x == nil "baz" else "wrong"),
      to.be({:a {:a "foo", :b "bar", :c "baz"}})
    );

  of_nil:
    expect(update_in(nil, [0], (x) -> x), to.be_nil());

  invalid_collection:
    expect_error(
      () -> update_in("foo", [1], (x) -> x),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  nested_invalid_collection:
    expect_error(
      () -> update_in(["foo"], [0, 1], (x) -> x),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  nil_key:
    expect_error(
      () -> update_in({}, [nil], (x) -> x),
      to.have_code("NIL_ERROR")
    );

  bad_key_for_dict:
    expect_error(
      () -> update_in({}, [[]], (x) -> x),
      to.have_code("CAST_ERROR")
    );

  nested_bad_key_for_dict:
    expect_error(
      () -> update_in({:a {}}, [:a, []], (x) -> x),
      to.have_code("CAST_ERROR")
    );

  bad_key_for_list:
    expect_error(
      () -> update_in([], [2019-01-01T], (x) -> x),
      to.have_code("CAST_ERROR")
    );

  nested_bad_key_for_list:
    expect_error(
      () -> update_in([[]], [0, 2019-01-01T], (x) -> x),
      to.have_code("CAST_ERROR")
    );

  neg_key_for_list:
    expect_error(
      () -> update_in([], [-1], (x) -> x),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

  nested_neg_key_for_list:
    expect_error(
      () -> update_in([[]], [0, -1], (x) -> x),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

  }