import strings as s from 'std.tf';

alias s.concat as concat;

library concat_spec {

  empty:
    concat([]) === "";

  simple:
    concat(["a", "b", "c"]) === "abc";

  with_nil:
    concat(["a", nil, "b"]) === "anilb";

  of_nil:
    concat(nil) === nil;

  of_non_list:
    try
      concat(1)
    catch error
      error[:code]
    ===
    "CAST_ERROR";

  # equivalent of concat("hello" as list), which is concat(["h", "e", "l", "l", "o"])
  of_string:
    concat("hello") === "hello";

  with_castable:
    concat([1,2,3]) === "123";

  with_non_castable:
    try
      concat(["a", {}])
    catch error
      error[:code]
    ===
    "CAST_ERROR";
}