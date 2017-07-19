
library lib {

  f: (x) -> x*2
  g: (x) -> x*x
  h: (x) -> x+1

  thread: ->> (1) f, g, h
  thread_expected: h(g(f(1)))

}