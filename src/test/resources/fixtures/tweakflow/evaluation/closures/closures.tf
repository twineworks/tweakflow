library lib
{
  make_adder: (long a) -> function
                (x) -> x + a;

  add_0: make_adder(0);
  add_1: make_adder(1);
  add_10: make_adder(10);

  n10: add_0(10);
  n11: add_1(10);
  n20: add_10(10);

}