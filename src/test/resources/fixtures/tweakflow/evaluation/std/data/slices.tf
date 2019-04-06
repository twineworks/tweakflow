import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.slices as slices;

library slices_spec {

  slices_of_empty:
    expect(slices([], 1), to.be([]));

  single_slices:
    expect(slices([1, 2, 3], 1), to.be([[1], [2], [3]]));

  pair_slices_even:
    expect(slices([1, 2, 3, 4], 2), to.be([[1, 2], [3, 4]]));

  pair_slices_odd:
    expect(slices([1, 2, 3, 4, 5], 2), to.be([[1, 2], [3, 4], [5]]));

  triple_slices_even_0:
    expect(slices([], 3), to.be([]));

  triple_slices_0_1_odd:
    expect(slices([1], 3), to.be([[1]]));

  triple_slices_0_2_odd:
    expect(slices([1, 2], 3), to.be([[1, 2]]));

  triple_slices_even_1:
    expect(slices([1, 2, 3], 3), to.be([[1, 2, 3]]));

  triple_slices_even_1_1_odd:
    expect(slices([1, 2, 3, 4], 3), to.be([[1, 2, 3], [4]]));

  triple_slices_even_1_2_odd:
    expect(slices([1, 2, 3, 4, 5], 3), to.be([[1, 2, 3], [4, 5]]));

  triple_slices_even_2:
    expect(slices([1, 2, 3, 4, 5, 6], 3), to.be([[1, 2, 3], [4, 5, 6]]));

  of_default:
    expect(slices(), to.be_nil());

  of_nil:
    expect(slices(nil, nil), to.be_nil());

  of_nil_s:
    expect(slices([], nil), to.be_nil());

  of_zero_s:
    expect_error(
      () -> slices([], 0),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  of_neg_s:
    expect_error(
      () -> slices([], -1),
      to.have_code("ILLEGAL_ARGUMENT")
    );

}