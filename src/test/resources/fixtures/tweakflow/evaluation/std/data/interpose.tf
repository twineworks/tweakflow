import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.interpose as interpose;

library interpose_spec {

  empty:
    expect(interpose([], "a"), to.be([]));

  singleton:
    expect(interpose([1], 0), to.be([1]));

  some:
    expect(interpose([1,2,3], 0), to.be([1, 0, 2, 0, 3]));

  of_nil:
    expect(interpose(nil, 1), to.be_nil());

}