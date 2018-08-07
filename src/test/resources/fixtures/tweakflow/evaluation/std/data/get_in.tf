import store from "./../../data.tf"
import * as std from "std.tf"

alias std.data.get_in as get_in
alias store.inventory as inv

library get_in_spec {

  bicycle_color:                inv[:bicycle, :color]      == "red";
  bicycle_color_str:            inv["bicycle", "color"]    == "red";

  lord_of_the_rings_price:      inv[:book, 3, :price]       == 22.99;
  lord_of_the_rings_price_str:  inv["book", 3, "price"]     == 22.99;

  moby_dick_isbn:               inv[:book][2][:isbn]      == "0-553-21311-3";

  sword_of_honour_first_char:   (inv[:book, 1, :title] as list)[0]     == "S";

  gets_nil:                     inv[nil]                  == nil;
  gets_from_nil:                nil[:book]                == nil;

  no_default_from_nil:          get_in(nil, [:title], "d")  == nil;
  no_default_from_nil_keys:     get_in({}, nil, "d")  == nil;
  default_from_empty_map:       get_in({}, [:title], "d")   == "d";
  default_from_missing_in_map:  get_in({:a "foo"}, [:title], "d")   == "d";

  default_from_empty_list:        get_in([], [2], "d")   == "d";
  default_from_missing_in_list:   get_in(["foo"], [2], "d")   == "d";

  default_from_invalid_in_list:   get_in(["foo"], [-2], "d")   == "d";
  default_from_high_in_list:      get_in(["foo"], [999999999999999999], "d")   == "d";

  nested_default_map:  get_in(inv, ["book", 3, "miss"], "d") == "d";
  nested_default_list:  get_in(inv, ["book", -1], "d")     == "d";

}