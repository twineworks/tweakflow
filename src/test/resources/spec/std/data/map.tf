import store from "./../../data.tf";
import * as std from "std.tf";
import assert, expect, expect_error, to, describe, it, subject, before, after from "std/spec";

alias store.inventory as inv;
alias std.data.map as map;

library books {
  all: inv[:book];
  moby_dick: inv[:book, 2];
  lotr: inv[:book, 3];
  ]);
}

library spec {
  spec:
    describe("map", [

  maps_id:  map(
              [1, 2, 3, 4, 5, 6],
              (x) -> x
            )
            ==
            [1, 2, 3, 4, 5, 6];

  maps_mult: map(
               [1, 2, 3, 4, 5, 6],
               (x) -> x*2
             )
             ==
             [2, 4, 6, 8, 10, 12];

  maps_index_closures:
    let {
      f_i: map(
             ["a", "b", "c"],
             (_, i) ->
               () -> i
           );
    }
    map(f_i, (f) -> f()) == [0, 1, 2];

  maps_value_closures:
    let {
      f_appenders: map(
        ["a", "b", "c"],
        (x) ->
          (y) -> y .. " " .. x
      );
    }
    map(f_appenders, (f) -> f("hello"))
    ==
    ["hello a", "hello b", "hello c"];

  maps_books_to_authors:
    map(books.all, (x) -> x[:author])
    ==
    ["Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"];

  prepends_map_strings_with_space:
    map(books.moby_dick, (v) -> if v is string then " " .. v else v)
    ==
    { :category " fiction",
      :author   " Herman Melville",
      :title    " Moby Dick",
      :isbn     " 0-553-21311-3",
      :price    8.99
    };

  masks_values_if_key_starts_with_a_or_i:
    map(
      books.moby_dick,
      (v, k) ->
        let{
          char_0: (k as list)[0];
        }
        if char_0 == "a" || char_0 == "i"
          nil
        else
          v
    )
    ==
    { :category "fiction",
      :author   nil,
      :title    "Moby Dick",
      :isbn     nil,
      :price    8.99
    };

  maps_nil_value:
    map(nil, (x) -> x) == nil;

  empty_list:
    expect(map([], (x) -> x), to.be([]));

  empty_dict:
    expect(map({}, (x) -> x), to.be({}));

  simple_list:
    expect(map([1,2,3], (x) -> x*x), to.be([1,4,9]));

  simple_list_with_index:
    expect(map([1,2,3], (_, i) -> i), to.be([0,1,2]));

  simple_dict:
    expect(map({:a 1, :b 2, :c 3}, (x) -> x*x), to.be({:a 1, :b 4, :c 9}));

  simple_dict_with_key:
    expect(
      map({:a 1, :b 2, :c 3}, (x, k) -> k .. "->" .. x),
      to.be({:a "a->1", :b "b->2", :c "c->3"})
    );

  nil_f:
    expect_error(
      () -> map([0,1], nil),
      to.have_code("NIL_ERROR")
    );

  zero_arg_f:
    expect_error(
      () -> map([0,1], () -> true),
      to.have_code("ILLEGAL_ARGUMENT")
    );

  non_collection_xs:
    expect_error(
      () -> map("foo", (x) -> x),
      to.have_code("ILLEGAL_ARGUMENT")
    );
  ]);
}