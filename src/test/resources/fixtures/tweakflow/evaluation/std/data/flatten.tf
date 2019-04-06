import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.flatten as flatten;


library flatten_spec {

  empty:
    expect(flatten([]), to.be([]));

  simple_list:
    expect(flatten([1,2,3,nil]), to.be([1,2,3,nil]));

  nested_lists:
    expect(flatten([[1],["foo"],[nil],[1,{},[]]]), to.be([1,"foo",nil,1,{},[]]));

  nested_empty_lists:
    expect(flatten([[],[],[],[]]), to.be([]));

  of_default:
    expect(flatten(), to.be_nil());

  of_nil:
    expect(flatten(nil), to.be_nil());

}