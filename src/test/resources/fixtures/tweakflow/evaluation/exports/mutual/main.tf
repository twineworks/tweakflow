import a from "./mutual_a.tf";
import b from "./mutual_b.tf";

library main_spec {
  a_a0: a.a0 == 0;
  a_a1: a.a1 == 1;
  b_b0: b.b0 == 1;
  b_b1: b.b1 == 0;
}