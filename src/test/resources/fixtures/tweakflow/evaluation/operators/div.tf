import math, time from "std";

alias math.NaN? as NaN?;

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil: nil / nil  == nil;

  # associativity
  assoc_1: 8*4/2 == 16;
  assoc_2: 8/4*2 == 4;
  assoc_3: 8/4/2 == 1;

  l0_l1: 0 / 1        == 0.0;
  l1_l0: 1 / 0        == Infinity;
  l1_l1: 1 / 1        == 1.0;

  d0_d1: 0.0 / 1.0    == 0.0;
  d1_d0: 1.0 / 0.0    == Infinity;
  d1_d1: 1.0 / 1.0    == 1.0;

  dc0_dc2: 0.0d / 2.0d    === 0.0d;
  dc1_dc2: 1.0d / 2.0d    === 0.5d;
  dc0_dc1: 0.0d / 1.0d    === 0.0d;
  dc1_dc1: 1.0d / 1.0d    === 1.0d;
  dc1_p5_dc3: 1.00000d / 3d  === 0.33333333333333333333d;
  dc1_p2_dc3: 1.00d / 3d  === 0.33333333333333333333d;
  dc1_dc3: 1d / 3d  === 0.33333333333333333333d;
  dc1_dc8: 1d / 8d  === 0.125d;
  dc1_dc0: try 1.0d / 0.0d catch e e[:code] == "DIVISION_BY_ZERO";
  dc0_dc0: try 0.0d / 0.0d catch e e[:code] == "DIVISION_BY_ZERO";

  dc0_l2: 0.0d / 2    === 0.0d;
  dc1_l2: 1.0d / 2    === 0.5d;
  dc0_l1: 0.0d / 1    === 0.0d;
  dc1_l1: 1.0d / 1    === 1.0d;
  dc1_p5_l3: 1.00000d / 3  === 0.33333333333333333333d;
  dc1_p2_l3: 1.00d / 3  === 0.33333333333333333333d;
  dc1_l3: 1d / 3  === 0.33333333333333333333d;
  dc1_l8: 1d / 8  === 0.125d;
  dc1_l0: try 1.0d / 0 catch e e[:code] == "DIVISION_BY_ZERO";
  dc0_l0: try 0.0d / 0 catch e e[:code] == "DIVISION_BY_ZERO";

  dc0_d2: 0.0d / 2.0    === 0.0d;
  dc1_d2: 1.0d / 2.0    === 0.5d;
  dc0_d1: 0.0d / 1.0    === 0.0d;
  dc1_d1: 1.0d / 1.0    === 1.0d;
  dc1_p5_d3: 1.00000d / 3.0  === 0.33333333333333333333d;
  dc1_p2_d3: 1.00d / 3.0  === 0.33333333333333333333d;
  dc1_d3: 1d / 3.0  === 0.33333333333333333333d;
  dc1_d8: 1d / 8.0  === 0.125d;
  dc1_d0: try 1.0d / 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  dc0_d0: try 0.0d / 0.0 catch e e[:code] == "DIVISION_BY_ZERO";

  d0_dc2: 0.0 / 2d  === 0.0d;
  d1_dc2: 1.0 / 2d  === 0.5d;
  d0_dc1: 0.0 / 1d  === 0.0d;
  d1_dc1: 1.0 / 1d  === 1.0d;
  d1_dc3: 1.0 / 3d  === 0.33333333333333333333d;
  d1_dc8: 1.0 / 8d  === 0.125d;
  d1_dc0: try 1.0 / 0d catch e e[:code] == "DIVISION_BY_ZERO";
  d0_dc0: try 0.0 / 0d catch e e[:code] == "DIVISION_BY_ZERO";

  l0_dc2: 0 / 2d    === 0d;
  l1_dc2: 1 / 2d    === 0.5d;
  l0_dc1: 0 / 1d    === 0d;
  l1_dc1: 1 / 1d    === 1d;
  l1_dc3: 1 / 3d  === 0.33333333333333333333d;
  l1_dc8: 1 / 8d  === 0.125d;
  l1_dc0: try 1 / 0d catch e e[:code] == "DIVISION_BY_ZERO";
  l0_dc0: try 0 / 0d catch e e[:code] == "DIVISION_BY_ZERO";

  l0_d0: NaN?(0 / 0.0) == true;
  l0_d1:   0 / 1.0    == 0.0;
  l1_d0:   1 / 0.0    == Infinity;
  ln1_d0: -1 / 0.0    == -Infinity;
  l1_d1:   1 / 1.0    == 1.0;

  d0_l0: NaN?(0.0 / 0) == true;
  d0_l1:   0.0 / 1    == 0.0;
  d1_l0:   1.0 / 0    == Infinity;
  dn1_l0: -1.0 / 0    == -Infinity;
  d1_l1:   1.0 / 1    == 1.0;

  nil_l0: nil / 0     == nil;
  l0_nil:   0 / nil   == nil;

  nil_d0: nil / 0.0   == nil;
  d0_nil: 0.0 / nil   == nil;

  inf_inf:    NaN?(Infinity / Infinity)   == true;
  ninf_inf:   NaN?(-Infinity / Infinity)  == true;
  inf_ninf:   NaN?(Infinity / -Infinity)  == true;
  ninf_ninf:  NaN?(-Infinity / -Infinity) == true;

  inf_d0:      Infinity / 0.0   == Infinity;
  ninf_d0:     -Infinity / 0.0  == -Infinity;
  inf_dc0:     Infinity / 0d    == Infinity;
  ninf_dc0:    -Infinity / 0d   == -Infinity;
  inf_l0:      Infinity / 0     == Infinity;
  ninf_l0:     -Infinity / 0    == -Infinity;

  d0_inf:      0.0 / Infinity   === 0.0;
  d0_ninf:     0.0 /-Infinity   === 0.0;
  l0_inf:      0 / Infinity   === 0.0;
  l0_ninf:     0 /-Infinity   === 0.0;
  dc0_inf:     0d / Infinity   === 0d;
  dc0_ninf:    0d /-Infinity   === 0d;

  nan_nan:    NaN?(NaN / NaN) == true;
  nan_d0:     NaN?(NaN / 0.0) == true;
  d0_nan:     NaN?(0.0 / NaN) == true;
  nan_dc0:    NaN?(NaN / 0d) == true;
  dc0_nan:    NaN?(0d / NaN) == true;
  nan_l0:     NaN?(NaN / 0) == true;
  l0_nan:     NaN?(0 / NaN) == true;

  nil_bar: try     nil / "bar"    catch "error" == "error";
  foo_nil: try   "foo" / nil      catch "error" == "error";
  foo_bar: try   "foo" / "bar"    catch "error" == "error";
  l0_bar:  try       0 / "bar"    catch "error" == "error";
  bar_l0:  try   "bar" / 0        catch "error" == "error";
  b00_l0:  try    0b00 / 0        catch "error" == "error";
  l0_b00:  try       0 / 0b00     catch "error" == "error";


  f_f:     try   lib.f / lib.f    catch "error" == "error";
  dt_dt:   try time.epoch / time.epoch catch "error" == "error";
}