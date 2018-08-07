
export library lib
{
  concat:           "foo" .. "bar";
  equal:            "foo" == "foo";
  not_equal:        "foo" == "bar";
  identical:        1 === 1;
  identical_false:  1 === 1.0;
  not_identical:    1 !== 1.0;
  not_identical_false: 1 !== 1;
  not_false:        !false;
  not_true:         !true;
  not_nil:          !nil;
  inequality_true:  1 != 2;
  inequality_false: 1 != 1;
  and_true:         1 && 1;
  and_false:        true && false;
  or_true:          true || false;
  or_false:         nil || false;
  lt_true:          1 < 2;
  lt_false:         2 < 1;
  lte_true:         2 <= 2;
  lte_false:        2 <= 1;
  gt_true:          2 > 1;
  gt_false:         1 > 2;
  gte_true:         2 >= 2;
  gte_false:        1 >= 2;
  negate:           -2;
  sum:              1+2;
  minus:            2-1;
  product:          1*2*3;
  power:            2**10;
  divide:           16/2;
  modulo:           16 % 3; # 16=3*5+1 -> 1
  get_in:           {:a ["x", "y", "z"], :b nil}[:a, 1];  # "y"
  get_in_nil:       [1, 2, 3][100];                     # nil
  list_concat:      [...[1, 2, 3], ...[4, 5, 6]];          # [1 2 3 4 5 6]
  map_merge:        {...{:a 1, :b 2}, ...{:a 2, :c 2}};  # {:a 2 :b 2 :c 2}
  list_comp:        for x <- [1, 2], y <- [3, 4], x*y;  # [3, 4, 6, 8]
}