import store from "./../../data.tf"
import data from "std"

alias data.get as get
alias store.inventory as inv

library get_spec {

  bicycle_color:                get(get(inv, :bicycle), :color)      == "red"

  lord_of_the_rings_price:      get(get(get(inv, :book), 3), :price)     == 22.99
  moby_dick_isbn:               get(get(get(inv, :book), 2), :isbn)      == "0-553-21311-3"

  gets_nil:                     get(inv, nil)                  == nil
  gets_from_nil:                get(nil, :book)                == nil

  no_default_from_nil:          get(nil, :title, "d")  == nil
  no_default_from_nil_key:      get({}, nil, "d")  == nil
  default_from_empty_map:       get({}, :title, "d")   == "d"
  default_from_missing_in_map:  get({:a "foo"}, :title, "d")   == "d"

  default_from_empty_list:        get([], 2, "d")   == "d"
  default_from_missing_in_list:   get(["foo"], 2, "d")   == "d"

  default_from_invalid_in_list:   get(["foo"], -2, "d")   == "d"
  default_from_high_in_list:      get(["foo"], 999999999999999999, "d")   == "d"

}