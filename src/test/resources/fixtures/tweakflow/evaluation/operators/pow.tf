import math, time from 'std.tf';

alias math.NaN? as NaN?;
alias math.min_long as min_long;
alias math.max_long as max_long;

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil: nil ** nil  == nil;

  l0_l0: 0 ** 0        === 1.0;
  l0_l1: 0 ** 1        === 0.0;
  l1_l0: 1 ** 0        === 1.0;
  l1_l1: 1 ** 1        === 1.0;

  d0_d0: 0.0 ** 0.0    === 1.0;
  d0_d1: 0.0 ** 1.0    === 0.0;
  d1_d0: 1.0 ** 0.0    === 1.0;
  d1_d1: 1.0 ** 1.0    === 1.0;

  dc0_dc0: 0d ** 0d   === 1.0;
  dc0_dc1: 0d ** 1d   === 0.0;
  dc1_dc0: 1d ** 0d   === 1.0;
  dc1_dc1: 1d ** 1d   === 1.0;

  l0_d0: 0 ** 0.0    === 1.0;
  l0_d1: 0 ** 1.0    === 0.0;
  l1_d0: 1 ** 0.0    === 1.0;
  l1_d1: 1 ** 1.0    === 1.0;

  l0_dc0: 0 ** 0d    === 1.0;
  l0_dc1: 0 ** 1d    === 0.0;
  l1_dc0: 1 ** 0d    === 1.0;
  l1_dc1: 1 ** 1d    === 1.0;

  d0_l0: 0.0 ** 0    === 1.0;
  d0_l1: 0.0 ** 1    === 0.0;
  d1_l0: 1.0 ** 0    === 1.0;
  d1_l1: 1.0 ** 1    === 1.0;

  d0_dc0: 0.0 ** 0d    === 1.0;
  d0_dc1: 0.0 ** 1d    === 0.0;
  d1_dc0: 1.0 ** 0d    === 1.0;
  d1_dc1: 1.0 ** 1d    === 1.0;

  # decimals to long power
  dc0_l0: 0d ** 0    === 1d;
  dc0_l1: 0d ** 1    === 0d;
  dc1_l0: 1d ** 0    === 1d;
  dc1_l1: 1d ** 1    === 1d;
  dc1_ll: try 1d ** 999_999_999_999 catch e e[:code] === "ILLEGAL_ARGUMENT";
  dc1_ln: try 1d ** -1 catch e e[:code] === "ILLEGAL_ARGUMENT";

  dc0_d0: 0d ** 0.0    === 1.0;
  dc0_d1: 0d ** 1.0    === 0.0;
  dc1_d0: 1d ** 0.0    === 1.0;
  dc1_d1: 1d ** 1.0    === 1.0;

  nil_l0: nil ** 0     == nil;
  l0_nil:   0 ** nil   == nil;

  nil_d0: nil ** 0.0   == nil;
  d0_nil: 0.0 ** nil   == nil;

  nil_dc0: nil ** 0d     == nil;
  dc0_nil:  0d ** nil   == nil;

  inf_inf:    Infinity ** Infinity  == Infinity;
  ninf_inf:   -Infinity ** Infinity == Infinity;
  inf_ninf:   Infinity ** -Infinity == 0.0;
  ninf_ninf: -Infinity ** -Infinity == 0.0;

  inf_d0:  Infinity ** 0.0   == 1.0;
  ninf_d0: -Infinity ** 0.0  == 1.0;
  d0_inf:  0.0 ** Infinity   == 0.0;
  d0_ninf: 0.0 ** -Infinity  == Infinity;

  inf_dc0:  Infinity ** 0d   == 1.0;
  ninf_dc0: -Infinity ** 0d  == 1.0;
  dc0_inf:  0d ** Infinity   == 0.0;
  dc0_ninf: 0d ** -Infinity  == Infinity;

  inf_l0:  Infinity ** 0   === 1.0;
  ninf_l0: -Infinity ** 0  === 1.0;
  l0_inf:  0 ** Infinity   === 0.0;
  l0_ninf: 0 ** -Infinity  == Infinity;

  nan_nan:   NaN?(NaN ** NaN) == true;
  nan_d0:    NaN ** 0.0 == 1.0;
  d0_nan:    NaN?(0.0 ** NaN) == true;
  nan_dc0:   NaN ** 0d == 1.0;
  dc0_nan:   NaN?(0d ** NaN) == true;
  nan_l0:    NaN ** 0 == 1.0;
  l0_nan:    NaN?(0 ** NaN) == true;

  nil_bar: try     nil ** "bar"    catch "error" == "error";
  foo_nil: try   "foo" ** nil      catch "error" == "error";
  foo_bar: try   "foo" ** "bar"    catch "error" == "error";
  l0_bar:  try       0 ** "bar"    catch "error" == "error";
  bar_l0:  try   "bar" ** 0        catch "error" == "error";
  b00_l0:  try    0b00 ** 0        catch "error" == "error";
  l0_b00:  try       0 ** 0b00     catch "error" == "error";

  f_f:     try   lib.f ** lib.f    catch "error" == "error";
  dt_dt:   try time.epoch ** time.epoch catch "error" == "error";

}