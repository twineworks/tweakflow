import time from 'std';

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil: (nil && nil)  == false;

  t_t:   (true  && true)  == true;
  t_f:   (true  && false) == false;
  f_t:   (false && true)  == false;
  f_f:   (false && false) == false;

  l0_l1: (0 && 1)        == false;
  l1_l0: (1 && 0)        == false;
  l1_l1: (1 && 1)        == true;

  d0_d1: (0.0 && 1.0)    == false;
  d1_d0: (1.0 && 0.0)    == false;
  d1_d1: (1.0 && 1.0)    == true;

  dc0_dc1: (0.0d && 1.0d)    == false;
  dc1_dc0: (1.0d && 0.0d)    == false;
  dc1_dc1: (1.0d && 1.0d)    == true;

  l0_d0: (0 && 0.0)    == false;
  l0_d1: (0 && 1.0)    == false;
  l1_d0: (1 && 0.0)    == false;
  l1_d1: (1 && 1.0)    == true;

  l0_dc0: (0 && 0.0d)    == false;
  l0_dc1: (0 && 1.0d)    == false;
  l1_dc0: (1 && 0.0d)    == false;
  l1_dc1: (1 && 1.0d)    == true;

  d0_l0: (0.0 && 0)    == false;
  d0_l1: (0.0 && 1)    == false;
  d1_l0: (1.0 && 0)    == false;
  d1_l1: (1.0 && 1)    == true;

  d0_dc0: (0.0 && 0d)    == false;
  d0_dc1: (0.0 && 1d)    == false;
  d1_dc0: (1.0 && 0d)    == false;
  d1_dc1: (1.0 && 1d)    == true;

  dc0_d0: (0d && 0.0)    == false;
  dc1_d0: (1d && 0.0)    == false;
  dc0_d1: (0d && 1.0)    == false;
  dc1_d1: (1d && 1.0)    == true;

  dc0_l0: (0d && 0)    == false;
  dc1_l0: (1d && 0)    == false;
  dc0_l1: (0d && 1)    == false;
  dc1_l1: (1d && 1)    == true;

  nil_l0: (nil && 0)     == false;
  l0_nil: (0 && nil)     == false;

  nil_l1: (nil && 1)     == false;
  l1_nil: (1 && nil)     == false;

  nil_d0: (nil && 0.0)   == false;
  d0_nil: (0.0 && nil)   == false;

  inf_inf:    (Infinity && Infinity)  == true;
  ninf_inf:   (-Infinity && Infinity) == true;
  inf_ninf:   (Infinity && -Infinity) == true;
  ninf_ninf:  (-Infinity && -Infinity) == true;

  inf_d0:     (Infinity && 0.0)   == false;
  ninf_d0:    (-Infinity && 0.0)   == false;
  d0_inf:     (0.0 && Infinity)   == false;
  d0_ninf:    (0.0 && -Infinity)  == false;

  nan_nan:    (NaN && NaN) == false;
  nan_d0:     (NaN && 0.0) == false;
  d0_nan:     (0.0 && NaN) == false;

  foo_bar: ("foo" && "bar") == true;
  l0_bar:      (0 && "bar") == false;
  bar_l0:  ("bar" && 0)     == false;
  be_be:     (0b && 0b)     == false;
  b0_b0:   (0b00 && 0b00)   == true;

  fu_fu:     (lib.f && lib.f) == true;

  dt_dt: (time.epoch && time.epoch) == true;

  m_:  ({} && 1)           == false;
  m_1: ({:a 1} && {:b 2})  == true;

  a_:   ([] && 1)    == false;
  a_1:  ([1] && [1])  == true;

}