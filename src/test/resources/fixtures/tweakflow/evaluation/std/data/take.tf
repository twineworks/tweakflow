import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.take as take;

library take_spec {

  take_0_of_empty:
    expect(take(0, []), to.be([]));

  take_1_of_empty:
    expect(take(1, []), to.be([]));

  take_2_of_empty:
    expect(take(2, []), to.be([]));

  take_neg_1_of_empty:
    expect(take(-1, []), to.be([]));

  take_0_of_one:
    expect(take(0, [1]), to.be([]));

  take_1_of_one:
    expect(take(1, [1]), to.be([1]));

  take_2_of_one:
    expect(take(2, [1]), to.be([1]));

  take_neg_1_of_one:
    expect(take(-1, [1]), to.be([]));

  take_0_of_some:
    expect(take(0, [1, 2, 3]), to.be([]));

  take_1_of_some:
    expect(take(1, [1, 2, 3]), to.be([1]));

  take_2_of_some:
    expect(take(2, [1, 2, 3]), to.be([1, 2]));

  take_neg_1_of_some:
    expect(take(-1, [1, 2, 3]), to.be([]));

  of_default:
    expect(take(), to.be_nil());

  nil_items:
    expect(take(nil, []), to.be_nil());

  from_nil:
    expect(take(0, nil), to.be_nil());

}