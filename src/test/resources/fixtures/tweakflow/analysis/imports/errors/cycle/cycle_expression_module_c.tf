import * as m_a from "./cycle_expression_module_a.tf";

export library lib_c {
  a: m_a.lib_a.a;
}