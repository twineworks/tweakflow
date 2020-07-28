library lib {

  boolean_to_long: true as long;
  boolean_to_double: true as double;
  boolean_to_decimal: true as decimal;
  boolean_to_string: true as string;
  string_to_boolean: "foo" as boolean;
  string_to_long: "123" as long;
  string_to_double: "1.2" as double;
  string_to_decimal: "123.45" as decimal;
  string_to_list: "123" as list;
  long_to_boolean: 1 as boolean;
  long_to_double: 123 as double;
  long_to_decimal: 123 as decimal;
  long_to_string: 123 as string;
  double_to_boolean: 1.0 as boolean;
  double_to_long: 1.2 as long;
  double_to_decimal: 1.2 as decimal;
  double_to_string: 1.2 as string;
  list_to_boolean: [1, 2, 3] as boolean;
  list_to_map: [["key", "value"]] as dict;
  map_to_boolean: {} as boolean;
  map_to_list: {} as list;
  nil_to_boolean: nil as boolean;
  function_to_boolean: (() -> nil) as boolean;

}