import * as m_b from "./cycle_expression_module_b.tf"

export library lib_a {
  a: m_b.lib_b.a
}