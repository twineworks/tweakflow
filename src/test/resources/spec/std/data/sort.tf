import data, math from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.sort as sort;
alias math.compare as cmp;

library spec {
  spec:
    describe("sort", [


  empty_list:
    expect(sort([], cmp), to.be([]));

  sorted_list:
    expect(sort([1,2,3], cmp), to.be([1,2,3]));

  unsorted_list:
    expect(sort([3,2,1], cmp), to.be([1,2,3]));

  stability: # retains the order of equivalents 2, 2.0, 2
    expect(sort([3, 2, 2.0, 2, 1], cmp), to.be([1, 2, 2.0, 2, 3]));

  of_default:
    expect(sort(), to.be_nil());

  of_nil:
    expect(sort(nil, cmp), to.be_nil());

  of_nil_cmp:
    expect(sort([], nil), to.be_nil());

  of_bad_cmp:
    expect_error(
      () -> sort([3,2,1], (a, b) -> "foo"), # cmp must return 0, <0, or >0 numbers
      to.have_code("ILLEGAL_ARGUMENT")
    );

  ]);
}