import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.put as put;


library put_spec {

  empty_list:
    expect(put([], 0, "a"), to.be(["a"]));

  append_list:
    expect(put([1,2,3], 3, "a"), to.be([1, 2, 3, "a"]));

  overwrite_list:
    expect(put([1,2,3], 0, "a"), to.be(["a", 2, 3]));

  extend_list:
    expect(put([1,2,3], 5, "a"), to.be([1, 2, 3, nil, nil, "a"]));

  empty_dict:
    expect(put({}, "a", "foo"), to.be({:a "foo"}));

  overwrite_dict:
    expect(put({:a "foo", :b "bar"}, :a, "changed"), to.be({:a "changed", :b "bar"}));

  extend_dict:
    expect(put({:a "foo", :b "bar"}, :c, "baz"), to.be({:a "foo", :b "bar", :c "baz"}));

  of_nil:
    expect(put(nil, 0, "a"), to.be_nil());

  invalid_collection:
    expect_error(
      () -> put("foo", 1, 0),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  nil_key:
    expect_error(
      () -> put({}, nil, 0),
      to.have_code("NIL_ERROR")
    );

  bad_key_for_dict:
    expect_error(
      () -> put({}, [], 0),
      to.have_code("CAST_ERROR")
    );

  bad_key_for_list:
    expect_error(
      () -> put([], 2019-01-01T, 0),
      to.have_code("CAST_ERROR")
    );

  neg_key_for_list:
    expect_error(
      () -> put([], -1, 0),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

  }