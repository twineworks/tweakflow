import * as std from "std.tf"

alias std.core.inspect as inspect

library inspect_spec {

  inspect_nil:
    inspect(nil) == "nil";

  inspect_true:
    inspect(true) == "true";

  inspect_false:
    inspect(false) == "false";

  inspect_long:
    inspect(1) == "1";

  inspect_double:
    inspect(3.14) == "3.14";

  inspect_nan:
    inspect(NaN) == "NaN";

  inspect_infinity:
    inspect(Infinity) == "Infinity";

  inspect_neg_infinity:
    inspect(-Infinity) == "-Infinity";

  inspect_empty_list:
    inspect([]) == "[]";

  inspect_simple_list:
    inspect([1, 2, 3]) == "[1, 2, 3]";

  inspect_empty_dict:
    inspect({}) == "{}";

  inspect_simple_dict:
    let {
      str: inspect({:k1 "v1", :k2 "v2"});
    }
    # item order in maps is not defined
    # so string representation could be in any order
    str ==
'{
  :k1 "v1",
  :k2 "v2"
}'
    ||
    str ==
'{
  :k2 "v2",
  :k1 "v1"
}'

  inspect_dict_escaped_keys:
  let {
    str: inspect({:`k 1` "v1", :`k 2` "v2"});
  }
  # item order in maps is not defined
  # so string representation could be in any order
  str ==
'{
  :`k 1` "v1",
  :`k 2` "v2"
}'
  ||
  str ==
'{
  :`k 2` "v2",
  :`k 1` "v1"
}'

}