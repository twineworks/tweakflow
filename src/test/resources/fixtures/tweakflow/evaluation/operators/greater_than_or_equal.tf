import time from "std";

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil: nil >= nil  == true;

  l0_l1: 0 >= 1        == false;
  l1_l0: 1 >= 0        == true;
  l1_l1: 1 >= 1        == true;

  d0_d1: 0.0 >= 1.0    == false;
  d1_d0: 1.0 >= 0.0    == true;
  d1_d1: 1.0 >= 1.0    == true;

  dc0_dc1: 0d >= 1d        == false;
  dc1_dc0: 1d >= 0d        == true;
  dc1_dc1: 1d >= 1d        == true;

  l0_d0: 0 >= 0.0    == true;
  l0_d1: 0 >= 1.0    == false;
  l1_d0: 1 >= 0.0    == true;
  l1_d1: 1 >= 1.0    == true;

  l0_dc0: 0 >= 0d    == true;
  l0_dc1: 0 >= 1d    == false;
  l1_dc0: 1 >= 0d    == true;
  l1_dc1: 1 >= 1d    == true;

  d0_l0: 0.0 >= 0    == true;
  d0_l1: 0.0 >= 1    == false;
  d1_l0: 1.0 >= 0    == true;
  d1_l1: 1.0 >= 1    == true;

  d0_dc0: 0.0 >= 0d    == true;
  d0_dc1: 0.0 >= 1d    == false;
  d1_dc0: 1.0 >= 0d    == true;
  d1_dc1: 1.0 >= 1d    == true;

  dc0_d0: 0d >= 0.0    == true;
  dc0_d1: 0d >= 1.0    == false;
  dc1_d0: 1d >= 0.0    == true;
  dc1_d1: 1d >= 1.0    == true;

  dc0_l0: 0d >= 0    == true;
  dc0_l1: 0d >= 1    == false;
  dc1_l0: 1d >= 0    == true;
  dc1_l1: 1d >= 1    == true;

  nil_l0: nil >= 0     == false;
  l0_nil:   0 >= nil   == false;

  nil_d0: nil >= 0.0   == false;
  d0_nil: 0.0 >= nil   == false;

  nil_dc0: nil >= 0d   == false;
  dc0_nil: 0d >= nil   == false;

  inf_inf:    Infinity >= Infinity  == true;
  ninf_inf:  -Infinity >= Infinity  == false;
  inf_ninf:   Infinity >= -Infinity == true;
  ninf_ninf: -Infinity >= -Infinity == true;

  inf_d0:      Infinity >= 0.0   == true;
  ninf_d0:    -Infinity >= 0.0   == false;
  d0_inf:      0.0 >= Infinity   == false;
  d0_ninf:     0.0 >= -Infinity  == true;

  inf_dc0:      Infinity >= 0d   == true;
  ninf_dc0:    -Infinity >= 0d   == false;
  dc0_inf:      0d >= Infinity   == false;
  dc0_ninf:     0d >= -Infinity  == true;

  nan_nan:    NaN >= NaN == false;
  nan_d0:     NaN >= 0.0 == false;
  d0_nan:     0.0 >= NaN == false;
  nan_dc0:     NaN >= 0d == false;
  dc0_nan:     0d >= NaN == false;

  nil_bar: try     nil >= "bar"    catch "error" == "error";
  foo_nil: try   "foo" >= nil      catch "error" == "error";
  foo_bar: try   "foo" >= "bar"    catch "error" == "error";
  l0_bar:  try       0 >= "bar"    catch "error" == "error";
  bar_l0:  try   "bar" >= 0        catch "error" == "error";
  b00_l0:  try    0b00 >= 0        catch "error" == "error";

  f_f:     try   lib.f >= lib.f    catch "error" == "error";

  dt_dt:   try time.epoch >= time.epoch catch "error" == "error";

}