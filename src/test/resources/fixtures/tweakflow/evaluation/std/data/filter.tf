import store from "./../../data.tf"

import * as std from "std.tf"

alias store.inventory as inv
alias std.data.filter as filter

library books {
  moby_dick: inv[:book 2]
}

library filter_spec {

  book_price_above_10:

    filter(inv[:book], (x) -> x[:price] > 10)
    ==
    [
      { :category "fiction",
        :author   "Evelyn Waugh",
        :title    "Sword of Honour",
        :price    12.99
      },
      { :category "fiction",
        :author   "J. R. R. Tolkien",
        :title    "The Lord of the Rings",
        :isbn     "0-395-19395-8",
        :price    22.99
      }
    ]

  even_index_books:
    filter(inv[:book], (_, i) -> i % 2 == 0)
    ==
    [inv[:book 0], inv[:book 2]]

  moby_dick_only_string_entries:                      # filters out price
    filter(books.moby_dick, (x) -> x is string)
    ==
    { :category "fiction",
      :author   "Herman Melville",
      :title    "Moby Dick",
      :isbn     "0-553-21311-3"
    }

  moby_dick_only_double_entries:                     # keeps only price
    filter(books.moby_dick, (x) -> x is double)
    ==
    {:price 8.99}


  moby_dick_only_keys_starting_with_a:
    filter(books.moby_dick, (_, k) -> (k as list)[0] == "a")
    ==
    {:author "Herman Melville"}

  filters_nil:
    filter(nil, (x) -> true) == nil

  filters_using_nil:
    try filter([0,1], nil) catch "error" == "error"

}