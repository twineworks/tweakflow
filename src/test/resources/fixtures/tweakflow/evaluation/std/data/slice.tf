import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.slice as slice;

library slice_spec {

  empty_slice_of_empty:
    expect(slice([], 0, 0), to.be([]));

  empty_slice_of_some:
    expect(slice([1, 2], 0, 0), to.be([]));

  empty_slice_of_some_more:
    expect(slice([1, 2], 1, 0), to.be([]));

  whole_slice:
    expect(slice([1, 2, 3], 0), to.be([1, 2, 3]));

  head_slice:
    expect(slice([1, 2, 3], 0, 1), to.be([1]));

  init_slice:
    expect(slice([1, 2, 3], 0, 2), to.be([1, 2]));

  mid_slice:
    expect(slice([1, 2, 3, 4], 1, 3), to.be([2, 3]));

  mid_empty_slice:
    expect(slice([1, 2, 3], 1, 1), to.be([]));

  tail_slice:
    expect(slice([1, 2, 3], 1, 3), to.be([2, 3]));

  last_slice:
    expect(slice([1, 2, 3], 2, nil), to.be([3]));

  overextended_slice:
    expect(slice([1, 2, 3], 1, 100), to.be([2, 3]));

  end_before_start_slice:
    expect(slice([1, 2, 3], 2, 1), to.be([]));

  start_post_size_slice:
    expect(slice([1, 2, 3], 4), to.be([]));

  of_default:
    expect(slice(), to.be_nil());

  of_nil:
    expect(slice(nil, nil, nil), to.be_nil());

  of_nil_end:
    expect(slice(nil, 0, nil), to.be_nil());

  of_nil_start:
    expect_error(
      () -> slice([], nil),
      to.have_code("NIL_ERROR")
    );

  of_neg_start:
    expect_error(
      () -> slice([], -1),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

}