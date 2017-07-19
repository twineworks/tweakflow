
library recursion {
  let_cross: let {

    function even: (long x) -> boolean
                  if x == 0
                    true
                  else
                    odd(x-1)

    function odd: (long x) -> boolean
                  if x == 0
                    false
                  else
                    even(x-1)

  }
  [even(10), odd(10)]

  let_self: let {
    fib: (long x) ->
      if x <= 0 then 0
      if x == 1 then 1
      fib(x-1) + fib(x-2)
  }
  fib(5)

  deep_self: let {
    x: let {
      x: let {
        fib: (long x) ->
          if x <= 0 then 0
          if x == 1 then 1
          fib(x-1) + fib(x-2)
      } fib(5)
    } x
  } x
}

library recursion_spec {

  let_cross: recursion.let_cross == [true, false]
  let_self:  recursion.let_self  == 5
  deep_self: recursion.deep_self == 5

}