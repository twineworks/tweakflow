
library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_nil: nil default nil       == nil;

  nil_a: nil default []          == [];
  ca_s:  [1,2,3][10] default "x" == "x";
  ca_p:  [1,2,3][2] default "x"  == 3;
  fn_d:  lib.f(nil) default "x"  == "x";

  let_many_defaults:
    let {
      n1: nil;
      n2: nil;
      n3: nil;
      n4: nil;
      n5: nil;
      n6: nil;
      n7: nil;
      n8: nil;
      n9: nil;
      n10: nil;
      n11: nil;
      n12: nil;
      n13: nil;
      n14: nil;
      n15: nil;
      n16: nil;
      n17: nil;
      n18: nil;
      n19: nil;
      n20: nil;
      n21: nil;
      n22: nil;
      n23: nil;
      n24: nil;
      n25: nil;
      n26: nil;
      n27: nil;
      n28: nil;
      n29: nil;
      n30: nil;
      n31: nil;
      n32: nil;
      n33: nil;
      s: (n1 default 0) + (n2 default 0) + (n3 default 0) + (n4 default 0) + (n5 default 0) + (n6 default 0) + (n7 default 0) + (n8 default 0) + (n9 default 0) + (n10 default 0) + (n11 default 0) + (n12 default 0) + (n13 default 0) + (n14 default 0) + (n15 default 0) + (n16 default 0) + (n17 default 0) + (n18 default 0) + (n19 default 0) + (n20 default 0) + (n21 default 0) + (n22 default 0) + (n23 default 0) + (n24 default 0) + (n25 default 0) + (n26 default 0) + (n27 default 0) + (n28 default 0) + (n29 default 0) + (n30 default 0) + (n31 default 0) + (n32 default 0) + (n33 default 0);
    } s == 0;
}
