import * as std from "std.tf";

alias std.core.hash as hash;

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil:  (nil !== nil)  == false;

  true_true:   (true !== true)   == false;
  false_true:  (false !== true)   == true;
  true_false:   (true !== false)  == true;
  false_false: (false !== false)  == false;

  l0_l1: (0 !== 1)        == true;
  l1_l0: (1 !== 0)        == true;
  l1_l1: (1 !== 1)        == false;

  d0_d1: (0.0 !== 1.0)    == true;
  d1_d0: (1.0 !== 0.0)    == true;
  d1_d1: (1.0 !== 1.0)    == false;

  dc0_dc1: (0d !== 1d)    == true;
  dc1_dc0: (1d !== 0d)    == true;
  dc1_dc1: (1d !== 1d)    == false;

  l0_d0: (0 !== 0.0)    == true;
  l0_d1: (0 !== 1.0)    == true;
  l1_d0: (1 !== 0.0)    == true;
  l1_d1: (1 !== 1.0)    == true;

  l0_dc0: (0 !== 0d)    == true;
  l0_dc1: (0 !== 1d)    == true;
  l1_dc0: (1 !== 0d)    == true;
  l1_dc1: (1 !== 1d)    == true;

  d0_l0: (0.0 !== 0)    == true;
  d0_l1: (0.0 !== 1)    == true;
  d1_l0: (1.0 !== 0)    == true;
  d1_l1: (1.0 !== 1)    == true;

  d0_dc0: (0.0 !== 0d)    == true;
  d0_dc1: (0.0 !== 1d)    == true;
  d1_dc0: (1.0 !== 0d)    == true;
  d1_dc1: (1.0 !== 1d)    == true;

  be_be: (0b != 0b) == false;
  be_b0: (0b != 0b00) == true;
  b0_be: (0b00 != 0b) == true;
  b0_b0: (0b00 != 0b00) == false;
  b0_b1: (0b00 != 0b01) == true;
  b1_b0: (0b01 != 0b00) == true;
  b1_b1: (0b01 != 0b01) == false;

  nil_l0: (nil !== 0)     == true;
  l0_nil:  (0 !== nil)   == true;

  nil_d0: (nil !== 0.0)   == true;
  d0_nil: (0.0 !== nil)   == true;

  nil_dc0: (nil !== 0d)   == true;
  dc0_nil: (0d !== nil)   == true;

  inf_inf:    (Infinity !== Infinity)  == false;
  ninf_inf:  (-Infinity !== Infinity)  == true;
  inf_ninf:   (Infinity !== -Infinity) == true;
  ninf_ninf: (-Infinity !== -Infinity) == false;

  inf_d0:      (Infinity !== 0.0)   == true;
  ninf_d0:    (-Infinity !== 0.0)   == true;
  d0_inf:      (0.0 !== Infinity)   == true;
  d0_ninf:     (0.0 !== -Infinity)  == true;

  inf_dc0:      (Infinity !== 0d)   == true;
  ninf_dc0:    (-Infinity !== 0d)   == true;
  dc0_inf:      (0d !== Infinity)   == true;
  dc0_ninf:     (0d !== -Infinity)  == true;

  inf_l0:      (Infinity !== 0)   == true;
  ninf_l0:    (-Infinity !== 0)   == true;
  l0_inf:      (0 !== Infinity)   == true;
  l0_ninf:     (0 !== -Infinity)  == true;

  nan_nan:    (NaN !== NaN) == true; # note that NaN is not identical to itself
  nan_d0:     (NaN !== 0.0) == true;
  d0_nan:     (0.0 !== NaN) == true;
  nan_dc0:    (NaN !== 0d) == true;
  dc0_nan:    (0d !== NaN) == true;
  nan_l0:     (NaN !== 0) == true;
  l0_nan:     (0 !== NaN) == true;

  foo_bar:    ("foo" !== "bar") == true;
  foo_foo:    ("foo" !== "foo") == false;
  l0_bar:         (0 !== "bar") == true;
  bar_l0:     ("bar" !== 0)     == true;
  l0_s0:         (0  !== "0")   == true;
  s0_l0:        ("0" !== 0)     == true;

  f_f:        (lib.f !== lib.f) == true; # note that there is no function equality or identity

  a_a:        ([] !== [])      == false;
  al0_a:     ([0] !== [])      == true;
  al0_al0:   ([0] !== [0])     == false;
  al0_ad0:   ([0] !== [0.0])   == true;
  al0_adc0:  ([0] !== [0d])   == true;
  anan_anan: ([NaN] !== [NaN]) == true;
  af_af:     ([lib.f] !== [lib.f]) == true;
  mnan_mnan: ({:a NaN} !== {:a NaN}) == true;
  mf_mf:     ({:a lib.f} !== {:a lib.f}) == true;

  dt_epoch:      (1970-01-01T00:00:00Z !== 1970-01-01T00:00:00Z) == false;
  dt_epoch_dt:   (1970-01-01T00:00:00Z !== 1970-01-01T23:59:59Z) == true;

  al0_ad1:   ([0] !== [1.0])   == true;
  ad9_al9:   ([9.0] !== [9]) == true;
  adc9_al9:  ([9d] !== [9]) == true;
  ad9_as9:   ([9.0] !== ["9"]) == true;

  a123_mixed: ([1.0, 2, 3d] !== [1d, 2.0, 3]) == true;
  a123_same: ([1.0, 2.0, 3.0] !== [1.0, 2.0, 3.0]) == false;

  m_m:        ({} !== {})          == false;
  mxl0_mxl0:  ({:x 0} !== {:x 0})  == false;
  mxl0_m:     ({:x 0} !== {})      == true;

  mab_mba:    ({:a 1, :b 2} !== {:b 2, :a 1})    == false;

  mld:        ({:a 1} !== {:a 1.0})             == true;
  mldc:       ({:a 1} !== {:a 1d})             == true;

  m_mixed:    ({:a 1, :b 2.0, :c 3d} !== {:a 1d, :b 2, :c 3.0}) == true;

  nested_mixed: {:a [1.0, 2, 3d, {:a 1.0, :b [0]}]}
                !==
                {:a [1d, 2.0, 3, {:b [0.0], :a 1}]}
                ==
                true;

  nested_same:   (
                  {:a [1.0, 2, 3.0, {:a 1.0, :b [0]}]}
                  !==
                  {:a [1.0, 2, 3.0, {:a 1.0, :b [0]}]}
                 )
                 ==
                 false;

}