import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.append as append;

library append_spec {

  empty_list:
    expect(append([], "x"), to.be(["x"]));

  simple_list:
    expect(append([1, nil ,3], "x"), to.be([1, nil, 3, "x"]));

  nil_entry:
    expect(append([1, nil ,3], nil), to.be([1, nil, 3, nil]));

  of_nil:
    expect(append(nil, 1), to.be_nil());

  }