library lib {

  function f: (string x=1) -> x == "1"

  param_default_value:  f()
  param_argument_value: f(1)

}