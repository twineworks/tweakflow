import math, time from "std.tf"
alias math.nan? as nan?
alias math.min_long as min_long
alias math.max_long as max_long

library lib {
  f: (x) -> x
}

library operator_spec {

  nil_nil: nil * nil  == nil

  l0_l1: 0 * 1        == 0
  l1_l0: 1 * 0        == 0
  l1_l1: 1 * 1        == 1

  # integer overflow
  max_inc: max_long * 2 < 0

  # multiplying with double forces double result
  max_inc_dbl: max_long * 2.0 > max_long

  d0_d1: 0.0 * 1.0    == 0.0
  d1_d0: 1.0 * 0.0    == 0.0
  d1_d1: 1.0 * 1.0    == 1.0

  l0_d0: 0 * 0.0    == 0.0
  l0_d1: 0 * 1.0    == 0.0
  l1_d0: 1 * 0.0    == 0.0
  l1_d1: 1 * 1.0    == 1.0

  d0_l0: 0.0 * 0    == 0.0
  d0_l1: 0.0 * 1    == 0.0
  d1_l0: 1.0 * 0    == 0.0
  d1_l1: 1.0 * 1    == 1.0

  nil_l0: nil * 0     == nil
  l0_nil:   0 * nil   == nil

  nil_d0: nil * 0.0   == nil
  d0_nil: 0.0 * nil   == nil

  inf_inf:    Infinity * Infinity  == Infinity
  ninf_inf:   -Infinity * Infinity == -Infinity
  inf_ninf:   Infinity * -Infinity == -Infinity
  ninf_ninf: -Infinity * -Infinity == Infinity

  inf_d0:  nan?(Infinity * 0.0)   == true
  ninf_d0: nan?(-Infinity * 0.0)  == true
  d0_inf:  nan?(0.0 * Infinity)   == true
  d0_ninf: nan?(0.0 * -Infinity)  == true

  nan_nan:    nan?(NaN * NaN) == true
  nan_d0:     nan?(NaN * 0.0) == true
  d0_nan:     nan?(0.0 * NaN) == true

  nil_bar: try     nil * "bar"    catch "error" == "error"
  foo_nil: try   "foo" * nil      catch "error" == "error"
  foo_bar: try   "foo" * "bar"    catch "error" == "error"
  l0_bar:  try       0 * "bar"    catch "error" == "error"
  bar_l0:  try   "bar" * 0        catch "error" == "error"

  f_f:     try   lib.f * lib.f    catch "error" == "error"
  dt_dt:   try time.epoch * time.epoch catch "error" == "error"

}