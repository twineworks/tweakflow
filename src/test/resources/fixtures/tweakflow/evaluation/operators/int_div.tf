import math, time from "std.tf"
alias math.nan? as nan?

library lib {
  f: (x) -> x
}

library operator_spec {

  nil_nil: nil // nil  == nil

  # associativity
  assoc_1: 8*4//2  == 16
  assoc_2: 8//4*2  == 4
  assoc_3: 8//4//2 == 1

  l0_l1: 0 // 1        == 0
  l1_l0: try   1 // 0  catch "error" == "error"
  l1_l1: 1 // 1        == 1

  d0_d1: 0.0 // 1.0    == 0
  d1_d0: try 1.0 // 0.0 catch "error" == "error"
  d1_d1: 1.0 // 1.0    == 1

  l0_d0: try   0 // 0.0 catch "error" == "error"
  l0_d1:       0 // 1.0    == 0
  l1_d0:  try  1 // 0.0 catch "error" == "error"
  ln1_d0: try -1 // 0.0 catch "error" == "error"
  l1_d1:       1 // 1.0    == 1

  d0_l0: try   0.0 // 0 catch "error" == "error"
  d0_l1:       0.0 // 1    == 0
  d1_l0: try   1.0 // 0 catch "error" == "error"
  dn1_l0: try -1.0 // 0 catch "error" == "error"
  d1_l1:       1.0 // 1    == 1

  nil_l0: nil // 0     == nil
  l0_nil:   0 // nil   == nil

  nil_d0: nil // 0.0   == nil
  d0_nil: 0.0 // nil   == nil

  # conversion to long makes Infinity == max_long and -Infinity == min_long
  inf_inf:    Infinity // Infinity   == 1
  ninf_inf:   -Infinity // Infinity  == -1
  inf_ninf:   Infinity // -Infinity  == 0
  ninf_ninf:  -Infinity // -Infinity == 1

  inf_d0:  try  Infinity // 0.0 catch "error" == "error"
  ninf_d0: try -Infinity // 0.0 catch "error" == "error"
  d0_inf:      0.0 // Infinity  == 0
  d0_ninf:     0.0 //-Infinity  == 0

  # conversion to long makes NaN == 0
  nan_nan:  try NaN // NaN catch "error" == "error"
  nan_d0:   try NaN // 0.0 catch "error" == "error"
  d0_nan:   try 0.0 // NaN catch "error" == "error"
  nan_1l:   NaN // 1 == 0

  nil_bar: try     nil // "bar"    catch "error" == "error"
  foo_nil: try   "foo" // nil      catch "error" == "error"
  foo_bar: try   "foo" // "bar"    catch "error" == "error"
  l0_bar:  try       0 // "bar"    catch "error" == "error"
  bar_l0:  try   "bar" // 0        catch "error" == "error"

  f_f:     try   lib.f // lib.f    catch "error" == "error"
  dt_dt:   try time.epoch // time.epoch catch "error" == "error"

}