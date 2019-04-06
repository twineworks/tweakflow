library lib
{
  function f: (long x) -> long
                if x == 0
                  0
                else
                  g(x-1);

  function g: (long x) -> long
                if x == 0
                  1
                else
                  f(x-1);

  f_10: f(10);
  g_10: g(10);

}