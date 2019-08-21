import math, time from "std.tf";

alias math.NaN? as NaN?;
alias math.min_long as min_long;
alias math.max_long as max_long;

library lib {
  f: (x) -> x;
}

library op {
  l_l: (long x, long y) -> x * y;
  d_d: (double x, double y) -> x * y;
  dc_dc: (decimal x, decimal y) -> x * y;
}

library operator_spec {

  nil_nil: nil * nil  == nil;

  l0_l1: 0 * 1        === 0;
  l1_l0: 1 * 0        === 0;
  l1_l1: 1 * 1        === 1;

  # integer overflow
  max_inc: max_long * 2 < 0;

  # multiplying with double forces double result
  max_inc_dbl: max_long * 2.0 > max_long;

  d0_d1: 0.0 * 1.0    === 0.0;
  d1_d0: 1.0 * 0.0    === 0.0;
  d1_d1: 1.0 * 1.0    === 1.0;

  dc0_dc1: 0d * 1d    === 0d;
  dc1_dc0: 1d * 0d    === 0d;
  dc1_dc1: 1d * 1d    === 1d;

  l0_d0: 0 * 0.0    === 0.0;
  l0_d1: 0 * 1.0    === 0.0;
  l1_d0: 1 * 0.0    === 0.0;
  l1_d1: 1 * 1.0    === 1.0;

  l0_dc0: 0 * 0d    === 0d;
  l0_dc1: 0 * 1d    === 0d;
  l1_dc0: 1 * 0d    === 0d;
  l1_dc1: 1 * 1d    === 1d;

  d0_l0: 0.0 * 0    === 0.0;
  d0_l1: 0.0 * 1    === 0.0;
  d1_l0: 1.0 * 0    === 0.0;
  d1_l1: 1.0 * 1    === 1.0;

  dc0_l0: 0d * 0    === 0d;
  dc0_l1: 0d * 1    === 0d;
  dc1_l0: 1d * 0    === 0d;
  dc1_l1: 1d * 1    === 1d;

  d0_dc0: 0.0 * 0d    === 0d;
  d0_dc1: 0.0 * 1d    === 0d;
  d1_dc0: 1.0 * 0d    === 0d;
  d1_dc1: 1.0 * 1d    === 1d;

  dc0_d0: 0d * 0.0    === 0d;
  dc0_d1: 0d * 1.0    === 0d;
  dc1_d0: 1d * 0.0    === 0d;
  dc1_d1: 1d * 1.0    === 1d;

  nil_l0: nil * 0     == nil;
  l0_nil:   0 * nil   == nil;

  nil_d0: nil * 0.0   == nil;
  d0_nil: 0.0 * nil   == nil;

  nil_dc0: nil * 0d   == nil;
  dc0_nil: 0d * nil   == nil;

  inf_inf:    Infinity * Infinity  == Infinity;
  ninf_inf:   -Infinity * Infinity == -Infinity;
  inf_ninf:   Infinity * -Infinity == -Infinity;
  ninf_ninf: -Infinity * -Infinity == Infinity;

  inf_l1:  Infinity * 1   == Infinity;
  ninf_l1: -Infinity * 1  == -Infinity;
  l1_inf:  1 * Infinity   == Infinity;
  l1_ninf: 1 * -Infinity  == -Infinity;

  inf_d1:  Infinity * 1.0   == Infinity;
  ninf_d1: -Infinity * 1.0  == -Infinity;
  d1_inf:  1.0 * Infinity   == Infinity;
  d1_ninf: 1.0 * -Infinity  == -Infinity;

  inf_dc1:  Infinity * 1d   == Infinity;
  ninf_dc1: -Infinity * 1d  == -Infinity;
  dc1_inf:  1d * Infinity   == Infinity;
  dc1_ninf: 1d * -Infinity  == -Infinity;

  inf_ln1:  Infinity * -1   == -Infinity;
  ninf_ln1: -Infinity * -1  == Infinity;
  ln1_inf:  -1 * Infinity   == -Infinity;
  ln1_ninf: -1 * -Infinity  == Infinity;

  inf_dn1:  Infinity * -1.0   == -Infinity;
  ninf_dn1: -Infinity * -1.0  == Infinity;
  dn1_inf:  -1.0 * Infinity   == -Infinity;
  dn1_ninf: -1.0 * -Infinity  == Infinity;

  inf_dcn1:  Infinity * -1d   == -Infinity;
  ninf_dcn1: -Infinity * -1d  == Infinity;
  dcn1_inf:  -1d * Infinity   == -Infinity;
  dcn1_ninf: -1d * -Infinity  == Infinity;

  inf_l0:  NaN?(Infinity * 0)   == true;
  ninf_l0: NaN?(-Infinity * 0)  == true;
  l0_inf:  NaN?(0 * Infinity)   == true;
  l0_ninf: NaN?(0 * -Infinity)  == true;

  inf_d0:  NaN?(Infinity * 0.0)   == true;
  ninf_d0: NaN?(-Infinity * 0.0)  == true;
  d0_inf:  NaN?(0.0 * Infinity)   == true;
  d0_ninf: NaN?(0.0 * -Infinity)  == true;

  inf_dc0:  NaN?(Infinity * 0d)   == true;
  ninf_dc0: NaN?(-Infinity * 0d)  == true;
  dc0_inf:  NaN?(0d * Infinity)   == true;
  dc0_ninf: NaN?(0d * -Infinity)  == true;

  nan_nan:    NaN?(NaN * NaN) == true;
  nan_d0:     NaN?(NaN * 0.0) == true;
  d0_nan:     NaN?(0.0 * NaN) == true;
  nan_dc0:    NaN?(NaN * 0d) == true;
  dc0_nan:    NaN?(0d * NaN) == true;
  nan_l0:     NaN?(NaN * 0) == true;
  l0_nan:     NaN?(0 * NaN) == true;

  nil_bar: try     nil * "bar"    catch "error" == "error";
  foo_nil: try   "foo" * nil      catch "error" == "error";
  foo_bar: try   "foo" * "bar"    catch "error" == "error";
  l0_bar:  try       0 * "bar"    catch "error" == "error";
  bar_l0:  try   "bar" * 0        catch "error" == "error";
  b00_l0:  try    0b00 * 0        catch "error" == "error";
  l0_b00:  try       0 * 0b00     catch "error" == "error";

  f_f:     try   lib.f * lib.f    catch "error" == "error";
  dt_dt:   try time.epoch * time.epoch catch "error" == "error";

  op_dc_dc_1_2: op.dc_dc(1d, 2d) === 2d;
  op_dc_dc_1_1: op.dc_dc(1d, 1d) === 1d;
  op_dc_dc_nil_2: op.dc_dc(nil, 2d) === nil;
  op_dc_dc_nil_nil: op.dc_dc(nil, nil) === nil;
  op_dc_dc_1_nil: op.dc_dc(1d, nil) === nil;
  op_dc_dc_1_0: op.dc_dc(1d, 0d) === 0d;

  op_d_d_1_2: op.d_d(1.0, 2.0) === 2.0;
  op_d_d_1_1: op.d_d(1.0, 1.0) === 1.0;
  op_d_d_1_nil: op.d_d(1.0, nil) === nil;
  op_d_d_nil_1: op.d_d(nil, 1.0) === nil;
  op_d_d_nil_nil: op.d_d(nil, nil) === nil;
  op_d_d_1_0: op.d_d(1.0, 0.0) === 0.0;
  op_d_d_n1_0: op.d_d(-1.0, 0.0) === 0.0;
  op_d_d_nan_1: NaN?(op.d_d(NaN, 1.0)) === true;
  op_d_d_nan_0: NaN?(op.d_d(NaN, 0.0)) === true;
  op_d_d_1_nan: NaN?(op.d_d(1.0, NaN)) === true;
  op_d_d_0_nan: NaN?(op.d_d(0.0, NaN)) === true;
  op_d_d_nan_nan: NaN?(op.d_d(NaN, NaN)) === true;
  op_d_d_0_inf: NaN?(op.d_d(0.0, Infinity)) === true;
  op_d_d_0_ninf: NaN?(op.d_d(0.0, -Infinity)) === true;
  op_d_d_inf_0: NaN?(op.d_d(Infinity, 0.0)) === true;
  op_d_d_ninf_0: NaN?(op.d_d(-Infinity, 0.0)) === true;

  op_l_l_1_2: op.l_l(1, 2) === 2;
  op_l_l_1_1: op.l_l(1, 1) === 1;
  op_l_l_1_0: op.l_l(1, 0) === 0;
  op_l_l_n1_0: op.l_l(-1, 0) === 0;
  op_l_l_1_nil: op.l_l(1, nil) === nil;
  op_l_l_nil_1: op.l_l(nil, 1) === nil;
  op_l_l_nil_nil: op.l_l(nil, nil) === nil;

}