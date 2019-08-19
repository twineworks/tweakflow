import time from "std";

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil: (nil || nil)  == false;

  t_t:   (true  || true)  == true;
  t_f:   (true  || false) == true;
  f_t:   (false || true)  == true;
  f_f:   (false || false) == false;


  l0_l0: (0 || 0)        == false;
  l0_l1: (0 || 1)        == true;
  l1_l0: (1 || 0)        == true;
  l1_l1: (1 || 1)        == true;

  d0_d0: (0.0 || 0.0)    == false;
  d0_d1: (0.0 || 1.0)    == true;
  d1_d0: (1.0 || 0.0)    == true;
  d1_d1: (1.0 || 1.0)    == true;

  dc0_dc0: (0d || 0d)    == false;
  dc0_dc1: (0d || 1d)    == true;
  dc1_dc0: (1d || 0d)    == true;
  dc1_dc1: (1d || 1d)    == true;

  l0_d0: (0 || 0.0)    == false;
  l0_d1: (0 || 1.0)    == true;
  l1_d0: (1 || 0.0)    == true;
  l1_d1: (1 || 1.0)    == true;

  l0_dc0: (0 || 0d)    == false;
  l0_dc1: (0 || 1d)    == true;
  l1_dc0: (1 || 0d)    == true;
  l1_dc1: (1 || 1d)    == true;

  d0_l0: (0.0 || 0)    == false;
  d0_l1: (0.0 || 1)    == true;
  d1_l0: (1.0 || 0)    == true;
  d1_l1: (1.0 || 1)    == true;

  d0_dc0: (0.0 || 0d)    == false;
  d0_dc1: (0.0 || 1d)    == true;
  d1_dc0: (1.0 || 0d)    == true;
  d1_dc1: (1.0 || 1d)    == true;

  dc0_l0: (0d || 0)    == false;
  dc0_l1: (0d || 1)    == true;
  dc1_l0: (1d || 0)    == true;
  dc1_l1: (1d || 1)    == true;

  dc0_d0: (0d || 0.0)    == false;
  dc0_d1: (0d || 1.0)    == true;
  dc1_d0: (1d || 0.0)    == true;
  dc1_d1: (1d || 1.0)    == true;

  nil_l0: (nil || 0)     == false;
  l0_nil: (0 || nil)     == false;

  nil_l1: (nil || 1)     == true;
  l1_nil: (1 || nil)     == true;

  nil_d0: (nil || 0.0)   == false;
  d0_nil: (0.0 || nil)   == false;

  nil_d1: (nil || 1.0)   == true;
  d1_nil: (1.0 || nil)   == true;

  nil_dc0: (nil || 0d)   == false;
  dc0_nil: (0d || nil)   == false;

  nil_dc1: (nil || 1d)   == true;
  dc1_nil: (1d || nil)   == true;

  inf_inf:    (Infinity || Infinity)  == true;
  ninf_inf:   (-Infinity || Infinity) == true;
  inf_ninf:   (Infinity || -Infinity) == true;
  ninf_ninf:  (-Infinity || -Infinity) == true;

  inf_l0:     (Infinity || 0)   == true;
  ninf_l0:    (-Infinity || 0)   == true;
  l0_inf:     (0 || Infinity)   == true;
  l0_ninf:    (0 || -Infinity)  == true;

  inf_d0:     (Infinity || 0.0)   == true;
  ninf_d0:    (-Infinity || 0.0)   == true;
  d0_inf:     (0.0 || Infinity)   == true;
  d0_ninf:    (0.0 || -Infinity)  == true;

  inf_dc0:     (Infinity || 0d)   == true;
  ninf_dc0:    (-Infinity || 0d)   == true;
  dc0_inf:     (0d || Infinity)   == true;
  dc0_ninf:    (0d || -Infinity)  == true;

  nan_nan:    (NaN || NaN) == false;
  nan_d0:     (NaN || 0.0) == false;
  d0_nan:     (0.0 || NaN) == false;
  nan_dc0:     (NaN || 0d) == false;
  dc0_nan:     (0d || NaN) == false;
  nan_l0:     (NaN || 0) == false;
  l0_nan:     (0 || NaN) == false;

  nan_d1:     (NaN || 1.0) == true;
  d1_nan:     (1.0 || NaN) == true;
  nan_dc1:     (NaN || 1d) == true;
  dc1_nan:     (1d || NaN) == true;
  nan_l1:     (NaN || 1) == true;
  l1_nan:     (1 || NaN) == true;

  be_be:     (0b || 0b)     == false;
  b0_be:   (0b00 || 0b)     == true;
  be_b0:     (0b || 0b00)   == true;
  b1_b0:   (0b01 || 0b00)   == true;

  foo_bar: ("foo" || "bar") == true;
  l0_bar:      (0 || "bar") == true;
  bar_l0:  ("bar" || 0)     == true;

  fu_fu:     (lib.f || lib.f) == true;

  m_:  ({} || 0)      == false;
  m_1: ({:a 1} || 0)  == true;

  a_:   ([] || 0)     == false;
  a_1:  ([1] || 0)    == true;

  dt_dt: (time.epoch || time.epoch) == true;

}