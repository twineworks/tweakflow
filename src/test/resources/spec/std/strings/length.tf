import strings as s from 'std.tf';

library spec {
  spec:
    describe("length", [

  empty:            s.length("") == 0;
  missing:          s.length(nil) == nil;
  simple:           s.length("a") == 1;
  basic:            s.length("foo") == 3;
  code_points:      s.length("𐐂") == 1;
  bmp_code_points:  s.length("你好") == 2;
  more_code_points: s.length("𝄞𝒜𝕍") == 3;
  ]);
}