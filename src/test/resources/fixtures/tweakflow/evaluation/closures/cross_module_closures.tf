
import a from "./module_a.tf"
import b from "./module_b.tf"

library closure_spec {
  a_g: a.f(1)(0) == [0, 1, "b"]
  b_g: b.f(2)(1) == [1, 2, "a"]
}