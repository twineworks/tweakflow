import math, time from "std";
alias math.max_long as max_long;

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil: (nil ^ nil)  == nil;

  l0_l0: (0 ^ 0)        == 0;
  l0_l1: (0 ^ 1)        == 1;
  l1_l0: (1 ^ 0)        == 1;
  l1_l1: (1 ^ 1)        == 0;

  l1_l2_l4_l8: (1^2^4^8) == 15;

  self_xor: (max_long ^ max_long) == 0;

  d0_d4: (0.0 ^ 4.0)    == 4;
  d0_d1: (0.0 ^ 1.0)    == 1;
  d1_d0: (1.0 ^ 0.0)    == 1;
  d1_d1: (1.0 ^ 1.0)    == 0;

  l0_d0: (0 ^ 0.0)    == 0;
  l0_d1: (0 ^ 1.0)    == 1;
  l1_d0: (1 ^ 0.0)    == 1;
  l1_d1: (1 ^ 1.0)    == 0;

  d0_l0: (0.0 ^ 0)    == 0;
  d0_l1: (0.0 ^ 1)    == 1;
  d1_l0: (1.0 ^ 0)    == 1;
  d1_l1: (1.0 ^ 1)    == 0;

  nil_l0: (nil ^ 0)     == nil;
  l0_nil: (  0 ^ nil)   == nil;

  nil_d0: (nil ^ 0.0)   == nil;
  d0_nil: (0.0 ^ nil)   == nil;

  nil_bar: try     nil ^ "bar"    catch "error" == "error";
  foo_nil: try   "foo" ^ nil      catch "error" == "error";
  foo_bar: try   "foo" ^ "bar"    catch "error" == "error";
  l0_bar:  try       0 ^ "bar"    catch "error" == "error";
  bar_l0:  try   "bar" ^ 0        catch "error" == "error";
  b00_l0:  try    0b00 ^ 0        catch "error" == "error";
  l0_b00:  try       0 ^ 0b00     catch "error" == "error";

  f_f:     try   lib.f ^ lib.f    catch "error" == "error";
  dt_dt:   try time.epoch ^ time.epoch catch "error" == "error";
}