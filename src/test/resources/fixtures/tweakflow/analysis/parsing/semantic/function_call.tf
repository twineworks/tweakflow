library lib
{
  f_call:           f();                      # call f
  f_call_1:         f(1);                     # call f with arg0=1
  f_call_a1:        f(a: 1);                  # call f with a=1
  f_call_1_2:       f(1, 2);                  # call f with arg0=1 arg1=2
  f_call_a1_b2:     f(a: 1, b: 2);            # call f with a=1 b=2
  f_call_1_2_c3:    f(1, 2, c: 3);            # call f with arg0=1 arg1=2 c=3
  f_call_1_sp_a1:   f(1, ...{:a 1});          # call f with arg0=1 arg1=splat(:a 1)
  f_call_a_b:       f(:a, :b);                # call f with arg0="a", arg1="b"
  f_call_a_bfoo:    f(:a, b: "foo");          # call f with arg0="a", b="foo"
}