import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.drop_until as drop_until;


library drop_until_spec {

  drop_everything_from_some:
    expect(drop_until((_) -> true, [1, 2, 3]), to.be([1, 2, 3]));

  drop_nothing_from_some:
    expect(drop_until((_) -> false, [1, 2, 3]), to.be([]));

  drop_one_from_some:
    expect(drop_until((x) -> x > 1, [1, 2, 3]), to.be([2, 3]));

  drop_some_from_some:
    expect(drop_until((x) -> x > 2, [1, 2, 3]), to.be([3]));

  drop_some_from_some_with_cast:
    expect(drop_until((x) -> if x > 2 then "yay" else "", [1, 2, 3]), to.be([3]));

  drop_indexed_one_from_some:
    expect(drop_until((_, i) -> i > 0, [1, 2, 3]), to.be([2, 3]));

  drop_indexed_some_from_some:
    expect(drop_until((_, i) -> i > 1, [1, 2, 3]), to.be([3]));

  drop_indexed_some_from_some_with_cast:
    expect(drop_until((_, i) -> if i > 1 then "yay" else nil, [1, 2, 3]), to.be([3]));

  of_default:
    expect(drop_until(nil, nil), to.be_nil());

  from_nil:
    expect(drop_until((_) -> true, nil), to.be_nil());

  nil_predicate:
    expect_error(
      () -> drop_until(nil, ["foo"]),
      to.have_code("NIL_ERROR")
    );

  bad_predicate:
    expect_error(
      () -> drop_until(() -> true, ["foo"]),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}