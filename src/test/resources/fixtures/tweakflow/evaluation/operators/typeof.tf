import time from "std"

library lib {
  f: (x) -> x
}

library operator_spec {

  nil_void: typeof nil      == "void"

  bool: typeof true         == "boolean"
  l0: typeof 0              == "long"
  d0: typeof 0.0            == "double"
  f: typeof lib.f           == "function"
  m: typeof {}              == "dict"
  a: typeof []              == "list"
  s: typeof ""              == "string"
  dt: typeof time.epoch     == "datetime"

}