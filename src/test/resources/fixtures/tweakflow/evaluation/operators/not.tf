import time from "std"

library lib {
  f: (x) -> x
}

library operator_spec {

  nil_: !nil == true

  m_: !{}       == true
  m_1: !{:a 1}  == false

  a_:   ![]  == true
  a_1:  ![1] == false

  bt:   !true == false
  bf:   !false == true

  l0:   !0 == true
  l1:   !1 == false
  ln1: !-1 == false

  d0:   !0.0    == true
  d1:   !1.0    == false
  dn1: !-1.0    == false

  foo: !"foo"  == false
  s_:  !""     == true      # empty string casts to boolean false
  f:   !lib.f  == false

  dt:  !time.epoch == false # time always casts to true

}