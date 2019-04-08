import math, time from "std.tf";

alias math.NaN? as NaN?;
alias math.abs as abs;
alias math.min_long as min_long;
alias math.max_long as max_long;

alias lib.close_to as close_to;

library lib {
  f: (x) -> x;
  close_to: (double a, double b, long decimal_places=16) -> boolean
    abs(a-b) < (10 ** -decimal_places);
}

library operator_spec {

  nil_nil: nil % nil  == nil;

  # integer operation
  l0_l1: 0 % 1        == 0;
  l1_l0: try 1 % 0 catch "error" == "error";
  l1_l1: 1 % 1        == 0;
  l8_l5:    8 %  5 ==  3;
  ln8_l5:  -8 %  5 == -3;
  ln8_ln5: -8 % -5 == -3;
  l8_ln5:   8 % -5 ==  3;

  # double operation
  d0_d1:  0.0 % 1.0    == 0;
  d1_d0:  NaN?(1.0 % 0.0) == true;
  d1_d1:  1.0 % 1.0    == 0;
  d8_d5:  8.0 % 5.0    == 3.0;
  d5_d25: 5.0 % 2.5    == 0;
  d1_d03:   close_to( 1.0 %  0.3,  0.1);
  dn1_d03:  close_to(-1.0 %  0.3, -0.1);
  dn1_dn03: close_to(-1.0 % -0.3, -0.1);
  d1_dn03:  close_to( 1.0 % -0.3,  0.1);

  l0_d0:   NaN?(0 % 0.0) == true;
  l0_d1:        0 % 1.0  == 0.0;
  l1_d0:  NaN?( 1 % 0.0) == true;
  ln1_d0: NaN?(-1 % 0.0) == true;
  l1_d1:        1 % 1.0  == 0.0;

  d0_l0: NaN?(0.0 % 0) == true;
  d0_l1:   0.0 % 1    == 0.0;
  d1_l0:  NaN?(1.0 % 0)  == true;
  dn1_l0: NaN?(-1.0 % 0) == true;
  d1_l1:   1.0 % 1    == 0.0;

  nil_l0: nil % 0     == nil;
  l0_nil:   0 % nil   == nil;

  nil_d0: nil % 0.0   == nil;
  d0_nil: 0.0 % nil   == nil;

  inf_inf:    NaN?(Infinity % Infinity)   == true;
  ninf_inf:   NaN?(-Infinity % Infinity)  == true;
  inf_ninf:   NaN?(Infinity % -Infinity)  == true;
  ninf_ninf:  NaN?(-Infinity % -Infinity) == true;

  inf_d0:      NaN?(Infinity % 0.0)   == true;
  ninf_d0:     NaN?(-Infinity % 0.0)  == true;
  d0_inf:      0.0 % Infinity         == 0.0;
  d0_ninf:     0.0 %-Infinity         == 0.0;

  nan_nan:    NaN?(NaN % NaN) == true;
  nan_d0:     NaN?(NaN % 0.0) == true;
  d0_nan:     NaN?(0.0 % NaN) == true;

  nil_bar: try     nil % "bar"    catch "error" == "error";
  foo_nil: try   "foo" % nil      catch "error" == "error";
  foo_bar: try   "foo" % "bar"    catch "error" == "error";
  l0_bar:  try       0 % "bar"    catch "error" == "error";
  bar_l0:  try   "bar" % 0        catch "error" == "error";

  f_f:     try   lib.f % lib.f    catch "error" == "error";
  dt_dt:   try time.epoch % time.epoch catch "error" == "error";
}