import data, math, strings from "std"

alias data.range as range
alias math.sqrt as sqrt

library lib {

  triangles: (long x) ->
    for a <- range(1, x),
        b <- range(a, x),
        c: sqrt(a*a+b*b),
        c == (c as long),
        [a, b, c as long]
}

library operator_spec {

  nil_gen: (for x <- nil, x) == nil
  nested_nil_gen: (for x <- [1], y <- nil, x) == nil

  simple: (for x <- [1, 2], x*2) == [2, 4]

  nested_2: (
    for x <- [1, 2],
        y <- [2, 3],
        x*y
    )
    ==
    [2, 3, 4, 6]

  nested_3: (
    for x <- [1, 2],
        y <- [2, 3],
        z <- [3, 4],
        x*y*z
    )
    ==
    [1*2*3, 1*2*4, 1*3*3, 1*3*4, 2*2*3, 2*2*4, 2*3*3, 2*3*4]

  nested_3_with_predicates: (
    for x <- [1, 2, 3],
        x >= 2,
        y <- [2, 3, 4],
        y >= 3,
        z <- [3, 4, 5],
        z >= 4,
        x*y*z
    )
    ==
    [2*3*4, 2*3*5, 2*4*4, 2*4*5, 3*3*4, 3*3*5, 3*4*4, 3*4*5]

  nested_3_with_predicates_and_local_bindings: (
    for x <- [1, 2, 3],
        x2: x*2,
        x2 > 4,

        y <- [2, 3, 4],
        y2: y*2,
        y2 > 6,

        z <- [3, 4, 5],
        z2: z*2,
        z2 > 8,

        x2+y2+z2
    )
    ==
    [3*2+4*2+5*2]

    pythagorean_triangles:
      lib.triangles(20)
      ==
      [[3 4 5], [5 12 13], [6 8 10], [8 15 17], [9 12 15], [12 16 20], [15 20 25]]

    predicate_cast_to_boolean: (
      for x <- ["yeah", true, nil, ""],
          x, # predicate: (x as boolean)
          x
      )
      ==
      ["yeah", true]

    predicate_function_cast_to_boolean: (
      for x <- ["yeah", "", nil],
          strings.length(x), # predicate: long as boolean
          x
    )
    ==
    ["yeah"]


}