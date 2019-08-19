import math, time from "std.tf";

alias math.NaN? as NaN?;
alias math.min_long as min_long;
alias math.max_long as max_long;

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil: nil - nil  == nil;

  l0_l0: 0 - 0 === 0;
  l0_l1: 0 - 1 === -1;
  l1_l0: 1 - 0 === 1;
  l1_l1: 1 - 1 === 0;

  # integer overflow
  min_dec: min_long - 1 == max_long;

  # going beyond double resolution, then converting back to long
  dmin_dec: min_long - 1.0 == min_long;

  # extremes
  min_max: max_long - min_long == -1;

  # associativity
  assoc_1: 1 - 2 + 3 === 2;
  assoc_2: 1 + 2 - 3 === 0;
  assoc_3: 3 - 2 - 1 === 0;

  d0_d0: 0.0 - 0.0    === 0.0;
  d0_d1: 0.0 - 1.0    === -1.0;
  d1_d0: 1.0 - 0.0    === 1.0;
  d1_d1: 1.0 - 1.0    === 0.0;

  dc0_dc0: 0d - 0d    === 0d;
  dc0_dc1: 0d - 1d    === -1d;
  dc1_dc0: 1d - 0d    === 1d;
  dc1_dc1: 1d - 1d    === 0d;

  l0_d0: 0 - 0.0    === 0.0;
  l0_d1: 0 - 1.0    === -1.0;
  l1_d0: 1 - 0.0    === 1.0;
  l1_d1: 1 - 1.0    === 0.0;

  l0_dc0: 0 - 0d    === 0d;
  l0_dc1: 0 - 1d    === -1d;
  l1_dc0: 1 - 0d    === 1d;
  l1_dc1: 1 - 1d    === 0d;

  d0_l0: 0.0 - 0    === 0.0;
  d0_l1: 0.0 - 1    === -1.0;
  d1_l0: 1.0 - 0    === 1.0;
  d1_l1: 1.0 - 1    === 0.0;

  d0_dc0: 0.0 - 0d    === 0d;
  d0_dc1: 0.0 - 1d    === -1d;
  d1_dc0: 1.0 - 0d    === 1d;
  d1_dc1: 1.0 - 1d    === 0d;

  dc0_d0: 0d - 0.0    === 0d;
  dc0_d1: 0d - 1.0    === -1d;
  dc1_d0: 1d - 0.0    === 1d;
  dc1_d1: 1d - 1.0    === 0d;

  dc0_l0: 0d - 0    === 0d;
  dc0_l1: 0d - 1    === -1d;
  dc1_l0: 1d - 0    === 1d;
  dc1_l1: 1d - 1    === 0d;

  nil_l0: nil - 0     == nil;
  l0_nil:   0 - nil   == nil;

  nil_d0: nil - 0.0   == nil;
  d0_nil: 0.0 - nil   == nil;

  nil_dc0: nil - 0d   == nil;
  dc0_nil: 0d - nil   == nil;

  inf_inf:    NaN?( Infinity - Infinity)  == true;
  ninf_inf:   -Infinity - Infinity == -Infinity;
  inf_ninf:   Infinity - -Infinity == Infinity;
  ninf_ninf:  NaN?(-Infinity - -Infinity) == true;

  inf_d0:      Infinity - 0.0   == Infinity;
  ninf_d0:    -Infinity - 0.0   == -Infinity;
  d0_inf:      0.0 - Infinity   == -Infinity;
  d0_ninf:     0.0 - -Infinity  == Infinity;

  inf_dc0:      Infinity - 0d   == Infinity;
  ninf_dc0:    -Infinity - 0d   == -Infinity;
  dc0_inf:      0d - Infinity   == -Infinity;
  dc0_ninf:     0d - -Infinity  == Infinity;

  inf_l0:      Infinity - 0   == Infinity;
  ninf_l0:    -Infinity - 0   == -Infinity;
  l0_inf:      0 - Infinity   == -Infinity;
  l0_ninf:     0 - -Infinity  == Infinity;

  nan_nan:    NaN?(NaN - NaN) == true;
  nan_d0:     NaN?(NaN - 0.0) == true;
  d0_nan:     NaN?(0.0 - NaN) == true;
  nan_l0:     NaN?(NaN - 0) == true;
  l0_nan:     NaN?(0 - NaN) == true;
  nan_dc0:     NaN?(NaN - 0d) == true;
  dc0_nan:     NaN?(0d - NaN) == true;

  nil_bar: try     nil - "bar"    catch "error" == "error";
  foo_nil: try   "foo" - nil      catch "error" == "error";
  foo_bar: try   "foo" - "bar"    catch "error" == "error";
  l0_bar:  try       0 - "bar"    catch "error" == "error";
  bar_l0:  try   "bar" - 0        catch "error" == "error";
  b00_l0:  try    0b00 - 0        catch "error" == "error";
  l0_b00:  try       0 - 0b00     catch "error" == "error";

  f_f:     try   lib.f - lib.f    catch "error" == "error";
  dt_dt:   try time.epoch - time.epoch catch "error" == "error";

}