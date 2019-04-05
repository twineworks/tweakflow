import store from "./../../data.tf"
import * as std from "std.tf"
import expect, expect_error, to from "std/assert.tf";

alias std.data.get_in as get_in
alias store.inventory as inv

library get_in_spec {

  bicycle_color:                get_in(inv, [:bicycle, :color])    == "red";

  lord_of_the_rings_price:      get_in(inv, [:book, 3, :price])    == 22.99;

  moby_dick_isbn:               get_in(inv, [:book, 2, :isbn])     == "0-553-21311-3";

  gets_nil:                     get_in(inv, [nil])                 == nil;
  gets_from_nil:                get_in(nil, [:book])                == nil;

  no_default_from_nil:          get_in(nil, [:title], "d")  == nil;
  no_default_from_nil_keys:     get_in({}, nil, "d")  == nil;

  default_from_empty_map:       get_in({}, [:title], "d")   == "d";
  default_from_missing_in_map:  get_in({:a "foo"}, [:title], "d")   == "d";

  default_from_empty_list:        get_in([], [2], "d")   == "d";
  default_from_missing_in_list:   get_in(["foo"], [2], "d")   == "d";

  default_from_invalid_in_list:   get_in(["foo"], [-2], "d")   == "d";
  default_from_high_in_list:      get_in(["foo"], [999999999999999999], "d")   == "d";

  nested_default_map:    get_in(inv, ["book", 3, "miss"], "d") == "d";
  nested_default_list:   get_in(inv, ["book", -1], "d")        == "d";

  of_non_list_key:
    expect_error(() -> get_in([1, 2, 3], 0), to.have_code("CAST_ERROR"));

  of_non_collection:
    expect_error(() -> get_in("foo", [0]), to.have_code("ILLEGAL_ARGUMENT"));

  of_invalid_dict_key:
    expect_error(() -> get_in({}, [[]]), to.have_code("CAST_ERROR"));

  of_invalid_list_key:
    expect_error(() -> get_in([], [2019-01-01T]), to.have_code("CAST_ERROR"));

}