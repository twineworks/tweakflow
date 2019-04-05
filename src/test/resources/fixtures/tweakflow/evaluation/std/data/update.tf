import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.update as update;


library update_spec {

  empty_list:
    expect(update([], 0, (x) -> if x == nil "a" else "wrong"), to.be(["a"]));

  append_list:
    expect(update([1,2,3], 3, (x) -> if x === nil "a" else "wrong"), to.be([1, 2, 3, "a"]));

  overwrite_list:
    expect(update([1,2,3], 0, (x) -> if x === 1 "a" else "wrong"), to.be(["a", 2, 3]));

  extend_list:
    expect(update([1,2,3], 5, (x) -> if x == nil "a" else "wrong"), to.be([1, 2, 3, nil, nil, "a"]));

  empty_dict:
    expect(update({}, "a", (x) -> if x == nil "foo" else "wrong"), to.be({:a "foo"}));

  overwrite_dict:
    expect(update({:a "foo", :b "bar"}, :a, (x) -> if x == "foo" "changed" else "wrong"), to.be({:a "changed", :b "bar"}));

  extend_dict:
    expect(update({:a "foo", :b "bar"}, :c, (x) -> if x == nil "baz" else "wrong"), to.be({:a "foo", :b "bar", :c "baz"}));

  of_nil:
    expect(update(nil, 0, (x) -> x), to.be_nil());

  invalid_collection:
    expect_error(
      () -> update("foo", 1, (x) -> x),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  nil_key:
    expect_error(
      () -> update({}, nil, (x) -> x),
      to.have_code("NIL_ERROR")
    );

  bad_key_for_dict:
    expect_error(
      () -> update({}, [], (x) -> x),
      to.have_code("CAST_ERROR")
    );

  bad_key_for_list:
    expect_error(
      () -> update([], 2019-01-01T, (x) -> x),
      to.have_code("CAST_ERROR")
    );

  neg_key_for_list:
    expect_error(
      () -> update([], -1, (x) -> x),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

  }