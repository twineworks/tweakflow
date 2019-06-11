import bin from "std";
import expect, expect_error, to from "std/assert.tf";

alias bin.slice as slice;

library slice_spec {

  empty_slice_of_empty:
    expect(slice(0b, 0, 0), to.be(0b));

  empty_slice_of_some:
    expect(slice(0b0001, 0, 0), to.be(0b));

  empty_slice_of_some_more:
    expect(slice(0b0001, 1, 0), to.be(0b));

  whole_slice:
    expect(slice(0b000102, 0), to.be(0b000102));

  head_slice:
    expect(slice(0b000102, 0, 1), to.be(0b00));

  init_slice:
    expect(slice(0b000102, 0, 2), to.be(0b0001));

  mid_slice:
    expect(slice(0b00010203, 1, 3), to.be(0b0102));

  mid_empty_slice:
    expect(slice(0b000102, 1, 1), to.be(0b));

  tail_slice:
    expect(slice(0b000102, 1, 3), to.be(0b0102));

  last_slice:
    expect(slice(0b000102, 2, nil), to.be(0b02));

  overextended_slice:
    expect(slice(0b000102, 1, 100), to.be(0b0102));

  end_before_start_slice:
    expect(slice(0b000102, 2, 1), to.be(0b));

  start_post_size_slice:
    expect(slice(0b000102, 4), to.be(0b));

  of_default:
    expect(slice(), to.be_nil());

  of_nil:
    expect(slice(nil, nil, nil), to.be_nil());

  of_nil_end:
    expect(slice(nil, 0, nil), to.be_nil());

  of_nil_start:
    expect_error(
      () -> slice(0b, nil),
      to.have_code("NIL_ERROR")
    );

  of_neg_start:
    expect_error(
      () -> slice(0b, -1),
      to.have_code("INDEX_OUT_OF_BOUNDS")
    );

}