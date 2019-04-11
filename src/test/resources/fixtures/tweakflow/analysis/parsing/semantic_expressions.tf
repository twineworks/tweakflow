library lib
{
  nest_exp:         (1);                      # nested expression
  bindings:         let {a: 1;} true;         # bindings in sub-expression
  string_inter:     "string #{e0}";           # string reference interpolation
  try_catch_e:      try 0 catch e false;      # try evaluating 0, catch as exception e and return false
  try_catch:        try 0 catch false;        # try evaluation 0, ignore exception e and return false
  try_catch_e_t:    try 0 catch e, trace nil; # try evaluating 0, catch exception and trace and return nil
  throw_nil:        throw nil;                # throw an exception
  cast_str_as_long: "0001" as long;           # cast a string to long
  if_then_else:     if true then 1 else 0;    # if ... then ... else ...
  if_else:          if true 1 else 0;         # if ... (then) ... else ...
  type_check:       "foo" is string;          # type check
  reference:        import_name.lib.x;        # reference string
  lib_ref:          library::e0;              # library reference
  mod_short_ref:    ::lib.e0;                 # module reference
  mod_ref:          module::lib.e0;           # module reference
  global_short_ref: $global_var;              # global reference
  global_ref:       global::global_var;       # global reference
  local_ref:        e0;                       # local reference
  f_call:           f();                      # call f
  f_call_1:         f(1);                     # call f with arg0=1
  f_call_a1:        f(a: 1);                  # call f with a=1
  f_call_1_2:       f(1, 2);                  # call f with arg0=1 arg1=2
  f_call_a1_b2:     f(a: 1, b: 2);            # call f with a=1 b=2
  f_call_1_2_c3:    f(1, 2, c: 3);            # call f with arg0=1 arg1=2 c=3
  f_call_1_sp_a1:   f(1, ...{:a 1});          # call f with arg0=1 arg1=splat(:a 1)
  f_call_a_b:       f(:a, :b);                # call f with arg0="a", arg1="b"
  f_call_a_bfoo:    f(:a, b: "foo");          # call f with arg0="a", b="foo"
  f_partial_a:      f(a="foo");               # partial f with a="foo"
  f_partial_a_b:    f(a="foo", b="bar");      # partial f with a="foo", b="bar"
  thread: ->> (8)
           (n) -> n*4,
           (n) -> n+3,
           (n) -> n*n;
  thread_expected: ((n) -> n*n)(((n) -> n+3)(((n) -> n*4)(8)));
  match_42: match 42
        10 ->      "ten",
        20 ->      "twenty",
        42 ->      "the answer to everything",
        default -> "unknown";

}