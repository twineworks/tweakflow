library lib {

  string string_direct: "hello"
  string string_cast: 1

  boolean boolean_direct: false
  boolean boolean_cast: 1

  long long_direct: 1
  long long_cast: "123"

  double double_direct: 1.0
  double double_cast: "123.0"

  list list_direct: []
  list list_cast: "hello"

  dict map_direct: {}
  dict map_cast:  ["key" "value"]

  map_keys: {1 "one" 2 "two"}

  function f_direct: () -> long 1
  function f_cast: () -> string 1

}