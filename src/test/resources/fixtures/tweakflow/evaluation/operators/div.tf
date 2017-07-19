import math, time from "std"
alias math.nan? as nan?


library lib {
  f: (x) -> x
}

library operator_spec {

  nil_nil: nil / nil  == nil

  l0_l1: 0 / 1        == 0.0
  l1_l0: 1 / 0        == Infinity
  l1_l1: 1 / 1        == 1.0

  d0_d1: 0.0 / 1.0    == 0.0
  d1_d0: 1.0 / 0.0    == Infinity
  d1_d1: 1.0 / 1.0    == 1.0

  l0_d0: nan?(0 / 0.0) == true
  l0_d1:   0 / 1.0    == 0.0
  l1_d0:   1 / 0.0    == Infinity
  ln1_d0: -1 / 0.0    == -Infinity
  l1_d1:   1 / 1.0    == 1.0

  d0_l0: nan?(0.0 / 0) == true
  d0_l1:   0.0 / 1    == 0.0
  d1_l0:   1.0 / 0    == Infinity
  dn1_l0: -1.0 / 0    == -Infinity
  d1_l1:   1.0 / 1    == 1.0

  nil_l0: nil / 0     == nil
  l0_nil:   0 / nil   == nil

  nil_d0: nil / 0.0   == nil
  d0_nil: 0.0 / nil   == nil

  inf_inf:    nan?(Infinity / Infinity)   == true
  ninf_inf:   nan?(-Infinity / Infinity)  == true
  inf_ninf:   nan?(Infinity / -Infinity)  == true
  ninf_ninf:  nan?(-Infinity / -Infinity) == true

  inf_d0:      Infinity / 0.0         == Infinity
  ninf_d0:     -Infinity / 0.0        == -Infinity
  d0_inf:      0.0 / Infinity         == 0.0
  d0_ninf:     0.0 /-Infinity         == 0.0

  nan_nan:    nan?(NaN / NaN) == true
  nan_d0:     nan?(NaN / 0.0) == true
  d0_nan:     nan?(0.0 / NaN) == true

  foo_bar: try   "foo" / "bar"    catch "error" == "error"
  l0_bar:  try       0 / "bar"    catch "error" == "error"
  bar_l0:  try   "bar" / 0        catch "error" == "error"

  f_f:     try   lib.f / lib.f    catch "error" == "error"
  dt_dt:   try time.epoch / time.epoch catch "error" == "error"
}