
library lib {
  f: (x) -> x
}

library operator_spec {

  nil_nil: nil default nil       == nil

  nil_a: nil default []          == []
  ca_s:  [1,2,3][10] default "x" == "x"
  ca_p:  [1,2,3][2] default "x"  == 3
  fn_d:  lib.f(nil) default "x"  == "x"

}