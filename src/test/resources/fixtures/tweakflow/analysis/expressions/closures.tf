library lib {
  a: 100;
  f: () -> a;
  g: (x) -> [a, x];
  h: (c) ->
       (x) -> [x, c, a];
}