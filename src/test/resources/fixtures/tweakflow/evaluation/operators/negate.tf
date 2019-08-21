import math, time from "std.tf";

alias math.NaN? as NaN?;
alias math.min_long as min_long;
alias math.max_long as max_long;

library lib {
  f: (x) -> x;
}

library operator_spec {

  of_nil: -nil  == nil;

  l0: -(0) === 0;
  l1: -(1) === -1;
  ln1: -(-1) === 1;

  # integer overflow
  l_min: -(min_long) === min_long;

  d0: -(0.0) === 0.0;
  d1: -(1.0) === -1.0;
  dn1: -(-1.0) === 1.0;

  dc0: -(0d) === 0d;
  dc1: -(1d) === -1d;
  dcn1: -(-1d) === 1d;

  ninf:   -(-Infinity) === Infinity;
  inf:     -(Infinity) === -Infinity;

  nan:    NaN?(-(NaN)) == true;

  bar: try     -("bar")    catch "error" == "error";
  b00:  try    -(0b00)     catch "error" == "error";

  f:    try   -(lib.f)      catch "error" == "error";
  dt:   try   -(time.epoch) catch "error" == "error";

}