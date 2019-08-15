library lib
{
  nothing:  nil;
  str:      "string value";
  long_1:   1;
  hex_1:    0x01;
  empty_list: [];
  simple_list: [1, 2, 3];
  nested_list: [1, "a", ["x","y"]];
  empty_dict: {};
  basic_dict: {:key "value"};
  simple_dict: {:key1 "value1", :key2 "value2"};
  nested_dict: {"k" "v", "sub" {:key "value"}};
  newline_escape: "-\n-";
  backslash_escape: "-\\n-";
  hash_alone: "-#-";
  foo: "bar";
  hash_interpolation: "-#{foo}-";
  hash_escaped: "-\#{foo}-";
  bool_t: true;
  bool_f: false;
  f_const: () -> true; # constant function returning true
  f_args: (long x = 0, long y = 0) -> list [x, y];
  dbl_1: 1.0;
  dbl_20: 2e1;
  epoch: 1970-01-01T00:00:00Z@UTC;
  dec_1: 1D;
  dec_20: 2e1_D;
}