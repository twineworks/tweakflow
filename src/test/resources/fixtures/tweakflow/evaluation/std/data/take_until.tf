import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.take_until as take_until;


library take_until_spec {

  take_everything_from_some:
    expect(take_until((_) -> false, [1, 2, 3]), to.be([1, 2, 3]));

  take_nothing_from_some:
    expect(take_until((_) -> true, [1, 2, 3]), to.be([]));

  take_one_from_some:
    expect(take_until((x) -> x > 1, [1, 2, 3]), to.be([1]));

  take_some_from_some:
    expect(take_until((x) -> x > 2, [1, 2, 3]), to.be([1, 2]));

  take_some_from_some_with_cast:
    expect(take_until((x) -> if x > 2 then "yay" else "", [1, 2, 3]), to.be([1, 2]));

  take_indexed_one_from_some:
    expect(take_until((_, i) -> i > 0, [1, 2, 3]), to.be([1]));

  take_indexed_some_from_some:
    expect(take_until((_, i) -> i > 1, [1, 2, 3]), to.be([1, 2]));

  take_indexed_some_from_some_with_cast:
    expect(take_until((_, i) -> if i > 1 then "yay" else nil, [1, 2, 3]), to.be([1, 2]));

  of_default:
    expect(take_until(nil, nil), to.be_nil());

  from_nil:
    expect(take_until((_) -> false, nil), to.be_nil());

  nil_predicate:
    expect_error(
      () -> take_until(nil, ["foo"]),
      to.have_code("NIL_ERROR")
    );

  bad_predicate:
    expect_error(
      () -> take_until(() -> false, ["foo"]),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}