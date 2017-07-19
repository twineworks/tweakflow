import * as module_a from "./libs/module_a.tf"
import lib_a as l_a from "./libs/module_a.tf"
import lib_b from "./libs/module_b.tf"

library main {
  e0: module_a.lib_a.a
  e1: l_a.a
  e2: lib_b.b
}