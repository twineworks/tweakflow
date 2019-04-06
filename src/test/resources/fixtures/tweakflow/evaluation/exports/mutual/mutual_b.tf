import a from "./mutual_a.tf";

export library b {
  b0: 1;
  b1: a.a0;
}