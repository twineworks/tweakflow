library lib
{
  simple: () -> true; # constant function returning true
  with_args: (double x = 0.0, double y = 0.0) -> list [x, y];
  native: (list xs) -> any via "native";
}