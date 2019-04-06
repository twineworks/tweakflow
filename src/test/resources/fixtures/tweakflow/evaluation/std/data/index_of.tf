import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.index_of as index_of;


library index_of_spec {

  empty:
    expect(index_of([], 1), to.be(-1));

  found_first:
    expect(index_of([1,2,3], 1), to.be(0));

  found_second:
    expect(index_of([1,2,3], 2), to.be(1));

  found_last:
    expect(index_of([1,2,3], 3), to.be(2));

  not_found:
    expect(index_of([1,2,3], 4), to.be(-1));

  found_at:
    expect(index_of([1,2,3,1], 1, 3), to.be(3));

  found_after:
    expect(index_of([1,2,3,1], 1, 1), to.be(3));

  found_after_mid:
    expect(index_of([1,2,3,1,3,2,1], 1, 1), to.be(3));

  found_after_last:
    expect(index_of([1,2,3,1,3,2,1], 1, 4), to.be(6));

  not_found_beyond_last:
    expect(index_of([1,2,3], 2, 2), to.be(-1));

  not_found_beyond_end:
    expect(index_of([1,2,3], 3, 99), to.be(-1));

  not_found_equivalent:
    expect(index_of([1,2,3], 1.0), to.be(-1));

  not_found_nan:
    expect(index_of([NaN], NaN), to.be(-1));

  not_found_f:
    let {
      f: () -> true;
    }
    expect(index_of([f], f), to.be(-1));

  of_nil:
    expect(index_of(nil, 0), to.be_nil());

  of_start_nil:
    expect(index_of([], 0, nil), to.be_nil());

  of_default:
    expect(index_of(), to.be_nil());

}