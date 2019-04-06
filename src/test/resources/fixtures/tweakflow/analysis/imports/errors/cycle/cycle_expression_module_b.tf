import * as m_c from "./cycle_expression_module_c.tf";

export library lib_b {
  a: m_c.lib_c.a;
}