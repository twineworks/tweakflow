import store as s from "./../../data.tf";
import * as std from "std.tf";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias s.inventory as inv;
alias std.data.reduce as reduce;

library books {
  all: inv[:book];
  ]);
}

library helper {
  group_by: (list items, function f) ->
    reduce(items, {}, (groups, item) ->
      let {
        group:            f(item);
        items_in_group:   if groups[group] then groups[group] else [];
      }
      {...groups, group [...items_in_group, item]}
    );
  ]);
}

library spec {
  spec:
    describe("reduce", [


  sum_book_prices:
    reduce(books.all, 0.0, (sum, book) -> sum + book[:price])
    ==
    books.all[0, :price] +
    books.all[1, :price] +
    books.all[2, :price] +
    books.all[3, :price];

  index_books_by_category:
    helper.group_by(books.all, (x) -> x[:category])
    ==
    {
      :reference  [
        books.all[0]
      ],

      :fiction    [
        books.all[1],
        books.all[2],
        books.all[3]
      ]

    };

  index_books_by_price_range:
    helper.group_by(books.all,
      (x) -> if x[:price] > 10
              "expensive"
              "affordable"
    )
    ==
    {
      :expensive  [
        books.all[1],
        books.all[3]
      ],

      :affordable [
        books.all[0],
        books.all[2]
      ]
    };

  sum:
    expect(reduce([1,2,3,4], 0, (a, x) -> a+x), to.be(10));

  initial_value:
    expect(reduce([], "foo", (a, x) -> a .. x), to.be("foo"));

  of_nil:
    expect(reduce(nil, 0, (a, x) -> x), to.be_nil());

  nil_f:
    expect_error(
      () -> reduce([0,1], 0, nil),
       to.have_code("NIL_ERROR")
    );

  bad_f:
    expect_error(
      () -> reduce([0,1], 0, (x) -> x), # f must accept 2 or more args
       to.have_code("ILLEGAL_ARGUMENT")
    );
  ]);
}