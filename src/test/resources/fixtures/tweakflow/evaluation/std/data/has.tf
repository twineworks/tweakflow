import store from "./../../data.tf"
import * as std from "std.tf"

alias store.inventory as inv
alias std.data.has? as has?

library books {
  moby_dick: inv[:book, 2]
}

library has_key_spec {

  found_key:
    has?(books.moby_dick, :author) == true

  missing_key:
    has?(books.moby_dick, :missing) == false

  looking_for_nil:
    has?({:a "foo"}, nil) == false

  looking_in_nil:
    has?(nil, :key) == nil

}