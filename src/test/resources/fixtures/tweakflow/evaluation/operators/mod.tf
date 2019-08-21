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

library op {
  l_l: (long x, long y) -> x % y;
  d_d: (double x, double y) -> x % y;
  dc_dc: (decimal x, decimal y) -> x % y;
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

  # decimal operation
  dc0_dc1:  0d % 1d    === 0d;
  dc1_dc0:  try 1d % 0d catch e e[:code] == "DIVISION_BY_ZERO";
  dc1_dc1:  1d % 1d    === 0d;
  dc8_dc5:  8d % 5d    === 3d;
  dc5_dc25: 5d % 2.5d  === 0d;
  dc1_dc03: 1d % 0.3d ===  0.1d;
  dcn1_dc03: -1d %  0.3d === -0.1d;
  dcn1_dcn03: -1d % -0.3d === -0.1d;
  dc1_dcn03:  1d % -0.3d === 0.1d;

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

  nil_dc0: nil % 0d   == nil;
  dc0_nil: 0d % nil   == nil;

  inf_inf:    NaN?(Infinity % Infinity)   == true;
  ninf_inf:   NaN?(-Infinity % Infinity)  == true;
  inf_ninf:   NaN?(Infinity % -Infinity)  == true;
  ninf_ninf:  NaN?(-Infinity % -Infinity) == true;

  inf_d0:      NaN?(Infinity % 0.0)   == true;
  ninf_d0:     NaN?(-Infinity % 0.0)  == true;
  inf_dc0:     NaN?(Infinity % 0d)    == true;
  ninf_dc0:    NaN?(-Infinity % 0d)   == true;
  inf_d1:      NaN?(Infinity % 1.0)   == true;
  ninf_d1:     NaN?(-Infinity % 1.0)  == true;
  inf_l1:      NaN?(Infinity % 1)     == true;
  ninf_l1:     NaN?(-Infinity % 1)    == true;
  inf_dc1:     NaN?(Infinity % 1d)    == true;
  ninf_dc1:    NaN?(-Infinity % 1d)   == true;

  d0_inf:      0.0 % Infinity       === 0.0;
  d0_ninf:     0.0 %-Infinity       === 0.0;
  l0_inf:      0 % Infinity         === 0;
  l0_ninf:     0 %-Infinity         === 0;
  dc0_inf:     0d % Infinity        === 0d;
  dc0_ninf:    0d %-Infinity        === 0d;

  d1_inf:      1.0 % Infinity       === 1.0;
  d1_ninf:     1.0 %-Infinity       === 1.0;
  l1_inf:      1 % Infinity         === 1;
  l1_ninf:     1 %-Infinity         === 1;
  dc1_inf:     1d % Infinity        === 1d;
  dc1_ninf:    1d %-Infinity        === 1d;

  nan_nan:    NaN?(NaN % NaN) == true;
  nan_d0:     NaN?(NaN % 0.0) == true;
  d0_nan:     NaN?(0.0 % NaN) == true;
  nan_l0:     NaN?(NaN % 0) == true;
  l0_nan:     NaN?(0 % NaN) == true;
  nan_dc0:    NaN?(NaN % 0d) == true;
  dc0_nan:    NaN?(0d % NaN) == true;

  nil_bar: try     nil % "bar"    catch "error" == "error";
  foo_nil: try   "foo" % nil      catch "error" == "error";
  foo_bar: try   "foo" % "bar"    catch "error" == "error";
  l0_bar:  try       0 % "bar"    catch "error" == "error";
  bar_l0:  try   "bar" % 0        catch "error" == "error";
  b00_l0:  try    0b00 % 0        catch "error" == "error";
  l0_b00:  try       0 % 0b00     catch "error" == "error";

  f_f:     try   lib.f % lib.f    catch "error" == "error";
  dt_dt:   try time.epoch % time.epoch catch "error" == "error";

  op_dc_dc_1_2: op.dc_dc(1d, 2d) === 1d;
  op_dc_dc_2_2: op.dc_dc(2d, 2d) === 0d;
  op_dc_dc_nil_2: op.dc_dc(nil, 2d) === nil;
  op_dc_dc_nil_nil: op.dc_dc(nil, nil) === nil;
  op_dc_dc_1_nil: op.dc_dc(1d, nil) === nil;
  op_dc_dc_1_0: try op.dc_dc(1d, 0d) catch e e[:code] === "DIVISION_BY_ZERO";

  op_d_d_1_2: op.d_d(1.0, 2.0) === 1.0;
  op_d_d_1_1: op.d_d(1.0, 1.0) === 0.0;
  op_d_d_1_nil: op.d_d(1.0, nil) === nil;
  op_d_d_nil_1: op.d_d(nil, 1.0) === nil;
  op_d_d_nil_nil: op.d_d(nil, nil) === nil;
  op_d_d_1_0: NaN?(op.d_d(1.0, 0.0)) === true;
  op_d_d_n1_0: NaN?(op.d_d(-1.0, 0.0)) === true;
  op_d_d_nan_1: NaN?(op.d_d(NaN, 1.0)) == true;
  op_d_d_nan_0: NaN?(op.d_d(NaN, 0.0)) == true;
  op_d_d_1_nan: NaN?(op.d_d(1.0, NaN)) == true;
  op_d_d_0_nan: NaN?(op.d_d(0.0, NaN)) == true;
  op_d_d_nan_nan: NaN?(op.d_d(NaN, NaN)) == true;
  op_d_d_0_inf: op.d_d(0.0, Infinity) === 0.0;
  op_d_d_0_ninf: op.d_d(0.0, -Infinity) === 0.0;
  op_d_d_inf_0: NaN?(op.d_d(Infinity, 0.0)) === true;
  op_d_d_ninf_0: NaN?(op.d_d(-Infinity, 0.0)) === true;

  op_l_l_1_2: op.l_l(1, 2) === 1;
  op_l_l_1_1: op.l_l(1, 1) === 0;
  op_l_l_1_0: try op.l_l(1, 0) catch e e[:code] === "DIVISION_BY_ZERO";
  op_l_l_n1_0: try op.l_l(-1, 0) catch e e[:code] === "DIVISION_BY_ZERO";
  op_l_l_1_nil: op.l_l(1, nil) === nil;
  op_l_l_nil_1: op.l_l(nil, 1) === nil;
  op_l_l_nil_nil: op.l_l(nil, nil) === nil;
}