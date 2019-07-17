import data, math from "std";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias data.unique as unique;
alias data.size as size;

library spec {
  spec:
    describe("unique", [


  empty:
    expect(unique([]), to.be([]));

  simple_duplicates:
    expect(unique([1, 2, 3, 3, 2, 1]), to.be([1, 2, 3]));

  mixed_duplicates:
    expect(unique(["a", 1, [0], {:foo "bar"}, {:foo "bar"}, [0], 1, "a"]), to.be(["a", 1, [0], {:foo "bar"}]));

  merely_equal_duplicates:
    expect(unique([1, 1.0, 2, 2.0]), to.be([1, 1.0, 2, 2.0]));

  non_comparables:
    let {
      f: () -> true;
      u: unique([NaN, NaN, f, f, 1, 1, 2, 2]);
    }
    # expecting [NaN, NaN, f, f, 1, 2], but can't
    # test using === because NaN and f don't compare as equal
    # testing individual elements instead
    expect(size(u), to.be(6)) &&
    expect(u[0], to.be_NaN()) &&
    expect(u[1], to.be_NaN()) &&
    expect(u[2], to.be_function()) &&
    expect(u[2](), to.be_true()) &&
    expect(u[3], to.be_function()) &&
    expect(u[3](), to.be_true()) &&
    expect(u[4], to.be(1)) &&
    expect(u[5], to.be(2));

  of_nil:
    expect(unique(nil), to.be_nil());

  ]);
}