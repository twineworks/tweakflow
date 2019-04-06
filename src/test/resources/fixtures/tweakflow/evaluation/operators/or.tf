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

  l0_d0: (0 || 0.0)    == false;
  l0_d1: (0 || 1.0)    == true;
  l1_d0: (1 || 0.0)    == true;
  l1_d1: (1 || 1.0)    == true;

  d0_l0: (0.0 || 0)    == false;
  d0_l1: (0.0 || 1)    == true;
  d1_l0: (1.0 || 0)    == true;
  d1_l1: (1.0 || 1)    == true;

  nil_l0: (nil || 0)     == false;
  l0_nil: (0 || nil)     == false;

  nil_l1: (nil || 1)     == true;
  l1_nil: (1 || nil)     == true;

  nil_d0: (nil || 0.0)   == false;
  d0_nil: (0.0 || nil)   == false;

  nil_d1: (nil || 1.0)   == true;
  d1_nil: (1.0 || nil)   == true;

  inf_inf:    (Infinity || Infinity)  == true;
  ninf_inf:   (-Infinity || Infinity) == true;
  inf_ninf:   (Infinity || -Infinity) == true;
  ninf_ninf:  (-Infinity || -Infinity) == true;

  inf_d0:     (Infinity || 0.0)   == true;
  ninf_d0:    (-Infinity || 0.0)   == true;
  d0_inf:     (0.0 || Infinity)   == true;
  d0_ninf:    (0.0 || -Infinity)  == true;

  nan_nan:    (NaN || NaN) == false;
  nan_d0:     (NaN || 0.0) == false;
  d0_nan:     (0.0 || NaN) == false;
  l0_nan:     (  1 || NaN) == true;

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