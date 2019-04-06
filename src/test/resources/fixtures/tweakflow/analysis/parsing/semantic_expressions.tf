library lib
{
  e0: (1);                     # nested expression
  e1: let {a: 1;} true;         # bindings in sub-expression
  e2: try 0 catch e false;     # try evaluating 0, catch as exception e and return false
  e3: try 0 catch false;       # try evaluation 0, ignore exception e and return false
  e4: throw nil;               # throw an exception
  e5: "0001" as long;          # cast a string to long
  e6: if true then 1 else 0;   # if ... then ... else ...
  e7: if true 1 else 0;        # if ... (then) ... else ...
  e9: try 0 catch e, trace nil; # try evaluating 0, catch exception and trace and return nil
  e10: "foo" is string;        # type check
  e11: import_name.lib.x ;     # reference string
  e12: f();                    # call f
  e13: f(1);                   # call f with arg0=1
  e14: f(a: 1);                # call f with a=1
  e15: f(1, 2);                # call f with arg0=1 arg1=2
  e16: f(a: 1, b: 2);          # call f with a=1 b=2
  e17: f(1, 2, c: 3);          # call f with arg0=1 arg1=2 c=3
  e18: f(1, ...{:a 1});        # call f with arg0=1 arg1=splat(:a 1)
  e20: library::e0;            # library reference
  e21: ::lib.e0;               # module reference
  e22: module::lib.e0;         # module reference
  e25: $global_var;            # global reference
  e26: global::global_var;     # global reference
  e27: e0;                     # local reference
  e29: "string #{e0}";         # string reference interpolation
  e30: f(:a, :b);              # call f with arg0="a", arg1="b"
  e31: f(:a, b: "foo");        # call f with arg0="a", b="foo"
  e32: ->> (8)
           (n) -> n*4,
           (n) -> n+3,
           (n) -> n*n;
  e32_expected: ((n) -> n*n)(((n) -> n+3)(((n) -> n*4)(8)));
  e33: match 42
        10 ->      "ten",
        20 ->      "twenty",
        42 ->      "the answer to everything",
        default -> "unknown";

}