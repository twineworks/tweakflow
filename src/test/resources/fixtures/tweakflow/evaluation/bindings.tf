library lib {

  a: let {long x: 1} x
  f: (long x) -> long
     let {
       long x_squared: x**2
     }
     x_squared + x_squared

  g: f(3) # 9+9 = 18

  make: (string x) ->
      let {
        f: if x == "inverse"
             (x) -> -x
           else
             (x) -> x

      }
      f

  id:   make()
  inv:  make("inverse")

  pos_one: id(1)
  neg_one: inv(1)

}