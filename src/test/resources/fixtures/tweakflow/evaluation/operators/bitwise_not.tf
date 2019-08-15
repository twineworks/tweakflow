import math, time from "std";
library lib {
  f: (x) -> x;
}

library operator_spec {

  nil_: ~nil == nil;

  l0:   ~0 == -1;
  l1:   ~1 == -2;
  ln1: ~-1 == 0;

  d0:   ~0.0    == -1;
  d1:   ~1.0    == -2;
  dn1: ~-1.0    == 0;

  dc0:   ~0.0    == -1;
  dc1:   ~1.0    == -2;
  dcn1: ~-1.0    == 0;

  b0:    try   ~0b00  catch "error" == "error";
  foo:   try   ~"foo"  catch "error" == "error";
  f:     try   ~lib.f  catch "error" == "error";
  dt:    try   ~time.epoch catch "error" == "error";

}