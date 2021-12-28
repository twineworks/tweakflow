import time from "std";

library lib {
  f: (x) -> x;
}

library op {
  l_l: (long x, long y) -> x >= y;
  d_d: (double x, double y) -> x >= y;
  dc_dc: (decimal x, decimal y) -> x >= y;
  dt_dt: (datetime x, datetime y) -> x >= y;
  any_any: (x, y) -> x >= y;
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
  dt_foo:  try  time.epoch >= "foo" catch "error" == "error";
  foo_dt:  try  "foo" >= time.epoch catch "error" == "error";
  dt_l0:   try  time.epoch >= 0     catch "error" == "error";
  l0_dt:   try  0 >= time.epoch     catch "error" == "error";
  dt_b00:  try  time.epoch >= 0b00  catch "error" == "error";
  b00_dt:  try  0b00 >= time.epoch  catch "error" == "error";

  f_f:     try   lib.f >= lib.f    catch "error" == "error";

  dt_dt_epoch_before:            time.epoch >= 1970-01-01T00:00:01        == false;
  dt_dt_epoch_before_other_tz:   time.epoch >= 1970-01-01T00:00:00-01:00  == false;
  dt_dt_epoch_epoch:             time.epoch >= time.epoch                 == true;
  dt_dt_epoch_epoch_other_tz:    time.epoch >= 1970-01-01T01:00:00+01:00  == true;
  dt_nil:                        time.epoch >= nil                        == false;
  nil_dt:                        nil >= time.epoch                        == false;

  op_dc_dc_1_2: op.dc_dc(1d, 2d) === false;
  op_dc_dc_1_1: op.dc_dc(1d, 1d) === true;
  op_dc_dc_nil_2: op.dc_dc(nil, 2d) === false;
  op_dc_dc_nil_nil: op.dc_dc(nil, nil) === true;
  op_dc_dc_1_nil: op.dc_dc(1d, nil) === false;
  op_dc_dc_1_0: op.dc_dc(1d, 0d) === true;

  op_d_d_1_2: op.d_d(1.0, 2.0) === false;
  op_d_d_1_1: op.d_d(1.0, 1.0) === true;
  op_d_d_1_nil: op.d_d(1.0, nil) === false;
  op_d_d_nil_1: op.d_d(nil, 1.0) === false;
  op_d_d_nil_nil: op.d_d(nil, nil) === true;
  op_d_d_1_0: op.d_d(1.0, 0.0) === true;
  op_d_d_n1_0: op.d_d(-1.0, 0.0) === false;
  op_d_d_nan_1: op.d_d(NaN, 1.0) == false;
  op_d_d_nan_0: op.d_d(NaN, 0.0) == false;
  op_d_d_1_nan: op.d_d(1.0, NaN) == false;
  op_d_d_0_nan: op.d_d(0.0, NaN) == false;
  op_d_d_nan_nan: op.d_d(NaN, NaN) == false;
  op_d_d_0_inf: op.d_d(0.0, Infinity) === false;
  op_d_d_0_ninf: op.d_d(0.0, -Infinity) === true;
  op_d_d_inf_0: op.d_d(Infinity, 0.0) === true;
  op_d_d_ninf_0: op.d_d(-Infinity, 0.0) === false;

  op_l_l_1_2: op.l_l(1, 2) === false;
  op_l_l_1_1: op.l_l(1, 1) === true;
  op_l_l_1_0: op.l_l(1, 0) === true;
  op_l_l_n1_0: op.l_l(-1, 0) === false;
  op_l_l_1_nil: op.l_l(1, nil) === false;
  op_l_l_nil_1: op.l_l(nil, 1) === false;
  op_l_l_nil_nil: op.l_l(nil, nil) === true;

  op_dt_dt_epoch_before: op.dt_dt(time.epoch, 1970-01-01T00:00:01) === false;
  op_dt_dt_epoch_before_other_tz: op.dt_dt(time.epoch, 1970-01-01T00:00:00-01:00) === false;
  op_dt_dt_epoch_after: op.dt_dt(time.epoch, 1969-01-01T00:00:00) === true;
  op_dt_dt_epoch_after_other_tz: op.dt_dt(time.epoch, 1969-01-01T00:00:00-01:00) === true;
  op_dt_dt_epoch_epoch: op.dt_dt(time.epoch, time.epoch) === true;
  op_dt_dt_epoch_epoch_other_tz: op.dt_dt(time.epoch, 1970-01-01T01:00:00+01:00) === true;
  op_dt_dt_nil_nil: op.dt_dt(nil, nil) === true;

  op_any_any_epoch_before: op.any_any(time.epoch, 1970-01-01T00:00:01) === false;
  op_any_any_epoch_after: op.any_any(time.epoch, 1969-01-01T00:00:00) === true;
  op_any_any_epoch_epoch: op.any_any(time.epoch, time.epoch) === true;
}