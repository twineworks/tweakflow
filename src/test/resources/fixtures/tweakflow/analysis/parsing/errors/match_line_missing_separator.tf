library lib
{
low_prime?: (long x) ->
  match x
    2 -> true,
    3 -> true
    5 -> true,
    7 -> true,
    default -> false;
}