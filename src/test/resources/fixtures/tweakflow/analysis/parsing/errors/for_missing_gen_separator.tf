library lib
{
  f: for x <- ["a", "b", "c"]
         y <- ["x", "y", "z"],
         "Hello: #{x}, #{y}"
}