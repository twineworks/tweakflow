import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.prepend as prepend;

library prepend_spec {

  empty_list:
    expect(prepend("x", []), to.be(["x"]));

  simple_list:
    expect(prepend("x", [1, nil ,3]), to.be(["x", 1, nil, 3]));

  nil_entry:
    expect(prepend(nil, [1, nil ,3]), to.be([nil, 1, nil, 3]));

  of_nil:
    expect(prepend(1, nil), to.be_nil());

  }