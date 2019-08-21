import math, time from "std.tf";

alias math.NaN? as NaN?;
alias math.min_long as min_long;
alias math.max_long as max_long;

library lib {
  f: (x) -> x;
}

library op {
  l_l: (long x, long y) -> x + y;
  d_d: (double x, double y) -> x + y;
  dc_dc: (decimal x, decimal y) -> x + y;
}

library operator_spec {

  nil_nil: nil + nil  == nil;

  l0_l1: 0 + 1        === 1;
  l1_l0: 1 + 0        === 1;
  l1_l1: 1 + 1        === 2;

  # integer overflow
  max_inc: max_long + 1 == min_long;

  # going beyond double resolution, then converting back to long
  dmax_inc: max_long + 1.0 == max_long;

  # adding extremes
  min_max: max_long + min_long + 1 == 0;

  d0_d0: 0.0 + 0.0    === 0.0;
  d0_d1: 0.0 + 1.0    === 1.0;
  d1_d0: 1.0 + 0.0    === 1.0;
  d1_d1: 1.0 + 1.0    === 2.0;

  dc0_dc0: 0d + 0d    === 0d;
  dc0_dc1: 0d + 1d    === 1d;
  dc1_dc0: 1d + 0d    === 1d;
  dc1_dc1: 1d + 1d    === 2d;

  dc0_d0: 0d + 0.0    === 0d;
  dc0_d1: 0d + 1.0    === 1d;
  dc1_d0: 1d + 0.0    === 1d;
  dc1_d1: 1d + 1.0    === 2d;

  dc0_l0: 0d + 0    === 0d;
  dc0_l1: 0d + 1    === 1d;
  dc1_l0: 1d + 0    === 1d;
  dc1_l1: 1d + 1    === 2d;

  l0_d0: 0 + 0.0    === 0.0;
  l0_d1: 0 + 1.0    === 1.0;
  l1_d0: 1 + 0.0    === 1.0;
  l1_d1: 1 + 1.0    === 2.0;

  l0_dc0: 0 + 0d    === 0d;
  l0_dc1: 0 + 1d    === 1d;
  l1_dc0: 1 + 0d    === 1d;
  l1_dc1: 1 + 1d    === 2d;

  d0_l0: 0.0 + 0    === 0.0;
  d0_l1: 0.0 + 1    === 1.0;
  d1_l0: 1.0 + 0    === 1.0;
  d1_l1: 1.0 + 1    === 2.0;

  d0_dc0: 0.0 + 0d    === 0d;
  d0_dc1: 0.0 + 1d    === 1d;
  d1_dc0: 1.0 + 0d    === 1d;
  d1_dc1: 1.0 + 1d    === 2d;

  nil_l0: nil + 0     == nil;
  l0_nil:   0 + nil   == nil;

  nil_d0: nil + 0.0   == nil;
  d0_nil: 0.0 + nil   == nil;

  nil_dc0: nil + 0d   == nil;
  dc0_nil: 0d + nil   == nil;

  inf_inf:    Infinity + Infinity  == Infinity;
  ninf_inf:   NaN?(-Infinity + Infinity) == true;
  inf_ninf:   NaN?(Infinity + -Infinity) == true;
  ninf_ninf: -Infinity + -Infinity == -Infinity;

  inf_d0:      Infinity + 0.0   == Infinity;
  ninf_d0:    -Infinity + 0.0   == -Infinity;
  d0_inf:      0.0 + Infinity   == Infinity;
  d0_ninf:     0.0 + -Infinity  == -Infinity;

  inf_l0:      Infinity + 0   == Infinity;
  ninf_l0:    -Infinity + 0   == -Infinity;
  l0_inf:      0 + Infinity   == Infinity;
  l0_ninf:     0 + -Infinity  == -Infinity;

  inf_dc0:      Infinity + 0d   == Infinity;
  ninf_dc0:    -Infinity + 0d   == -Infinity;
  dc0_inf:      0d + Infinity   == Infinity;
  dc0_ninf:     0d + -Infinity  == -Infinity;

  nan_nan:    NaN?(NaN + NaN) == true;
  nan_d0:     NaN?(NaN + 0.0) == true;
  d0_nan:     NaN?(0.0 + NaN) == true;
  nan_dc0:    NaN?(NaN + 0d) == true;
  dc0_nan:    NaN?(0d + NaN) == true;
  nan_l0:     NaN?(NaN + 0) == true;
  l0_nan:     NaN?(0 + NaN) == true;

  nil_bar: try     nil + "bar"    catch "error" == "error";
  foo_nil: try   "foo" + nil      catch "error" == "error";
  foo_bar: try   "foo" + "bar"    catch "error" == "error";
  l0_bar:  try       0 + "bar"    catch "error" == "error";
  bar_l0:  try   "bar" + 0        catch "error" == "error";
  b00_l0:  try    0b00 + 0        catch "error" == "error";
  l0_b00:  try       0 + 0b00     catch "error" == "error";

  f_f:     try   lib.f + lib.f    catch "error" == "error";
  dt_dt:   try time.epoch + time.epoch catch "error" == "error";

  op_dc_dc_1_2: op.dc_dc(1d, 2d) === 3d;
  op_dc_dc_1_1: op.dc_dc(1d, 1d) === 2d;
  op_dc_dc_nil_2: op.dc_dc(nil, 2d) === nil;
  op_dc_dc_nil_nil: op.dc_dc(nil, nil) === nil;
  op_dc_dc_1_nil: op.dc_dc(1d, nil) === nil;
  op_dc_dc_1_0: op.dc_dc(1d, 0d) === 1d;

  op_d_d_1_2: op.d_d(1.0, 2.0) === 3.0;
  op_d_d_1_1: op.d_d(1.0, 1.0) === 2.0;
  op_d_d_1_nil: op.d_d(1.0, nil) === nil;
  op_d_d_nil_1: op.d_d(nil, 1.0) === nil;
  op_d_d_nil_nil: op.d_d(nil, nil) === nil;
  op_d_d_1_0: op.d_d(1.0, 0.0) === 1.0;
  op_d_d_n1_0: op.d_d(-1.0, 0.0) === -1.0;
  op_d_d_nan_1: NaN?(op.d_d(NaN, 1.0)) === true;
  op_d_d_nan_0: NaN?(op.d_d(NaN, 0.0)) === true;
  op_d_d_1_nan: NaN?(op.d_d(1.0, NaN)) === true;
  op_d_d_0_nan: NaN?(op.d_d(0.0, NaN)) === true;
  op_d_d_nan_nan: NaN?(op.d_d(NaN, NaN)) === true;
  op_d_d_0_inf: op.d_d(0.0, Infinity) === Infinity;
  op_d_d_0_ninf: op.d_d(0.0, -Infinity) === -Infinity;
  op_d_d_inf_0: op.d_d(Infinity, 0.0) === Infinity;
  op_d_d_ninf_0: op.d_d(-Infinity, 0.0) === -Infinity;

  op_l_l_1_2: op.l_l(1, 2) === 3;
  op_l_l_1_1: op.l_l(1, 1) === 2;
  op_l_l_1_0: op.l_l(1, 0) === 1;
  op_l_l_n1_0: op.l_l(-1, 0) === -1;
  op_l_l_1_nil: op.l_l(1, nil) === nil;
  op_l_l_nil_1: op.l_l(nil, 1) === nil;
  op_l_l_nil_nil: op.l_l(nil, nil) === nil;

}