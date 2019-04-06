import store as s from "./../../data.tf";
import * as std from "std.tf";

alias s.inventory as inv;
alias std.data.reduce as reduce;

library books {
  all: inv[:book];
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
}

library reduce_spec {

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

  reduces_nil_value:
    reduce(nil, 0, (x) -> x) == nil;

  reduces_nil_function:
    reduce([0,1], 0, nil) == nil;
}