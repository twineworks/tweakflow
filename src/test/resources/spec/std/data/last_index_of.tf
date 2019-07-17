import data from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.last_index_of as last_index_of;

library spec {
  spec:
    describe("last_index_of", [


  empty:
    expect(last_index_of([], 1), to.be(-1));

  beyond_empty:
    expect(last_index_of([], 1, 3), to.be(-1));

  found_first:
    expect(last_index_of([1,2,3], 1), to.be(0));

  found_second:
    expect(last_index_of([1,2,3], 2), to.be(1));

  found_last:
    expect(last_index_of([1,2,3], 3), to.be(2));

  not_found:
    expect(last_index_of([1,2,3], 4), to.be(-1));

  found_at_last:
    expect(last_index_of([1,2,3,1], 1, 3), to.be(3));

  found_at_mid:
    expect(last_index_of([1,2,3,1,2], 2, 1), to.be(1));

  found_at_first:
    expect(last_index_of([1,2,3,1,2], 1, 0), to.be(0));

  found_before:
    expect(last_index_of([1,2,3,1], 1, 1), to.be(0));

  found_before_mid:
    expect(last_index_of([1,2,3,1,3,2,1], 1, 4), to.be(3));

  found_before_first:
    expect(last_index_of([1,2,3,1,3,2,1], 1, 2), to.be(0));

  not_found_beyond_first:
    expect(last_index_of([1,2,3], 2, 0), to.be(-1));

  not_found_beyond_end:
    expect(last_index_of([1,2,3], 3, -1), to.be(-1));

  not_found_equivalent:
    expect(last_index_of([1,2,3], 1.0), to.be(-1));

  not_found_nan:
    expect(last_index_of([NaN], NaN), to.be(-1));

  not_found_f:
    let {
      f: () -> true;
    }
    expect(last_index_of([f], f), to.be(-1));

  of_nil:
    expect(last_index_of(nil, 0), to.be_nil());

  of_default:
    expect(last_index_of(), to.be_nil());

  ]);
}