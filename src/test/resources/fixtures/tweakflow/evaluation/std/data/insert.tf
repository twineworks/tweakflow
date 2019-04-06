import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.insert as insert;

library insert_spec {

  empty_list_append:
    expect(insert([], 0, "a"), to.be(["a"]));

  empty_list_extend:
    expect(insert([], 2, "a"), to.be([nil, nil, "a"]));

  list_append:
    expect(insert([1, 2], 2, "a"), to.be([1, 2, "a"]));

  list_extend:
    expect(insert([1, 2], 4, "a"), to.be([1, 2, nil, nil, "a"]));

  list_prepend:
    expect(insert([1, 2], 0, "a"), to.be(["a", 1, 2]));

  list_shift:
    expect(insert([1, 2], 1, "a"), to.be([1, "a", 2]));

  of_nil:
    expect(insert(nil, 0, "a"), to.be_nil());

  of_nil_pos:
    expect_error(
      () -> insert([], nil, "a"),
      to.have_code("NIL_ERROR")
    );

  of_neg_pos:
    expect_error(
      () -> insert([], -1, "a"),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );
}