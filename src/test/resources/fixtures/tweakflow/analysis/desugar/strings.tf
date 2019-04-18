
library lib {

  a: "alice";
  b: "bob";

  interpolation: "#{a}:#{b}";
  interpolation_expected: (a as string)..":"..(b as string);

  simple_interpolation: "#{a}";
  simple_interpolation_expected: a as string;

}