library lib {
  thread: ->> (8)
           (n) -> n*4,
           (n) -> n+3,
           (n) -> n*n;

  thread_expected: ((n) -> n*n)(((n) -> n+3)(((n) -> n*4)(8)));
}