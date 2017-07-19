library lib
{
  function fib: (long x) -> long
            if (x == nil)   nil
            if (x <= 0)     0
            if (x == 1)     1
            else            fib(x-1) + fib(x-2)

  fib_0:  fib(0)
  fib_1:  fib(1)
  fib_2:  fib(2)
  fib_3:  fib(3)
  fib_10: fib(10)

}