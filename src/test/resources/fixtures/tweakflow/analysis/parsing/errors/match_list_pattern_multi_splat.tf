library lib
{
low_prime?: (long x) ->
  match x
    [@...start, @, @...end] -> true,
    default -> false;
}