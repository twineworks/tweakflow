library a {
  n: "a"

  f: (c) ->
    (x) -> [x, c, n]

  g: (c) ->
    (x) -> [x, c, b.n]

  h: (c) ->
    (x) -> [x, c, library::n]

}

library b {
  n: "b"

  f: (c) ->
    (x) -> [x, c, n]

  g: (c) ->
    (x) -> [x, c, a.n]

  h: (c) ->
    (x) -> [x, c, library::n]

}

library closure_spec {
  a_f: a.f(1)(0) == [0, 1, "a"]
  b_f: b.f(2)(1) == [1, 2, "b"]

  a_g: a.g(1)(0) == [0, 1, "b"]
  b_g: b.g(2)(1) == [1, 2, "a"]

  a_h: a.h(1)(0) == [0, 1, "a"]
  b_h: b.h(2)(1) == [1, 2, "b"]

}