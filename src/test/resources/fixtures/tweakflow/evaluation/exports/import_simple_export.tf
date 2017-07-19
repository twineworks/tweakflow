import library_a as a from "./lib/a.tf"
import library_b as b from "./lib/b.tf"
import * as m_a from "./lib/a.tf"
import * as m_b from "./lib/b.tf"

library main_spec {
  ae0:   a.e0 == 0
  ae1:   a.e1 == 1
  be0:   b.e0 == 10
  be1:   b.e1 == 20
  m_ae0: m_a.library_a.e0  == 0
  m_ae1: m_a.library_a.e1  == 1
  m_be0: m_b.library_b.e0  == 10
  m_be1: m_b.library_b.e1  == 20
}
