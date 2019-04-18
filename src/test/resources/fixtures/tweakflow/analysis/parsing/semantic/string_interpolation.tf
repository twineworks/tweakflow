library lib {
  string_ref_inter:           "string #{e0}";
  string_ref_inter_expected:  "string "..(e0 as string);
  string_sum_inter:           "string #{1+2}";
  string_sum_inter_expected:  "string "..((1+2) as string);
}