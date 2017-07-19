import strings as s from "std.tf"

library length_spec {
  empty:            s.length("") == 0
  missing:          s.length(nil) == nil
  simple:           s.length("a") == 1
  basic:            s.length("foo") == 3
  code_points:      s.length("𐐂") == 1
  more_code_points: s.length("𝒜𝕍") == 2
}