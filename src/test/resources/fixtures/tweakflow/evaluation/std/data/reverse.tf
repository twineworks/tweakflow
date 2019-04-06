import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.reverse as reverse;

library reverse_spec {

  empty_list:
    expect(reverse([]), to.be([]));

  simple_list:
    expect(reverse([1,2,3]), to.be([3,2,1]));

  of_nil:
    expect(reverse(nil), to.be_nil());

}