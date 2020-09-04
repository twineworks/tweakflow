library vars {
  any a: 1/[;
  string b: {foo;
  long c: ref;
  any d foo;
  e: -;
  f: for <-;
  g: ->> (;
  h: +;
  i: match;

## some expressions are not picked up by recovery and lead to more damaging errors
#   - an opening let
#      L: let {;
#      K: 1;        # recovery of extra ; above places K in bindings of L effectively capturing subsequent vars
#

}

