import
        exp_a as conf_a,
        exp_b as conf_b
        from "./lib/conf.tf"

library main_spec {
  conf_a_e0: conf_a.e0 == 0
  conf_a_e1: conf_a.e1 == 1
  conf_b_e0: conf_b.e0 == 10
  conf_b_e1: conf_b.e1 == 20
}
