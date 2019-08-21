import math, time from "std.tf";

alias math.NaN? as NaN?;

library lib {
  f: (x) -> x;
}

library op {
  l_l: (long x, long y) -> x // y;
  d_d: (double x, double y) -> x // y;
  dc_dc: (decimal x, decimal y) -> x // y;
}

library operator_spec {

  nil_nil: nil // nil  == nil;

  # associativity
  assoc_1: 8*4//2  == 16;
  assoc_2: 8//4*2  == 4;
  assoc_3: 8//4//2 == 1;

  # negative arguments
  l8_l3:    8 //  3 ==  2;
  ln8_l3:  -8 //  3 == -2;
  l8_ln3:   8 // -3 == -2;
  ln8_ln3: -8 // -3 ==  2;

  l0_l1: 0 // 1        == 0;
  l1_l0: try   1 // 0  catch e e[:code] == "DIVISION_BY_ZERO";
  l1_l1: 1 // 1        == 1;

  d0_d1: 0.0 // 1.0    == 0;
  d1_d0: try 1.0 // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  d1_d1: 1.0 // 1.0    == 1;

  dc0_dc1: 0d // 1d    == 0;
  dc1_dc0: try 1d // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  dc1_dc1: 1d // 1d    == 1;

  l0_d0: try   0 // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  l0_d1:       0 // 1.0    == 0;
  l1_d0:  try  1 // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  ln1_d0: try -1 // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  l1_d1:       1 // 1.0    == 1;
  l0_dc0: try   0 // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  l0_dc1:       0 // 1d    == 0;
  l1_dc0:  try  1 // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  ln1_dc0: try -1 // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  l1_dc1:       1 // 1d    == 1;

  d0_l0: try   0.0 // 0 catch e e[:code] == "DIVISION_BY_ZERO";
  d0_l1:       0.0 // 1    == 0;
  d1_l0: try   1.0 // 0 catch e e[:code] == "DIVISION_BY_ZERO";
  dn1_l0: try -1.0 // 0 catch e e[:code] == "DIVISION_BY_ZERO";
  d1_l1:       1.0 // 1    == 1;
  d0_dc0: try   0.0 // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  d0_dc1:       0.0 // 1d    == 0;
  d1_dc0: try   1.0 // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  dn1_dc0: try -1.0 // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  d1_dc1:       1.0 // 1d    == 1;

  dc0_l0: try   0d // 0 catch e e[:code] == "DIVISION_BY_ZERO";
  dc0_l1:       0d // 1    == 0;
  dc1_l0: try   1d // 0 catch e e[:code] == "DIVISION_BY_ZERO";
  dcn1_l0: try -1d // 0 catch e e[:code] == "DIVISION_BY_ZERO";
  dc1_l1:       1d // 1    == 1;
  dc0_d0: try   0d // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  dc0_d1:       0d // 1.0    == 0;
  dc1_d0: try   1d // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  dcn1_d0: try -1d // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  dc1_d1:       1d // 1.0    == 1;

  nil_l0: nil // 0     == nil;
  l0_nil:   0 // nil   == nil;

  nil_d0: nil // 0.0   == nil;
  d0_nil: 0.0 // nil   == nil;

  nil_dc0: nil // 0d   == nil;
  dc0_nil: 0d // nil   == nil;

  # conversion to long makes Infinity == max_long and -Infinity == min_long
  inf_inf:    Infinity // Infinity   == 1;
  ninf_inf:   -Infinity // Infinity  == -1;
  inf_ninf:   Infinity // -Infinity  == 0;
  ninf_ninf:  -Infinity // -Infinity == 1;

  inf_d0:  try  Infinity // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  ninf_d0: try -Infinity // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  d0_inf:      0.0 // Infinity  == 0;
  d0_ninf:     0.0 //-Infinity  == 0;

  inf_dc0:  try  Infinity // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  ninf_dc0: try -Infinity // 0d catch e e[:code] == "DIVISION_BY_ZERO";
  dc0_inf:      0d // Infinity  == 0;
  dc0_ninf:     0d //-Infinity  == 0;

  # conversion to long makes NaN == 0
  nan_nan:  try NaN // NaN catch e e[:code] == "DIVISION_BY_ZERO";
  nan_d0:   try NaN // 0.0 catch e e[:code] == "DIVISION_BY_ZERO";
  d0_nan:   try 0.0 // NaN catch e e[:code] == "DIVISION_BY_ZERO";
  dc0_nan:  try 0d // NaN catch e e[:code] == "DIVISION_BY_ZERO";
  nan_1l:   NaN // 1 == 0;

  nil_bar: try     nil // "bar"    catch "error" == "error";
  foo_nil: try   "foo" // nil      catch "error" == "error";
  foo_bar: try   "foo" // "bar"    catch "error" == "error";
  l0_bar:  try       0 // "bar"    catch "error" == "error";
  bar_l0:  try   "bar" // 0        catch "error" == "error";
  b00_l0:  try    0b00 // 0        catch "error" == "error";
  l0_b00:  try       0 // 0b00     catch "error" == "error";


  f_f:     try   lib.f // lib.f    catch "error" == "error";
  dt_dt:   try time.epoch // time.epoch catch "error" == "error";

  op_dc_dc_1_2: op.dc_dc(1d, 2d) === 0;
  op_dc_dc_7_2: op.dc_dc(7d, 2d) === 3;
  op_dc_dc_nil_2: op.dc_dc(nil, 2d) === nil;
  op_dc_dc_nil_nil: op.dc_dc(nil, nil) === nil;
  op_dc_dc_1_nil: op.dc_dc(1d, nil) === nil;
  op_dc_dc_1_0: try op.dc_dc(1d, 0d) catch e e[:code] === "DIVISION_BY_ZERO";

  op_d_d_1_2: op.d_d(1.0, 2.0) === 0;
  op_d_d_7_2: op.d_d(7.0, 2.0) === 3;
  op_d_d_1_nil: op.d_d(1.0, nil) === nil;
  op_d_d_nil_1: op.d_d(nil, 1.0) === nil;
  op_d_d_nil_nil: op.d_d(nil, nil) === nil;
  op_d_d_1_0: try op.d_d(1.0, 0.0) catch e e[:code] === "DIVISION_BY_ZERO";
  op_d_d_n1_0: try op.d_d(-1.0, 0.0) catch e e[:code] === "DIVISION_BY_ZERO";
  op_d_d_nan_1: op.d_d(NaN, 1.0) === 0;
  op_d_d_nan_0: try op.d_d(NaN, 0.0) catch e e[:code] === "DIVISION_BY_ZERO";
  op_d_d_1_nan: try op.d_d(1.0, NaN) catch e e[:code] === "DIVISION_BY_ZERO";
  op_d_d_0_nan: try op.d_d(0.0, NaN) catch e e[:code] === "DIVISION_BY_ZERO";
  op_d_d_nan_nan: try op.dc_dc(NaN, NaN) catch e e[:code] === "DIVISION_BY_ZERO";
  op_d_d_0_inf: op.d_d(0.0, Infinity) === 0;
  op_d_d_0_ninf: op.d_d(0.0, -Infinity) === 0;
  op_d_d_inf_0: try op.dc_dc(Infinity, 0.0) catch e e[:code] === "DIVISION_BY_ZERO";
  op_d_d_ninf_0: try op.dc_dc(-Infinity, 0.0) catch e e[:code] === "DIVISION_BY_ZERO";

  op_l_l_1_2: op.l_l(1, 2) === 0;
  op_l_l_7_2: op.l_l(7, 2) === 3;
  op_l_l_1_0: try op.l_l(1, 0) catch e e[:code] === "DIVISION_BY_ZERO";
  op_l_l_n1_0: try op.l_l(-1, 0) catch e e[:code] === "DIVISION_BY_ZERO";
  op_l_l_1_nil: op.l_l(1, nil) === nil;
  op_l_l_nil_1: op.l_l(nil, 1) === nil;
  op_l_l_nil_nil: op.l_l(nil, nil) === nil;

}