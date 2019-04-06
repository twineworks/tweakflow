import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.drop as drop;

library drop_spec {

  drop_0_of_empty:
    expect(drop(0, []), to.be([]));

  drop_1_of_empty:
    expect(drop(1, []), to.be([]));

  drop_2_of_empty:
    expect(drop(2, []), to.be([]));

  drop_neg_1_of_empty:
    expect(drop(-1, []), to.be([]));

  drop_0_of_one:
    expect(drop(0, [1]), to.be([1]));

  drop_1_of_one:
    expect(drop(1, [1]), to.be([]));

  drop_2_of_one:
    expect(drop(2, [1]), to.be([]));

  drop_neg_1_of_one:
    expect(drop(-1, [1]), to.be([1]));

  drop_0_of_some:
    expect(drop(0, [1, 2, 3]), to.be([1, 2, 3]));

  drop_1_of_some:
    expect(drop(1, [1, 2, 3]), to.be([2, 3]));

  drop_2_of_some:
    expect(drop(2, [1, 2, 3]), to.be([3]));

  drop_neg_1_of_some:
    expect(drop(-1, [1, 2, 3]), to.be([1, 2, 3]));

  of_default:
    expect(drop(), to.be_nil());

  nil_items:
    expect(drop(nil, []), to.be_nil());

  from_nil:
    expect(drop(0, nil), to.be_nil());

}