import time from "std"

library lib {
  f: (x) -> x
}

library operator_spec {

  nil_nil: nil < nil  == false

  l0_l1: 0 < 1        == true
  l1_l0: 1 < 0        == false
  l1_l1: 1 < 1        == false

  d0_d1: 0.0 < 1.0    == true
  d1_d0: 1.0 < 0.0    == false
  d1_d1: 1.0 < 1.0    == false

  l0_d0: 0 < 0.0    == false
  l0_d1: 0 < 1.0    == true
  l1_d0: 1 < 0.0    == false
  l1_d1: 1 < 1.0    == false

  d0_l0: 0.0 < 0    == false
  d0_l1: 0.0 < 1    == true
  d1_l0: 1.0 < 0    == false
  d1_l1: 1.0 < 1    == false

  nil_l0: nil < 0     == false
  l0_nil:   0 < nil   == false

  nil_d0: nil < 0.0   == false
  d0_nil: 0.0 < nil   == false

  inf_inf:    Infinity < Infinity  == false
  ninf_inf:  -Infinity < Infinity  == true
  inf_ninf:   Infinity < -Infinity == false
  ninf_ninf: -Infinity < -Infinity == false

  inf_d0:      Infinity < 0.0   == false
  ninf_d0:    -Infinity < 0.0   == true
  d0_inf:      0.0 < Infinity   == true
  d0_ninf:     0.0 < -Infinity  == false

  nan_nan:    NaN < NaN == false
  nan_d0:     NaN < 0.0 == false
  d0_nan:     0.0 < NaN == false

  foo_bar: try   "foo" < "bar"    catch "error" == "error"
  l0_bar:  try       0 < "bar"    catch "error" == "error"
  bar_l0:  try   "bar" < 0        catch "error" == "error"

  f_f:     try   lib.f < lib.f    catch "error" == "error"
  dt_dt:   try time.epoch < time.epoch catch "error" == "error"

}