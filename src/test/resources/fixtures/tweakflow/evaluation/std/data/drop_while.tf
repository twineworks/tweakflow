import data from "std";
import expect, expect_error, to from "std/assert.tf";

alias data.drop_while as drop_while;


library drop_while_spec {

  drop_everything_from_some:
    expect(drop_while((_) -> true, [1, 2, 3]), to.be([]));

  drop_nothing_from_some:
    expect(drop_while((_) -> false, [1, 2, 3]), to.be([1, 2, 3]));

  drop_one_from_some:
    expect(drop_while((x) -> x <= 1, [1, 2, 3]), to.be([2, 3]));

  drop_some_from_some:
    expect(drop_while((x) -> x <= 2, [1, 2, 3]), to.be([3]));

  drop_some_from_some_with_cast:
    expect(drop_while((x) -> if x <= 2 then "yay" else "", [1, 2, 3]), to.be([3]));

  drop_indexed_one_from_some:
    expect(drop_while((_, i) -> i <= 0, [1, 2, 3]), to.be([2, 3]));

  drop_indexed_some_from_some:
    expect(drop_while((_, i) -> i <= 1, [1, 2, 3]), to.be([3]));

  drop_indexed_some_from_some_with_cast:
    expect(drop_while((_, i) -> if i <= 1 then "yay" else nil, [1, 2, 3]), to.be([3]));

  of_default:
    expect(drop_while(nil, nil), to.be_nil());

  from_nil:
    expect(drop_while((_) -> true, nil), to.be_nil());

  nil_predicate:
    expect_error(
      () -> drop_while(nil, ["foo"]),
      to.have_code("NIL_ERROR")
    );

  bad_predicate:
    expect_error(
      () -> drop_while(() -> true, ["foo"]),
      to.have_code("ILLEGAL_ARGUMENT")
    );
}