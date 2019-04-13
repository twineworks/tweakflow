library lib
{
vector?: (long x) ->
  match x
    [@, @, name:] -> true,
    default -> false;
}