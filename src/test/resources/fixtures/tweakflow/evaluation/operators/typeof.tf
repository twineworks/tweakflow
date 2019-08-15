import time from "std";

library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_void: typeof nil      == "void";

  bool: typeof true         == "boolean";
  b: typeof 0b              == "binary";
  l0: typeof 0              == "long";
  d0: typeof 0.0            == "double";
  dc0: typeof 0d            == "decimal";
  f: typeof lib.f           == "function";
  m: typeof {}              == "dict";
  a: typeof []              == "list";
  s: typeof ""              == "string";
  dt: typeof time.epoch     == "datetime";

}