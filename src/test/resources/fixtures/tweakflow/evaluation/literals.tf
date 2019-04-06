library lib
{
  e0: nil;
  e1: "string value";
  e2: 1;
  e3: 0x01;
  e4: [];
  e5: [1, 2, 3];
  e6: [1, "a", ["x","y"]];
  e7: {};
  e8: {:key "value"};
  e9: {:key1 "value1", :key2 "value2"};
  e10: {"k" "v", "sub" {:key "value"}};
  e11: "-\n-";
  e12: true;
  e13: false;
  e14: () -> true; # constant function returning true
  e15: (long x = 0, long y = 0) -> list [x, y];
  e16: if true then "yes" else "no";
  e17: if false then "yes" else "no";
  e18: "foo" is string;
  e19: 1.0;
  e20: 2e1;
}